package eu.reactivesystems.league.command;

import eu.reactivesystems.league.api.Club;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record AddClub(@TargetAggregateIdentifier String leagueId, Club club) {}
