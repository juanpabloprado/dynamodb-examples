package com.example.domain;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import javax.validation.constraints.NotBlank;

@Serdeable
@DynamoDbBean
public abstract class GenericEntity implements Identified {
    @NotBlank
    private String teamId;

    @NotBlank
    private String sk;

    protected GenericEntity(@NonNull String teamId, @NonNull String sk) {
        this.teamId = teamId;
        this.sk = sk;
    }

    public GenericEntity() {
    }

    @Override
    @NonNull
    @DynamoDbPartitionKey
    @DynamoDbAttribute("TeamID")
    public String getId() {
        return teamId;
    }

    @NonNull
    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }
}
