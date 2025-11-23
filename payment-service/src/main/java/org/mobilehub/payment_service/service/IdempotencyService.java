package org.mobilehub.payment_service.service;

import lombok.RequiredArgsConstructor;
import org.mobilehub.payment_service.entity.IdempotencyKey;
import org.mobilehub.payment_service.exception.ConflictException;
import org.mobilehub.payment_service.repository.IdempotencyKeyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {
    private final IdempotencyKeyRepository repo;

    @Transactional(readOnly = true)
    public Optional<Long> lookupPaymentId(String key, String endpoint, String requestHash) {
        String k = normalize(key);
        String ep = normalize(endpoint);

        return repo.findByKeyAndEndpoint(k, ep)
                .map(ik -> {
                    if (!Objects.equals(ik.getRequestHash(), requestHash)) {
                        throw new ConflictException("Idempotency-Key used with different request");
                    }
                    return ik.getPaymentId();
                });
    }

    /**
     * Upsert-safe:
     * - Nếu key chưa có => insert
     * - Nếu đã có:
     *    + hash khác => Conflict
     *    + hash giống => không làm gì (idempotent)
     */
    @Transactional
    public void storePaymentMapping(String key, String endpoint, String requestHash, Long paymentId) {
        String k = normalize(key);
        String ep = normalize(endpoint);

        try {
            IdempotencyKey ik = IdempotencyKey.builder()
                    .key(k)
                    .endpoint(ep)
                    .requestHash(requestHash)
                    .paymentId(paymentId)
                    .build();

            repo.save(ik);

        } catch (DataIntegrityViolationException ex) {
            // Đã có record do request khác insert trước đó => đọc lại và verify hash
            IdempotencyKey existing = repo.findByKeyAndEndpoint(k, ep)
                    .orElseThrow(() -> ex);

            if (!Objects.equals(existing.getRequestHash(), requestHash)) {
                throw new ConflictException("Idempotency-Key used with different request");
            }

            // hash giống => ok, coi như save thành công (idempotent)
        }
    }

    private String normalize(String s) {
        return s == null ? null : s.trim();
    }
}
