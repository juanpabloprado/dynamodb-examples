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
public class Team extends GenericEntity {
    protected static final StaticTableSchema<Team> TEAM_TABLE_SCHEMA =
            StaticTableSchema.builder(Team.class)
                    .newItemSupplier(Team::new)
                    .addAttribute(String.class, a -> a.name("TeamName")
                            .getter(Team::getTeamName)
                            .setter(Team::setTeamName))
                    .extend(GENERIC_RECORD_SCHEMA)
                    .build();
    private GenericEntity genericEntity;
    @NotBlank
    private String teamName;

    public Team(@NonNull String teamId, @NonNull String sk, @NonNull String teamName) {
        super(teamId, sk);
        this.teamName = teamName;
    }

    public Team() {
    }

    @NonNull
    @DynamoDbAttribute("TeamName")
    public String getTeamName() {
        return teamName;
    }

    public GenericEntity getGenericEntity() {
        return genericEntity;
    }

    public void setGenericEntity(GenericEntity genericEntity) {
        this.genericEntity = genericEntity;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
