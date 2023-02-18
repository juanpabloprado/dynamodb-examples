package com.example.domain;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import javax.validation.constraints.NotBlank;

import static com.example.domain.DynamoRepository.GENERIC_RECORD_SCHEMA;

@Serdeable
@DynamoDbBean
public class Player extends GenericEntity {
    protected static final StaticTableSchema<Player> PLAYER_TABLE_SCHEMA =
            StaticTableSchema.builder(Player.class)
                    .newItemSupplier(Player::new)
                    .addAttribute(String.class, a -> a.name("PlayerName")
                            .getter(Player::getPlayerName)
                            .setter(Player::setPlayerName))
                    .addAttribute(String.class, a -> a.name("Position")
                            .getter(Player::getPosition)
                            .setter(Player::setPosition))
                    .extend(GENERIC_RECORD_SCHEMA)
                    .build();
    @NotBlank
    private String playerName;

    @NotBlank
    private String position;

    public Player(@NonNull String teamId,
                  @NonNull String sk,
                  @NonNull String playerName,
                  @NonNull String position) {
        super(teamId, sk);
        this.playerName = playerName;
        this.position = position;
    }

    public Player() {
    }

    @NonNull
    @DynamoDbAttribute("PlayerName")
    public String getPlayerName() {
        return playerName;
    }

    @NonNull
    @DynamoDbAttribute("Position")
    public String getPosition() {
        return position;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
