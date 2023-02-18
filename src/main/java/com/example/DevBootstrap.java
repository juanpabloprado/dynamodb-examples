package com.example;

import com.example.domain.DefaultGameRepository;
import com.example.domain.DynamoRepository;
import com.example.domain.EnhancedGameRepository;
import com.example.domain.Game;
import com.example.domain.InsertGame;
import com.example.domain.InsertPlayer;
import com.example.domain.InsertTeam;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.context.event.StartupEvent;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Requires(env = Environment.DEVELOPMENT)
@Singleton
public class DevBootstrap implements ApplicationEventListener<StartupEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(DevBootstrap.class);
    private final DynamoRepository dynamoRepository;
    private final InsertGame insertGame;
    private final InsertPlayer insertPlayer;
    private final InsertTeam insertTeam;

    private final DefaultGameRepository gameRepository;
    private final EnhancedGameRepository enhancedGameRepository;

    public DevBootstrap(DynamoRepository dynamoRepository, InsertGame insertGame, InsertPlayer insertPlayer, InsertTeam insertTeam, DefaultGameRepository gameRepository, EnhancedGameRepository enhancedGameRepository) {
        this.dynamoRepository = dynamoRepository;
        this.insertGame = insertGame;
        this.insertPlayer = insertPlayer;
        this.insertTeam = insertTeam;
        this.gameRepository = gameRepository;
        this.enhancedGameRepository = enhancedGameRepository;
    }

    @Override
    public void onApplicationEvent(StartupEvent event) {
        if (!dynamoRepository.existsTable()) {
            dynamoRepository.createTable();
            insertGame.createItems();
            insertPlayer.createItems();
            insertTeam.createItems();
        }
        List<Game> game = gameRepository.findAllByTeamIdAndDate("LAA", "20190420");
        LOG.info(game.toString());
        List<Game> games = gameRepository.findAllByTeamIdAndBetweenDate("LAA", "20190401", "20190501");
        LOG.info(games.toString());
        List<Game> games2 = gameRepository.findAllByTeamIdAndBetweenDate("LAA", "20190401", "20190501", "SEA");
        LOG.info(games2.toString());


        Iterator<Game> results = enhancedGameRepository.findAll();
        while (results.hasNext()) {
            Game rec = results.next();
            LOG.info("The record id is " + rec.getId());
            LOG.info("The game is " + rec);
        }

        Game laa = enhancedGameRepository.findByTeamIdAndDate("LAA", "20190420");
        LOG.info(laa + "");

        Optional<Game> deletedGame = enhancedGameRepository.deleteByTeamIdAndDate("LAA", "20190420");
        LOG.info(deletedGame + "");

        Iterator<Page<Game>> laaGames = enhancedGameRepository.findAllByTeamId("LAA");
        laaGames.forEachRemaining(gamePage -> LOG.info(gamePage + "") );
        deletedGame.ifPresent(enhancedGameRepository::save);
    }
}