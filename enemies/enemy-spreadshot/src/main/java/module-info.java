module sdu.asteroids.enemy.spreadshot {
    requires sdu.asteroids.common;
    requires sdu.asteroids.enemy.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.enemy.spreadshot.SpreadShotPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.enemy.spreadshot.SpreadShotPlugin;
    provides sdu.asteroids.common.services.EnemySpawnSPI
            with sdu.asteroids.enemy.spreadshot.SpreadShotPlugin;
}
