package com.ead.course.configs;

//import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ResolverConfig implements WebMvcConfigurer {

//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(new SpecificationArgumentResolver());
//
//        var pageableResolver = new PageableHandlerMethodArgumentResolver();
//        pageableResolver.setFallbackPageable(PageRequest.of(0, 10));
//        resolvers.add(pageableResolver);
//    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").maxAge(3600);
    }
}