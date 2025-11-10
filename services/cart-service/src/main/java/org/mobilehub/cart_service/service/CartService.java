package org.mobilehub.cart_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.cart_service.client.UserClient;
import org.mobilehub.cart_service.client.ProductClient;
import org.mobilehub.cart_service.dto.request.CartAddRequest;
import org.mobilehub.cart_service.dto.response.*;
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
import java.util.ArrayList;
import java.util.List;
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
    private final UserClient gatewayClient;


    public CartResponseDTO getCart(Long userId) {
        if (userId == null)
            throw new IllegalArgumentException("User ID must not be null");

        if(!gatewayClient.exists(userId))
            throw new CartNotFoundException("cart not found for user with id: " + userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        return getCartResponse(cart.getItems());
    }

    public CartResponseDTO addItemToCart(Long userId, CartAddRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        boolean isVariantValid = productClient.checkProductVariantValid(request.getProductId(), request.getVariantId());
        if (!isVariantValid) {
            throw new RuntimeException("Không thể lấy thông tin sản phẩm từ Product Service");
        }

        Optional<CartItem> existingOpt = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingOpt.isPresent()) {
            CartItem existing = existingOpt.get();
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = cartMapper.toCartItem(request);
            newItem.setCart(cart);
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        Cart saved = cartRepository.save(cart);
        return getCartResponse(saved.getItems());
    }

    public UpdateQuantityResponse updateItemQuantity(Long userId, Long itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem item = validateCartItem(userId, itemId);
        item.setQuantity(quantity);
        CartItem saved = cartItemRepository.save(item);
        return cartMapper.toUpdateQuantityResponse(saved);
    }

    public UpdateVariantResponse updateItemVariant(Long userId, Long itemId, Long variantId) {
        CartItem item = validateCartItem(userId, itemId);

        productClient.checkProductVariantValid(item.getProductId(), variantId);
        item.setVariantId(variantId);
        CartItem saved = cartItemRepository.save(item);
        return cartMapper.toUpdateVariantResponse(saved);
    }

    private CartItem validateCartItem(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("cart not found for user with id: " + userId));

        if (!cart.getId().equals(item.getCart().getId())) {
            throw new CartItemNotFoundException(itemId);
        }

        return item;
    }

    public void removeItem(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        CartItem item = cartItemRepository.findByIdAndCartId(itemId, cart.getId())
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

//    public BigDecimal getTotal(Long userId) {
//        Cart cart = cartRepository.findByUserId(userId)
//                .orElseThrow(() -> new CartNotFoundException(userId));
//        return cartMapper.calculateTotal(cart);
//    }


    private CartResponseDTO getCartResponse(List<CartItem> items) {
        CartResponseDTO response = new CartResponseDTO();
        var products = productClient.getProductsInCart(
                items.stream().map(CartItem::getProductId).toList());

        List<CartItemResponseDTO> cartItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < products.size(); i++) {
            ProductCartResponse productResponse = products.get(i);
            CartItemResponseDTO cartItemResponseDTO = cartMapper.toCartItemResponseDTO(productResponse);
            cartItemResponseDTO.setId(items.get(i).getId());
            cartItemResponseDTO.setProductId(items.get(i).getProductId());
            cartItemResponseDTO.setVariantId(items.get(i).getVariantId());
            cartItemResponseDTO.setQuantity(items.get(i).getQuantity());
            cartItems.add(cartItemResponseDTO);
            total = total.add(productResponse.getPrice(items.get(i).getVariantId()));
        }

        response.setItems(cartItems);
        response.setTotalPrice(total);

        return response;
    }
}
