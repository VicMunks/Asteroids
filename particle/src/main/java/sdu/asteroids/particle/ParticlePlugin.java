package sdu.asteroids.particle;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.PolygonShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.IPostEntityProcessorService;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ParticlePlugin implements IGamePluginService, IPostEntityProcessorService {

    private static final int COUNT_MIN = 8;
    private static final int COUNT_MAX = 12;
    private static final double SPEED_MIN = 100.0;
    private static final double SPEED_MAX = 200.0;
    private static final double LIFETIME_MIN = 0.4;
    private static final double LIFETIME_MAX = 0.6;
    private static final double RADIUS = 1.5;

    private final Random rng = new Random();
    private Set<String> wasActiveLastFrame = new HashSet<>();

    @Override
    public void start(GameData gameData, World world) {
        wasActiveLastFrame = new HashSet<>();
    }

    @Override
    public void stop(GameData gameData, World world) {
        world.getEntities(EntityType.PARTICLE).forEach(e -> e.setActive(false));
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;
        double dt = gameData.getDeltaTime();
        double w = gameData.getDisplayWidth();
        double h = gameData.getDisplayHeight();

        for (Entity p : world.getEntities(EntityType.PARTICLE)) {
            p.setX(((p.getX() + p.getDx() * dt) % w + w) % w);
            p.setY(((p.getY() + p.getDy() * dt) % h + h) % h);
            double remaining = p.getLifetime() - dt;
            if (remaining <= 0) {
                p.setActive(false);
            } else {
                p.setLifetime(remaining);
            }
        }

        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.PARTICLE) continue;
            if (wasActiveLastFrame.contains(entity.getId()) && !entity.isActive()) {
                EntityType type = entity.getType();
                if (type == EntityType.PLAYER || type == EntityType.ENEMY || type == EntityType.ASTEROID) {
                    spawnBurst(entity, world);
                }
            }
        }

        Set<String> nextFrame = new HashSet<>();
        for (Entity entity : world.getEntities()) {
            if (entity.isActive()) {
                nextFrame.add(entity.getId());
            }
        }
        wasActiveLastFrame = nextFrame;
    }

    private void spawnBurst(Entity source, World world) {
        double[] color = colorFor(source.getType());
        int count = COUNT_MIN + rng.nextInt(COUNT_MAX - COUNT_MIN + 1);
        for (int i = 0; i < count; i++) {
            double angle = rng.nextDouble() * 2 * Math.PI;
            double speed = SPEED_MIN + rng.nextDouble() * (SPEED_MAX - SPEED_MIN);
            Entity p = new Entity();
            p.setType(EntityType.PARTICLE);
            p.setX(source.getX());
            p.setY(source.getY());
            p.setDx(Math.cos(angle) * speed);
            p.setDy(Math.sin(angle) * speed);
            p.setRadius(RADIUS);
            p.setLifetime(LIFETIME_MIN + rng.nextDouble() * (LIFETIME_MAX - LIFETIME_MIN));
            p.setShape(new PolygonShape(new double[]{0, -2, 0, 2}, color));
            world.addEntity(p);
        }
    }

    private double[] colorFor(EntityType type) {
        return switch (type) {
            case PLAYER -> new double[]{0, 1, 1, 1};
            case ENEMY  -> new double[]{1, 0, 1, 1};
            default     -> new double[]{1, 1, 1, 1};
        };
    }
}
