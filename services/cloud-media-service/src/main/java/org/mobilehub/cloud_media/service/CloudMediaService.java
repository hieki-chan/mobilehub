package org.mobilehub.cloud_media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.mobilehub.cloud_media.dto.response.DeleteImageResponse;
import org.mobilehub.cloud_media.dto.response.ImageResponse;
import org.mobilehub.cloud_media.dto.response.ImageVersions;
import org.mobilehub.cloud_media.dto.response.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.shared.common.converter.MediaConverter;
import org.mobilehub.shared.contracts.media.ImageDeleteEvent;
import org.mobilehub.shared.contracts.media.ImageTopics;
import org.mobilehub.shared.contracts.media.ImageUploadEvent;
import org.mobilehub.shared.contracts.media.ImageUploadedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudMediaService {

    private final Cloudinary cloudinary;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = ImageTopics.IMAGE_UPLOAD)
    public void handleUpload(ImageUploadEvent event) {
        boolean isDefault = true;
        for (String base64 : event.getFiles()) {
            try {
                // Decode Base64 to bytes
                byte[] bytes = MediaConverter.decodeFromBase64(base64);

                var result = cloudinary.uploader().upload(bytes,
                        ObjectUtils.asMap(
                                "folder", "products",
                                "resource_type", "image"
                        ));

                String publicId = (String) result.get("public_id");
                String url = (String) result.get("secure_url");

                // uploaded callback
                ImageUploadedEvent uploadedEvent = new ImageUploadedEvent();
                uploadedEvent.setProductId(event.getProductId());
                uploadedEvent.setPublicId(publicId);
                uploadedEvent.setUrl(url);
                uploadedEvent.setMain(isDefault);
                isDefault = false;

                kafkaTemplate.send(ImageTopics.IMAGE_UPLOADED, uploadedEvent);

                System.out.println("Uploaded: " + publicId);

            } catch (Exception e) {
                System.err.println("Upload failed: " + e.getMessage());
            }
        }
    }

    @KafkaListener(topics = ImageTopics.IMAGE_DELETE)
    public void handleImageDeleted(ImageDeleteEvent event) {
        try {
            deleteImage(event.getPublicId());
            System.out.println("Image deleted: " + event.getPublicId());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public UploadResponse uploadImage(MultipartFile file, String folder) throws IOException {
        var uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder != null ? folder : "assets",
                        "resource_type", "image",
                        "quality", "auto",
                        "fetch_format", "auto"
                )
        );

        String publicId = uploadResult.get("public_id").toString();

        return new UploadResponse(
                publicId,
                uploadResult.get("url").toString(),
                uploadResult.get("secure_url").toString(),
                (Integer) uploadResult.get("width"),
                (Integer) uploadResult.get("height"),
                uploadResult.get("format").toString(),
                generateImageVersions(publicId)
        );
    }

    public ImageResponse getImageInfo(String publicId) throws Exception {
        var resource = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());

        return ImageResponse.builder()
                .publicId(publicId)
                .url(cloudinary.url().generate(publicId))
                .secureUrl(cloudinary.url().secure(true).generate(publicId))
                .thumbnailUrl(cloudinary.url()
                        .transformation(new Transformation().width(200).height(200).crop("thumb"))
                        .generate(publicId))
                .width((Integer) resource.get("width"))
                .height((Integer) resource.get("height"))
                .format((String) resource.get("format"))
                .size(((Number) resource.get("bytes")).longValue())
                .build();
    }

    public byte[] downloadImage(String publicId) throws IOException {
        String url = cloudinary.url().secure(true).generate(publicId);
        System.out.println(url);
        try (InputStream inputStream = new URI(url).toURL().openStream()) {
            return inputStream.readAllBytes();
        } catch (URISyntaxException e) {
            return new byte[0];
        }
    }

    public List<UploadResponse> uploadMultipleImages(List<MultipartFile> files, String folder) throws IOException {
        List<UploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            responses.add(uploadImage(file, folder));
        }
        return responses;
    }

    public DeleteImageResponse deleteImage(String publicId) throws IOException{
        try {
            //System.out.println("delete image" + publicId);
            Map<?, ?> response = cloudinary.uploader().destroy(publicId, Map.of());
            String result = (String) response.get("result");
            System.out.println(result);

            if (result.equals("ok")) {
                return DeleteImageResponse.builder().isSuccess(true).build();
            } else {
                return DeleteImageResponse.builder().isSuccess(false).build();
            }

        } catch (IOException e) {
            //System.err.println("Error deleting image from Cloudinary: " + e.getMessage());
            throw e;
        }
    }

    public void deleteMultipleImages(List<String> publicIds) throws Exception {
        cloudinary.api().deleteResources(publicIds, ObjectUtils.emptyMap());
    }

    private String buildUrl(String publicId, int width, int height, String crop) {
        return cloudinary.url()
                .transformation(new Transformation().width(width).height(height).crop(crop))
                .secure(true)
                .generate(publicId);
    }

    private ImageVersions generateImageVersions(String publicId) {
        return new ImageVersions(
                buildUrl(publicId, 150, 150, "fill"),
                buildUrl(publicId, 500, 500, "limit"),
                buildUrl(publicId, 1200, 1200, "limit"),
                cloudinary.url().secure(true).generate(publicId)
        );
    }
}
