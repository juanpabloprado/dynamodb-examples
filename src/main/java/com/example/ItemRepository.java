package com.example;

import com.example.model.BaseEntity;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.example.DynamoRepository.ATTRIBUTE_PK;
import static com.example.DynamoRepository.ATTRIBUTE_SK;

@Singleton
public class ItemRepository<T extends BaseEntity> {
    private static final Logger LOG = LoggerFactory.getLogger(ItemRepository.class);
    protected final DynamoDbClient dynamoDbClient;
    protected final DynamoConfiguration dynamoConfiguration;

    public ItemRepository(DynamoDbClient dynamoDbClient, DynamoConfiguration dynamoConfiguration) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoConfiguration = dynamoConfiguration;
    }

    @NonNull
    protected Map<String, AttributeValue> item(@NonNull T entity) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(ATTRIBUTE_PK, AttributeValue.builder().s(entity.getId()).build());
        item.put(ATTRIBUTE_SK, AttributeValue.builder().s(entity.getSK()).build());
        return item;
    }

    protected void save(@NonNull @NotNull @Valid T entity) {
        PutItemResponse itemResponse = dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .item(item(entity))
                .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug(itemResponse.toString());
        }
    }
}
