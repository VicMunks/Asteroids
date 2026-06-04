package sdu.asteroids.player;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.FilledPolygonShape;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.PolygonShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.data.ShotConfig;
import sdu.asteroids.common.services.BulletSPI;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.WeaponModifierSPI;
import sdu.asteroids.common.util.ServiceLocator;

import java.util.ArrayList;
import java.util.List;

public class PlayerPlugin implements IGamePluginService, IEntityProcessorService {

    private static final double TURN_SPEED = Math.PI;
    private static final double THRUST = 170.0;
    private static final double DRAG = 0.985;
    private static final double RADIUS = 12.0;
    private static final double SHIP_SIZE = 14.0;
    private static final double SPAWN_INVINCIBILITY = 2.0;
    private static final double FIRE_RATE = 0.5;
    private static final double BULLET_SPAWN_OFFSET = 18.0;
    private static final int MAX_HEALTH = 3;

    private Entity player;
    private Entity shieldRing;
    private double invincibilityTimer;
    private double shootCooldown;
    private final List<Entity> pips = new ArrayList<>();

    @Override
    public void start(GameData gameData, World world) {
        invincibilityTimer = SPAWN_INVINCIBILITY;
        shootCooldown = 0.0;
        player = new Entity();
        player.setType(EntityType.PLAYER);
        player.setX(gameData.getDisplayWidth() / 2.0);
        player.setY(gameData.getDisplayHeight() / 2.0);
        player.setRadius(RADIUS);
        player.setHealth(MAX_HEALTH);
        player.setShape(new PolygonShape(
                new double[]{0, -SHIP_SIZE, -9.8, SHIP_SIZE, 9.8, SHIP_SIZE},
                new double[]{0, 1, 1, 1}
        ));
        gameData.setPlayerMaxHealth(MAX_HEALTH);
        gameData.setPlayerHealth(MAX_HEALTH);
        gameData.setPlayerSpeedMultiplier(1.0);
        gameData.setPlayerDamageMultiplier(1.0);
        gameData.setShieldActiveSeconds(0.0);
        gameData.getActiveWeapons().clear();
        world.addEntity(player);

        shieldRing = new Entity();
        shieldRing.setType(EntityType.HUD);
        shieldRing.setShape(buildShieldShape());
        world.addEntity(shieldRing);

        for (int i = 0; i < MAX_HEALTH; i++) {
            Entity pip = new Entity();
            pip.setType(EntityType.HUD);
            pip.setShape(new FilledPolygonShape(
                    new double[]{-4, -4, 4, -4, 4, 4, -4, 4},
                    new double[]{0, 1, 0, 1}
            ));
            world.addEntity(pip);
            pips.add(pip);
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        player.setActive(false);
        shieldRing.setActive(false);
        pips.forEach(p -> p.setActive(false));
        pips.clear();
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;

        if (gameData.isPlayerHitFlag()) {
            respawn(gameData);
            gameData.setPlayerHitFlag(false);
        }

        double dt = gameData.getDeltaTime();
        double w = gameData.getDisplayWidth();
        double h = gameData.getDisplayHeight();

        if (invincibilityTimer > 0) {
            invincibilityTimer -= dt;
        }
        if (gameData.getShieldActiveSeconds() > 0) {
            gameData.setShieldActiveSeconds(gameData.getShieldActiveSeconds() - dt);
        }
        gameData.setPlayerInvincible(invincibilityTimer > 0 || gameData.getShieldActiveSeconds() > 0);

        if (shootCooldown > 0) {
            shootCooldown -= dt;
        }

        if (gameData.isPressed("LEFT")) {
            player.setRotation(player.getRotation() - TURN_SPEED * dt);
        }
        if (gameData.isPressed("RIGHT")) {
            player.setRotation(player.getRotation() + TURN_SPEED * dt);
        }
        if (gameData.isPressed("UP")) {
            double rot = player.getRotation();
            player.setDx(player.getDx() + Math.sin(rot) * THRUST * gameData.getPlayerSpeedMultiplier() * dt);
            player.setDy(player.getDy() - Math.cos(rot) * THRUST * gameData.getPlayerSpeedMultiplier() * dt);
        }

        player.setDx(player.getDx() * DRAG);
        player.setDy(player.getDy() * DRAG);

        player.setX(player.getX() + player.getDx() * dt);
        player.setY(player.getY() + player.getDy() * dt);
        player.setX(((player.getX() % w) + w) % w);
        player.setY(((player.getY() % h) + h) % h);

        int targetPips = gameData.getPlayerMaxHealth();
        if (pips.size() != targetPips) {
            pips.forEach(p -> p.setActive(false));
            pips.clear();
            for (int i = 0; i < targetPips; i++) {
                Entity pip = new Entity();
                pip.setType(EntityType.HUD);
                pip.setShape(new FilledPolygonShape(
                        new double[]{-4, -4, 4, -4, 4, 4, -4, 4},
                        new double[]{0, 1, 0, 1}
                ));
                world.addEntity(pip);
                pips.add(pip);
            }
        }

        shieldRing.setX(player.getX());
        shieldRing.setY(player.getY());
        boolean shieldActive = gameData.getShieldActiveSeconds() > 0;
        double[] sc = shieldRing.getShape().getColorRGBA();
        sc[3] = shieldActive ? 0.8 : 0.0;

        if (gameData.isPressed("SPACE") && shootCooldown <= 0) {
            double rot = player.getRotation();
            double bx = player.getX() + Math.sin(rot) * BULLET_SPAWN_OFFSET;
            double by = player.getY() - Math.cos(rot) * BULLET_SPAWN_OFFSET;
            BulletSPI bulletSPI = ServiceLocator.getService(BulletSPI.class);
            if (bulletSPI != null) {
                ShotConfig config = new ShotConfig();
                for (WeaponModifierSPI mod : ServiceLocator.getServices(WeaponModifierSPI.class)) {
                    mod.modifyShot(config, gameData);
                }
                bulletSPI.spawnShot(config, bx, by, rot, EntityType.PLAYER);
                shootCooldown = FIRE_RATE;
            }
        }

        int currentHealth = gameData.getPlayerHealth();
        int maxPips = pips.size();
        for (int i = 0; i < maxPips; i++) {
            Entity pip = pips.get(i);
            pip.setX(player.getX() - ((maxPips - 1) * 10.0 / 2.0) + i * 10.0);
            pip.setY(player.getY() - 24);
            double[] c = pip.getShape().getColorRGBA();
            if (i < currentHealth) {
                c[0] = 0; c[1] = 1; c[2] = 0; c[3] = 1;
            } else {
                c[0] = 0.3; c[1] = 0.3; c[2] = 0.3; c[3] = 0.7;
            }
        }
    }

    private void respawn(GameData gameData) {
        player.setX(gameData.getDisplayWidth() / 2.0);
        player.setY(gameData.getDisplayHeight() / 2.0);
        player.setDx(0);
        player.setDy(0);
        invincibilityTimer = SPAWN_INVINCIBILITY;
    }

    private PolygonShape buildShieldShape() {
        int n = 24;
        double r = 22.0;
        double[] pts = new double[n * 2];
        for (int i = 0; i < n; i++) {
            double a = i * 2 * Math.PI / n;
            pts[i * 2] = Math.sin(a) * r;
            pts[i * 2 + 1] = -Math.cos(a) * r;
        }
        return new PolygonShape(pts, new double[]{0, 1, 1, 0});
    }
}
