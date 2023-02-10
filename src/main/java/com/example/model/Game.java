package com.example.model;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;

@Serdeable
public class Game extends BaseEntity {

    @NonNull
    private final int runs;

    @NonNull
    @NotBlank
    private final String opposingTeamId;

    @NonNull
    private final int opposingTeamRuns;

    public Game(@NonNull String teamId,
                @NonNull String sk,
                @NonNull int runs,
                @NonNull String opposingTeamId,
                @NonNull int opposingTeamRuns) {
        super(teamId, sk);
        this.runs = runs;
        this.opposingTeamId = opposingTeamId;
        this.opposingTeamRuns = opposingTeamRuns;
    }

    @NonNull
    public int getRuns() {
        return runs;
    }

    @NonNull
    public String getOpposingTeamId() {
        return opposingTeamId;
    }

    @NonNull
    public int getOpposingTeamRuns() {
        return opposingTeamRuns;
    }

}
