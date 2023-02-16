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
public class Game extends GenericEntity {

    protected static final StaticTableSchema<Game> GAME_TABLE_SCHEMA =
            StaticTableSchema.builder(Game.class)
                    .newItemSupplier(Game::new)
                    .addAttribute(Integer.class, a -> a.name("Runs")
                            .getter(Game::getRuns)
                            .setter(Game::setRuns))
                    .addAttribute(String.class, a -> a.name("OpposingTeamID")
                            .getter(Game::getOpposingTeamId)
                            .setter(Game::setOpposingTeamId))
                    .addAttribute(Integer.class, a -> a.name("OpposingTeamRuns")
                            .getter(Game::getOpposingTeamRuns)
                            .setter(Game::setOpposingTeamRuns))
                    .extend(GENERIC_RECORD_SCHEMA)
                    .build();
    private GenericEntity genericEntity;
    private Integer runs;

    @NotBlank
    private String opposingTeamId;

    private Integer opposingTeamRuns;

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

    public Game() {
    }

    @NonNull
    @DynamoDbAttribute("Runs")
    public int getRuns() {
        return runs;
    }

    @NonNull
    @DynamoDbAttribute("OpposingTeamID")
    public String getOpposingTeamId() {
        return opposingTeamId;
    }

    @NonNull
    @DynamoDbAttribute("OpposingTeamRuns")
    public int getOpposingTeamRuns() {
        return opposingTeamRuns;
    }

    @Override
    public String toString() {
        return "Game{" +
                "teamId='" + getId() + '\'' +
                ", sk='" + getSk() + '\'' +
                ", runs=" + runs +
                ", opposingTeamId='" + opposingTeamId + '\'' +
                ", opposingTeamRuns=" + opposingTeamRuns +
                '}';
    }
    public void setRuns(int runs) {
        this.runs = runs;
    }

    public void setOpposingTeamId(String opposingTeamId) {
        this.opposingTeamId = opposingTeamId;
    }

    public void setOpposingTeamRuns(int opposingTeamRuns) {
        this.opposingTeamRuns = opposingTeamRuns;
    }

}
