package com.donglai.web.config;

import com.donglai.web.config.exception.MyHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private MyHandlerInterceptor myHandlerInterceptor;

    @Bean
    public WebConfig getMyWebMvcConfig(){
        WebConfig myWebMvcConfig = new WebConfig() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {

            }
            //注册拦截器
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(myHandlerInterceptor).addPathPatterns("/**");
            }
        };
        return myWebMvcConfig;
    }

}
