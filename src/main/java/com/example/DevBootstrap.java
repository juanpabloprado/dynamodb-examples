package com.example;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;

@Requires(env = Environment.DEVELOPMENT)
@Singleton
public class DevBootstrap implements ApplicationEventListener<StartupEvent> {
    private final DynamoRepository dynamoRepository;
    private final InsertGame insertGame;
    private final InsertPlayer insertPlayer;
    private final InsertTeam insertTeam;

    public DevBootstrap(DynamoRepository dynamoRepository, InsertGame insertGame, InsertPlayer insertPlayer, InsertTeam insertTeam) {
        this.dynamoRepository = dynamoRepository;
        this.insertGame = insertGame;
        this.insertPlayer = insertPlayer;
        this.insertTeam = insertTeam;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        if (!dynamoRepository.existsTable()) {
            dynamoRepository.createTable();
        }
        insertGame.createItems();
        insertPlayer.createItems();
        insertTeam.createItems();
    }
}