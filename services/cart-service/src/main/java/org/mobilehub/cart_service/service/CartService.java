package org.mobilehub.cart_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mobilehub.cart_service.dto.CartAddRequest;
import org.mobilehub.cart_service.dto.CartDTO;
import org.mobilehub.cart_service.dto.CartItemDTO;
import org.mobilehub.cart_service.dto.ProductCartResponse;
import org.mobilehub.cart_service.entity.Cart;
import org.mobilehub.cart_service.entity.CartItem;
import org.mobilehub.cart_service.exception.CartItemNotFoundException;
import org.mobilehub.cart_service.exception.CartNotFoundException;
import org.mobilehub.cart_service.mapper.CartMapper;
import org.mobilehub.cart_service.repository.CartItemRepository;
import org.mobilehub.cart_service.repository.CartRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate;

    @Value("${services.product.url:http://localhost:8082/api/products}")
    private String productServiceUrl;

    /**
     * ✅ Lấy giỏ hàng theo userId, nếu chưa có thì tạo mới.
     */
    public CartDTO getCart(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        return cartMapper.toCartDTO(cart);
    }

    /**
     * ✅ Thêm sản phẩm vào giỏ hàng — tự động lấy thông tin từ ProductService.
     */
    public CartDTO addItemToCart(Long userId, CartAddRequest request) {
        // 1️⃣ Kiểm tra user và cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().userId(userId).build()));

        // 2️⃣ Gọi sang product-service để lấy thông tin sản phẩm
        String url = productServiceUrl + "/" + request.getProductId() + "/detail";
        ProductCartResponse productInfo = restTemplate.getForObject(url, ProductCartResponse.class);

        if (productInfo == null) {
            throw new RuntimeException("Không thể lấy thông tin sản phẩm từ Product Service");
        }

        // 3️⃣ Kiểm tra sản phẩm đã có trong giỏ chưa
        Optional<CartItem> existingOpt = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.getProductId()))
                .findFirst();

        if (existingOpt.isPresent()) {
            // ✅ Nếu đã có thì tăng số lượng
            CartItem existing = existingOpt.get();
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setPrice(productInfo.getDiscountedPrice() != null
                    ? productInfo.getDiscountedPrice()
                    : productInfo.getPrice());
            cartItemRepository.save(existing);
        } else {
            // ✅ Nếu chưa có, tạo mới item và set dữ liệu từ productInfo
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(request.getProductId())
                    .productName(productInfo.getName())
                    .thumbnailUrl(productInfo.getImageUrl())
                    .price(productInfo.getDiscountedPrice() != null
                            ? productInfo.getDiscountedPrice()
                            : productInfo.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        // 4️⃣ Lưu lại cart
        Cart saved = cartRepository.save(cart);
        return cartMapper.toCartDTO(saved);
    }

    /**
     * ✅ Cập nhật số lượng sản phẩm trong giỏ.
     */
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

    /**
     * ✅ Xóa 1 sản phẩm trong giỏ hàng (đảm bảo thuộc user đó).
     */
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

    /**
     * ✅ Xóa toàn bộ sản phẩm trong giỏ hàng.
     */
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        cartItemRepository.deleteByCartId(cart.getId());
    }

    /**
     * ✅ Tính tổng tiền giỏ hàng.
     */
    public BigDecimal getTotal(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        return cartMapper.calculateTotal(cart);
    }
}
