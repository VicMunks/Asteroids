package sdu.asteroids.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;

import static org.junit.jupiter.api.Assertions.*;

class PlayerPluginTest {

    private PlayerPlugin plugin;
    private GameData gameData;
    private World world;

    @BeforeEach
    void setUp() {
        plugin = new PlayerPlugin();
        gameData = new GameData();
        gameData.setDisplayWidth(1280);
        gameData.setDisplayHeight(800);
        gameData.setGameState(GameState.RUNNING);
        gameData.setDeltaTime(0.1);
        world = new World();
    }

    @Test
    void test_playerSpawnsOnStart() {
        plugin.start(gameData, world);
        assertEquals(1, world.getEntities(EntityType.PLAYER).size());
    }

    @Test
    void test_thrustIncreasesVelocity() {
        plugin.start(gameData, world);
        gameData.addKey("UP");
        plugin.process(gameData, world);
        Entity player = world.getEntities(EntityType.PLAYER).get(0);
        assertTrue(player.getDx() != 0 || player.getDy() != 0);
    }

    @Test
    void test_rotationChangesOnInput() {
        plugin.start(gameData, world);
        gameData.addKey("LEFT");
        plugin.process(gameData, world);
        Entity player = world.getEntities(EntityType.PLAYER).get(0);
        assertNotEquals(0.0, player.getRotation());
    }

    @Test
    void test_noMovementWhenNotRunning() {
        plugin.start(gameData, world);
        Entity player = world.getEntities(EntityType.PLAYER).get(0);
        double x = player.getX();
        double y = player.getY();
        gameData.addKey("UP");
        gameData.setGameState(GameState.STARTING);
        plugin.process(gameData, world);
        assertEquals(x, player.getX());
        assertEquals(y, player.getY());
    }
}
