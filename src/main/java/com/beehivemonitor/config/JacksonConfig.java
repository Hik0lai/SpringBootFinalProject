package com.beehivemonitor.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.createXmlMapper(false).build();
        
        // Register Hibernate module to handle lazy loading and proxies
        Hibernate6Module hibernateModule = new Hibernate6Module();
        // Don't force lazy loading - ignore unloaded properties
        hibernateModule.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
        // Serialize only IDs for lazy-loaded associations
        hibernateModule.configure(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        // Replace persistent collections with empty collections if not loaded
        hibernateModule.configure(Hibernate6Module.Feature.REPLACE_PERSISTENT_COLLECTIONS, true);
        mapper.registerModule(hibernateModule);
        
        // Additional Jackson configuration
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        return mapper;
    }
}

