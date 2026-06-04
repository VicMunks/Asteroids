package sdu.asteroids.enemy.chaser;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.PolygonShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.enemy.common.AbstractEnemyPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChaserPlugin extends AbstractEnemyPlugin {

    private static final double MOVEMENT_SPEED = 70.0;
    private static final double TURN_SPEED = Math.PI / 2.0;
    private static final double FIRE_INTERVAL = 1.0;
    private static final double DIR_CHANGE_INTERVAL = 3.0;
    private static final double CHASE_RANGE = 250.0;
    private static final double RADIUS = 12.0;
    private static final double BULLET_SPAWN_OFFSET = 18.0;

    private final Map<String, ChaserState> states = new HashMap<>();

    private static class ChaserState {
        double shootCooldown;
        double dirChangeCooldown;
        double targetHeading;
    }

    @Override
    public String getEnemyType() {
        return "CHASER";
    }

    @Override
    public void spawnEnemy(World world, GameData gameData) {
        Entity enemy = createEnemyAtEdge(gameData, RADIUS, new PolygonShape(
                new double[]{0, -12, -9.8, 14, 9.8, 14},
                new double[]{1, 0, 1, 1}
        ));
        ChaserState s = new ChaserState();
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

        List<Entity> players = world.getEntities(EntityType.PLAYER);

        for (Entity enemy : spawned) {
            if (!enemy.isActive()) {
                removePipsFor(enemy);
                continue;
            }
            ChaserState s = states.get(enemy.getId());
            if (s == null) continue;

            s.dirChangeCooldown -= dt;
            s.shootCooldown -= dt;

            double rot = enemy.getRotation();

            if (!players.isEmpty()) {
                Entity player = players.get(0);
                double dx = player.getX() - enemy.getX();
                double dy = player.getY() - enemy.getY();
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < CHASE_RANGE) {
                    s.targetHeading = Math.atan2(dx, -dy);
                } else if (s.dirChangeCooldown <= 0) {
                    s.targetHeading = rng.nextDouble() * 2 * Math.PI;
                    s.dirChangeCooldown = DIR_CHANGE_INTERVAL;
                }
            } else if (s.dirChangeCooldown <= 0) {
                s.targetHeading = rng.nextDouble() * 2 * Math.PI;
                s.dirChangeCooldown = DIR_CHANGE_INTERVAL;
            }

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
