package org.mobilehub.cart_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private Long productId;

    private String productName;
    // Lưu URL ảnh thumbnail sản phẩm
    private String thumbnailUrl;

    private BigDecimal price;

    private int quantity;

    public double getSubtotal(){
        return price.doubleValue() * quantity;
    }
}
