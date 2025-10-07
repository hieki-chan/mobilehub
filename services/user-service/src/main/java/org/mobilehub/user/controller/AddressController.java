package org.mobilehub.user.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.shared.common.token.TokenProvider;
import org.mobilehub.user.dto.request.CreateAddressRequest;
import org.mobilehub.user.dto.response.AddressResponse;
import org.mobilehub.user.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {

    AddressService addressService;
    TokenProvider tokenProvider;

    @PostMapping("/create")
    public ResponseEntity<AddressResponse> createAddress(
            @RequestBody CreateAddressRequest request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = bearerToken.replace("Bearer ", "");
        String userId = null; // custom method parse claim
        try {
            userId = tokenProvider.extractSubject(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        AddressResponse res = addressService.createAddress(request, userId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeAddress(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String userId = null;
        try {
            userId = tokenProvider.extractSubject(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        addressService.removeAddressFromUser(id, userId);
        return ResponseEntity.ok("Deleted successfully");
    }
}
