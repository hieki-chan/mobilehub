package org.mobilehub.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateProductSpecRequest {
    @NotBlank(message = "Operating system (os) is required")
    private String os;

    @NotBlank(message = "CPU is required")
    private String cpu;

    @NotBlank(message = "CPU speed is required")
    private String cpu_speed;

    @NotBlank(message = "GPU is required")
    private String gpu;

    @NotBlank(message = "RAM is required")
    private String ram;

    @NotBlank(message = "Storage capacity is required")
    private String storage_cap;

    @NotBlank(message = "Battery capacity is required")
    private String battery_cap;

    @NotBlank(message = "Rear camera spec is required")
    private String rear_cam;

    @NotBlank(message = "Front camera spec is required")
    private String front_cam;

    @NotBlank(message = "Screen resolution is required")
    private String screen_res;

    @NotBlank(message = "Features field is required")
    private String features;

    @NotBlank(message = "Material field is required")
    private String material;

    @NotBlank(message = "Size & weight is required")
    private String size_weight;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Release date is required")
    private LocalDateTime release_date;
}
