package eu.reactivesystems.league.api;

public record Game(Club home, Club away, int round, int homeGoals, int awayGoals) {}
