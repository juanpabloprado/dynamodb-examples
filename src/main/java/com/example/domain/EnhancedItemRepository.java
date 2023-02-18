package com.example.domain;

import com.example.DynamoConfiguration;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Singleton
public class EnhancedItemRepository<T extends GenericEntity> {
    protected final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    protected final DynamoConfiguration dynamoConfiguration;

    public EnhancedItemRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoConfiguration dynamoConfiguration) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.dynamoConfiguration = dynamoConfiguration;
    }
}
