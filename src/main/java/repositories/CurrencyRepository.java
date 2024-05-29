package repositories;

import dbc.DatabaseConnection;
import interfaces.ICurrencyRepository;
import models.Currency;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CurrencyRepository implements ICurrencyRepository {

    @Override
    public Currency read(Long id) {
        return null;
    }

    @Override
    public void update(Currency currency) {

    }

    @Override
    public void delete(Currency currency) {

    }

    @Override
    public List<Currency> getAllCurrencies() {
        String request ="SELECT * FROM currencies";
        List<Currency> currencies = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()){
                currencies.add(new Currency(
                        resultSet.getLong("id"),
                        resultSet.getString("full_name"),
                        resultSet.getString("code"),
                        resultSet.getString("sign")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return currencies;
    }

    @Override
    public void create(Currency currency) {
        String request = "insert into currencies(code,full_name,sign)" +
                "VALUES('" + currency.getCode()+"','" + currency.getFullName()+"','" +
                currency.getSign()+"');";
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            Statement statement= connection.createStatement();
            statement.executeUpdate(request);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public Optional<Currency> getCurrencyByCode(String code) {
        Currency currency =null;
        String request = "SELECT * FROM currencies WHERE code ='"+code+"';";
        try{
            Connection connection= DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);
            if (resultSet.next()){
                currency = new Currency(
                        Long.parseLong(resultSet.getString("id")),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign")
                );
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(currency);
    }
}