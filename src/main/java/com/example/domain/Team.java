package com.example.domain;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;

@Serdeable
public class Team extends BaseEntity {
    @NonNull
    @NotBlank
    private final String teamName;

    public Team(@NonNull String teamId, @NonNull String sk, @NonNull String teamName) {
        super(teamId, sk);
        this.teamName = teamName;
    }

    @NonNull
    public String getTeamName() {
        return teamName;
    }
}
