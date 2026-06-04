package sdu.asteroids.collision;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.AsteroidSplittingSPI;
import sdu.asteroids.common.services.ScoreSPI;
import sdu.asteroids.common.util.ServiceLocator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CollisionPluginTest {

    private CollisionPlugin plugin;
    private GameData gameData;
    private World world;
    private AsteroidSplittingSPI mockSplitter;
    private ScoreSPI mockScorer;

    @BeforeEach
    void setUp() {
        plugin = new CollisionPlugin();
        gameData = new GameData();
        gameData.setGameState(GameState.RUNNING);
        gameData.setPlayerHealth(3);
        gameData.setPlayerMaxHealth(3);
        gameData.setPlayerDamageMultiplier(1.0);
        world = new World();
        mockSplitter = mock(AsteroidSplittingSPI.class);
        mockScorer = mock(ScoreSPI.class);
        ServiceLocator.registerForTesting(AsteroidSplittingSPI.class, mockSplitter);
        ServiceLocator.registerForTesting(ScoreSPI.class, mockScorer);
    }

    @Test
    void test_playerBulletDestroysAsteroid() {
        Entity bullet = new Entity();
        bullet.setType(EntityType.BULLET);
        bullet.setOwner(EntityType.PLAYER);
        bullet.setX(100);
        bullet.setY(100);
        bullet.setRadius(3);
        world.addEntity(bullet);

        Entity asteroid = new Entity();
        asteroid.setType(EntityType.ASTEROID);
        asteroid.setX(101);
        asteroid.setY(101);
        asteroid.setRadius(64);
        world.addEntity(asteroid);

        plugin.process(gameData, world);

        assertFalse(bullet.isActive());
        verify(mockSplitter).split(asteroid, world);
    }

    @Test
    void test_enemyBulletKillsPlayer() {
        Entity bullet = new Entity();
        bullet.setType(EntityType.BULLET);
        bullet.setOwner(EntityType.ENEMY);
        bullet.setX(100);
        bullet.setY(100);
        bullet.setRadius(3);
        world.addEntity(bullet);

        Entity player = new Entity();
        player.setType(EntityType.PLAYER);
        player.setX(101);
        player.setY(101);
        player.setRadius(12);
        world.addEntity(player);

        gameData.setPlayerHealth(1);
        plugin.process(gameData, world);

        assertFalse(player.isActive());
        assertEquals(GameState.GAME_OVER, gameData.getGameState());
    }

    @Test
    void test_invinciblePlayerNotHurtByEnemyBullet() {
        Entity bullet = new Entity();
        bullet.setType(EntityType.BULLET);
        bullet.setOwner(EntityType.ENEMY);
        bullet.setX(100); bullet.setY(100); bullet.setRadius(3);
        world.addEntity(bullet);

        Entity player = new Entity();
        player.setType(EntityType.PLAYER);
        player.setX(101); player.setY(101); player.setRadius(12);
        world.addEntity(player);

        gameData.setPlayerInvincible(true);
        plugin.process(gameData, world);

        assertEquals(3, gameData.getPlayerHealth());
        assertNotEquals(GameState.GAME_OVER, gameData.getGameState());
    }

    @Test
    void test_noFriendlyFire() {
        Entity bullet = new Entity();
        bullet.setType(EntityType.BULLET);
        bullet.setOwner(EntityType.PLAYER);
        bullet.setX(100);
        bullet.setY(100);
        bullet.setRadius(3);
        world.addEntity(bullet);

        Entity player = new Entity();
        player.setType(EntityType.PLAYER);
        player.setX(101);
        player.setY(101);
        player.setRadius(12);
        world.addEntity(player);

        plugin.process(gameData, world);

        assertTrue(player.isActive());
    }
}
