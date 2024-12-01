import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScores {
    private List<HighScoreEntry> highScores;

    public HighScores() {
        this.highScores = new ArrayList<>();
    }

    public void addScore(String nickname) {
        String time = null;
        highScores.add(new HighScoreEntry(nickname, time));
        sortHighScores();
    }

    public List<String> getTopScores() {
        List<String> topScores = new ArrayList<>();
        for (HighScoreEntry entry : highScores) {
            topScores.add(entry.getNickname() + ": " + entry.getTime());
        }
        return topScores;
    }

    private void sortHighScores() {
        // Sort high scores in ascending order based on time (e.g., fastest times first)
        Collections.sort(highScores);
    }

    void saveScore(String nickname, int time) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    // Inner class to represent a high score entry
    private static class HighScoreEntry implements Comparable<HighScoreEntry> {
        private String nickname;
        private String time;

        public HighScoreEntry(String nickname, String time) {
            this.nickname = nickname;
            this.time = time;
        }

        public String getNickname() {
            return nickname;
        }

        public String getTime() {
            return time;
        }

        @Override
        public int compareTo(HighScoreEntry o) {
            return this.time.compareTo(o.time);  // Compare times (assuming the time is in a comparable format)
        }
    }
}
