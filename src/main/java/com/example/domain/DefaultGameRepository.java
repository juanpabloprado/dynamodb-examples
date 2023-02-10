package com.example.domain;

import com.example.DynamoConfiguration;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.domain.DynamoRepository.ATTRIBUTE_PK;
import static com.example.domain.DynamoRepository.ATTRIBUTE_SK;

@Singleton
public class DefaultGameRepository extends ItemRepository<Game> implements GameRepository {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultGameRepository.class);
    public static final String RUNS = "Runs";
    public static final String OPPOSING_TEAM_ID = "OpposingTeamID";
    public static final String OPPOSING_TEAM_RUNS = "OpposingTeamRuns";

    public DefaultGameRepository(DynamoDbClient dynamoDbClient, DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbClient, dynamoConfiguration);
    }

    @Override
    public List<Game> findAllByTeamIdAndDate(@NonNull String teamId, @NonNull String date) {
        QueryResponse response = dynamoDbClient.query(findGameQueryRequest(teamId, date));
        if (LOG.isTraceEnabled()) {
            LOG.trace(response.toString());
        }
        return new ArrayList<>(parseInResponse(response));
    }

    @Override
    public List<Game> findAllByTeamIdAndBetweenDate(String teamId, String startDate, String endDate) {
        QueryResponse response = dynamoDbClient.query(findGameQueryRequest(teamId, startDate, endDate));
        if (LOG.isTraceEnabled()) {
            LOG.trace(response.toString());
        }
        return new ArrayList<>(parseInResponse(response));
    }

    @Override
    public List<Game> findAllByTeamIdAndBetweenDate(String teamId, String startDate, String endDate, String opposingTeamId) {
        QueryResponse response = dynamoDbClient.query(findGameQueryRequest(teamId, startDate, endDate, opposingTeamId));
        if (LOG.isTraceEnabled()) {
            LOG.trace(response.toString());
        }
        return new ArrayList<>(parseInResponse(response));
    }

    private List<Game> parseInResponse(QueryResponse response) {
        List<Map<String, AttributeValue>> items = response.items();
        List<Game> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (Map<String, AttributeValue> item : items) {
                result.add(gameOf(item));
            }
        }
        return result;
    }

    @NonNull
    private Game gameOf(@NonNull Map<String, AttributeValue> item) {
        return new Game(item.get(ATTRIBUTE_PK).s(),
                item.get(ATTRIBUTE_SK).s(),
                Integer.parseInt(item.get(RUNS).n()),
                item.get(OPPOSING_TEAM_ID).s(),
                Integer.parseInt(item.get(OPPOSING_TEAM_RUNS).n())
        );
    }

    @NonNull
    private QueryRequest findGameQueryRequest(@NonNull String teamId, @Nullable String dateStr) {
        QueryRequest.Builder builder = QueryRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .scanIndexForward(false);

        Map<String, AttributeValue> keyToGet = new HashMap<>();

        keyToGet.put(":t", AttributeValue.builder().s("GAMES_" + teamId).build());
        keyToGet.put(":sk", AttributeValue.builder().s(dateStr).build());

        return builder.keyConditionExpression("TeamID = :t and SK = :sk")
                .expressionAttributeValues(keyToGet)
                .build();
    }

    @NonNull
    private QueryRequest findGameQueryRequest(@NonNull String teamId, @NonNull String startDate, @NonNull String endDate) {
        QueryRequest.Builder builder = QueryRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .scanIndexForward(false);

        Map<String, AttributeValue> keyToGet = new HashMap<>();

        keyToGet.put(":t", AttributeValue.builder().s("GAMES_" + teamId).build());
        keyToGet.put(":startDate", AttributeValue.builder().s(startDate).build());
        keyToGet.put(":endDate", AttributeValue.builder().s(endDate).build());

        return builder.keyConditionExpression("TeamID = :t and SK BETWEEN :startDate and :endDate")
                .expressionAttributeValues(keyToGet)
                .build();
    }

    @NonNull
    private QueryRequest findGameQueryRequest(@NonNull String teamId, @NonNull String startDate, @NonNull String endDate, @NonNull String opposingTeamId) {
        QueryRequest.Builder builder = QueryRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .scanIndexForward(false);

        Map<String, AttributeValue> keyToGet = new HashMap<>();

        keyToGet.put(":t", AttributeValue.builder().s("GAMES_" + teamId).build());
        keyToGet.put(":startDate", AttributeValue.builder().s(startDate).build());
        keyToGet.put(":endDate", AttributeValue.builder().s(endDate).build());
        keyToGet.put(":opp", AttributeValue.builder().s(opposingTeamId).build());

        return builder.keyConditionExpression("TeamID = :t and SK BETWEEN :startDate and :endDate")
                .expressionAttributeValues(keyToGet)
                .filterExpression("OpposingTeamID = :opp")
                .build();
    }
}
