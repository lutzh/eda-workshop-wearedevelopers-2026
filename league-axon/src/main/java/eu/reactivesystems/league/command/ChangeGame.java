package eu.reactivesystems.league.command;

import eu.reactivesystems.league.api.Game;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ChangeGame(@TargetAggregateIdentifier String leagueId, Game game) {}
