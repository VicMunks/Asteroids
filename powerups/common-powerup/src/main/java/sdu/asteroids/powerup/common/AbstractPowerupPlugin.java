package sdu.asteroids.powerup.common;

import sdu.asteroids.common.data.GameData;
import sdu.asteroids.common.data.World;
import sdu.asteroids.common.services.IGamePluginService;
import sdu.asteroids.common.services.PowerupSPI;

public abstract class AbstractPowerupPlugin implements IGamePluginService, PowerupSPI {

    @Override
    public void start(GameData gameData, World world) {}

    @Override
    public void stop(GameData gameData, World world) {}
}
