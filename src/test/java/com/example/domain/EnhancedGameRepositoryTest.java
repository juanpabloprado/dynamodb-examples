package com.example.domain;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@MicronautTest
class EnhancedGameRepositoryTest {

    @Inject
    EnhancedGameRepository gameRepository;

    @Test
    @DisplayName("A DynamoDB test container is required to run this test")
    void save() {
        Game game = new Game("GAMES_LAA", "20230420", 12, "SEA", 7);
        gameRepository.save(game);
        Iterator<Game> gameIterator = gameRepository.findAll();
        List<Game> games = List.of(gameIterator.next());
        assertEquals("GAMES_LAA", games.get(0).getId());
        assertEquals("20230420", games.get(0).getSk());
        assertFalse(gameIterator.hasNext());
    }

}