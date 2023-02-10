package com.example;

import com.example.model.BaseEntity;
import io.micronaut.core.annotation.NonNull;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.example.DynamoRepository.ATTRIBUTE_PK;
import static com.example.DynamoRepository.ATTRIBUTE_SK;

public class ItemRepository<T extends BaseEntity> {
    @NonNull
    protected Map<String, AttributeValue> item(@NonNull T entity) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(ATTRIBUTE_PK, AttributeValue.builder().s(entity.getId()).build());
        item.put(ATTRIBUTE_SK, AttributeValue.builder().s(entity.getSK()).build());
        return item;
    }
}
