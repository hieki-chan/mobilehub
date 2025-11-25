# notification-service (MobileHub)

Service này:
- Nghe Kafka events:
  - payment.captured
  - installment.approved
  - installment.payment.due
- Tạo in-app notification (MySQL)
- Gửi email qua SMTP (Gmail App Password)

## Setup nhanh

1) Tạo DB MySQL:
```sql
CREATE DATABASE notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2) Sửa `application.yml`:
- `spring.datasource.*`
- `spring.mail.username/password`
- `app.jwt.signer-key` = giống y hệt identity-service.

3) Chạy service:
```bash
mvn spring-boot:run
```

## Gateway route
Thêm route:
```yaml
- id: notification-service
  uri: http://localhost:8099
  predicates:
    - Path=/api/v1/notifications/**
```

> Các contract trong `org.mobilehub.shared.contracts.notification` hiện để tạm trong service để build standalone.
> Khi tích hợp vào monorepo, bạn move nó về module `shared-contracts` và add dependency tương ứng.
