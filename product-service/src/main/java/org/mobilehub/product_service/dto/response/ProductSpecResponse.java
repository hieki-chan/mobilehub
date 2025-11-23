package org.mobilehub.product_service.dto.response;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductSpecResponse {
    String os;

    String cpu;

    Integer cpu_speed;

    String gpu;

    Integer battery_cap;

    String rear_cam;

    String front_cam;

    String screen_res;

    String features;

    String material;

    Integer size_weight;

    String brand;

    String release_date;
}
