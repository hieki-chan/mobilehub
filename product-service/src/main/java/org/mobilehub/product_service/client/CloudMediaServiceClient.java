package org.mobilehub.product_service.client;

import org.mobilehub.product_service.config.FeignMultipartConfig;
import org.mobilehub.shared.contracts.media.ImageUploadEvent;
import org.mobilehub.shared.contracts.media.MultipleImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "cloud-media-service",
        url = "${cloud-media.service.url}",
        configuration = FeignMultipartConfig.class
)
public interface CloudMediaServiceClient {
    @PostMapping(value = "/media/upload-multiple")
    MultipleImageResponse uploadImages(@RequestBody ImageUploadEvent event);
}
