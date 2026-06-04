package sdu.asteroids.scoring;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ScoreService {

    private final List<ScoreEntry> entries = Collections.synchronizedList(new ArrayList<>());
    private final List<Integer> sessionTotals = Collections.synchronizedList(new ArrayList<>());

    public void add(String type, int points) {
        entries.add(new ScoreEntry(type, points, System.currentTimeMillis()));
    }

    public List<ScoreEntry> getAll() {
        return List.copyOf(entries);
    }

    public void addSession(int score) {
        sessionTotals.add(score);
    }

    public int getHighScore() {
        return sessionTotals.stream().mapToInt(Integer::intValue).max().orElse(0);
    }
}
