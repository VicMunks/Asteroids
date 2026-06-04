package sdu.asteroids.common.services;

import sdu.asteroids.common.data.Entity;
import sdu.asteroids.common.data.World;

public interface AsteroidSplittingSPI {
    void split(Entity asteroid, World world);
    void destroy(Entity asteroid, World world);
}
