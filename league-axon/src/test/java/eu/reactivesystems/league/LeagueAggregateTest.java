package eu.reactivesystems.league;

import eu.reactivesystems.league.aggregate.LeagueAggregate;
import eu.reactivesystems.league.api.Club;
import eu.reactivesystems.league.api.Game;
import eu.reactivesystems.league.command.AddClub;
import eu.reactivesystems.league.command.AddGame;
import eu.reactivesystems.league.command.ChangeGame;
import eu.reactivesystems.league.event.ClubRegistered;
import eu.reactivesystems.league.event.GamePlayed;
import eu.reactivesystems.league.event.ResultRevoked;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeagueAggregateTest {

    private static final String LEAGUE = "bundesliga";
    private static final Club FCB = new Club("FCB");
    private static final Club SVW = new Club("SVW");

    private FixtureConfiguration<LeagueAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(LeagueAggregate.class);
    }

    @Test
    void addClub() {
        fixture.givenNoPriorActivity()
                .when(new AddClub(LEAGUE, FCB))
                .expectEvents(new ClubRegistered(LEAGUE, FCB));
    }

    @Test
    void addClubTwiceIsRejected() {
        fixture.given(new ClubRegistered(LEAGUE, FCB))
                .when(new AddClub(LEAGUE, FCB))
                .expectException(IllegalStateException.class);
    }

    @Test
    void addGameAutoRegistersClubs() {
        Game game = new Game(FCB, SVW, 1, 6, 0);

        fixture.givenNoPriorActivity()
                .when(new AddGame(LEAGUE, game))
                .expectEvents(
                        new ClubRegistered(LEAGUE, FCB),
                        new ClubRegistered(LEAGUE, SVW),
                        new GamePlayed(LEAGUE, game)
                );
    }

    @Test
    void addGameSkipsRegisteredClubs() {
        Game game = new Game(FCB, SVW, 1, 6, 0);

        fixture.given(
                        new ClubRegistered(LEAGUE, FCB),
                        new ClubRegistered(LEAGUE, SVW)
                )
                .when(new AddGame(LEAGUE, game))
                .expectEvents(new GamePlayed(LEAGUE, game));
    }

    @Test
    void addGameTwiceIsRejected() {
        Game game = new Game(FCB, SVW, 1, 6, 0);

        fixture.given(new GamePlayed(LEAGUE, game))
                .when(new AddGame(LEAGUE, game))
                .expectException(IllegalStateException.class);
    }

    @Test
    void changeGame() {
        Game original = new Game(FCB, SVW, 1, 6, 0);
        Game corrected = new Game(FCB, SVW, 1, 3, 1);

        fixture.given(
                        new ClubRegistered(LEAGUE, FCB),
                        new ClubRegistered(LEAGUE, SVW),
                        new GamePlayed(LEAGUE, original)
                )
                .when(new ChangeGame(LEAGUE, corrected))
                .expectEvents(
                        new ResultRevoked(LEAGUE, original),
                        new GamePlayed(LEAGUE, corrected)
                );
    }

    @Test
    void changeNonExistentGameIsRejected() {
        fixture.given(
                        new ClubRegistered(LEAGUE, FCB),
                        new ClubRegistered(LEAGUE, SVW)
                )
                .when(new ChangeGame(LEAGUE, new Game(FCB, SVW, 1, 3, 1)))
                .expectException(IllegalStateException.class);
    }
}
