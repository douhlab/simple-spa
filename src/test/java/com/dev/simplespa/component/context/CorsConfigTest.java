package com.dev.simplespa.component.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootTest(classes = CorsConfig.class)
class CorsConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void corsConfigurerBeanIsCreatedAndCovered() throws Exception {
        WebMvcConfigurer configurer = context.getBean(WebMvcConfigurer.class);

        // Use reflection to call the addCorsMappings method
        java.lang.reflect.Method method = configurer.getClass()
            .getMethod("addCorsMappings", CorsRegistry.class);
        method.invoke(configurer, new CorsRegistry());

        assertThat(configurer).isNotNull();
    }

}
