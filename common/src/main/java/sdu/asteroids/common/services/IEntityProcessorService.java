package sdu.asteroids.common.services;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;

public interface IEntityProcessorService {
    void process(GameData gameData, World world);
}
