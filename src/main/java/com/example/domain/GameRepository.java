package com.example.domain;

import io.micronaut.core.annotation.NonNull;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

public interface GameRepository {
    @NonNull
    List<Game> findAllByTeamIdAndDate(@NonNull String teamId, @NonNull String date);
    @NonNull
    List<Game> findAllByTeamIdAndBetweenDate(@NonNull String teamId, @NonNull String startDate,
                                             @NonNull String endDate);
    @NonNull
    List<Game> findAllByTeamIdAndBetweenDate(@NonNull String teamId, @NonNull String startDate,
                                             @NonNull String endDate, @NonNull String opposingTeamId);
}