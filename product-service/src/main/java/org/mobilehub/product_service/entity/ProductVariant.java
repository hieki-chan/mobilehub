package org.mobilehub.product_service.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_variant")
public class ProductVariant {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    String color_label;

    @Column(nullable = false)
    String color_hex;

    @Column(nullable = false)
    Integer storage_cap;

    @Column(nullable = false)
    Integer ram;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    BigDecimal price;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<ProductImage> images = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_image_id", foreignKey = @ForeignKey(name = "fk_product_key_image"))
    ProductImage keyImage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_variant_product"))
    Product product;

    // INFO
    @Column(name = "sold")
    private Integer sold = 0;

    public String getImageUrl()
    {
        return keyImage != null ? keyImage.getImageUrl() : null;
    }

    public List<String> getSubImageUrls() {
        if (images == null || images.isEmpty()) return List.of();

        return images.stream()
                .filter(img -> keyImage == null || !img.getId().equals(keyImage.getId()))
                .map(ProductImage::getImageUrl)
                .toList();
    }
}
