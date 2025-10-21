package org.mobilehub.cart_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.cart_service.client.ProductClient;
import org.mobilehub.cart_service.dto.*;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductClient productClient;


    public CartDTO getCart(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        return cartMapper.toCartDTO(cart);
    }


    public CartDTO addItemToCart(Long userId, CartAddRequest request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));


        ProductCartResponse productInfo = productClient.getProductById(request.getProductId());

        if (productInfo == null) {
            throw new RuntimeException("Không thể lấy thông tin sản phẩm từ Product Service");
        }


        Optional<CartItem> existingOpt = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        BigDecimal priceToUse = productInfo.getDiscountedPrice() != null
                ? productInfo.getDiscountedPrice()
                : productInfo.getPrice();

        if (existingOpt.isPresent()) {
            CartItem existing = existingOpt.get();
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setPrice(priceToUse);
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(productInfo.getName())
                    .thumbnailUrl(productInfo.getImageUrl())
                    .price(priceToUse)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }


        Cart saved = cartRepository.save(cart);
        return cartMapper.toCartDTO(saved);
    }

    public CartItemDTO updateItemQuantity(Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        item.setQuantity(quantity);
        CartItem saved = cartItemRepository.save(item);
        return cartMapper.toCartItemDTO(saved);
    }


    public void removeItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        CartItem item = cartItemRepository.findById(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new CartItemNotFoundException(
                        "Item " + itemId + " not found in cart of user " + userId
                ));

        cartItemRepository.delete(item);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        cartItemRepository.deleteByCartId(cart.getId());
    }

    public BigDecimal getTotal(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        return cartMapper.calculateTotal(cart);
    }
}
