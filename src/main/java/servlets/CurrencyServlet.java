package servlets;

import com.google.gson.Gson;
import models.Currency;
import repositories.CurrencyRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Optional;

public class CurrencyServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getPathInfo();
        if (url == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Код валюты отсутсвует в адресе");
            return;
        }

        CurrencyRepository currencyRepository = new CurrencyRepository();
        String code=url.substring(1);
        Optional<Currency> currency = currencyRepository.getCurrencyByCode(code);
        if (currency.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Валюта не найдена в БД");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(currency);
        resp.setContentType("application/json");
        PrintWriter printWriter =resp.getWriter();
        printWriter.print(json);
    }
}
