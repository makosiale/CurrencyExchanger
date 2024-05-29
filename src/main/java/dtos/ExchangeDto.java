package dtos;

import models.Currency;

import java.math.BigDecimal;

public record ExchangeDto(
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount
) {
}
