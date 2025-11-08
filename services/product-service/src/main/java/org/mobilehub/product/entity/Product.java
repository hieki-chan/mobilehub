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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_discount_id", referencedColumnName = "id", unique = true)
    ProductDiscount discount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductVariant> variants = new ArrayList<>();

    // default
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_variant_id", foreignKey = @ForeignKey(name = "fk_product_default_variant"))
    private ProductVariant defaultVariant;

    public BigDecimal getDiscountedPriceDefault() {
//        if (discount == null || discount.getValueInPercent() == null) {
//            return price;
//        }
//
//        BigDecimal discountPercent = BigDecimal.valueOf(discount.getValueInPercent());
//        BigDecimal multiplier = BigDecimal.ONE.subtract(discountPercent.divide(BigDecimal.valueOf(100)));
//
//        return price.multiply(multiplier);
        return null;
    }

    public String getImageUrlDefault(){
        return getMainImageUrlDefault();
    }

    public String getMainImageUrlDefault() {

        return null;
    }

    public List<String> getOtherImageUrlsDefault()
    {
//        return getImages().stream()
//                .filter(img -> !img.isMain())
//                .map(ProductImage::getImageUrl)
//                .toList();
        return null;
    }

    public ProductVariant resolveVariant(Long variantId) {
        if (variantId != null) {
            return getVariants() == null ? null
                    : getVariants().stream()
                    .filter(v -> variantId.equals(v.getId()))
                    .findFirst()
                    .orElse(null);
        }
        if (getDefaultVariant() != null) return getDefaultVariant();

        if (getVariants() != null && !getVariants().isEmpty()) {
            return getVariants().stream()
                    .filter(v -> v.getPrice() != null)
                    .min((a, b) -> a.getPrice().compareTo(b.getPrice()))
                    .orElse(getVariants().get(0));
        }
        return null;
    }
}