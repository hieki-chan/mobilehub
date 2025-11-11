package org.mobilehub.payment_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.entity.IdempotencyKey;
import org.mobilehub.payment_service.repo.IdempotencyKeyRepository;
import org.mobilehub.payment_service.exception.ConflictException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final IdempotencyKeyRepository repo;

    public Optional<Long> lookupPaymentId(String key, String endpoint, String requestHash) {
        return repo.findByKeyAndEndpoint(key, endpoint)
            .map(ik -> {
                if (!ik.getRequestHash().equals(requestHash)) {
                    throw new ConflictException("Idempotency-Key used with different request");
                }
                return ik.getPaymentId();
            });
    }

    public void storePaymentMapping(String key, String endpoint, String requestHash, Long paymentId) {
        IdempotencyKey ik = IdempotencyKey.builder()
                .key(key).endpoint(endpoint).requestHash(requestHash).paymentId(paymentId).build();
        repo.save(ik);
    }
}
