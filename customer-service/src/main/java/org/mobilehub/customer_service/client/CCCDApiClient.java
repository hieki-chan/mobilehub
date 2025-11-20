package org.mobilehub.customer_service.client;

import org.mobilehub.customer_service.dto.response.VerifyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "cccd-api", url = "http://127.0.0.1:9096")
public interface CCCDApiClient {

    @PostMapping(value = "/api/v1/extract-cccd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    VerifyResponse verifyCCCD(@RequestPart("front_image") MultipartFile frontImage);
}
