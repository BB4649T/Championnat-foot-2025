package org.example.centralapi.repository;

import org.example.centralapi.model.Championship;
import org.example.centralapi.model.Club;
import org.example.centralapi.model.ClubStatistics;
import org.example.centralapi.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CentralRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CentralRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void saveChampionships(List<Championship> championships) {
        // D'abord, vider la table
        jdbcTemplate.update("DELETE FROM championship");

        // Ensuite, insérer les données
        for (Championship championship : championships) {
            jdbcTemplate.update(
                    "INSERT INTO championship (id, name, country) VALUES (?, ?, ?)",
                    championship.getId(),
                    championship.getName(),
                    championship.getCountry()
            );
        }
    }

    @Transactional
    public void saveClubs(List<Club> clubs) {
        // D'abord, vider la table
        jdbcTemplate.update("DELETE FROM club");

        // Ensuite, insérer les données
        for (Club club : clubs) {
            jdbcTemplate.update(
                    "INSERT INTO club (id, name, acronym, foundation_year, stadium_name, championship_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    club.getId(),
                    club.getName(),
                    club.getAcronym(),
                    club.getFoundationYear(),
                    club.getStadiumName(),
                    club.getChampionshipId()
            );
        }
    }

    @Transactional
    public void savePlayers(List<Player> players) {
        // D'abord, vider la table
        jdbcTemplate.update("DELETE FROM player");

        // Ensuite, insérer les données
        for (Player player : players) {
            jdbcTemplate.update(
                    "INSERT INTO player (id, name, number, position, nationality, age, club_id, goals, playing_time) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    player.getId(),
                    player.getName(),
                    player.getNumber(),
                    player.getPosition(),
                    player.getNationality(),
                    player.getAge(),
                    player.getClubId(),
                    player.getGoals(),
                    player.getPlayingTime()
            );
        }
    }

    @Transactional
    public void saveStatistics(List<ClubStatistics> statistics) {
        // D'abord, vider la table
        jdbcTemplate.update("DELETE FROM club_statistics");

        // Ensuite, insérer les données
        for (ClubStatistics stats : statistics) {
            jdbcTemplate.update(
                    "INSERT INTO club_statistics (club_id, club_name, championship_name, points, goals_scored, " +
                            "goals_conceded, goal_difference, clean_sheets) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    stats.getClubId(),
                    stats.getClubName(),
                    stats.getChampionshipName(),
                    stats.getPoints(),
                    stats.getGoalsScored(),
                    stats.getGoalsConceded(),
                    stats.getGoalDifference(),
                    stats.getCleanSheets()
            );
        }
    }

    public List<Player> getBestPlayers(int top, String playingTimeUnit) {
        String sql;
        if ("total".equals(playingTimeUnit)) {
            sql = "SELECT p.*, c.name as club_name FROM player p " +
                    "JOIN club c ON p.club_id = c.id " +
                    "ORDER BY p.goals DESC LIMIT ?";
        } else {
            sql = "SELECT p.*, c.name as club_name, " +
                    "CASE WHEN p.playing_time > 0 THEN (p.goals * 90.0 / p.playing_time) ELSE 0 END as efficiency " +
                    "FROM player p " +
                    "JOIN club c ON p.club_id = c.id " +
                    "WHERE p.playing_time > 0 " +
                    "ORDER BY efficiency DESC " +
                    "LIMIT ?";
        }

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Player player = new Player();
                    player.setId(rs.getInt("id"));
                    player.setName(rs.getString("name"));
                    player.setNumber(rs.getInt("number"));
                    player.setPosition(rs.getString("position"));
                    player.setNationality(rs.getString("nationality"));
                    player.setAge(rs.getInt("age"));
                    player.setClubId(rs.getInt("club_id"));
                    player.setClubName(rs.getString("club_name"));
                    player.setGoals(rs.getInt("goals"));
                    player.setPlayingTime(rs.getInt("playing_time"));
                    return player;
                },
                top
        );
    }

    public List<Championship> getChampionshipRankings() {
        String sql = "SELECT c.id, c.name, c.country, " +
                "SUM(cs.points) as total_points, " +
                "SUM(cs.goals_scored) as total_goals, " +
                "AVG(cs.points) as avg_points_per_club, " +
                "SUM(cs.clean_sheets) as total_clean_sheets " +
                "FROM championship c " +
                "JOIN club cl ON c.id = cl.championship_id " +
                "JOIN club_statistics cs ON cl.id = cs.club_id " +
                "GROUP BY c.id, c.name, c.country " +
                "ORDER BY total_points DESC, total_goals DESC";

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Championship championship = new Championship();
                    championship.setId(rs.getString("id"));
                    championship.setName(rs.getString("name"));
                    championship.setCountry(rs.getString("country"));
                    // You could add these stats to the Championship model if needed
                    // championship.setTotalPoints(rs.getInt("total_points"));
                    // championship.setTotalGoals(rs.getInt("total_goals"));
                    return championship;
                }
        );
    }

}