package sdu.asteroids.enemy.bomber;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.FilledPolygonShape;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.enemy.common.AbstractEnemyPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BomberPlugin extends AbstractEnemyPlugin {

    private static final double MOVEMENT_SPEED = 40.0;
    private static final double TURN_SPEED = Math.PI / 3.0;
    private static final double DETECTION_RANGE = 130.0;
    private static final double APPROACH_EXTRA_TIME = 1.0;
    private static final double BLINK_COUNTDOWN = 3.0;
    private static final double EXPLOSION_RADIUS = 110.0;
    private static final double BLINK_INTERVAL = 0.15;
    private static final double RADIUS = 14.0;

    private final Map<String, BomberState> states = new HashMap<>();

    @Override
    public String getEnemyType() {
        return "BOMBER";
    }

    @Override
    public void stop(GameData gameData, World world) {
        removeSpawned();
        states.clear();
    }

    @Override
    public void spawnEnemy(World world, GameData gameData) {
        Entity enemy = createEnemyAtEdge(gameData, RADIUS, new FilledPolygonShape(
                new double[]{-8, -8, 8, -8, 8, 8, -8, 8},
                new double[]{1, 0.5, 0, 1}
        ));
        states.put(enemy.getId(), new BomberState());
        enemy.setHealth(1);
        enemy.setDamage(1);
        world.addEntity(enemy);
        spawnPips(enemy, world);
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
            BomberState s = states.get(enemy.getId());
            if (s == null || s.detonated) continue;

            Entity player = players.isEmpty() ? null : players.get(0);
            double distToPlayer = Double.MAX_VALUE;
            if (player != null) {
                double dx = player.getX() - enemy.getX();
                double dy = player.getY() - enemy.getY();
                distToPlayer = Math.sqrt(dx * dx + dy * dy);
            }

            if (!s.approachingExtra && !s.blinking) {
                if (distToPlayer < DETECTION_RANGE) {
                    s.approachingExtra = true;
                    s.extraTimer = APPROACH_EXTRA_TIME;
                } else {
                    if (player != null) {
                        headToward(enemy, player.getX(), player.getY(), dt, w, h);
                    }
                }
            }

            if (s.approachingExtra && !s.blinking) {
                if (player != null) {
                    headToward(enemy, player.getX(), player.getY(), dt, w, h);
                }
                s.extraTimer -= dt;
                if (s.extraTimer <= 0) {
                    s.approachingExtra = false;
                    s.blinking = true;
                    s.blinkCountdown = BLINK_COUNTDOWN;
                    s.blinkTimer = 0;
                    enemy.setDx(0);
                    enemy.setDy(0);
                }
            }

            if (s.blinking) {
                s.blinkCountdown -= dt;
                s.blinkTimer -= dt;
                if (s.blinkTimer <= 0) {
                    s.blinkTimer = BLINK_INTERVAL;
                    double[] c = enemy.getShape().getColorRGBA();
                    c[3] = (c[3] > 0.5) ? 0.2 : 1.0;
                }
                if (s.blinkCountdown <= 0) {
                    s.detonated = true;
                    enemy.setActive(false);
                    if (player != null && distToPlayer < EXPLOSION_RADIUS) {
                        player.setActive(false);
                        gameData.setGameState(GameState.GAME_OVER);
                    }
                }
            }
            updatePips(enemy);
        }
    }

    private void headToward(Entity enemy, double tx, double ty, double dt, double w, double h) {
        double dx = tx - enemy.getX();
        double dy = ty - enemy.getY();
        double targetAngle = Math.atan2(dx, -dy);
        double rot = enemy.getRotation();
        double diff = angleDiff(targetAngle, rot);
        double maxTurn = TURN_SPEED * dt;
        if (Math.abs(diff) <= maxTurn) {
            rot = targetAngle;
        } else {
            rot += Math.signum(diff) * maxTurn;
        }
        enemy.setRotation(rot);
        enemy.setDx(Math.sin(rot) * MOVEMENT_SPEED);
        enemy.setDy(-Math.cos(rot) * MOVEMENT_SPEED);
        enemy.setX(wrapCoord(enemy.getX() + enemy.getDx() * dt, w));
        enemy.setY(wrapCoord(enemy.getY() + enemy.getDy() * dt, h));
    }

    private static class BomberState {
        boolean approachingExtra = false;
        double extraTimer = 0;
        boolean blinking = false;
        double blinkCountdown = 0;
        double blinkTimer = 0;
        boolean detonated = false;
    }
}
