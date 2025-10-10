package org.mobilehub.product.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.mobilehub.product.entity.Product;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
    String name;
    String description;

    private String os;
    private String cpu;
    private String cpu_speed;
    private String gpu;
    private String ram;
    private String storage_cap;
    private String battery_cap;
    private String rear_cam;
    private String front_cam;
    private String screen_res;
    private String features;
    private String material;
    private String size_weight;
    private String brand;
    private String release_date;
    private Product product;

    BigDecimal price;
    Integer discountInPercent;
    BigDecimal discountedPrice;
}
