package sdu.asteroids.waveconfig;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WaveConfigController {

    private static final int[][] HARDCODED = {
        {3, 1}, {5, 1}, {6, 2}, {6, 2},
        {7, 2}, {8, 3}, {9, 3}, {9, 4}
    };

    @GetMapping("/wave/{waveNumber}")
    public Map<String, Object> getWaveConfig(@PathVariable("waveNumber") int waveNumber) {
        int asteroidCount;
        int enemyCount;
        if (waveNumber >= 1 && waveNumber <= 8) {
            asteroidCount = HARDCODED[waveNumber - 1][0];
            enemyCount = HARDCODED[waveNumber - 1][1];
        } else {
            asteroidCount = 5 + (waveNumber - 1) * 2;
            enemyCount = 1 + (waveNumber - 1) / 3;
        }
        double speedMultiplier = 1.0 + (waveNumber - 1) * 0.1;
        return Map.of(
                "asteroidCount", asteroidCount,
                "enemyCount", enemyCount,
                "speedMultiplier", speedMultiplier
        );
    }
}
