package eu.reactivesystems.league.event;

import eu.reactivesystems.league.api.Game;

public record ResultRevoked(String leagueId, Game game) {}
