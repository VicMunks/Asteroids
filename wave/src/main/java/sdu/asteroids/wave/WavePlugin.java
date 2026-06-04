package sdu.asteroids.wave;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.TextShape;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.AsteroidSpawnSPI;
import sdu.asteroids.common.services.EnemySpawnSPI;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.util.ServiceLocator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class WavePlugin implements IGamePluginService, IEntityProcessorService {

    private static final double ANNOUNCEMENT_DURATION = 2.5;
    private static final double RADIUS_LARGE = 64.0;
    private static final String WAVE_SERVICE_URL = "http://localhost:8081/wave/";

    private int currentWave;
    private double announcementTimer;
    private boolean announcing;
    private boolean waveActive;

    private final Random rng = new Random();
    private String announcementEntityId;
    private HttpClient httpClient;

    @Override
    public void start(GameData gameData, World world) {
        currentWave = 0;
        gameData.setCurrentWave(0);
        announcementTimer = 0;
        announcing = false;
        waveActive = false;
        httpClient = HttpClient.newHttpClient();

        Entity label = new Entity();
        label.setType(EntityType.HUD);
        label.setX(gameData.getDisplayWidth() / 2.0);
        label.setY(gameData.getDisplayHeight() / 2.0 - 40);
        label.setShape(new TextShape("", 36, new double[]{1, 1, 0, 1}));
        world.addEntity(label);
        announcementEntityId = label.getId();
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity e : world.getEntities(EntityType.HUD)) {
            if (e.getId().equals(announcementEntityId)) {
                e.setActive(false);
            }
        }
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;

        if (announcing) {
            announcementTimer -= gameData.getDeltaTime();
            if (announcementTimer <= 0) {
                announcing = false;
                setAnnouncementText(world, "");
                spawnWave(world, gameData);
                waveActive = true;
            }
            return;
        }

        if (waveActive && isArenaClear(world)) {
            waveActive = false;
            gameData.setGameState(GameState.SHOPPING);
        }

        if (!waveActive && !announcing) {
            startNextWave(world, gameData);
        }
    }

    private boolean isArenaClear(World world) {
        return world.getEntities(EntityType.ASTEROID).isEmpty()
                && world.getEntities(EntityType.ENEMY).isEmpty();
    }

    private void startNextWave(World world, GameData gameData) {
        currentWave++;
        gameData.setCurrentWave(currentWave);
        announcing = true;
        announcementTimer = ANNOUNCEMENT_DURATION;
        setAnnouncementText(world, "WAVE " + currentWave);
    }

    private void spawnWave(World world, GameData gameData) {
        WaveConfig config = fetchConfig(currentWave);
        AsteroidSpawnSPI asteroidSPI = ServiceLocator.getService(AsteroidSpawnSPI.class);
        if (asteroidSPI != null) {
            for (int i = 0; i < config.asteroidCount; i++) {
                asteroidSPI.spawnAsteroid(world, gameData, RADIUS_LARGE);
            }
        }

        List<EnemySpawnSPI> pool = getUnlockedPool(currentWave);
        for (int i = 0; i < config.enemyCount; i++) {
            if (pool.isEmpty()) break;
            pool.get(rng.nextInt(pool.size())).spawnEnemy(world, gameData);
        }
    }

    private List<EnemySpawnSPI> getUnlockedPool(int waveNumber) {
        Collection<EnemySpawnSPI> all = ServiceLocator.getServices(EnemySpawnSPI.class);
        List<EnemySpawnSPI> pool = new ArrayList<>();
        for (EnemySpawnSPI s : all) {
            String type = s.getEnemyType();
            if (waveNumber <= 3) {
                if ("NORMAL".equals(type)) pool.add(s);
            } else if (waveNumber <= 5) {
                if ("NORMAL".equals(type) || "CHASER".equals(type)) pool.add(s);
            } else if (waveNumber <= 7) {
                if ("NORMAL".equals(type) || "CHASER".equals(type) || "SPREADSHOT".equals(type)) pool.add(s);
            } else {
                pool.add(s);
            }
        }
        return pool;
    }

    private void setAnnouncementText(World world, String text) {
        for (Entity e : world.getEntities(EntityType.HUD)) {
            if (e.getId().equals(announcementEntityId) && e.getShape() instanceof TextShape ts) {
                ts.setText(text);
            }
        }
    }

    private WaveConfig fetchConfig(int waveNumber) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WAVE_SERVICE_URL + waveNumber))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseConfig(response.body());
            }
        } catch (Exception ignored) {
        }
        return calculateConfig(waveNumber);
    }

    private WaveConfig parseConfig(String json) {
        try {
            int asteroidCount = extractInt(json, "asteroidCount");
            int enemyCount = extractInt(json, "enemyCount");
            return new WaveConfig(asteroidCount, enemyCount);
        } catch (Exception e) {
            return calculateConfig(currentWave);
        }
    }

    private int extractInt(String json, String key) {
        int idx = json.indexOf("\"" + key + "\"");
        int colon = json.indexOf(':', idx);
        int start = colon + 1;
        while (start < json.length() && (json.charAt(start) == ' ' || json.charAt(start) == '\t')) start++;
        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        return Integer.parseInt(json.substring(start, end));
    }

    private WaveConfig calculateConfig(int waveNumber) {
        int asteroidCount = 5 + (waveNumber - 1) * 2;
        int enemyCount = 1 + (waveNumber - 1) / 3;
        return new WaveConfig(asteroidCount, enemyCount);
    }

    private record WaveConfig(int asteroidCount, int enemyCount) {}
}
