module sdu.asteroids.enemy.chaser {
    requires sdu.asteroids.common;
    requires sdu.asteroids.enemy.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.enemy.chaser.ChaserPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.enemy.chaser.ChaserPlugin;
    provides sdu.asteroids.common.services.EnemySpawnSPI
            with sdu.asteroids.enemy.chaser.ChaserPlugin;
}
