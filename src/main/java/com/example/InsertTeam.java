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

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import static com.example.DynamoRepository.ATTRIBUTE_PK;
import static com.example.DynamoRepository.ATTRIBUTE_SK;

@Singleton
public class InsertTeam extends ItemRepository<Team> {
    private static final Logger LOG = LoggerFactory.getLogger(InsertTeam.class);
    public static final String TEAM_NAME = "TeamName";
    private final ObjectMapper mapper;

    public InsertTeam(ObjectMapper mapper, DynamoDbClient client, DynamoConfiguration dynamoConfiguration) {
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
                String teamNameVal = currentNode.path(TEAM_NAME).path("S").asText();
                save(new Team(teamIdVal, skValue, teamNameVal));
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

    @Override
    protected void save(Team entity) {
        super.save(entity);
    }
}
