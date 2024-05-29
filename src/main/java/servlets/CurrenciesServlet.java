package servlets;

import models.Currency;
import netscape.javascript.JSObject;
import repositories.CurrencyRepository;
import validators.Validator;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;


public class CurrenciesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CurrencyRepository currencyRepository = new CurrencyRepository();
        List<Currency> currencies = currencyRepository.getAllCurrencies();
        if (currencies.isEmpty()) {
            System.out.println("Нет валют в базе данных");
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(currencies);
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("full_name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        Validator validator = new Validator();
        if (!validator.isDataValid(code, name, sign)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Не хватает данных в запросе");
            return;
        }
        CurrencyRepository currencyRepository = new CurrencyRepository();
        Optional<Currency> currency = currencyRepository.getCurrencyByCode(code);
        if (currency.isPresent()) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Такая валюта уже добавлена");
            return;
        }
        currencyRepository.create(new Currency(code, name, sign));
    }
}
