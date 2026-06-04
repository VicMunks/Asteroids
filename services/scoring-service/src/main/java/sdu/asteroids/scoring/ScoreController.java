package sdu.asteroids.scoring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/score")
    public void postScore(@RequestBody Map<String, Object> body) {
        String type = (String) body.get("type");
        int points = ((Number) body.get("points")).intValue();
        scoreService.add(type, points);
    }

    @GetMapping("/scores")
    public List<ScoreEntry> getScores() {
        return scoreService.getAll();
    }

    @PostMapping("/session")
    public void postSession(@RequestBody Map<String, Object> body) {
        int score = ((Number) body.get("score")).intValue();
        scoreService.addSession(score);
    }

    @GetMapping("/highscore")
    public int getHighScore() {
        return scoreService.getHighScore();
    }
}
