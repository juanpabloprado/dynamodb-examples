package com.example.domain;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

import javax.validation.constraints.NotBlank;

@Serdeable
public abstract class BaseEntity implements Identified {
    @NonNull
    @NotBlank
    private final String teamId;

    @NonNull
    @NotBlank
    private final String SK;

    protected BaseEntity(@NonNull String teamId, @NonNull String sk) {
        this.teamId = teamId;
        SK = sk;
    }

    @Override
    @NonNull
    public String getId() {
        return teamId;
    }

    @NonNull
    public String getSK() {
        return SK;
    }
}
