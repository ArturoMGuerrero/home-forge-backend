package com.homeforge.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.homeforge.security.TenantGuard;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TenantGuard tenantGuard;
    private final String uploadsDirectory;

    public WebConfig(
            TenantGuard tenantGuard,
            @Value("${app.uploads.directory:uploads}") String uploadsDirectory
    ) {
        this.tenantGuard = tenantGuard;
        this.uploadsDirectory = uploadsDirectory;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantGuard).addPathPatterns("/api/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploads = Path.of(uploadsDirectory).toAbsolutePath().normalize().toUri().toString();
        if (!uploads.endsWith("/")) {
            uploads += "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(uploads);
    }
}
