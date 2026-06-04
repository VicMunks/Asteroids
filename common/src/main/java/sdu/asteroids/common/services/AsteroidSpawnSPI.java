package sdu.asteroids.common.services;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;

public interface AsteroidSpawnSPI {
    void spawnAsteroid(World world, GameData gameData, double radius);
}
