package com.donglai.model.config.es;

import com.donglai.common.util.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@Order(value = 1)
public class EsConfig {

    @Value("${elasticsearch.hosts}")
    public List<String> esHosts;

    @Value("${elasticsearch.certificate}")
    public String esCertificate;


    /*
    @Bean
    @Primary
    public RestClientBuilder restClientBuilder() {
        return RestClient.builder(HttpHost.create(esHost));
    }
    @Bean(name = "highLevelClient")
    @Primary
    @Profile("!prod")
    public RestHighLevelClient esClient(RestClientBuilder restClientBuilder){

        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        return new RestHighLevelClient (restClientBuilder);
    }*/

    @Bean(name = "highLevelClient")
    @Primary
    public RestHighLevelClient esClusterClient(){
        HttpHost[] hosts = new HttpHost[esHosts.size()];
        for (int i = 0; i < esHosts.size(); i++) {
            hosts[i] = HttpHost.create(esHosts.get(i));
        }
        RestClientBuilder restClient = RestClient.builder(hosts);
        if(!StringUtils.isNullOrBlank(esCertificate)){
            String[] split = esCertificate.split(":");
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(split[0], split[1]));
            restClient.setHttpClientConfigCallback(httpClientBuilder -> {
                httpClientBuilder.disableAuthCaching();
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            });
        }
        return new RestHighLevelClient (restClient);
    }
}
