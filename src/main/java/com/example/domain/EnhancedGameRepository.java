package com.example.domain;

import com.example.DynamoConfiguration;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.Optional;

@Singleton
public class EnhancedGameRepository extends EnhancedItemRepository<Game> {
    private final DynamoDbTable<Game> gameTable;

    public EnhancedGameRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbEnhancedClient, dynamoConfiguration);
        gameTable = dynamoDbEnhancedClient.table(dynamoConfiguration.getTableName(), Game.GAME_TABLE_SCHEMA);
    }

    public void save(@NonNull @NotNull @Valid Game game) {
        gameTable.putItem(PutItemEnhancedRequest.builder(Game.class).item(game).build());
    }

    public Game findByTeamIdAndDate(@NonNull String teamId, @NonNull String date) {
        Key key = Key.builder().partitionValue("GAMES_" + teamId).sortValue(date).build();
        return gameTable.getItem((GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));
    }

    public Optional<Game> deleteByTeamIdAndDate(@NonNull String teamId, @NonNull String date) {
        Key key = Key.builder().partitionValue("GAMES_" + teamId).sortValue(date).build();
        Game previouslyPersistedGame = gameTable.deleteItem(DeleteItemEnhancedRequest.builder().key(key).build());
        return Optional.of(previouslyPersistedGame);
    }

    public Iterator<Game> findAll() {
        return gameTable.scan().items().iterator();
    }

    public Iterator<Page<Game>> findAllByTeamId(@NonNull String teamId) {

        QueryConditional queryConditional = QueryConditional
                .keyEqualTo(Key.builder().partitionValue("GAMES_" + teamId)
                        .build());

        return gameTable.query((QueryEnhancedRequest.Builder requestBuilder) ->
                        requestBuilder.queryConditional(queryConditional)
                                .scanIndexForward(false))
                .iterator();
    }

}
