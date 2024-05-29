package interfaces;

import models.Currency;

import java.util.List;
import java.util.Optional;
import java.util.spi.CurrencyNameProvider;

public interface ICurrencyRepository {
    List<Currency> getAllCurrencies();
    void create(Currency currency);
    Currency read(Long id);
    void update(Currency currency);
    void delete(Currency currency);
    Optional<Currency> getCurrencyByCode(String code);
}