package org.mobilehub.cart_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

//    public BigDecimal getTotalAmount() {
//        if (items == null || items.isEmpty()) {
//            return BigDecimal.ZERO;
//        }
//
//        return items.stream()
//                .map(CartItem::getSubtotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
}
