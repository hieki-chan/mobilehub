package org.mobilehub.cart_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.cart_service.dto.CartAddRequest;
import org.mobilehub.cart_service.dto.CartDTO;
import org.mobilehub.cart_service.dto.CartItemDTO;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;
import org.mobilehub.cart_service.exception.CartItemNotFoundException;
import org.mobilehub.cart_service.exception.CartNotFoundException;
import org.mobilehub.cart_service.mapper.CartMapper;
import org.mobilehub.cart_service.repository.CartItemRepository;
import org.mobilehub.cart_service.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    public CartDTO getCart(Long userId) {
        if (userId == null) throw new IllegalArgumentException("User ID must not be null");

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        return cartMapper.toCartDTO(cart);
    }

    public CartDTO addItemToCart(Long userId, CartAddRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        List<CartItem> existingItems = cartItemRepository.findByCartId(cart.getId());
        CartItem existing = existingItems.stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setPrice(request.getPrice());
            existing.setProductName(request.getProductName());
            existing.setThumbnailUrl(request.getThumbnailUrl());
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .thumbnailUrl(request.getThumbnailUrl())
                    .price(request.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
        }

        return cartMapper.toCartDTO(cartRepository.findById(cart.getId())
                .orElseThrow(() -> new CartNotFoundException(userId)));
    }

    public CartItemDTO updateItemQuantity(Long itemId, int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));
        item.setQuantity(quantity);
        CartItem saved = cartItemRepository.save(item);
        return cartMapper.toCartItemDTO(saved);
    }

    public void removeItem(Long userId, Long itemId) {
        // Tìm cart theo userId
        var cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        // Tìm item theo id và cartId (đảm bảo item thuộc giỏ của user)
        var item = cartItemRepository.findById(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new CartItemNotFoundException(
                        "Item " + itemId + " not found in cart of user " + userId
                ));

        // Xóa item
        cartItemRepository.delete(item);
    }


    //Xoa toan bo san pham trong gio hang
    public void clearCart(Long userId){
        CartDTO cart = getCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    public BigDecimal getTotal(Long userId){
        CartDTO cart = getCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
