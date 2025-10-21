package com.example.CurrencyServ;

import org.javamoney.moneta.Money;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.springframework.stereotype.Service;

@Service
public class ForexService {
    private final ExchangeRateRepository repository;

    public ForexService(ExchangeRateRepository repository) {
        this.repository = repository;
    }

    public double convert(String from, String to, double amount) {
        ExchangeRate rate = repository.findByFromCurrencyAndToCurrency(from, to);
        if (rate == null) throw new IllegalArgumentException("Taxa não encontrada");
        CurrencyUnit fromUnit = Monetary.getCurrency(from);
        Money money = Money.of(amount, fromUnit);
        return money.multiply(rate.getRate()).getNumber().doubleValue();
    }

    public void addRate(String from, String to, double rate) {
        ExchangeRate er = new ExchangeRate();
        er.setFromCurrency(from);
        er.setToCurrency(to);
        er.setRate(rate);
        repository.save(er);
}
}
