package org.mobilehub.payment_service.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayOSConfig {

    @Bean
    public PayOS payOS() {
        // Đọc .env trong thư mục payment-service
        Dotenv dotenv = Dotenv.configure()
                .directory("services/payment-service")
                .ignoreIfMissing()
                .load();

        String clientId = dotenv.get("PAYOS_CLIENT_ID");
        String apiKey = dotenv.get("PAYOS_API_KEY");
        String checksumKey = dotenv.get("PAYOS_CHECKSUM_KEY");

        if (clientId == null || apiKey == null || checksumKey == null) {
            throw new RuntimeException("PAYOS keys not loaded. Check your .env file!");
        }

        // Trả về PayOS bean để Spring sử dụng
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
