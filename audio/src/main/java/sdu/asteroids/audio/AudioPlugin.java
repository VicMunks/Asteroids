package sdu.asteroids.audio;

import javafx.scene.media.AudioClip;
import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.EntityType;
import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.GameState;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.IPostEntityProcessorService;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AudioPlugin implements IGamePluginService, IPostEntityProcessorService {

    private AudioClip shootClip;
    private AudioClip explosionClip;
    private Set<String> bulletsLastFrame = new HashSet<>();
    private Set<String> wasActiveLastFrame = new HashSet<>();

    @Override
    public void start(GameData gameData, World world) {
        shootClip = loadClip("/sounds/shoot.wav");
        explosionClip = loadClip("/sounds/explosion.wav");
        bulletsLastFrame = new HashSet<>();
        wasActiveLastFrame = new HashSet<>();
    }

    @Override
    public void stop(GameData gameData, World world) {
    }

    @Override
    public void process(GameData gameData, World world) {
        if (gameData.getGameState() != GameState.RUNNING) return;
        Set<String> currentBullets = new HashSet<>();
        for (Entity entity : world.getEntities(EntityType.BULLET)) {
            if (!entity.isActive()) continue;
            currentBullets.add(entity.getId());
            if (!bulletsLastFrame.contains(entity.getId()) && shootClip != null) {
                shootClip.play();
            }
        }

        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.PARTICLE) continue;
            if (wasActiveLastFrame.contains(entity.getId()) && !entity.isActive()) {
                EntityType type = entity.getType();
                if (type == EntityType.PLAYER || type == EntityType.ENEMY || type == EntityType.ASTEROID) {
                    if (explosionClip != null) {
                        explosionClip.play();
                    }
                }
            }
        }

        bulletsLastFrame = currentBullets;

        Set<String> nextActive = new HashSet<>();
        for (Entity entity : world.getEntities()) {
            if (entity.isActive()) {
                nextActive.add(entity.getId());
            }
        }
        wasActiveLastFrame = nextActive;
    }

    private AudioClip loadClip(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) {
                System.err.println("Audio resource not found: " + path);
                return null;
            }
            return new AudioClip(url.toExternalForm());
        } catch (Exception e) {
            System.err.println("Failed to load audio: " + path + ": " + e.getMessage());
            return null;
        }
    }
}
