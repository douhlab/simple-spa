package com.dev.simplespa.component.context;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CorsConfig.class)
class CorsConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void corsConfigurerBeanShouldExist() {
        WebMvcConfigurer bean = context.getBean(WebMvcConfigurer.class);
        assertThat(bean).isNotNull();
    }

}
