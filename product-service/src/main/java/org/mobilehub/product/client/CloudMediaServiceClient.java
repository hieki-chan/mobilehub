package org.mobilehub.product.client;

import org.mobilehub.product.config.FeignMultipartConfig;
import org.mobilehub.shared.contracts.media.ImageUploadEvent;
import org.mobilehub.shared.contracts.media.MultipleImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
        name = "cloud-media-service",
        url = "${cloud-media.service.url}",
        configuration = FeignMultipartConfig.class
)
public interface CloudMediaServiceClient {
    @PostMapping(value = "/media/upload-multiple")
    MultipleImageResponse uploadImages(@RequestBody ImageUploadEvent event);
}
