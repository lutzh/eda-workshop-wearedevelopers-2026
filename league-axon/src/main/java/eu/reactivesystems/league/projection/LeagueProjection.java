package eu.reactivesystems.league.projection;

import eu.reactivesystems.league.api.Game;
import eu.reactivesystems.league.event.ClubRegistered;
import eu.reactivesystems.league.event.GamePlayed;
import eu.reactivesystems.league.event.ResultRevoked;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("league-projection")
public class LeagueProjection {

    private final LeagueRepository repository;

    public LeagueProjection(LeagueRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void on(ClubRegistered event) {
        repository.save(new LeagueEntry(event.leagueId(), event.club().name()));
    }

    @EventHandler
    public void on(GamePlayed event) {
        Game game = event.game();
        updateTeam(event.leagueId(), game.home().name(), game.homeGoals(), game.awayGoals());
        updateTeam(event.leagueId(), game.away().name(), game.awayGoals(), game.homeGoals());
    }

    @EventHandler
    public void on(ResultRevoked event) {
        Game game = event.game();
        revokeTeam(event.leagueId(), game.home().name(), game.homeGoals(), game.awayGoals());
        revokeTeam(event.leagueId(), game.away().name(), game.awayGoals(), game.homeGoals());
    }

    private void updateTeam(String leagueId, String team, int goalsFor, int goalsAgainst) {
        LeagueEntry entry = repository.findByLeagueIdAndTeam(leagueId, team)
                .orElseThrow(() -> new IllegalStateException("Team not found in league: " + team));
        entry.setGamesPlayed(entry.getGamesPlayed() + 1);
        entry.setPoints(entry.getPoints() + points(goalsFor, goalsAgainst));
        repository.save(entry);
    }

    private void revokeTeam(String leagueId, String team, int goalsFor, int goalsAgainst) {
        LeagueEntry entry = repository.findByLeagueIdAndTeam(leagueId, team)
                .orElseThrow(() -> new IllegalStateException("Team not found in league: " + team));
        entry.setGamesPlayed(entry.getGamesPlayed() - 1);
        entry.setPoints(entry.getPoints() - points(goalsFor, goalsAgainst));
        repository.save(entry);
    }

    private int points(int goalsFor, int goalsAgainst) {
        if (goalsFor > goalsAgainst) return 3;
        if (goalsFor == goalsAgainst) return 1;
        return 0;
    }
}
