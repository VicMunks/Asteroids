package sdu.asteroids.core;

import org.junit.jupiter.api.Test;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IGamePluginService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RemovabilityTest {

    @Test
    void test_gameRunsWithNoPlugins() {
        GameData gameData = new GameData();
        gameData.setGameState(GameState.RUNNING);
        gameData.setDeltaTime(0.016);
        World world = new World();

        Game game = new Game(List.of(), List.of(), List.of());
        assertDoesNotThrow(() -> game.runOneTickForTesting(gameData, world));
        assertEquals(GameState.RUNNING, gameData.getGameState());
    }

    @Test
    void test_gameRunsWithSubsetOfPlugins() {
        GameData gameData = new GameData();
        gameData.setGameState(GameState.RUNNING);
        gameData.setDeltaTime(0.016);
        World world = new World();

        IGamePluginService playerOnlyPlugin = new IGamePluginService() {
            public void start(GameData gd, World w) {}
            public void stop(GameData gd, World w) {}
        };

        Game game = new Game(List.of(playerOnlyPlugin), List.of(), List.of());
        assertDoesNotThrow(() -> game.runOneTickForTesting(gameData, world));
    }
}
