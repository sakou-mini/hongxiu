package com.donglai.web.config;

import org.springframework.context.annotation.Configuration;

/**
 * 解决跨域问题
 *
 * @author : YeTianYi
 * @date : 2019-11-25 16:10
 **/

@Configuration
public class CorsConfig {
    /*@Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        // 设置你要允许的网站域名，如果全允许则设为 *
        config.addAllowedOrigin("*");
        //config.addAllowedOrigin("http://localhost:9527");
        // 如果要限制 HEADER 或 METHOD 请自行更改
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }*/
}
