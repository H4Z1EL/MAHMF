package com.utez.misestadias.service;

import com.utez.misestadias.dto.AttachmentResponseDTO;
import com.utez.misestadias.model.Activity;
import com.utez.misestadias.model.Attachment;
import com.utez.misestadias.repository.ActivityRepository;
import com.utez.misestadias.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final ActivityRepository activityRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.base-url:http://129.146.137.202:8080/files}")
    private String baseUrl;

    @Transactional
    public AttachmentResponseDTO uploadFile(Long activityId,
                                            MultipartFile file,
                                            String studentEmail) {
        // 1. Buscar actividad
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Actividad no encontrada: " + activityId));

        // 2. Verificar que la actividad pertenece al alumno autenticado
        if (!activity.getStudent().getEmail().equals(studentEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para subir archivos a esta actividad");
        }

        // 3. Validar archivo no vacío
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo está vacío");
        }

        // 4. Determinar tipo IMAGE o DOCUMENT
        String contentType = file.getContentType() != null ? file.getContentType() : "";
        String fileType = contentType.startsWith("image/") ? "IMAGE" : "DOCUMENT";

        // 5. Nombre único para evitar colisiones
        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename() : "archivo";
        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String uniqueName = UUID.randomUUID() + extension;

        // 6. Guardar en disco
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(),
                    uploadPath.resolve(uniqueName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al guardar el archivo: " + e.getMessage());
        }

        // 7. Registrar en BD
        Attachment attachment = new Attachment();
        attachment.setActivity(activity);
        attachment.setFileUrl(baseUrl + "/" + uniqueName);
        attachment.setFileType(fileType);
        Attachment saved = attachmentRepository.save(attachment);

        return toDTO(saved, originalName);
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponseDTO> getAttachmentsByActivity(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Actividad no encontrada: " + activityId);
        }
        return attachmentRepository.findByActivity_ActivityId(activityId)
                .stream()
                .map(a -> toDTO(a, null))
                .collect(Collectors.toList());
    }

    private AttachmentResponseDTO toDTO(Attachment a, String fileName) {
        return AttachmentResponseDTO.builder()
                .attachmentId(a.getAttachmentId())
                .activityId(a.getActivity().getActivityId())
                .fileUrl(a.getFileUrl())
                .fileType(a.getFileType())
                .fileName(fileName)
                .createdAt(a.getCreatedAt())
                .build();
    }
}