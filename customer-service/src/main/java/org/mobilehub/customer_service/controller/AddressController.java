package org.mobilehub.customer_service.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.customer_service.dto.request.UpdateAddressRequest;
import org.mobilehub.customer_service.dto.response.DeletedAddressResponse;
import org.mobilehub.customer_service.dto.request.CreateAddressRequest;
import org.mobilehub.customer_service.dto.response.AddressResponse;
import org.mobilehub.customer_service.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddressesFromUser(@PathVariable Long userId) {
        validateUserAccess(userId);
        return ResponseEntity.ok(addressService.getAddressesFromUser(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{userId}/addresses")
    public ResponseEntity<List<AddressResponse>> adminGetAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressesFromUser(userId));
    }

    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressResponse> createAddress(
            @PathVariable Long userId,
            @RequestBody CreateAddressRequest request) {

        validateUserAccess(userId);
        return ResponseEntity.ok(addressService.createAddress(request, userId));
    }

    @GetMapping("/{userId}/addresses/default")
    public ResponseEntity<AddressResponse> getDefaultAddress(@PathVariable Long userId) {
        validateUserAccess(userId);
        return ResponseEntity.ok(addressService.getDefaultAddress(userId));
    }

    @PutMapping("/{userId}/addresses/{addressId}/default")
    public ResponseEntity<?> setDefaultAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        validateUserAccess(userId);
        addressService.setDefaultAddress(userId, addressId);
        return ResponseEntity.ok("Default address has been set successfully");
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @RequestBody UpdateAddressRequest request) {

        validateUserAccess(userId);
        return ResponseEntity.ok(addressService.updateAddress(userId, addressId, request));
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<DeletedAddressResponse> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {

        validateUserAccess(userId);
        return ResponseEntity.ok(addressService.deleteAddressFromUser(userId, addressId));
    }

    private Long getPrincipalId() {
        return (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void validateUserAccess(Long pathUserId) {
        Long principalId = getPrincipalId();
        if (!principalId.equals(pathUserId)) {
            throw new AccessDeniedException("You have no permission to access other customer data");
        }
    }
}
