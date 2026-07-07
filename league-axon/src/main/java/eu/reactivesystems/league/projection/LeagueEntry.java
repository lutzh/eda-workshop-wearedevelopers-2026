package eu.reactivesystems.league.projection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "league")
@IdClass(LeagueEntry.LeagueEntryId.class)
public class LeagueEntry {

    @Id
    private String leagueId;

    @Id
    private String team;

    private int gamesPlayed;
    private int points;

    public LeagueEntry() {}

    public LeagueEntry(String leagueId, String team) {
        this.leagueId = leagueId;
        this.team = team;
        this.gamesPlayed = 0;
        this.points = 0;
    }

    public String getLeagueId() { return leagueId; }
    public String getTeam() { return team; }
    public int getGamesPlayed() { return gamesPlayed; }
    public int getPoints() { return points; }

    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public void setPoints(int points) { this.points = points; }

    public static class LeagueEntryId implements Serializable {
        private String leagueId;
        private String team;

        public LeagueEntryId() {}

        public LeagueEntryId(String leagueId, String team) {
            this.leagueId = leagueId;
            this.team = team;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LeagueEntryId that)) return false;
            return Objects.equals(leagueId, that.leagueId) && Objects.equals(team, that.team);
        }

        @Override
        public int hashCode() {
            return Objects.hash(leagueId, team);
        }
    }
}
