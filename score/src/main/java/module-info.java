module sdu.asteroids.score {
    requires sdu.asteroids.common;
    requires java.net.http;
    provides sdu.asteroids.common.services.IGamePluginService
            with sdu.asteroids.score.ScorePlugin;
    provides sdu.asteroids.common.services.IEntityProcessorService
            with sdu.asteroids.score.ScorePlugin;
    provides sdu.asteroids.common.services.ScoreSPI
            with sdu.asteroids.score.ScorePlugin;
}
