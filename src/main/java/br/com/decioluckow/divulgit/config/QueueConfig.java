package br.com.decioluckow.divulgit.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mongodb.store.MongoDbMessageStore;

@Configuration
@EnableIntegration
public class QueueConfig {

    @Value("${spring.data.mongodb.host}")
    private String host;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.port}")
    private int port;

    @Bean
    public MongoClient mongoClients() {
        return MongoClients.create("mongodb://"+host+":"+port+"/"+database+"?readPreference=primary&appname=DivulGit&ssl=false");
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(final MongoClient mongoClients) {
        return new SimpleMongoClientDatabaseFactory(mongoClients, database);
    }

    @Bean
    public MongoDbMessageStore mongoDbMessageStore(final MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoDbMessageStore(mongoDatabaseFactory);
    }
}
