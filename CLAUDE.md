# AsteroidsFX — CLAUDE.md

## Project Overview

Component-based Asteroids game in Java. Each game feature lives in its own JPMS module
loaded at runtime via ModuleLayer. The core engine discovers plugins through ServiceLoader
and interacts with them exclusively through service interfaces declared in
`sdu.asteroids.common`. No plugin knows about another plugin. All cross-plugin
communication goes through ServiceLocator.

## Tech Stack

- Java 25 (LTS)
- JavaFX 25
- Maven (multi-module, single parent pom)
- Spring Framework 7.0.7 (core module only — DI wiring)
- Spring Boot 4.0.6 (scoring-service and wave-config-service — separate JVMs, not in JPMS graph)
- JUnit 6 + Mockito (tests)

## Implementation Rules

- No comments in code
- Implement only what the current phase specifies — nothing more
- Clean, minimal, structured code derived from the design
- No plugin class may directly reference a class from another plugin module
- All cross-plugin communication goes through ServiceLocator in common

---

## Module Names and Responsibilities

All JPMS module names carry the prefix `sdu.asteroids.`

| Maven artifact      | JPMS module name                | Responsibility                                                         |
|---------------------|---------------------------------|------------------------------------------------------------------------|
| common              | sdu.asteroids.common            | Shared API: data classes, service interfaces, ServiceLocator           |
| core                | sdu.asteroids.core              | Game loop, JavaFX window, Canvas renderer, Spring DI, PluginLoader     |
| player              | sdu.asteroids.player            | Player ship: movement, rotation, shooting via BulletSPI                |
| bullet              | sdu.asteroids.bullet            | Bullet trajectory, lifetime-based removal, BulletSPI provider          |
| asteroid            | sdu.asteroids.asteroid          | Asteroid spawning, movement, AsteroidSplittingSPI + AsteroidSpawnSPI   |
| collision           | sdu.asteroids.collision         | Radius-based collision detection and response                          |
| particle            | sdu.asteroids.particle          | Visual particle effects on entity destruction                          |
| audio               | sdu.asteroids.audio             | Sound effects on game events                                           |
| score               | sdu.asteroids.score             | ScoreSPI provider; POSTs kills to scoring-service, syncs score/highscore to GameData |
| hud                 | sdu.asteroids.hud               | HUD overlay: renders SCORE and BEST from GameData each frame           |
| screens             | sdu.asteroids.screens           | Start screen (STARTING state) and game-over screen (GAME_OVER state)   |
| wave                | sdu.asteroids.wave              | Wave sequencing: announcements, fetches config from wave-config-service, spawns asteroids + enemies |
| common-enemy        | sdu.asteroids.enemy.common      | AbstractEnemyPlugin base class shared by all enemy plugins             |
| enemy-normal        | sdu.asteroids.enemy.normal      | Normal enemy: random movement + shooting, EnemySpawnSPI provider       |
| enemy-chaser        | sdu.asteroids.enemy.chaser      | Chaser enemy: pursues player within range, EnemySpawnSPI provider      |
| enemy-spreadshot    | sdu.asteroids.enemy.spreadshot  | Spreadshot enemy: fires ring of 8 bullets, EnemySpawnSPI provider      |
| enemy-bomber        | sdu.asteroids.enemy.bomber      | Bomber enemy: approaches player, blinks, explodes, EnemySpawnSPI provider |
| scoring-service     | (not JPMS)                      | Spring Boot REST: POST /score, GET /scores, GET /highscore, POST /session/reset |
| wave-config-service | (not JPMS)                      | Spring Boot REST: GET /wave/{n} → { asteroidCount, enemyCount }        |

---

## Package Structure (sdu.asteroids.common)

```
common/src/main/java/
  sdu/asteroids/common/
    data/
      Entity.java
      EntityType.java
      GameData.java
      GameState.java
      IShape.java
      PolygonShape.java
      FilledPolygonShape.java
      TextShape.java
      World.java
    services/
      IGamePluginService.java
      IEntityProcessorService.java
      IPostEntityProcessorService.java
      BulletSPI.java
      AsteroidSplittingSPI.java
      AsteroidSpawnSPI.java
      EnemySpawnSPI.java
      ScoreSPI.java
    util/
      ServiceLocator.java
  module-info.java
```

---

## Data Model

### Entity

| Field          | Type         | Description                                                                                                      |
|----------------|--------------|------------------------------------------------------------------------------------------------------------------|
| id             | String       | UUID string                                                                                                      |
| type           | EntityType   |                                                                                                                  |
| owner          | EntityType   | Which entity type created this (used by collision for bullets)                                                   |
| x, y           | double       | Position                                                                                                         |
| dx, dy         | double       | Velocity                                                                                                         |
| rotation       | double       | Radians                                                                                                          |
| rotationSpeed  | double       | Radians per second                                                                                               |
| radius         | double       | Collision radius                                                                                                  |
| speed          | double       |                                                                                                                  |
| health         | int          |                                                                                                                  |
| damage         | double       |                                                                                                                  |
| shape          | IShape       |                                                                                                                  |
| lifetime       | double       | Seconds remaining; -1 means infinite. When > 0, decremented each frame; reaches 0 → active = false              |
| active         | boolean      | Set false to mark for removal; world.cleanUp() performs the actual removal at end of frame                        |

All fields have public getters and setters.

### EntityType (enum)
`PLAYER`, `ENEMY`, `ASTEROID`, `BULLET`, `PARTICLE`, `HUD`

### GameState (enum)
`STARTING`, `RUNNING`, `PAUSED`, `GAME_OVER`, `RESTARTING`

### IShape
```java
double[] getPoints();
double[] getColorRGBA();
```

### PolygonShape implements IShape
- `points: double[]` — flat vertex array [x0, y0, x1, y1, ...]
- `color: double[]` — RGBA values 0.0–1.0

### FilledPolygonShape implements IShape
Same as PolygonShape but renders as a filled polygon with alpha flicker (used by BomberPlugin).

### TextShape implements IShape
- `text: String`
- `fontSize: double`
- `color: double[]` — RGBA
- Used by HUD entities; rendered as text via GraphicsContext, not as polygon vertices

### GameData

| Field         | Type           | Description                                     |
|---------------|----------------|-------------------------------------------------|
| displayWidth  | int            |                                                 |
| displayHeight | int            |                                                 |
| deltaTime     | double         | Seconds since last frame                        |
| keysPressed   | Set\<String\>  | Plain strings — no JavaFX dependency            |
| gameState     | GameState      |                                                 |
| score         | int            | Current session score; updated by ScorePlugin   |
| highScore     | int            | All-time high; updated by ScorePlugin           |

Methods: `isPressed(String k): boolean`, `addKey(String k): void`, `removeKey(String k): void`

### World

| Method                          | Returns         | Description                                  |
|---------------------------------|-----------------|----------------------------------------------|
| addEntity(Entity e)             | void            |                                              |
| getEntities()                   | List\<Entity\>  | Returns List.copyOf snapshot                 |
| getEntities(EntityType t)       | List\<Entity\>  | Filtered snapshot by type                    |
| cleanUp()                       | void            | Removes all entities where active == false   |

---

## Service Interfaces

### IGamePluginService
```
void start(GameData gameData, World world)
void stop(GameData gameData, World world)
```

### IEntityProcessorService
```
void process(GameData gameData, World world)
```
Called every frame before any post-processors. Entity count in world must be unchanged.

### IPostEntityProcessorService
```
void process(GameData gameData, World world)
```
Called every frame after all IEP processors. May add or remove entities.

### BulletSPI
```
void spawnBullet(double x, double y, double rotation, EntityType owner)
```
Provided by: `bullet` module. Consumed by: player, enemy plugins — via ServiceLocator.

### AsteroidSplittingSPI
```
void split(Entity asteroid, World world)
void destroy(Entity asteroid, World world)
```
Provided by: `asteroid` module. Consumed by: `collision` — via ServiceLocator.

### AsteroidSpawnSPI
```
void spawnAsteroid(World world, GameData gameData, double radius)
```
Provided by: `asteroid` module. Consumed by: `wave` — via ServiceLocator.

### EnemySpawnSPI
```
void spawnEnemy(World world, GameData gameData)
String getEnemyType()
```
Provided by: each enemy module. Consumed by: `wave` — via ServiceLocator.getServices().
`getEnemyType()` returns a string identifier used by WavePlugin's unlock pool logic:
`"NORMAL"`, `"CHASER"`, `"SPREADSHOT"`, `"BOMBER"`

### ScoreSPI
```
void recordKill(EntityType destroyed, int points)
```
Provided by: `score` module. Consumed by: `collision` — via ServiceLocator.

---

## ServiceLocator (sdu.asteroids.common.util)

Static utility. Wraps ServiceLoader. Plugins call it directly — never injected.

```java
public class ServiceLocator {
    public static <T> T getService(Class<T> serviceClass)
    public static <T> Collection<T> getServices(Class<T> serviceClass)
    public static void setModuleLayer(ModuleLayer layer)
}
```

### Instance caching (REQUIRED)

The ServiceLocator caches concrete service instances and returns the **same object** every
time it is requested. A plugin like `BulletPlugin` that provides three SPIs must return the
same instance for all three. Cache key is the **concrete provider class**, not the SPI.

---

## module-info.java Contracts

### common
```java
module sdu.asteroids.common {
    exports sdu.asteroids.common.data;
    exports sdu.asteroids.common.services;
    exports sdu.asteroids.common.util;
    uses sdu.asteroids.common.services.IGamePluginService;
    uses sdu.asteroids.common.services.IEntityProcessorService;
    uses sdu.asteroids.common.services.IPostEntityProcessorService;
    uses sdu.asteroids.common.services.BulletSPI;
    uses sdu.asteroids.common.services.AsteroidSplittingSPI;
    uses sdu.asteroids.common.services.ScoreSPI;
    uses sdu.asteroids.common.services.EnemySpawnSPI;
    uses sdu.asteroids.common.services.AsteroidSpawnSPI;
}
```

### common-enemy
```java
module sdu.asteroids.enemy.common {
    requires sdu.asteroids.common;
    exports sdu.asteroids.enemy.common;
}
```

### enemy-normal / enemy-chaser / enemy-spreadshot
```java
module sdu.asteroids.enemy.normal {          // .chaser / .spreadshot
    requires sdu.asteroids.common;
    requires sdu.asteroids.enemy.common;
    provides sdu.asteroids.common.services.IGamePluginService   with ...;
    provides sdu.asteroids.common.services.IEntityProcessorService with ...;
    provides sdu.asteroids.common.services.EnemySpawnSPI        with ...;
}
```

### wave
```java
module sdu.asteroids.wave {
    requires sdu.asteroids.common;
    requires java.net.http;
    provides sdu.asteroids.common.services.IGamePluginService    with sdu.asteroids.wave.WavePlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService with sdu.asteroids.wave.WavePlugin;
}
```

---

## AbstractEnemyPlugin (sdu.asteroids.enemy.common)

Base class for all enemy plugins. Provides:
- `protected final Random rng`
- `protected final List<Entity> spawned` — tracks all live instances spawned by this plugin
- `protected Entity createEnemyAtEdge(GameData, double radius, IShape)` — spawns at a random arena edge, adds to `spawned`
- `protected void removeSpawned()` — sets all spawned entities inactive, clears list
- `protected double angleDiff(double target, double current)` — shortest angular delta
- `protected double wrapCoord(double value, double max)` — toroidal wrap
- `protected void fireBulletAt(double x, double y, double rotation, double offset)` — calls BulletSPI

Each concrete enemy plugin:
- Extends `AbstractEnemyPlugin`
- Implements `IGamePluginService`, `IEntityProcessorService`, `EnemySpawnSPI`
- Uses a **per-entity state map** (`Map<String, XxxState>`) keyed by entity id — see BomberPlugin as the reference pattern
- `stop()` calls `removeSpawned()` then `states.clear()`

---

## Wave System

`WavePlugin` (implements IGP + IEP):
- Tracks `currentWave`, `announcing`, `waveActive`
- On `process()`: if RUNNING and wave is clear (no ASTEROID, no ENEMY entities), starts next wave
- Announcement: sets HUD text "WAVE N" for `ANNOUNCEMENT_DURATION` seconds, then calls `spawnWave()`
- `spawnWave()`: fetches config from `http://localhost:8081/wave/{n}`, falls back to `calculateConfig()` on error
  - `calculateConfig(n)`: `asteroidCount = 5 + (n-1)*2`, `enemyCount = 1 + (n-1)/3`
- Enemy unlock pool by wave: ≤3 → NORMAL only; ≤5 → + CHASER; ≤7 → + SPREADSHOT; 8+ → all types

`wave-config-service` runs on port 8081. Returns `{ "asteroidCount": N, "enemyCount": M }`.

---

## Scoring System

`ScorePlugin` (implements IGP + IEP + ScoreSPI):
- `recordKill(type, points)`: adds points to `gameData.score`, POSTs to `http://localhost:8080/score`
- On `GAME_OVER` detection: POSTs session end, fetches updated highscore from `http://localhost:8080/highscore`
- On `RESTARTING`: POSTs session reset to `http://localhost:8080/session`
- `HudPlugin` reads `gameData.score` and `gameData.highScore` and updates its TextShape entities each frame

`scoring-service` runs on port 8080.

---

## Game States

| State      | Meaning                                                              |
|------------|----------------------------------------------------------------------|
| STARTING   | Title screen visible; waiting for SPACE                              |
| RUNNING    | Normal gameplay                                                      |
| PAUSED     | Reserved; not currently used                                         |
| GAME_OVER  | Game over screen visible; waiting for SPACE to restart               |
| RESTARTING | Triggers `Game.restart()` in GameEngine; transitions back to STARTING |

---

## Game Loop

JavaFX AnimationTimer `handle(long now)`:
1. If `RESTARTING`: call `game.restart()` and return
2. `gameData.setDeltaTime(delta)` — delta in seconds
3. For each IEntityProcessorService: `process(gameData, world)`
4. For each IPostEntityProcessorService: `process(gameData, world)`
5. `world.cleanUp()`
6. `render(world.getEntities())`

---

## Rendering

Canvas-based. `GameEngine` holds a `GraphicsContext`.
Each frame:
1. Clear canvas (black fill)
2. Render all non-HUD entities: translate to (x, y), rotate by entity.rotation, draw polygon from IShape.getPoints() using IShape.getColorRGBA() as stroke color
3. Render all HUD entities on top: TextShape renders via `gc.fillText()`, PolygonShape renders normally

---

## Arena and Game Feel Constants

### Arena
| Constant          | Value              |
|-------------------|--------------------|
| Width             | 1280               |
| Height            | 800                |
| Boundary behavior | Toroidal wrap (all entities wrap; bullets despawn via lifetime) |

### Player
| Constant            | Value           |
|---------------------|-----------------|
| TURN_SPEED          | Math.PI rad/s   |
| THRUST              | 170.0 px/s²     |
| DRAG                | 0.985 per frame |
| RADIUS              | 12.0            |
| SHIP_SIZE           | 14.0            |
| SPAWN_INVINCIBILITY | 2.0s            |
| FIRE_RATE           | 0.5s            |
| BULLET_SPAWN_OFFSET | 18.0 px         |

### Bullet
| Constant  | Value      |
|-----------|------------|
| SPEED     | 400.0 px/s |
| RADIUS    | 3.0        |
| LIFETIME  | 1.5s       |

### Asteroid
| Constant          | Value         |
|-------------------|---------------|
| RADIUS_LARGE      | 64.0          |
| RADIUS_MEDIUM     | 32.0          |
| RADIUS_SMALL      | 16.0          |
| MIN_RADIUS        | 16.0          |
| SPEED_MIN / MAX   | 40 / 100 px/s |

### Enemy (base values — each plugin may vary)
| Constant      | Value       |
|---------------|-------------|
| TURN_SPEED    | π/2 rad/s   |
| FIRE_INTERVAL | 1.0–3.0s    |
| MOVEMENT_SPEED| 40–70 px/s  |
| RADIUS        | 12–14       |

### Keys
`"LEFT"`, `"RIGHT"`, `"UP"`, `"SPACE"`

---

## /plugins Directory

Located at project root: `/plugins`
All plugin JARs are copied here during Maven install. `core` reads this path at startup.
Excludes: `common`, `core`, `scoring-service`, `wave-config-service` (not plugins).

---

## Build and Run

```bash
mvn clean install                        # build all, copy JARs to /plugins
mvn exec:exec -f core/pom.xml            # run the game
# In a separate terminal, if score/wave services are needed:
mvn spring-boot:run -f scoring-service/pom.xml
mvn spring-boot:run -f wave-config-service/pom.xml
```

---

## Phase Plan

- [x] Phase 1  — Project scaffold: parent pom, all module stubs, /plugins dir
- [x] Phase 2  — `common`: all data classes, enums, interfaces, ServiceLocator
- [x] Phase 3  — `core`: JavaFX window, game loop, PluginLoader, Spring DI, Canvas renderer
- [x] Phase 4  — `bullet` + `player`: ship moves and shoots, bullets travel and despawn
- [x] Phase 5  — `asteroid`: asteroids spawn at edges, drift, toroidal wrap
- [x] Phase 6  — `collision`: bullets split/destroy asteroids, player dies on impact
- [x] Phase 7  — `enemy-normal`: random movement and shooting
- [x] Phase 8  — `particle`: destruction bursts on entity death
- [x] Phase 9  — `audio`: sound effects on shoot and destroy
- [x] Phase 10 — `scoring-service` + `score` plugin: kills recorded and persisted
- [x] Phase 11 — `screens`: start screen + game over screen + restart flow
- [x] Phase 12 — `hud`: score and high score overlay
- [x] Phase 13 — `wave` + `wave-config-service`: wave sequencing, announcements, escalating spawns
- [x] Phase 14 — `common-enemy` + `enemy-chaser` + `enemy-spreadshot` + `enemy-bomber`: enemy variety
- [ ] **Phase 15** — Bug fixes (current task — see below)
- [ ] Phase 16 — Player health system
- [ ] Phase 17 — Shop system (between-wave powerup selection)
- [ ] Phase 18 — Powerup framework (`common-powerup`, first powerup module)
- [ ] Phase 19 — Additional powerup modules (incremental)

---

## Current Task — Phase 15: Bug Fixes

Three targeted fixes. No comments. Do not change any behavior outside what is described.

---

### Fix 1 — Shared state in EnemyPlugin, ChaserPlugin, SpreadShotPlugin

**File:** `enemy-normal/src/main/java/sdu/asteroids/enemy/normal/EnemyPlugin.java`
**File:** `enemy-chaser/src/main/java/sdu/asteroids/enemy/chaser/ChaserPlugin.java`
**File:** `enemy-spreadshot/src/main/java/sdu/asteroids/enemy/spreadshot/SpreadShotPlugin.java`

**Problem:** `shootCooldown`, `dirChangeCooldown`, and `targetHeading` are single instance
fields shared across all spawned enemies of the same type. Multiple enemies of the same
type fire simultaneously and change direction in sync.

**Fix pattern:** Follow BomberPlugin exactly — it already uses `Map<String, BomberState>`.

For `EnemyPlugin`, add:
```java
private static class NormalState {
    double shootCooldown;
    double dirChangeCooldown;
    double targetHeading;
}
private final Map<String, NormalState> states = new HashMap<>();
```

For `ChaserPlugin`, add:
```java
private static class ChaserState {
    double shootCooldown;
    double dirChangeCooldown;
    double targetHeading;
}
private final Map<String, ChaserState> states = new HashMap<>();
```

For `SpreadShotPlugin`, add:
```java
private static class SpreadState {
    double shootCooldown;
    double dirChangeCooldown;
    double targetHeading;
}
private final Map<String, SpreadState> states = new HashMap<>();
```

For each of the three plugins:
- **`start()`**: Remove the instance field initialisations (they no longer exist). `start()` body can be empty or removed if the plugin has no other init logic.
- **`spawnEnemy()`**: After `createEnemyAtEdge()`, create a new state object with randomised `targetHeading` (`rng.nextDouble() * 2 * Math.PI`) and full cooldown values, then `states.put(enemy.getId(), state)`.
- **`process()`**: Inside the `for (Entity enemy : spawned)` loop, look up `states.get(enemy.getId())` as the first statement, and read/write cooldowns and targetHeading from that state object instead of the removed instance fields.
- **`stop()`**: Override `stop()` to call `removeSpawned()` then `states.clear()`.
- **Remove** the old `shootCooldown`, `dirChangeCooldown`, `targetHeading` instance fields entirely.

Also add `import java.util.HashMap;` and `import java.util.Map;` to each file.

---

### Fix 2 — WavePlugin: remove dead constants, fix fallback formula, move Random

**File:** `wave/src/main/java/sdu/asteroids/wave/WavePlugin.java`

Remove these three unused constants entirely:
```
BASE_ASTEROIDS
ASTEROIDS_PER_WAVE
ENEMY_EVERY_N_WAVES
```

Update `calculateConfig()` to match the wave-config-service formula exactly:
```java
private WaveConfig calculateConfig(int waveNumber) {
    int asteroidCount = 5 + (waveNumber - 1) * 2;
    int enemyCount = 1 + (waveNumber - 1) / 3;
    return new WaveConfig(asteroidCount, enemyCount);
}
```

Move the `Random` instance from a local variable inside `spawnWave()` to an instance field:
```java
private final Random rng = new Random();
```
Remove the `Random rng = new Random();` local variable declaration from `spawnWave()`.

---

### Verification

After both fixes:
- `mvn clean install` succeeds
- Wait for wave 3 (two NORMAL enemies): they fire at different times and change direction independently
- Wait for wave 6 (enemies include SPREADSHOT): the spreadshot enemy fires its ring independently of other enemies

---

## Next Up — Phase 16: Player Health System

This phase is a prerequisite for the boost and help powerups.

**Changes to `common`:**
Add to `GameData`:
```java
private int playerHealth;
private int playerMaxHealth;
```
With getters/setters.

**Changes to `player`:**
- Add constant `MAX_HEALTH = 3`
- `start()`: set `player.setHealth(MAX_HEALTH)` and `gameData.setPlayerMaxHealth(MAX_HEALTH)` and `gameData.setPlayerHealth(MAX_HEALTH)`
- Add a `respawn(GameData, World)` private method: reposition player to arena center, reset velocity to 0, reset invincibility timer to `SPAWN_INVINCIBILITY`
- Remove `player.setActive(false)` from `CollisionPlugin` for player hits — instead the player module handles health

**Changes to `collision`:**
- When a player is hit (by bullet or asteroid): call `gameData.setPlayerHealth(gameData.getPlayerHealth() - 1)`. If health > 0, do NOT set player inactive or GAME_OVER — the player plugin will handle the respawn. If health == 0, set player inactive and `GAME_OVER`.
- To signal a hit without killing: add a field `playerHitThisFrame` flag, or use a convention (e.g., a new `GameData` field `playerHitFlag: boolean`). Simplest approach: add `private boolean playerHitFlag` to `GameData` that `CollisionPlugin` sets to `true` on hit and `PlayerPlugin` clears and acts on.

**Changes to `hud`:**
- Add a health display: render `playerMaxHealth` heart/dot icons (or simple text "HP: N") reading from `GameData`.

Full detail for this phase will be specified in the Phase 16 prompt when Phase 15 is complete.

---

## Future Architecture — Powerup System (Phase 17+)

Documented here for context. Do not implement until Phase 16 is complete.

### New common types (to be added to `common` in Phase 17)

**ShotConfig (new class in `sdu.asteroids.common.data`):**
```java
public class ShotConfig {
    public int bulletCount = 1;
    public double spreadAngle = 0;
    public int pierceCount = 0;
    public boolean explosive = false;
    public double explosionRadius = 0;
    public int volleys = 1;
    public double volleyDelayMs = 0;
}
```

**WeaponModifierSPI (new interface in `sdu.asteroids.common.services`):**
```java
public interface WeaponModifierSPI {
    void modifyShot(ShotConfig config, GameData gameData);
}
```

**PowerupSPI (new interface in `sdu.asteroids.common.services`):**
```java
public interface PowerupSPI {
    String getId();          // e.g. "weapon-shotgun", "boost-speed"
    String getDisplayName(); // shown in shop
    String getCategory();    // "WEAPON", "BOOST", "HELP"
    void onAcquire(GameData gameData, World world);
}
```

### New GameData fields (Phase 17)
```java
private Map<String, Integer> activeWeapons = new HashMap<>(); // weaponId → level
private double playerSpeedMultiplier = 1.0;
private double playerDamageMultiplier = 1.0;
private double playerDefense = 0.0;
private double shieldActiveSeconds = 0.0;
```

### New GameState (Phase 17)
Add `SHOPPING` to the `GameState` enum.

### BulletSPI change (Phase 17)
Add an overload or replace `spawnBullet` with `spawnShot(ShotConfig config, double x, double y, double rotation, EntityType owner)`.
`BulletPlugin` reads `ShotConfig` to spawn the right number of bullets with the right properties.
Existing `spawnBullet` calls stay working until migrated.

### New Bullet entity fields (Phase 17)
Add to `Entity`:
```java
private int pierceRemaining = 0;
private double explosionRadius = 0.0;
```

### Planned powerup modules
```
common-powerup/       AbstractPowerupPlugin, AbstractWeaponPlugin, AbstractBoostPlugin, AbstractHelpPlugin
shop/                 ShopPlugin: between-wave selection UI, 3 random options, keypress to select

weapon-shotgun/       multiple bullets in spread
weapon-rapidfire/     volleys with slight delay
weapon-piercing/      bullets pass through N enemies
weapon-bazooka/       explosive bullets

boost-speed/          playerSpeedMultiplier += per level
boost-damage/         playerDamageMultiplier += per level
boost-defense/        playerDefense += per level (reduces damage taken)
boost-maxhealth/      playerMaxHealth += 1 per level, restores health

help-shield/          shieldActiveSeconds = 5 (one-time)
help-heal/            restores playerHealth to playerMaxHealth (one-time)
```

### Shop flow (Phase 17)
1. WavePlugin: when wave clears, set `gameData.setGameState(GameState.SHOPPING)` instead of immediately starting next wave
2. ShopPlugin (IGP + IEP): on `SHOPPING`, picks 3 random powerups from `ServiceLocator.getServices(PowerupSPI.class)`, renders 3 HUD option cards, reads "1"/"2"/"3" keypresses, calls `selected.onAcquire(gameData, world)`, then sets state back to `RUNNING`
3. WavePlugin resumes on next `RUNNING` frame
