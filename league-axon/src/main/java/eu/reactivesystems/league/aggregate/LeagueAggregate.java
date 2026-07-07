package eu.reactivesystems.league.aggregate;

import eu.reactivesystems.league.api.Club;
import eu.reactivesystems.league.api.Game;
import eu.reactivesystems.league.command.AddClub;
import eu.reactivesystems.league.command.AddGame;
import eu.reactivesystems.league.command.ChangeGame;
import eu.reactivesystems.league.event.ClubRegistered;
import eu.reactivesystems.league.event.GamePlayed;
import eu.reactivesystems.league.event.ResultRevoked;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Aggregate
public class LeagueAggregate {

    private static final int LEAGUE_MAX = 18;

    @AggregateIdentifier
    private String leagueId;

    private Set<String> clubs = new HashSet<>();
    private Map<String, Game> games = new HashMap<>();

    public LeagueAggregate() {}

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(AddClub cmd) {
        if (clubs.contains(cmd.club().name())) {
            throw new IllegalStateException("Club already registered: " + cmd.club().name());
        }
        if (clubs.size() >= LEAGUE_MAX) {
            throw new IllegalStateException("League is full (max " + LEAGUE_MAX + " clubs)");
        }
        AggregateLifecycle.apply(new ClubRegistered(cmd.leagueId(), cmd.club()));
    }

    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void handle(AddGame cmd) {
        Game game = cmd.game();
        String key = gameKey(game.home(), game.away());
        if (games.containsKey(key)) {
            throw new IllegalStateException(
                "Game already recorded: " + game.home().name() + " vs " + game.away().name());
        }
        // Auto-register clubs that aren't in the league yet
        if (!clubs.contains(game.home().name())) {
            if (clubs.size() >= LEAGUE_MAX) {
                throw new IllegalStateException("League is full, cannot auto-register " + game.home().name());
            }
            AggregateLifecycle.apply(new ClubRegistered(cmd.leagueId(), game.home()));
        }
        if (!clubs.contains(game.away().name())) {
            if (clubs.size() >= LEAGUE_MAX) {
                throw new IllegalStateException("League is full, cannot auto-register " + game.away().name());
            }
            AggregateLifecycle.apply(new ClubRegistered(cmd.leagueId(), game.away()));
        }
        AggregateLifecycle.apply(new GamePlayed(cmd.leagueId(), game));
    }

    @CommandHandler
    public void handle(ChangeGame cmd) {
        Game newGame = cmd.game();
        String key = gameKey(newGame.home(), newGame.away());
        Game oldGame = games.get(key);
        if (oldGame == null) {
            throw new IllegalStateException(
                "Game not found: " + newGame.home().name() + " vs " + newGame.away().name());
        }
        AggregateLifecycle.apply(new ResultRevoked(cmd.leagueId(), oldGame));
        AggregateLifecycle.apply(new GamePlayed(cmd.leagueId(), newGame));
    }

    @EventSourcingHandler
    public void on(ClubRegistered event) {
        this.leagueId = event.leagueId();
        clubs.add(event.club().name());
    }

    @EventSourcingHandler
    public void on(GamePlayed event) {
        this.leagueId = event.leagueId();
        Game game = event.game();
        games.put(gameKey(game.home(), game.away()), game);
    }

    @EventSourcingHandler
    public void on(ResultRevoked event) {
        Game game = event.game();
        games.remove(gameKey(game.home(), game.away()));
    }

    private static String gameKey(Club home, Club away) {
        return home.name() + "-" + away.name();
    }
}
