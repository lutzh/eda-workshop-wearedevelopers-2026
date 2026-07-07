package eu.reactivesystems.league.event;

import eu.reactivesystems.league.api.Club;

public record ClubRegistered(String leagueId, Club club) {}
