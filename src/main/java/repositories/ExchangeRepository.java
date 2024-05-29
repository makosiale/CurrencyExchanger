package repositories;

import dbc.DatabaseConnection;
import dtos.ExchangeRateDto;
import interfaces.IExchangeRepository;
import models.Currency;
import models.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRepository implements IExchangeRepository {

    @Override
    public List<ExchangeRate> getAllExchangeRates() {
        String request ="SELECT rate,b.id as baseId, b.code as baseCode, " +
                "b.full_name as baseFullName, b.sign as baseSign,\n" +
                "\t   t.id as targetId,t.code as targetCode, " +
                "t.full_name as targetFullName, t.sign as targetSign\n" +
                "FROM exchange_rates er\n" +
                "\tJOIN currencies as b ON er.base_currency_id = b.id\n" +
                "\tJOIN currencies as t ON er.target_currency_id = t.id";
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);
            while(resultSet.next()){
                exchangeRates.add(new ExchangeRate(new Currency(
                        resultSet.getLong("baseId"),
                        resultSet.getString("baseCode"),
                        resultSet.getString("baseFullName"),
                        resultSet.getString("baseSign")),
                        new Currency(
                                resultSet.getLong("targetId"),
                                resultSet.getString("targetCode"),
                                resultSet.getString("targetFullName"),
                                resultSet.getString("targetSign")),
                        resultSet.getBigDecimal("rate")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> getExchangeRateByCodes(String base, String target) {
        String request = "SELECT rate,b.id as baseId, b.code as baseCode," +
                " b.full_name as baseFullName, b.sign as baseSign,\n" +
                "\t   t.id as targetId,t.code as targetCode, " +
                "t.full_name as targetFullName, t.sign as targetSign\n" +
                "FROM exchange_rates er\n" +
                "\tJOIN currencies as b ON er.base_currency_id = b.id\n" +
                "\tJOIN currencies as t ON er.target_currency_id = t.id\n" +
                "WHERE b.code ='"+base+"' and t.code='"+target+"'";
        ExchangeRate exchangeRate = null;
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()){
                exchangeRate = new ExchangeRate(new Currency(
                        resultSet.getLong("baseId"),
                        resultSet.getString("baseCode"),
                        resultSet.getString("baseFullName"),
                        resultSet.getString("baseSign")),
                        new Currency(
                                resultSet.getLong("targetId"),
                                resultSet.getString("targetCode"),
                                resultSet.getString("targetFullName"),
                                resultSet.getString("targetSign")),
                        resultSet.getBigDecimal("rate"));
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(exchangeRate);
    }

    @Override
    public void create(ExchangeRateDto exchangeRateDto) {
        String request = "INSERT INTO exchange_rates(base_currency_id,target_currency_id,rate)\n" +
                "VALUES((SELECT id\n" +
                "\t  FROM currencies\n" +
                "\t  WHERE code=?),\n" +
                "\t  (SELECT id\n" +
                "\t  FROM currencies\n" +
                "\t  WHERE code=?),\n" +
                "\t  ?)";
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setString(1, exchangeRateDto.base());
            preparedStatement.setString(2,exchangeRateDto.target());
            preparedStatement.setBigDecimal(3,exchangeRateDto.rate());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ExchangeRate read(Long id) {
        return null;
    }

    @Override
    public void update(ExchangeRateDto exchangeRateDto) {
        String request = "UPDATE exchange_rates\n" +
                "SET rate= ?\n" +
                "WHERE base_currency_id IN(SELECT id\n" +
                "\t\t\t\t\t\t  FROM currencies\n" +
                "\t\t\t\t\t\t  WHERE code = ?)\n" +
                "\tand target_currency_id IN(SELECT id\n" +
                "\t\t\t\t\t\t\t  FROM currencies\n" +
                "\t\t\t\t\t\t\t  WHERE code = ?)";
        try{
            Connection connection =DatabaseConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(request);
            preparedStatement.setBigDecimal(1,exchangeRateDto.rate());
            preparedStatement.setString(2,exchangeRateDto.base());
            preparedStatement.setString(3,exchangeRateDto.target());
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ExchangeRate exchangeRate) {

    }

}
