package sdu.asteroids.score;

import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IEntityProcessorService;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.ScoreSPI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ScorePlugin implements IGamePluginService, IEntityProcessorService, ScoreSPI {

    private static final String SCORE_URL = "http://localhost:8080/score";
    private static final String SESSION_URL = "http://localhost:8080/session";
    private static final String HIGHSCORE_URL = "http://localhost:8080/highscore";

    private HttpClient client;
    private GameData cachedGameData;
    private GameState previousState;

    @Override
    public void start(GameData gameData, World world) {
        client = HttpClient.newHttpClient();
        cachedGameData = gameData;
        previousState = gameData.getGameState();
        try {
            String body = get(HIGHSCORE_URL);
            int hs = Integer.parseInt(body.trim());
            if (hs > 0) gameData.setHighScore(hs);
        } catch (Exception ignored) {}
    }

    @Override
    public void stop(GameData gameData, World world) {
    }

    @Override
    public void recordKill(EntityType destroyed, int points) {
        cachedGameData.setScore(cachedGameData.getScore() + points);
        try {
            String json = String.format(
                    "{\"type\":\"%s\",\"points\":%d,\"timestamp\":%d}",
                    destroyed.name(), points, System.currentTimeMillis());
            post(SCORE_URL, json);
        } catch (Exception ignored) {}
    }

    @Override
    public void process(GameData gameData, World world) {
        if (previousState != GameState.GAME_OVER && gameData.getGameState() == GameState.GAME_OVER) {
            try {
                String json = String.format("{\"score\":%d}", gameData.getScore());
                post(SESSION_URL, json);
                String body = get(HIGHSCORE_URL);
                int hs = Integer.parseInt(body.trim());
                if (hs > gameData.getHighScore()) gameData.setHighScore(hs);
            } catch (Exception ignored) {}
        }
        previousState = gameData.getGameState();
    }

    private String get(String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString()).body();
    }

    private void post(String url, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        client.send(req, HttpResponse.BodyHandlers.ofString());
    }
}
