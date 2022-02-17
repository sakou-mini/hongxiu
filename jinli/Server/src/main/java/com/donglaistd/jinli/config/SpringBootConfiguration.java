package com.donglaistd.jinli.config;

import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.MenuRole;
import com.donglaistd.jinli.database.entity.system.Menu;
import com.donglaistd.jinli.http.entity.TokenRequestResult;
import com.donglaistd.jinli.util.BloomFilterHelper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
@EnableWebMvc
public class SpringBootConfiguration implements WebMvcConfigurer {

    @Value("${redis.bloom.init.space}")
    private int initSpaceNUm;
    @Value("${redis.bloom.error.rate}")
    private double errorRate;
    @Value("${spring.redis.password}")
    private String redisClusterPassword;
    @Value("${spring.redis.nodes}")
    private String[] redisClusterNodes;

    private static final Logger logger = Logger.getLogger(SpringBootConfiguration.class.getName());

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String s = "file:///" + Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/images/";
        registry.addResourceHandler("/images/**").addResourceLocations(s);
        s = "file:///" + Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/config/json/";
        registry.addResourceHandler("/config/**").addResourceLocations(s);
        s = "file:///" + Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/config/static/clientResource/";
        registry.addResourceHandler("/clientResource/**").addResourceLocations(s);
        s = "file:///" + Paths.get("").toAbsolutePath().toString().replace("\\", "/") + "/config/static/";
        registry.addResourceHandler("/config/static/**").addResourceLocations(s);
    }

    @Bean
    public RedisTemplate<String, User> userTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        RedisTemplate<String, User> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, String> userLockTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        return template;
    }

    @Bean
    public RedisTemplate<String, LiveUser> liveUserTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        RedisTemplate<String, LiveUser> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }
    @Bean
    public RedisTemplate<String, Room> roomTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        RedisTemplate<String, Room> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        return template;
    }

    @Bean
    public RedisTemplate<String, TokenRequestResult> tokenResultTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        RedisTemplate<String, TokenRequestResult> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, List<Menu>> menuTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        RedisTemplate<String, List<Menu>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, List<MenuRole>> menuRoleTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        RedisTemplate<String, List<MenuRole>> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> commonRedisTemplate(@Qualifier("connectionFactory") LettuceConnectionFactory redisSeparateDatabaseFactory) {
        logger.fine("init common template");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisSeparateDatabaseFactory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean
    @Primary
    @Profile("!prod")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        logger.fine("init redis Standalone Configuration");
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
        standaloneConfig.setHostName(redisClusterNodes[0].split(":")[0]);
        standaloneConfig.setPort(Integer.parseInt(redisClusterNodes[0].split(":")[1]));
        return standaloneConfig;
    }

    @Bean
    @Profile("prod")
    public RedisClusterConfiguration redisClusterConfiguration() {
        logger.fine("init redis cluster Configuration");
        var clusterConfiguration = new RedisClusterConfiguration();
        var nodes = new ArrayList<RedisNode>();
        for (var nodeInfo : redisClusterNodes) {
            var ni = nodeInfo.split(":");
            nodes.add(new RedisNode(ni[0], Integer.parseInt(ni[1])));
        }
        clusterConfiguration.setClusterNodes(nodes);
        clusterConfiguration.setPassword(redisClusterPassword);
        return clusterConfiguration;
    }


    @Bean
    @Profile("!prod")
    public LettuceConnectionFactory connectionFactory(@Qualifier("redisStandaloneConfiguration") RedisStandaloneConfiguration redisStandaloneConfiguration) {
        logger.fine("dev/test redis config");
        var lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfigurationBuilder.build());
    }


    @Bean(name = "connectionFactory")
    @Profile("prod")
    public LettuceConnectionFactory connectionFactoryProd(@Qualifier("redisClusterConfiguration") RedisClusterConfiguration redisClusterConfiguration) {
        logger.fine("prod redis config");
        var lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfigurationBuilder.build());
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    public RedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        return serializer;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/bo").setViewName("login");
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver bean = new InternalResourceViewResolver();
        bean.setViewClass(JstlView.class);
        bean.setPrefix("/WEB-INF/view/");
        bean.setSuffix(".jsp");
        return bean;
    }

    @Bean
    public BloomFilterHelper<String> initBloomFilterHelper() {
        return new BloomFilterHelper((Funnel<String>) (from, into) -> into.putString(from, Charsets.UTF_8)
                .putString(from, Charsets.UTF_8), initSpaceNUm, errorRate);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(6000);
        requestFactory.setReadTimeout(6000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        return restTemplate;
    }

    @Autowired
    WebFilterHandlerIntercepter webFilterHandlerIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webFilterHandlerIntercepter).addPathPatterns("/**");
    }
}
