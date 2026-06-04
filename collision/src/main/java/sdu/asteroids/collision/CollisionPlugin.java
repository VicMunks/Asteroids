package sdu.asteroids.collision;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.AsteroidSplittingSPI;
import sdu.asteroids.common.services.IPostEntityProcessorService;
import sdu.asteroids.common.services.ScoreSPI;
import sdu.asteroids.common.util.ServiceLocator;

import java.util.List;

public class CollisionPlugin implements IPostEntityProcessorService {

    private static final double MIN_RADIUS = 16.0;

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;
        List<Entity> bullets = world.getEntities(EntityType.BULLET);
        List<Entity> asteroids = world.getEntities(EntityType.ASTEROID);
        List<Entity> players = world.getEntities(EntityType.PLAYER);
        List<Entity> enemies = world.getEntities(EntityType.ENEMY);

        AsteroidSplittingSPI splitter = ServiceLocator.getService(AsteroidSplittingSPI.class);
        ScoreSPI scorer = ServiceLocator.getService(ScoreSPI.class);

        for (Entity bullet : bullets) {
            if (!bullet.isActive()) continue;

            if (bullet.getOwner() == EntityType.PLAYER) {

                if (splitter != null) {
                    for (Entity asteroid : asteroids) {
                        if (!asteroid.isActive()) continue;
                        if (!bullet.isActive() && bullet.getPierceRemaining() == 0) break;
                        if (collides(bullet, asteroid)) {
                            if (bullet.getExplosionRadius() > 0) {
                                explode(bullet, world, gameData, splitter, scorer);
                            } else {
                                if (asteroid.getRadius() > MIN_RADIUS) {
                                    splitter.split(asteroid, world);
                                    if (scorer != null) scorer.recordKill(EntityType.ASTEROID, 20);
                                } else {
                                    splitter.destroy(asteroid, world);
                                    if (scorer != null) scorer.recordKill(EntityType.ASTEROID, 50);
                                }
                            }
                            if (bullet.getPierceRemaining() > 0) {
                                bullet.setPierceRemaining(bullet.getPierceRemaining() - 1);
                            } else {
                                bullet.setActive(false);
                                break;
                            }
                        }
                    }
                }

                for (Entity enemy : enemies) {
                    if (!enemy.isActive()) continue;
                    if (!bullet.isActive() && bullet.getPierceRemaining() == 0) break;
                    if (collides(bullet, enemy)) {
                        if (bullet.getExplosionRadius() > 0) {
                            explode(bullet, world, gameData, splitter, scorer);
                        } else {
                            enemy.setHealth(enemy.getHealth() - (int) gameData.getPlayerDamageMultiplier());
                            if (enemy.getHealth() <= 0) {
                                enemy.setActive(false);
                                if (scorer != null) scorer.recordKill(EntityType.ENEMY, 200);
                            }
                        }
                        if (bullet.getPierceRemaining() > 0) {
                            bullet.setPierceRemaining(bullet.getPierceRemaining() - 1);
                        } else {
                            bullet.setActive(false);
                            break;
                        }
                    }
                }
            }

            if (bullet.isActive() && bullet.getOwner() == EntityType.ENEMY) {
                for (Entity player : players) {
                    if (!player.isActive()) continue;
                    if (gameData.isPlayerInvincible()) continue;
                    if (collides(bullet, player)) {
                        bullet.setActive(false);
                        int remaining = gameData.getPlayerHealth() - 1;
                        gameData.setPlayerHealth(remaining);
                        if (remaining <= 0) {
                            player.setActive(false);
                            gameData.setGameState(GameState.GAME_OVER);
                        } else {
                            gameData.setPlayerHitFlag(true);
                        }
                    }
                }
            }
        }

        for (Entity player : players) {
            if (!player.isActive()) continue;
            if (!gameData.isPlayerInvincible()) {
                for (Entity asteroid : asteroids) {
                    if (!asteroid.isActive()) continue;
                    if (collides(player, asteroid)) {
                        int remaining = gameData.getPlayerHealth() - (int) Math.max(1, asteroid.getDamage());
                        gameData.setPlayerHealth(remaining);
                        if (remaining <= 0) {
                            player.setActive(false);
                            gameData.setGameState(GameState.GAME_OVER);
                        } else {
                            gameData.setPlayerHitFlag(true);
                        }
                        break;
                    }
                }
            }
            if (!player.isActive()) continue;
            if (!gameData.isPlayerInvincible()) {
                for (Entity enemy : enemies) {
                    if (!enemy.isActive()) continue;
                    if (collides(player, enemy)) {
                        enemy.setActive(false);
                        if (scorer != null) scorer.recordKill(EntityType.ENEMY, 200);
                        int remaining = gameData.getPlayerHealth() - (int) Math.max(1, enemy.getDamage());
                        gameData.setPlayerHealth(remaining);
                        if (remaining <= 0) {
                            player.setActive(false);
                            gameData.setGameState(GameState.GAME_OVER);
                        } else {
                            gameData.setPlayerHitFlag(true);
                        }
                        break;
                    }
                }
            }
        }
    }

    private void explode(Entity bullet, World world, GameData gameData,
                         AsteroidSplittingSPI splitter, ScoreSPI scorer) {
        double bx = bullet.getX(), by = bullet.getY(), er = bullet.getExplosionRadius();
        bullet.setActive(false);
        for (Entity target : world.getEntities()) {
            if (!target.isActive()) continue;
            double dx = target.getX() - bx, dy = target.getY() - by;
            if (Math.sqrt(dx * dx + dy * dy) > er) continue;
            if (target.getType() == EntityType.ENEMY) {
                target.setHealth(target.getHealth() - 1);
                if (target.getHealth() <= 0) {
                    target.setActive(false);
                    if (scorer != null) scorer.recordKill(EntityType.ENEMY, 200);
                }
            } else if (target.getType() == EntityType.ASTEROID) {
                if (target.getRadius() > MIN_RADIUS) {
                    splitter.split(target, world);
                    if (scorer != null) scorer.recordKill(EntityType.ASTEROID, 20);
                } else {
                    splitter.destroy(target, world);
                    if (scorer != null) scorer.recordKill(EntityType.ASTEROID, 50);
                }
            }
        }
    }

    private boolean collides(Entity a, Entity b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < a.getRadius() + b.getRadius();
    }
}
