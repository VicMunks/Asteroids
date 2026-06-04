package sdu.asteroids.enemy.common;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.FilledPolygonShape;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.IShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.BulletSPI;
import sdu.asteroids.common.services.EnemySpawnSPI;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.util.ServiceLocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class AbstractEnemyPlugin implements IGamePluginService, IEntityProcessorService, EnemySpawnSPI {

    protected final Random rng = new Random();
    protected final List<Entity> spawned = new ArrayList<>();
    protected final Map<String, List<Entity>> pipMap = new HashMap<>();

    protected Entity createEnemyAtEdge(GameData gameData, double radius, IShape shape) {
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
        Entity enemy = new Entity();
        enemy.setType(EntityType.ENEMY);
        enemy.setX(x);
        enemy.setY(y);
        enemy.setRadius(radius);
        enemy.setShape(shape);
        spawned.add(enemy);
        return enemy;
    }

    protected void spawnPips(Entity enemy, World world) {
        int maxHealth = enemy.getHealth();
        List<Entity> pips = new ArrayList<>();
        for (int i = 0; i < maxHealth; i++) {
            Entity pip = new Entity();
            pip.setType(EntityType.HUD);
            pip.setShape(new FilledPolygonShape(
                    new double[]{-3, -3, 3, -3, 3, 3, -3, 3},
                    new double[]{1, 0, 0, 1}
            ));
            world.addEntity(pip);
            pips.add(pip);
        }
        pipMap.put(enemy.getId(), pips);
    }

    protected void updatePips(Entity enemy) {
        List<Entity> pips = pipMap.get(enemy.getId());
        if (pips == null) return;
        int currentHealth = enemy.getHealth();
        for (int i = 0; i < pips.size(); i++) {
            Entity pip = pips.get(i);
            pip.setX(enemy.getX() - ((pips.size() - 1) * 8.0 / 2.0) + i * 8.0);
            pip.setY(enemy.getY() - 22);
            double[] c = pip.getShape().getColorRGBA();
            if (i < currentHealth) {
                c[0] = 1; c[1] = 0; c[2] = 0; c[3] = 1;
            } else {
                c[0] = 0.3; c[1] = 0.3; c[2] = 0.3; c[3] = 0.7;
            }
        }
    }

    protected void removePipsFor(Entity enemy) {
        List<Entity> pips = pipMap.remove(enemy.getId());
        if (pips != null) pips.forEach(p -> p.setActive(false));
    }

    protected void removeSpawned() {
        spawned.forEach(e -> e.setActive(false));
        spawned.clear();
        for (List<Entity> pips : pipMap.values()) {
            pips.forEach(p -> p.setActive(false));
        }
        pipMap.clear();
    }

    protected double angleDiff(double target, double current) {
        double d = (target - current) % (2 * Math.PI);
        if (d > Math.PI) d -= 2 * Math.PI;
        if (d < -Math.PI) d += 2 * Math.PI;
        return d;
    }

    protected double wrapCoord(double value, double max) {
        return ((value % max) + max) % max;
    }

    protected void fireBulletAt(double x, double y, double rotation, double offset) {
        double bx = x + Math.sin(rotation) * offset;
        double by = y - Math.cos(rotation) * offset;
        BulletSPI spi = ServiceLocator.getService(BulletSPI.class);
        if (spi != null) {
            spi.spawnBullet(bx, by, rotation, EntityType.ENEMY);
        }
    }

    @Override
    public void start(GameData gameData, World world) {
    }

    @Override
    public void stop(GameData gameData, World world) {
        removeSpawned();
    }

    public abstract String getEnemyType();

    public abstract void spawnEnemy(World world, GameData gameData);

    public abstract void process(GameData gameData, World world);
}
