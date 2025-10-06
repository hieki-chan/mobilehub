package org.mobilehub.product_service.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.mobilehub.product_service.converter.MediaConverter;
import org.mobilehub.product_service.converter.MultipartJacksonHttpMessageConverter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
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
        converters.add(0, multipartConverter);
    }

    @Bean
    public MediaConverter mediaConverter() {
        return new MediaConverter();
    }
}
