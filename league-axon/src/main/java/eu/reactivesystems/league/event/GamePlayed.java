package eu.reactivesystems.league.event;

import eu.reactivesystems.league.api.Game;

public record GamePlayed(String leagueId, Game game) {}
