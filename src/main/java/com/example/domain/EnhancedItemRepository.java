package com.example.domain;

import com.example.DynamoConfiguration;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.example.domain.DynamoRepository.ATTRIBUTE_PK;
import static com.example.domain.DynamoRepository.ATTRIBUTE_SK;

@Singleton
public class EnhancedItemRepository<T extends GenericEntity> {
    protected final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    protected final DynamoConfiguration dynamoConfiguration;

    public EnhancedItemRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoConfiguration dynamoConfiguration) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.dynamoConfiguration = dynamoConfiguration;
    }

    @NonNull
    protected Map<String, AttributeValue> item(@NonNull T entity) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(ATTRIBUTE_PK, AttributeValue.builder().s(entity.getId()).build());
        item.put(ATTRIBUTE_SK, AttributeValue.builder().s(entity.getSk()).build());
        return item;
    }
}
