package org.mobilehub.payment_service.repo;

import org.mobilehub.payment_service.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByKeyAndEndpoint(String key, String endpoint);
}
