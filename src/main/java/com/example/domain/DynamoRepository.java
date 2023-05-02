package com.example.domain;

import com.example.CIAwsCredentialsProviderChainCondition;
import com.example.CIAwsRegionProviderChainCondition;
import com.example.DynamoConfiguration;
import io.micronaut.context.annotation.Requires;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.util.Arrays;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;

@Requires(condition = CIAwsRegionProviderChainCondition.class)
@Requires(condition = CIAwsCredentialsProviderChainCondition.class)
@Requires(beans = {DynamoConfiguration.class, DynamoDbClient.class})
@Singleton
public class DynamoRepository {
    private static final Logger LOG = LoggerFactory.getLogger(DynamoRepository.class);
    protected static final String ATTRIBUTE_PK = "TeamID";
    protected static final String ATTRIBUTE_SK = "SK";

    protected static final StaticTableSchema<GenericEntity> GENERIC_RECORD_SCHEMA =
            StaticTableSchema.builder(GenericEntity.class)
                    .addAttribute(String.class, a -> a.name(ATTRIBUTE_PK)
                            .getter(GenericEntity::getId)
                            .setter(GenericEntity::setTeamId)
                            .tags(primaryPartitionKey()))
                    .addAttribute(String.class, a -> a.name(ATTRIBUTE_SK)
                            .getter(GenericEntity::getSk)
                            .setter(GenericEntity::setSk)
                            .tags(primarySortKey()))
                    .build();
    private final DynamoDbClient dynamoDbClient;
    private final DynamoConfiguration dynamoConfiguration;

    public DynamoRepository(DynamoDbClient dynamoDbClient,
                            DynamoConfiguration dynamoConfiguration) {
        this.dynamoDbClient = dynamoDbClient;
        this.dynamoConfiguration = dynamoConfiguration;
    }

    public void createTableIfNotExist() {
        String tableName = dynamoConfiguration.getTableName();

        if (notExists(tableName)) {
            createTable(tableName);
        }

    }

    private boolean notExists(String tableName) {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build());
            return false;
        } catch (ResourceNotFoundException e) {
            return true;
        }
    }

    private void createTable(String tableName) {
        DynamoDbWaiter dbWaiter = dynamoDbClient.waiter();
        CreateTableRequest request = CreateTableRequest.builder()
                .keySchema(Arrays.asList(KeySchemaElement.builder()
                                .attributeName(ATTRIBUTE_PK)
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName(ATTRIBUTE_SK)
                                .keyType(KeyType.RANGE)
                                .build()))
                .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_PK)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_SK)
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .provisionedThroughput(ProvisionedThroughput.builder()
                        .readCapacityUnits(5L)
                        .writeCapacityUnits(5L)
                        .build())
                .tableName(tableName)
                .build();

        CreateTableResponse response = dynamoDbClient.createTable(request);

        DescribeTableRequest tableRequest = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();

        dbWaiter.waitUntilTableExists(tableRequest);
        WaiterResponse<DescribeTableResponse> waiterResponse = dbWaiter.waitUntilTableExists(tableRequest);
        waiterResponse.matched().response().ifPresent(res -> LOG.info("Table {} created", response.tableDescription().tableName()));
    }

}
