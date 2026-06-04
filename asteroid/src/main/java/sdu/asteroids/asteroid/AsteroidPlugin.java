package sdu.asteroids.asteroid;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.PolygonShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.AsteroidSpawnSPI;
import sdu.asteroids.common.services.AsteroidSplittingSPI;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AsteroidPlugin implements IGamePluginService, IEntityProcessorService, AsteroidSplittingSPI, AsteroidSpawnSPI {

    private static final double SPEED_MIN = 40.0;
    private static final double SPEED_MAX = 100.0;
    private static final double ROTATION_SPEED = Math.PI / 6.0;
    private static final int SHAPE_POINTS = 10;
    private static final double JITTER_MIN = 0.75;
    private static final double JITTER_MAX = 1.15;

    private final Random rng = new Random();
    private final List<Entity> spawned = new ArrayList<>();

    @Override
    public void start(GameData gameData, World world) {
    }

    @Override
    public void stop(GameData gameData, World world) {
        spawned.forEach(e -> e.setActive(false));
        spawned.clear();
    }

    @Override
    public void spawnAsteroid(World world, GameData gameData, double radius) {
        Entity a = buildAsteroid(gameData, radius);
        spawned.add(a);
        world.addEntity(a);
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;
        double dt = gameData.getDeltaTime();
        double w = gameData.getDisplayWidth();
        double h = gameData.getDisplayHeight();
        for (Entity a : world.getEntities(EntityType.ASTEROID)) {
            a.setX(((a.getX() + a.getDx() * dt) % w + w) % w);
            a.setY(((a.getY() + a.getDy() * dt) % h + h) % h);
            a.setRotation(a.getRotation() + a.getRotationSpeed() * dt);
        }
    }

    @Override
    public void split(Entity asteroid, World world) {
        asteroid.setActive(false);
        double r = asteroid.getRadius() / 2.0;
        for (int i = 0; i < 2; i++) {
            Entity child = new Entity();
            child.setType(EntityType.ASTEROID);
            child.setX(asteroid.getX());
            child.setY(asteroid.getY());
            child.setRadius(r);
            double angle = rng.nextDouble() * 2 * Math.PI;
            double speed = SPEED_MIN + rng.nextDouble() * (SPEED_MAX - SPEED_MIN);
            child.setDx(Math.cos(angle) * speed);
            child.setDy(Math.sin(angle) * speed);
            child.setRotationSpeed((rng.nextDouble() * 2 - 1) * ROTATION_SPEED);
            child.setShape(buildShape(r));
            world.addEntity(child);
        }
    }

    @Override
    public void destroy(Entity asteroid, World world) {
        asteroid.setActive(false);
    }

    private Entity buildAsteroid(GameData gameData, double radius) {
        double w = gameData.getDisplayWidth();
        double h = gameData.getDisplayHeight();
        double x, y;
        if (rng.nextBoolean()) {
            x = rng.nextDouble() * w;
            y = rng.nextBoolean() ? 0 : h;
        } else {
            x = rng.nextBoolean() ? 0 : w;
            y = rng.nextDouble() * h;
        }
        double angle = rng.nextDouble() * 2 * Math.PI;
        double speed = SPEED_MIN + rng.nextDouble() * (SPEED_MAX - SPEED_MIN);
        Entity a = new Entity();
        a.setType(EntityType.ASTEROID);
        a.setX(x);
        a.setY(y);
        a.setRadius(radius);
        a.setDx(Math.cos(angle) * speed);
        a.setDy(Math.sin(angle) * speed);
        a.setRotationSpeed((rng.nextDouble() * 2 - 1) * ROTATION_SPEED);
        a.setShape(buildShape(radius));
        return a;
    }

    private PolygonShape buildShape(double radius) {
        double[] points = new double[SHAPE_POINTS * 2];
        for (int i = 0; i < SHAPE_POINTS; i++) {
            double angle = 2 * Math.PI * i / SHAPE_POINTS;
            double r = radius * (JITTER_MIN + rng.nextDouble() * (JITTER_MAX - JITTER_MIN));
            points[i * 2]     = Math.cos(angle) * r;
            points[i * 2 + 1] = Math.sin(angle) * r;
        }
        return new PolygonShape(points, new double[]{1, 1, 1, 1});
    }
}
