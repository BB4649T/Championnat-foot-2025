package org.example.centralapi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "championship")
public class ChampionshipApiConfig {
    private List<ApiConfig> apis;

    public List<ApiConfig> getApis() {
        return apis;
    }

    public void setApis(List<ApiConfig> apis) {
        this.apis = apis;
    }

    public static class ApiConfig {
        private String name;
        private String url;
        private String key;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}