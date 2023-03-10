package com.example.domain;

import com.example.DynamoConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static com.example.domain.DefaultGameRepository.OPPOSING_TEAM_ID;
import static com.example.domain.DefaultGameRepository.OPPOSING_TEAM_RUNS;
import static com.example.domain.DefaultGameRepository.RUNS;
import static com.example.domain.DynamoRepository.ATTRIBUTE_PK;
import static com.example.domain.DynamoRepository.ATTRIBUTE_SK;

@Singleton
public class InsertGame extends ItemRepository<Game> {
    private static final Logger LOG = LoggerFactory.getLogger(InsertGame.class);
    private final ObjectMapper mapper;

    public InsertGame(ObjectMapper mapper, DynamoDbClient client, DynamoConfiguration dynamoConfiguration) {
        super(client, dynamoConfiguration);
        this.mapper = mapper;
    }

    public void createItems() {
        // Load the data
        var games = getClass().getClassLoader().getResourceAsStream("games.json");
        putGameItems(games);
    }

    private void putGameItems(InputStream stream) {
        try {
            JsonNode node = mapper.readTree(stream);
            Iterator<JsonNode> iter = node.path("BaseballStats").iterator();
            ObjectNode currentNode;
            while (iter.hasNext()) {
                currentNode = (ObjectNode) iter.next().path("PutRequest").path("Item");
                String teamIdVal = currentNode.path(ATTRIBUTE_PK).path("S").asText();
                String skValue = currentNode.path(ATTRIBUTE_SK).path("S").asText();
                int runsVal = currentNode.path(RUNS).path("N").asInt();
                String opposingTeamIdVal = currentNode.path(OPPOSING_TEAM_ID).path("S").asText();
                int opposingTeamRunsVal = currentNode.path(OPPOSING_TEAM_RUNS).path("N").asInt();

                save(new Game(teamIdVal, skValue, runsVal, opposingTeamIdVal, opposingTeamRunsVal));
                LOG.info(dynamoConfiguration.getTableName() + " was successfully updated");
            }

        } catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    protected Map<String, AttributeValue> item(@NonNull Game game) {
        Map<String, AttributeValue> result = super.item(game);

        result.put(RUNS, AttributeValue.builder().n(String.valueOf(game.getRuns())).build());
        result.put(OPPOSING_TEAM_ID, AttributeValue.builder().s(game.getOpposingTeamId()).build());
        result.put(OPPOSING_TEAM_RUNS, AttributeValue.builder().n(String.valueOf(game.getOpposingTeamRuns())).build());

        return result;
    }


    @Override
    protected void save(Game entity) {
        super.save(entity);
    }
}
