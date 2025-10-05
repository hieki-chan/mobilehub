package org.mobilehub.cart_service.service;

import org.mobilehub.cart_service.dto.CartAddRequest;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;
import org.mobilehub.cart_service.repository.CartItemRepository;
import org.mobilehub.cart_service.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    private CartRepository cartRepository;
    private CartItemRepository cartItemRepository;

    public Cart getCart(Long userId){
        if(userId != null){
            return cartRepository.findById(userId)
                    .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));
        } else {
            throw new IllegalArgumentException("userId not null");
        }
    }

    public Cart addItemToCart(Long userId, CartAddRequest request){
        Cart cart = getCart(userId);

        List<CartItem> existingItems = cartItemRepository.findByCartId(cart.getId());
        CartItem existing = existingItems.stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElse(null);

        if(existing != null){
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

        return cartRepository.findById(cart.getId()).orElseThrow();
    }

    public CartItem updateItemQuantity(Long itemId, int quantity){
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item does not exist"));
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    //Xoa 1 san pham trong gio hang
    public void removeItem(Long itemId){
        if(!cartItemRepository.existsById(itemId)){
            throw new RuntimeException("Item does not exist");
        }
        cartItemRepository.deleteById(itemId);
    }

    //Xoa toan bo san pham trong gio hang
    public void clearCart(Long userId){
        Cart cart = getCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    public BigDecimal getTotal(Long userId){
        Cart cart = getCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        return items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
