package org.mobilehub.cloud_media_service.controller;

import java.io.IOException;
import java.util.List;

import org.mobilehub.cloud_media_service.dto.response.DeleteImageResponse;
import org.mobilehub.cloud_media_service.dto.response.UploadResponse;
import org.mobilehub.cloud_media_service.service.CloudMediaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class CloudMediaController {

    private final CloudMediaService mediaService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder) throws IOException {
        UploadResponse response = mediaService.uploadImage(file, folder);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/upload-multiple", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UploadResponse>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestPart(value = "folder", required = false) String folder) throws IOException {
        List<UploadResponse> responses = mediaService.uploadMultipleImages(files, folder);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/download/{publicId}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String publicId) {
        try {
            String decodedPublicId = publicId.replace("_", "/");
            byte[] imageData = mediaService.downloadImage(decodedPublicId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            headers.setContentDispositionFormData("attachment", publicId + ".jpg");

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<DeleteImageResponse> deleteImage(@PathVariable String publicId) {
        //System.out.println("delete" + publicId);
        String decodedPublicId = publicId.replace("_", "/");
        DeleteImageResponse response = null;
        try {
            response = mediaService.deleteImage(decodedPublicId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }


    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteMultipleImages(@RequestBody List<String> publicIds) throws Exception {
        mediaService.deleteMultipleImages(publicIds);
        return ResponseEntity.noContent().build();
    }
}
