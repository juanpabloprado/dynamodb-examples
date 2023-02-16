package com.example.domain;

import com.example.DynamoConfiguration;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Iterator;
import java.util.Optional;

@Singleton
public class EnhancedGameRepository extends EnhancedItemRepository<Game> {
    private static final Logger LOG = LoggerFactory.getLogger(EnhancedGameRepository.class);
    private final DynamoDbTable<Game> gameTable;

    public EnhancedGameRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoConfiguration dynamoConfiguration) {
        super(dynamoDbEnhancedClient, dynamoConfiguration);
        gameTable = this.dynamoDbEnhancedClient.table(dynamoConfiguration.getTableName(), Game.GAME_TABLE_SCHEMA);
    }

    protected void save(@NonNull @NotNull @Valid Game game) {
        gameTable.putItem(PutItemEnhancedRequest.builder(Game.class).item(game).build());
    }

    public Optional<Game> findByTeamIdAndDate(@NonNull String teamId, @NonNull String date) {
        Key key = Key.builder().partitionValue(teamId).sortValue(date).build();
        Game game = gameTable.getItem((GetItemEnhancedRequest.Builder requestBuilder) -> requestBuilder.key(key));
        return Optional.of(game);
    }

    public Optional<Game> deleteByTeamIdAndDate(@NonNull String teamId, @NonNull String date) {
        Key key = Key.builder().partitionValue(teamId).sortValue(date).build();
        Game previouslyPersistedGame = gameTable.deleteItem(DeleteItemEnhancedRequest.builder().key(key).build());
        return Optional.of(previouslyPersistedGame);
    }

    public Iterator<Game> findAll() {
        return gameTable.scan().items().iterator();
    }

}
