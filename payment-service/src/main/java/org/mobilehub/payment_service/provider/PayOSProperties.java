package org.mobilehub.payment_service.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "payos")
public class PayOSProperties {
    private String webhookSecret = "changeme";
    private String signatureHeader = "X-Signature";
    private String baseUrl = "http://localhost:5173/mock/payos";
    public String getWebhookSecret(){ return webhookSecret; }
    public void setWebhookSecret(String s){ this.webhookSecret = s; }
    public String getSignatureHeader(){ return signatureHeader; }
    public void setSignatureHeader(String s){ this.signatureHeader = s; }
    public String getBaseUrl(){ return baseUrl; }
    public void setBaseUrl(String s){ this.baseUrl = s; }
}
