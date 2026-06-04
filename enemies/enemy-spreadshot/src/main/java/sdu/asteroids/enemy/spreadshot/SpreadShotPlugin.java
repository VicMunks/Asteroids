package sdu.asteroids.enemy.spreadshot;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.PolygonShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.BulletSPI;
import sdu.asteroids.common.util.ServiceLocator;
import sdu.asteroids.enemy.common.AbstractEnemyPlugin;

import java.util.HashMap;
import java.util.Map;

public class SpreadShotPlugin extends AbstractEnemyPlugin {

    private static final double MOVEMENT_SPEED = 55.0;
    private static final double TURN_SPEED = Math.PI / 2.0;
    private static final double FIRE_INTERVAL = 3.0;
    private static final double DIR_CHANGE_INTERVAL = 3.0;
    private static final int SHOT_COUNT = 8;
    private static final double RADIUS = 14.0;
    private static final double BULLET_SPAWN_OFFSET = 18.0;

    private final Map<String, SpreadState> states = new HashMap<>();

    private static class SpreadState {
        double shootCooldown;
        double dirChangeCooldown;
        double targetHeading;
    }

    @Override
    public String getEnemyType() {
        return "SPREADSHOT";
    }

    @Override
    public void spawnEnemy(World world, GameData gameData) {
        Entity enemy = createEnemyAtEdge(gameData, RADIUS, new PolygonShape(
                new double[]{0, -14, 14, 0, 0, 14, -14, 0},
                new double[]{0, 1, 1, 1}
        ));
        SpreadState s = new SpreadState();
        s.targetHeading = rng.nextDouble() * 2 * Math.PI;
        s.shootCooldown = FIRE_INTERVAL;
        s.dirChangeCooldown = DIR_CHANGE_INTERVAL;
        states.put(enemy.getId(), s);
        enemy.setRotation(s.targetHeading);
        enemy.setHealth(2 + gameData.getCurrentWave() / 3);
        enemy.setDamage(1);
        world.addEntity(enemy);
        spawnPips(enemy, world);
    }

    @Override
    public void stop(GameData gameData, World world) {
        removeSpawned();
        states.clear();
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;
        double dt = gameData.getDeltaTime();
        double w = gameData.getDisplayWidth();
        double h = gameData.getDisplayHeight();

        for (Entity enemy : spawned) {
            if (!enemy.isActive()) {
                removePipsFor(enemy);
                continue;
            }
            SpreadState s = states.get(enemy.getId());
            if (s == null) continue;

            s.dirChangeCooldown -= dt;
            if (s.dirChangeCooldown <= 0) {
                s.targetHeading = rng.nextDouble() * 2 * Math.PI;
                s.dirChangeCooldown = DIR_CHANGE_INTERVAL;
            }

            s.shootCooldown -= dt;

            double rot = enemy.getRotation();
            double diff = angleDiff(s.targetHeading, rot);
            double maxTurn = TURN_SPEED * dt;
            if (Math.abs(diff) <= maxTurn) {
                rot = s.targetHeading;
            } else {
                rot += Math.signum(diff) * maxTurn;
            }
            enemy.setRotation(rot);

            enemy.setDx(Math.sin(rot) * MOVEMENT_SPEED);
            enemy.setDy(-Math.cos(rot) * MOVEMENT_SPEED);
            enemy.setX(wrapCoord(enemy.getX() + enemy.getDx() * dt, w));
            enemy.setY(wrapCoord(enemy.getY() + enemy.getDy() * dt, h));

            if (s.shootCooldown <= 0) {
                BulletSPI bulletSPI = ServiceLocator.getService(BulletSPI.class);
                if (bulletSPI != null) {
                    for (int i = 0; i < SHOT_COUNT; i++) {
                        double angle = i * (2 * Math.PI / SHOT_COUNT);
                        double bx = enemy.getX() + Math.sin(angle) * BULLET_SPAWN_OFFSET;
                        double by = enemy.getY() - Math.cos(angle) * BULLET_SPAWN_OFFSET;
                        bulletSPI.spawnBullet(bx, by, angle, EntityType.ENEMY);
                    }
                }
                s.shootCooldown = FIRE_INTERVAL;
            }
            updatePips(enemy);
        }
    }
}
