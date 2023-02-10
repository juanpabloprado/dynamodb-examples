package com.example;

import com.example.model.Team;
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
public class InsertTeam extends ItemRepository<Team> {
    private static final Logger LOG = LoggerFactory.getLogger(InsertTeam.class);
    public static final String TEAM_NAME = "TeamName";
    private final DynamoDbClient client;
    private final ObjectMapper mapper;

    private final DynamoConfiguration dynamoConfiguration;


    public InsertTeam(ObjectMapper mapper, DynamoDbClient client, DynamoConfiguration dynamoConfiguration) {
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
                String teamNameVal = currentNode.path(TEAM_NAME).path("S").asText();
                putItem(new Team(teamIdVal, skValue, teamNameVal));
                LOG.info(dynamoConfiguration.getTableName() + " was successfully updated");
            }

        } catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());
        }
    }

    @Override
    protected Map<String, AttributeValue> item(@NonNull Team team) {
        Map<String, AttributeValue> result = super.item(team);

        result.put(TEAM_NAME, AttributeValue.builder().s(team.getTeamName()).build());

        return result;
    }


    private void putItem(Team team) {
        PutItemRequest request = PutItemRequest.builder()
                .tableName(dynamoConfiguration.getTableName())
                .item(item(team))
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
