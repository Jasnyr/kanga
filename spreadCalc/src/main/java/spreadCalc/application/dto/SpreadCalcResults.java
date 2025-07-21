package spreadCalc.application.dto;

import java.time.ZonedDateTime;

public record SpreadCalcResults(ZonedDateTime timestamp, SpreadRanking ranking) {}
