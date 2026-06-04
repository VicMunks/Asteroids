package sdu.asteroids.common.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameData {
    private int displayWidth;
    private int displayHeight;
    private double deltaTime;
    private final Set<String> keysPressed = new HashSet<>();
    private GameState gameState;
    private int score;
    private int highScore;
    private int playerHealth;
    private int playerMaxHealth;
    private boolean playerHitFlag;
    private boolean playerInvincible;
    private int currentWave;
    private double playerSpeedMultiplier = 1.0;
    private double playerDamageMultiplier = 1.0;
    private double shieldActiveSeconds = 0.0;
    private final Map<String, Integer> activeWeapons = new HashMap<>();

    public int getDisplayWidth() { return displayWidth; }
    public void setDisplayWidth(int displayWidth) { this.displayWidth = displayWidth; }

    public int getDisplayHeight() { return displayHeight; }
    public void setDisplayHeight(int displayHeight) { this.displayHeight = displayHeight; }

    public double getDeltaTime() { return deltaTime; }
    public void setDeltaTime(double deltaTime) { this.deltaTime = deltaTime; }

    public GameState getGameState() { return gameState; }
    public void setGameState(GameState gameState) { this.gameState = gameState; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getHighScore() { return highScore; }
    public void setHighScore(int highScore) { this.highScore = highScore; }

    public int getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(int playerHealth) { this.playerHealth = playerHealth; }

    public int getPlayerMaxHealth() { return playerMaxHealth; }
    public void setPlayerMaxHealth(int playerMaxHealth) { this.playerMaxHealth = playerMaxHealth; }

    public boolean isPlayerHitFlag() { return playerHitFlag; }
    public void setPlayerHitFlag(boolean playerHitFlag) { this.playerHitFlag = playerHitFlag; }

    public boolean isPlayerInvincible() { return playerInvincible; }
    public void setPlayerInvincible(boolean playerInvincible) { this.playerInvincible = playerInvincible; }

    public int getCurrentWave() { return currentWave; }
    public void setCurrentWave(int currentWave) { this.currentWave = currentWave; }

    public double getPlayerSpeedMultiplier() { return playerSpeedMultiplier; }
    public void setPlayerSpeedMultiplier(double playerSpeedMultiplier) { this.playerSpeedMultiplier = playerSpeedMultiplier; }

    public double getPlayerDamageMultiplier() { return playerDamageMultiplier; }
    public void setPlayerDamageMultiplier(double playerDamageMultiplier) { this.playerDamageMultiplier = playerDamageMultiplier; }

    public double getShieldActiveSeconds() { return shieldActiveSeconds; }
    public void setShieldActiveSeconds(double shieldActiveSeconds) { this.shieldActiveSeconds = shieldActiveSeconds; }

    public Map<String, Integer> getActiveWeapons() { return activeWeapons; }

    public boolean isPressed(String k) { return keysPressed.contains(k); }
    public void addKey(String k) { keysPressed.add(k); }
    public void removeKey(String k) { keysPressed.remove(k); }
}
