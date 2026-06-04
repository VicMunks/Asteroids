package sdu.asteroids.asteroid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsteroidPluginTest {

    private AsteroidPlugin plugin;
    private GameData gameData;
    private World world;

    @BeforeEach
    void setUp() {
        plugin = new AsteroidPlugin();
        gameData = new GameData();
        gameData.setDisplayWidth(1280);
        gameData.setDisplayHeight(800);
        world = new World();
        plugin.start(gameData, world);
    }

    @Test
    void test_startSpawnsNothing() {
        assertTrue(world.getEntities(EntityType.ASTEROID).isEmpty());
    }

    @Test
    void test_spawnAsteroidAddsEntityToWorld() {
        plugin.spawnAsteroid(world, gameData, 64.0);
        assertEquals(1, world.getEntities(EntityType.ASTEROID).size());
    }

    @Test
    void test_splitProducesTwoSmallerAsteroids() {
        plugin.spawnAsteroid(world, gameData, 64.0);
        Entity asteroid = world.getEntities(EntityType.ASTEROID).get(0);
        plugin.split(asteroid, world);
        assertFalse(asteroid.isActive());
        List<Entity> children = world.getEntities(EntityType.ASTEROID).stream()
                .filter(Entity::isActive)
                .filter(e -> e.getRadius() == asteroid.getRadius() / 2.0)
                .toList();
        assertEquals(2, children.size());
    }

    @Test
    void test_destroyRemovesAsteroid() {
        Entity asteroid = new Entity();
        asteroid.setType(EntityType.ASTEROID);
        asteroid.setRadius(16.0);
        world.addEntity(asteroid);
        plugin.destroy(asteroid, world);
        assertFalse(asteroid.isActive());
    }
}
