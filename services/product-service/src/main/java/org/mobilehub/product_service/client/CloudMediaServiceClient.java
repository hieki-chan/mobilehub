package org.mobilehub.product_service.client;

import org.mobilehub.cloud_media_service.dto.response.UploadResponse;
import org.mobilehub.product_service.config.FeignMultipartConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "cloud-media-service",
        url = "${cloud-media.service.url}",
        configuration = FeignMultipartConfig.class
)
public interface CloudMediaServiceClient {
    @PostMapping(value = "/api/media/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    List<UploadResponse> uploadMultipleImages(
            @RequestPart("files") List<MultipartFile> files,
            @RequestPart(value = "folder", required = false) String folder
    );
}