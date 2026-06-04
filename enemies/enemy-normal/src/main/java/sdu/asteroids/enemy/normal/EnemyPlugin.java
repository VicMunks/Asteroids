package sdu.asteroids.enemy.normal;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.PolygonShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.enemy.common.AbstractEnemyPlugin;

import java.util.HashMap;
import java.util.Map;

public class EnemyPlugin extends AbstractEnemyPlugin {

    private static final double TURN_SPEED = Math.PI / 2.0;
    private static final double MOVEMENT_SPEED = 60.0;
    private static final double FIRE_INTERVAL = 1.0;
    private static final double RADIUS = 12.0;
    private static final double BULLET_SPAWN_OFFSET = 18.0;
    private static final double DIR_CHANGE_MIN = 2.5;
    private static final double DIR_CHANGE_MAX = 3.5;

    private final Map<String, NormalState> states = new HashMap<>();

    private static class NormalState {
        double shootCooldown;
        double dirChangeCooldown;
        double targetHeading;
    }

    @Override
    public String getEnemyType() {
        return "NORMAL";
    }

    @Override
    public void spawnEnemy(World world, GameData gameData) {
        Entity enemy = createEnemyAtEdge(gameData, RADIUS, new PolygonShape(
                new double[]{0, -12, -10, 10, 10, 10},
                new double[]{1, 0, 1, 1}
        ));
        NormalState s = new NormalState();
        s.targetHeading = rng.nextDouble() * 2 * Math.PI;
        s.shootCooldown = FIRE_INTERVAL;
        s.dirChangeCooldown = DIR_CHANGE_MIN + rng.nextDouble() * (DIR_CHANGE_MAX - DIR_CHANGE_MIN);
        states.put(enemy.getId(), s);
        enemy.setRotation(s.targetHeading);
        enemy.setHealth(1 + gameData.getCurrentWave() / 4);
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
            NormalState s = states.get(enemy.getId());
            if (s == null) continue;

            s.dirChangeCooldown -= dt;
            if (s.dirChangeCooldown <= 0) {
                s.targetHeading = rng.nextDouble() * 2 * Math.PI;
                s.dirChangeCooldown = DIR_CHANGE_MIN + rng.nextDouble() * (DIR_CHANGE_MAX - DIR_CHANGE_MIN);
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
                fireBulletAt(enemy.getX(), enemy.getY(), rot, BULLET_SPAWN_OFFSET);
                s.shootCooldown = FIRE_INTERVAL;
            }
            updatePips(enemy);
        }
    }
}
