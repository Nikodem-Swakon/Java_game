package game.utils;


import java.io.*;
import java.util.*;

public class HighScoreManager {
   private static final String FILE_NAME = "C:/Users/Admin-Niko/Documents/_STUDIA/_Net_Java/Javagame/SNAKE/src/highscores.txt";


    public static void saveScore(String playerName, int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(playerName + ":" + score);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getTopScores(int topN) {
        List<String> scores = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {
            // Jeśli plik nie istnieje - zwracamy pustą listę
        }

        scores.sort((a, b) -> {
            int scoreA = Integer.parseInt(a.split(":")[1]);
            int scoreB = Integer.parseInt(b.split(":")[1]);
            return Integer.compare(scoreB, scoreA);
        });

        return scores.subList(0, Math.min(topN, scores.size()));
    }
}
