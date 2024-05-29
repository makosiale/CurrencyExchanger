package dtos;

import java.math.BigDecimal;

public record ExchangeRateDto(
        String base,
        String target,
        BigDecimal rate) { }
