package org.mobilehub.user.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mobilehub.shared.common.token.TokenProvider;
import org.mobilehub.user.dto.request.CreateAddressRequest;
import org.mobilehub.user.dto.response.AddressResponse;
import org.mobilehub.user.dto.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {

    AddressService addressService;
    TokenProvider tokenProvider;

    @PostMapping
    public ResponseEntity<AddressResponse> createAddress(
            @RequestBody CreateAddressRequest request,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = bearerToken.replace("Bearer ", "");
        Long userId = tokenProvider.ex(token); // custom method parse claim
        AddressResponse res = addressService.createAddress(request, userId);
        return ResponseEntity.ok(res);
    }

}
