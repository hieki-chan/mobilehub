package org.mobilehub.product.dto.response;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpecResponse {
    String os;

    String cpu;

    String cpu_speed;

    String gpu;

    String battery_cap;

    String rear_cam;

    String front_cam;

    String screen_res;

    String features;

    String material;

    String size_weight;

    String brand;

    String release_date;
}
