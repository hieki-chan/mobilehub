package org.mobilehub.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.mobilehub.product.util.ProductStatusConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column
    String description;

    @Column(name = "status")
    @Convert(converter = ProductStatusConverter.class)
    ProductStatus status =  ProductStatus.ACTIVE;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_spec_id", referencedColumnName = "id", unique = true)
    ProductSpec spec;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProductImage> images = new ArrayList<>();

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    BigDecimal price;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_discount_id", referencedColumnName = "id", unique = true)
    ProductDiscount discount;

    public BigDecimal getDiscountedPrice() {
        if (discount == null || discount.getValueInPercent() == null) {
            return price;
        }

        BigDecimal discountPercent = BigDecimal.valueOf(discount.getValueInPercent());
        BigDecimal multiplier = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100)));

        return price.multiply(multiplier);
    }

    public String getMainImage() {
        if (images == null || images.isEmpty()) {
            return "";
        }

        return images.stream()
                .filter(ProductImage::isMain)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElse(images.getFirst().getImageUrl());
    }
}