package org.mobilehub.customer_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.customer_service.dto.request.UpdateAddressRequest;
import org.mobilehub.customer_service.dto.response.DeletedAddressResponse;
import org.mobilehub.shared.common.token.TokenProvider;
import org.mobilehub.customer_service.dto.request.CreateAddressRequest;
import org.mobilehub.customer_service.dto.response.AddressResponse;
import org.mobilehub.customer_service.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SuppressWarnings("unused")
public class AddressController {

    CustomerService addressService;
    TokenProvider tokenProvider;

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressResponse>> getAddressesFromUser() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(addressService.getAddressesFromUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<List<AddressResponse>> getAddressesFromUser(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressesFromUser(userId));
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressResponse> createAddress(@RequestBody CreateAddressRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AddressResponse res = addressService.createAddress(request, userId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/addresses/{addressId}/default")
    public ResponseEntity<?> setDefaultAddress(@PathVariable Long addressId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok("Default address has been set successfully");
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long addressId,
            @RequestBody UpdateAddressRequest request) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, request));
    }


    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<DeletedAddressResponse> deleteAddress(@PathVariable Long addressId) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(addressService.deleteAddressFromUser(userId, addressId));
    }
}
