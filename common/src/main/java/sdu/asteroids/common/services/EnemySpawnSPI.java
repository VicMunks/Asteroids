package sdu.asteroids.common.services;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;

public interface EnemySpawnSPI {
    void spawnEnemy(World world, GameData gameData);
    String getEnemyType();
}
