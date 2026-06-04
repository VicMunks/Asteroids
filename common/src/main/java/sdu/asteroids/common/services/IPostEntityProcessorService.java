package sdu.asteroids.common.services;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;

public interface IPostEntityProcessorService {
    void process(GameData gameData, World world);
}
