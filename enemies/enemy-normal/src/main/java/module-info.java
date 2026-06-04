module sdu.asteroids.enemy.normal {
    requires sdu.asteroids.common;
    requires sdu.asteroids.enemy.common;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.enemy.normal.EnemyPlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.enemy.normal.EnemyPlugin;
    provides sdu.asteroids.common.services.EnemySpawnSPI
            with sdu.asteroids.enemy.normal.EnemyPlugin;
}
