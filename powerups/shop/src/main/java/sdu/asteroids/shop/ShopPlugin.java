package sdu.asteroids.shop;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.TextShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.PowerupSPI;
import sdu.asteroids.common.util.ServiceLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ShopPlugin implements IGamePluginService, IEntityProcessorService {

    private static final int OPTION_COUNT = 3;
    private static final String[] KEYS = {"DIGIT1", "DIGIT2", "DIGIT3"};

    private final Random rng = new Random();
    private boolean active = false;
    private final List<PowerupSPI> options = new ArrayList<>();
    private final List<String> hudIds = new ArrayList<>();

    @Override
    public void start(GameData gameData, World world) {
        active = false;
        options.clear();
        hudIds.clear();
    }

    @Override
    public void stop(GameData gameData, World world) {
        clearHud(world);
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() == GameState.SHOPPING && !active) {
            openShop(gameData, world);
        }

        if (gameData.getGameState() == GameState.SHOPPING && active) {
            for (int i = 0; i < OPTION_COUNT; i++) {
                if (gameData.isPressed(KEYS[i])) {
                    options.get(i).onAcquire(gameData, world);
                    closeShop(gameData, world);
                    return;
                }
            }
        }

        if (gameData.getGameState() != GameState.SHOPPING && active) {
            clearHud(world);
            active = false;
        }
    }

    private void openShop(GameData gameData, World world) {
        Collection<PowerupSPI> all = ServiceLocator.getServices(PowerupSPI.class);
        if (all.isEmpty()) {
            gameData.setGameState(GameState.RUNNING);
            return;
        }
        List<PowerupSPI> pool = new ArrayList<>(all);
        options.clear();
        for (int i = 0; i < OPTION_COUNT; i++) {
            options.add(pool.get(rng.nextInt(pool.size())));
        }

        double cx = gameData.getDisplayWidth() / 2.0;
        double cy = gameData.getDisplayHeight() / 2.0;

        addHud(world, "CHOOSE YOUR UPGRADE", 32, new double[]{1, 1, 0, 1}, cx, cy - 120);
        for (int i = 0; i < OPTION_COUNT; i++) {
            PowerupSPI p = options.get(i);
            String label = "[" + (i + 1) + "]  " + p.getDisplayName() + "  (" + p.getCategory() + ")";
            addHud(world, label, 22, new double[]{1, 1, 1, 1}, cx, cy - 40 + i * 50);
        }
        active = true;
    }

    private void closeShop(GameData gameData, World world) {
        clearHud(world);
        active = false;
        options.clear();
        gameData.setGameState(GameState.RUNNING);
    }

    private void addHud(World world, String text, double size, double[] color, double x, double y) {
        Entity e = new Entity();
        e.setType(EntityType.HUD);
        e.setX(x);
        e.setY(y);
        e.setShape(new TextShape(text, size, color));
        world.addEntity(e);
        hudIds.add(e.getId());
    }

    private void clearHud(World world) {
        for (Entity e : world.getEntities()) {
            if (hudIds.contains(e.getId())) e.setActive(false);
        }
        hudIds.clear();
    }
}
