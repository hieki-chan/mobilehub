package org.mobilehub.payment_service.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payos")
public class PayOSProperties {

    // PayOS Merchant API base (production)
    private String apiBaseUrl = "https://api-merchant.payos.vn";

    // Lấy từ PayOS dashboard
    private String clientId;
    private String apiKey;
    private String checksumKey;

    // FE thật để PayOS redirect
    private String returnUrlBase;
    private String cancelUrlBase;

    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getChecksumKey() { return checksumKey; }
    public void setChecksumKey(String checksumKey) { this.checksumKey = checksumKey; }

    public String getReturnUrlBase() { return returnUrlBase; }
    public void setReturnUrlBase(String returnUrlBase) { this.returnUrlBase = returnUrlBase; }

    public String getCancelUrlBase() { return cancelUrlBase; }
    public void setCancelUrlBase(String cancelUrlBase) { this.cancelUrlBase = cancelUrlBase; }
}
