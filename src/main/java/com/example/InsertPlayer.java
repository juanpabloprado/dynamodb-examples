package com.example;

import com.example.model.Player;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static com.example.DynamoRepository.ATTRIBUTE_PK;
import static com.example.DynamoRepository.ATTRIBUTE_SK;

@Singleton
public class InsertPlayer extends ItemRepository<Player> {
    private static final Logger LOG = LoggerFactory.getLogger(InsertPlayer.class);
    public static final String PLAYER_NAME = "PlayerName";
    public static final String POSITION = "Position";
    private final DynamoDbClient client;
    private final ObjectMapper mapper;

    private final DynamoConfiguration dynamoConfiguration;


    public InsertPlayer(ObjectMapper mapper, DynamoDbClient client, DynamoConfiguration dynamoConfiguration) {
        this.mapper = mapper;
        this.client = client;
        this.dynamoConfiguration = dynamoConfiguration;
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
                putItem(new Player(teamIdVal, skValue, playerNameVal, positionVal));
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


    private void putItem(Player player) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .item(item(player))
                .build();

        try {
            client.putItem(request);

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", dynamoConfiguration.getTableName());
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
