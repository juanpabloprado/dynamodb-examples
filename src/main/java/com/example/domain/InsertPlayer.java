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

import static com.example.domain.DynamoRepository.ATTRIBUTE_PK;
import static com.example.domain.DynamoRepository.ATTRIBUTE_SK;

@Singleton
public class InsertPlayer extends ItemRepository<Player> {
    private static final Logger LOG = LoggerFactory.getLogger(InsertPlayer.class);
    public static final String PLAYER_NAME = "PlayerName";
    public static final String POSITION = "Position";
    private final ObjectMapper mapper;

    public InsertPlayer(ObjectMapper mapper, DynamoDbClient client, DynamoConfiguration dynamoConfiguration) {
        super(client, dynamoConfiguration);
        this.mapper = mapper;
    }

    public void createItems() {
        var players = getClass().getClassLoader().getResourceAsStream("players.json");
        putPlayerItems(players);
    }

    private void putPlayerItems(InputStream stream) {
        try {
            JsonNode node = mapper.readTree(stream);
            Iterator<JsonNode> iter = node.path("BaseballStats").iterator();
            ObjectNode currentNode;
            while (iter.hasNext()) {
                currentNode = (ObjectNode) iter.next().path("PutRequest").path("Item");
                String teamIdVal = currentNode.path(ATTRIBUTE_PK).path("S").asText();
                String skValue = currentNode.path(ATTRIBUTE_SK).path("S").asText();
                String playerNameVal = currentNode.path(PLAYER_NAME).path("S").asText();
                String positionVal = currentNode.path(POSITION).path("S").asText();
                save(new Player(teamIdVal, skValue, playerNameVal, positionVal));
                LOG.info(dynamoConfiguration.getTableName() + " was successfully updated");
            }

        } catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    protected Map<String, AttributeValue> item(@NonNull Player player) {
        Map<String, AttributeValue> result = super.item(player);

        result.put(PLAYER_NAME, AttributeValue.builder().s(player.getPlayerName()).build());
        result.put(POSITION, AttributeValue.builder().s(player.getPosition()).build());

        return result;
    }

    @Override
    protected void save(Player entity) {
        super.save(entity);
    }
}
