package servlets;

import com.google.gson.Gson;
import dtos.ExchangeRateDto;
import models.ExchangeRate;
import repositories.CurrencyRepository;
import repositories.ExchangeRepository;
import validators.Validator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ExchangeRepository exchangeRepository = new ExchangeRepository();
        List<ExchangeRate> exchangeRates = exchangeRepository.getAllExchangeRates();
        if (exchangeRates.isEmpty()){
            System.out.println("Нет обменных курсов в БД");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(exchangeRates);
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String base = req.getParameter("baseCurrencyCode");
        String target = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");

        if(!Validator.isDataValid(base,target,rateStr)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Не хватает данных в запросе");
            return;
        }

        CurrencyRepository currencyRepository = new CurrencyRepository();
        if(currencyRepository.getCurrencyByCode(base).isEmpty()
        || currencyRepository.getCurrencyByCode(target).isEmpty()){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,"Одна (или обе) валюта из валютной пары не существует в БД");
            return;
        }

        ExchangeRepository exchangeRepository = new ExchangeRepository();
        Optional<ExchangeRate> exchangeRate = exchangeRepository.getExchangeRateByCodes(base,target);
        if (!exchangeRate.isEmpty()){
            resp.sendError(HttpServletResponse.SC_CONFLICT,"Валютная пара с таким кодом уже существует");
        }

        exchangeRepository.create(new ExchangeRateDto(base,target,new BigDecimal(rateStr)));
    }


}
