package sdu.asteroids.bullet;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.PolygonShape;
import sdu.asteroids.common.data.ShotConfig;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.BulletSPI;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletPlugin implements IGamePluginService, IEntityProcessorService, BulletSPI {

    private static final double SPEED = 400.0;
    private static final double RADIUS = 3.0;
    private static final double LIFETIME = 1.5;

    private World world;

    private static class PendingVolley {
        final ShotConfig config;
        final double x, y, rotation;
        final EntityType owner;
        double delay;

        PendingVolley(ShotConfig config, double x, double y, double rotation, EntityType owner, double delay) {
            this.config = config;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.owner = owner;
            this.delay = delay;
        }
    }

    private final List<PendingVolley> volleyQueue = new ArrayList<>();

    @Override
    public void start(GameData gameData, World world) {
        this.world = world;
    }

    @Override
    public void stop(GameData gameData, World world) {
        world.getEntities(EntityType.BULLET).forEach(e -> e.setActive(false));
        volleyQueue.clear();
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;
        double dt = gameData.getDeltaTime();

        Iterator<PendingVolley> it = volleyQueue.iterator();
        while (it.hasNext()) {
            PendingVolley pv = it.next();
            pv.delay -= dt;
            if (pv.delay <= 0) {
                spawnVolley(pv.config, pv.x, pv.y, pv.rotation, pv.owner);
                it.remove();
            }
        }

        double w = gameData.getDisplayWidth();
        double h = gameData.getDisplayHeight();
        for (Entity bullet : world.getEntities(EntityType.BULLET)) {
            bullet.setX(bullet.getX() + bullet.getDx() * dt);
            bullet.setY(bullet.getY() + bullet.getDy() * dt);
            bullet.setX(((bullet.getX() % w) + w) % w);
            bullet.setY(((bullet.getY() % h) + h) % h);
            double remaining = bullet.getLifetime() - dt;
            if (remaining <= 0) {
                bullet.setActive(false);
            } else {
                bullet.setLifetime(remaining);
            }
        }
    }

    @Override
    public void spawnBullet(double x, double y, double rotation, EntityType owner) {
        Entity bullet = new Entity();
        bullet.setType(EntityType.BULLET);
        bullet.setOwner(owner);
        bullet.setX(x);
        bullet.setY(y);
        bullet.setRotation(rotation);
        bullet.setDx(Math.sin(rotation) * SPEED);
        bullet.setDy(-Math.cos(rotation) * SPEED);
        bullet.setRadius(RADIUS);
        bullet.setLifetime(LIFETIME);
        bullet.setShape(new PolygonShape(
                new double[]{0, -5, 0, 5},
                new double[]{1, 1, 1, 1}
        ));
        world.addEntity(bullet);
    }

    @Override
    public void spawnShot(ShotConfig config, double x, double y, double rotation, EntityType owner) {
        spawnVolley(config, x, y, rotation, owner);
        for (int v = 1; v < config.volleys; v++) {
            volleyQueue.add(new PendingVolley(config, x, y, rotation, owner, v * config.volleyDelaySeconds));
        }
    }

    private void spawnVolley(ShotConfig config, double x, double y, double rotation, EntityType owner) {
        for (int i = 0; i < config.bulletCount; i++) {
            double angle = config.bulletCount == 1 ? rotation
                    : rotation - config.spreadAngle / 2.0 + i * config.spreadAngle / (config.bulletCount - 1);
            spawnOneBullet(x, y, angle, owner, config.pierceCount, config.explosionRadius);
        }
    }

    private void spawnOneBullet(double x, double y, double rotation, EntityType owner,
                                int pierceCount, double explosionRadius) {
        Entity bullet = new Entity();
        bullet.setType(EntityType.BULLET);
        bullet.setOwner(owner);
        bullet.setX(x);
        bullet.setY(y);
        bullet.setRotation(rotation);
        bullet.setDx(Math.sin(rotation) * SPEED);
        bullet.setDy(-Math.cos(rotation) * SPEED);
        bullet.setRadius(RADIUS);
        bullet.setLifetime(LIFETIME);
        bullet.setPierceRemaining(pierceCount);
        bullet.setExplosionRadius(explosionRadius);
        double[] color = explosionRadius > 0 ? new double[]{1, 0.5, 0, 1}
                : pierceCount > 0 ? new double[]{0, 1, 1, 1}
                : new double[]{1, 1, 1, 1};
        bullet.setShape(new PolygonShape(new double[]{0, -5, 0, 5}, color));
        world.addEntity(bullet);
    }
}
