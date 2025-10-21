package com.example.CurrencyServ;

import jakarta.persistence.*;

@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCurrency;
    private String toCurrency;
    private double rate;

    // Getters e setters
    public Long getId() { return id; }
    public String getFromCurrency() { return fromCurrency; }
    public void setFromCurrency(String fromCurrency) { this.fromCurrency = fromCurrency; }
    public String getToCurrency() { return toCurrency; }
    public void setToCurrency(String toCurrency) { this.toCurrency = toCurrency; }
    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate=rate;}
}
