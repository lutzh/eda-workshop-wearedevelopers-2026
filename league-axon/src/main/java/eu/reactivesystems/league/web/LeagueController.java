package eu.reactivesystems.league.web;

import eu.reactivesystems.league.api.Club;
import eu.reactivesystems.league.api.Game;
import eu.reactivesystems.league.command.AddClub;
import eu.reactivesystems.league.command.AddGame;
import eu.reactivesystems.league.command.ChangeGame;
import eu.reactivesystems.league.projection.LeagueEntry;
import eu.reactivesystems.league.projection.LeagueRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/league/{leagueId}")
public class LeagueController {

    private final CommandGateway commandGateway;
    private final LeagueRepository leagueRepository;

    public LeagueController(CommandGateway commandGateway, LeagueRepository leagueRepository) {
        this.commandGateway = commandGateway;
        this.leagueRepository = leagueRepository;
    }

    @PostMapping("/club")
    public ResponseEntity<Void> addClub(
            @PathVariable String leagueId,
            @RequestBody Club club) {
        try {
            commandGateway.sendAndWait(new AddClub(leagueId, club));
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/game")
    public ResponseEntity<Void> addGame(
            @PathVariable String leagueId,
            @RequestBody Game game) {
        try {
            commandGateway.sendAndWait(new AddGame(leagueId, game));
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/game")
    public ResponseEntity<Void> changeGame(
            @PathVariable String leagueId,
            @RequestBody Game game) {
        try {
            commandGateway.sendAndWait(new ChangeGame(leagueId, game));
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<LeagueEntry> getLeague(@PathVariable String leagueId) {
        return leagueRepository.findByLeagueIdOrderByPointsDesc(leagueId);
    }
}
