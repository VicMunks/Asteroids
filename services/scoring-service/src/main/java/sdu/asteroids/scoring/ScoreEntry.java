package sdu.asteroids.scoring;

public class ScoreEntry {
    private final String type;
    private final int points;
    private final long timestamp;

    public ScoreEntry(String type, int points, long timestamp) {
        this.type = type;
        this.points = points;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public int getPoints() { return points; }
    public long getTimestamp() { return timestamp; }
}
