package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/calcio"; // URL del database
    private static final String USER = "root"; // Username del DB
    private static final String PASSWORD = ""; // password di MySQL

    // Metodo per connettersi al database
    public Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Metodo per verificare se il database è vuoto
    public boolean isDatabaseEmpty() {
        String query = "SELECT COUNT(*) AS count FROM Partite";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // Considera il database vuoto in caso di errore
    }

    // Metodo per salvare una partita nel database
    public void saveMatch(String team1, String team2, String result, String date, String source) {
        String query = "INSERT INTO Partite (squadra1, squadra2, risultato, data, fonte) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, team1);
            stmt.setString(2, team2);
            stmt.setString(3, result);
            stmt.setString(4, date);
            stmt.setString(5, source);
            stmt.executeUpdate();

            System.out.println("Partita salvata nel database: " + team1 + " vs " + team2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metodo per recuperare le partite di oggi
    public String getTodayMatches() {
        StringBuilder results = new StringBuilder();
        String query = "SELECT * FROM Partite WHERE data = CURDATE()";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String match = rs.getString("squadra1") + " vs " + rs.getString("squadra2") +
                        " - Risultato: " + rs.getString("risultato") + "\n";
                results.append(match);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results.toString();
    }

    // Metodo per recuperare le partite per una data specifica
    public String getMatchesByDate(String date) {
        StringBuilder results = new StringBuilder();
        String query = "SELECT * FROM Partite WHERE data = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String match = rs.getString("squadra1") + " vs " + rs.getString("squadra2") +
                        " - Orario: " + rs.getString("ora") + "\n";
                results.append(match);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results.toString();
    }

    // Metodo per recuperare informazioni su una squadra
    public String getTeamInfo(String teamName) {
        StringBuilder results = new StringBuilder();
        String query = "SELECT * FROM Partite WHERE squadra1 = ? OR squadra2 = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, teamName);
            stmt.setString(2, teamName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String match = rs.getString("data") + ": " +
                        rs.getString("squadra1") + " vs " + rs.getString("squadra2") +
                        " - Risultato: " + rs.getString("risultato") + "\n";
                results.append(match);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results.toString();
    }

    // Metodo per recuperare statistiche delle squadre
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        String query = "SELECT squadra1 AS squadra, COUNT(*) AS partite_giocate " +
                "FROM Partite GROUP BY squadra1 " +
                "ORDER BY partite_giocate DESC";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            stats.append("Statistiche delle squadre:\n");
            while (rs.next()) {
                stats.append("Squadra: ").append(rs.getString("squadra"))
                        .append(" - Partite giocate: ").append(rs.getInt("partite_giocate"))
                        .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats.toString();
    }
}
