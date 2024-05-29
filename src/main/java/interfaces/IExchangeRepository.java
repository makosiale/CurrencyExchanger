package interfaces;

        import dtos.ExchangeRateDto;
        import models.Currency;
        import models.ExchangeRate;

        import java.util.List;
        import java.util.Optional;
        import java.util.spi.CurrencyNameProvider;

public interface IExchangeRepository  {
    List<ExchangeRate> getAllExchangeRates();
    Optional<ExchangeRate> getExchangeRateByCodes(String base, String target);
    void create(ExchangeRateDto exchangeRateDto);
    ExchangeRate read(Long id);
    void update(ExchangeRateDto exchangeRateDto);
    void delete(ExchangeRate exchangeRate);
}