package org.mobilehub.shared.common.token;

import java.util.HashMap;
import java.util.Map;

public class ClaimSet
{
    private final Map<String, Object> claims = new HashMap<>();

    public ClaimSet add(String key, Object value) {
        this.claims.put(key, value);
        return this;
    }

    public Map<String, Object> asMap() {
        return new HashMap<>(this.claims);
    }
}