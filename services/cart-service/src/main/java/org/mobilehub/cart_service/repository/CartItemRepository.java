package org.mobilehub.cart_service.repository;

import jakarta.transaction.Transactional;
import org.mobilehub.cart_service.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);
    @Transactional
    void deleteByCartId(Long cartId);
    Optional<CartItem> findByIdAndCartId(Long itemId, Long cartId);
}
