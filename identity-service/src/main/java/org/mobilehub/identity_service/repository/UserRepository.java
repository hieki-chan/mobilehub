package org.mobilehub.identity_service.repository;

import org.mobilehub.identity_service.entity.Role;
import org.mobilehub.identity_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllByRole(Role role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);

    @Query("""
    SELECT COUNT(u)
    FROM User u
    WHERE MONTH(u.createdAt) = MONTH(CURRENT_DATE)
      AND YEAR(u.createdAt) = YEAR(CURRENT_DATE)
""")
    Long countNewUsersThisMonth();
}
