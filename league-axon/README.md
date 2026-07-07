# League Axon

An event sourcing example using the [Axon Framework](https://www.axoniq.io/axon-framework) and Spring Boot.
It models a football league: clubs can be registered, game results recorded, and results corrected.

This project mirrors the `league-lagom` example, implemented in Java instead of Scala.

## Architecture

The application follows CQRS and event sourcing patterns as provided by Axon:

- **Write side** — a `LeagueAggregate` handles commands and emits events, which are stored in Axon's JPA event store (H2 in-memory database).
- **Read side** — a `LeagueProjection` consumes events via a tracking processor and maintains a denormalized `league` table (team, games played, points).

### Commands

| Command | HTTP | Description |
|---------|------|-------------|
| `AddClub` | `POST /league/{leagueId}/club` | Register a club. Rejected if the club is already in the league or the league is full (max 18). |
| `AddGame` | `POST /league/{leagueId}/game` | Record a game result. Clubs that aren't registered yet are auto-registered. |
| `ChangeGame` | `PUT /league/{leagueId}/game` | Correct a previously recorded result. |

### Events

| Event | Trigger |
|-------|---------|
| `ClubRegistered` | Club added explicitly or auto-registered via `AddGame` |
| `GamePlayed` | Game result recorded or new result after a correction |
| `ResultRevoked` | Previous result removed as part of `ChangeGame` |

## Running the application

No external services required — H2 is embedded and Axon's JPA event store is auto-configured.

```
cd league-axon
mvn spring-boot:run
```

The application starts on port 8080. The H2 console is available at http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:leaguedb`, user: `sa`, no password).

## Using the application

The API speaks JSON. Examples use [httpie](https://httpie.io/); replace `http` with `curl -X POST -H 'Content-Type: application/json' -d ...` if you prefer.

### Register a club

```
http POST :8080/league/bundesliga/club name="FCB"
```

### Record a game result

Clubs that haven't been registered yet are auto-registered when a game is submitted, so you can skip the registration step entirely.

```
http POST :8080/league/bundesliga/game \
    home:='{"name":"FCB"}' away:='{"name":"SVW"}' \
    round:=1 homeGoals:=6 awayGoals:=0
```

### Correct a game result

```
http PUT :8080/league/bundesliga/game \
    home:='{"name":"FCB"}' away:='{"name":"SVW"}' \
    round:=1 homeGoals:=3 awayGoals:=1
```

### View the league table

```
http GET :8080/league/bundesliga
```

Returns the clubs sorted by points (descending):

```json
[
    {"leagueId": "bundesliga", "team": "FCB", "gamesPlayed": 1, "points": 3},
    {"leagueId": "bundesliga", "team": "SVW", "gamesPlayed": 1, "points": 0}
]
```

### Load sample data

A set of Bundesliga match data is in `../misc/leaguedata.json`. The submit script from the Lagom example works unchanged, just point it at port 8080:

```bash
while read p; do
    echo $p | http POST :8080/league/bundesliga/game
done < leaguedata.json
```

Then check the standings:

```
http GET :8080/league/bundesliga
```

## Running the tests

The aggregate is tested in isolation using Axon's `AggregateTestFixture` — no Spring context required, just pure given/when/then:

```
mvn test
```
