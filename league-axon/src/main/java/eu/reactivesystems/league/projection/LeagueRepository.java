package eu.reactivesystems.league.projection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeagueRepository extends JpaRepository<LeagueEntry, LeagueEntry.LeagueEntryId> {

    Optional<LeagueEntry> findByLeagueIdAndTeam(String leagueId, String team);

    List<LeagueEntry> findByLeagueIdOrderByPointsDesc(String leagueId);
}
