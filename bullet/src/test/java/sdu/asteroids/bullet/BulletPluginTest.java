package sdu.asteroids.bullet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;

import static org.junit.jupiter.api.Assertions.*;

class BulletPluginTest {

    private BulletPlugin plugin;
    private GameData gameData;
    private World world;

    @BeforeEach
    void setUp() {
        plugin = new BulletPlugin();
        gameData = new GameData();
        gameData.setDisplayWidth(1280);
        gameData.setDisplayHeight(800);
        gameData.setGameState(GameState.RUNNING);
        world = new World();
        plugin.start(gameData, world);
    }

    @Test
    void test_bulletLifetimeExpires() {
        plugin.spawnBullet(640, 400, 0, EntityType.PLAYER);
        gameData.setDeltaTime(2.0);
        plugin.process(gameData, world);
        Entity bullet = world.getEntities(EntityType.BULLET).get(0);
        assertFalse(bullet.isActive());
    }

    @Test
    void test_bulletTravelsInFiredDirection() {
        plugin.spawnBullet(640, 400, Math.PI / 2, EntityType.PLAYER);
        gameData.setDeltaTime(0.1);
        plugin.process(gameData, world);
        Entity bullet = world.getEntities(EntityType.BULLET).get(0);
        assertTrue(bullet.getX() > 640);
    }
}
