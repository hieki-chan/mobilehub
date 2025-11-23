package org.mobilehub.product_service.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminProductVariantResponse {
    Long id;
    String color_label;
    String color_hex;
    Integer storage_cap;
    Integer ram;
    BigDecimal price;
    String imageUrl;
    List<String> subImageUrls;
}
