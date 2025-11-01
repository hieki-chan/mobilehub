package org.mobilehub.product.config;

import org.mobilehub.shared.common.converter.MediaConverter;
import org.mobilehub.product.converter.MultipartJacksonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MultipartJacksonHttpMessageConverter multipartConverter;

    public WebConfig(MultipartJacksonHttpMessageConverter multipartConverter) {
        this.multipartConverter = multipartConverter;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addFirst(multipartConverter);
    }
}
