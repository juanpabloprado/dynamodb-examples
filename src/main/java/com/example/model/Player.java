package com.example.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;

@Serdeable
public class Player extends BaseEntity {
    @NonNull
    @NotBlank
    private final String playerName;

    @NonNull
    @NotBlank
    private final String position;

    public Player(@NonNull String teamId,
                  @NonNull String sk,
                  @NonNull String playerName,
                  @NonNull String position) {
        super(teamId, sk);
        this.playerName = playerName;
        this.position = position;
    }

    @NonNull
    public String getPlayerName() {
        return playerName;
    }

    @NonNull
    public String getPosition() {
        return position;
    }
}
