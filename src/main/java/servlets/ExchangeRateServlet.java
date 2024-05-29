package servlets;

import com.google.gson.Gson;
import dtos.ExchangeRateDto;
import models.ExchangeRate;
import repositories.ExchangeRepository;
import validators.Validator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Optional;

public class ExchangeRateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getPathInfo();
        if (url == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Коды валют отсутсвует в адресе");
            return;
        }

        ExchangeRepository exchangeRepository = new ExchangeRepository();
        String base  = url.substring(1,4);
        String target = url.substring(4,7);
        Optional<ExchangeRate> exchangeRate = exchangeRepository.getExchangeRateByCodes(base,target);

        if (exchangeRate.isEmpty()){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Обменный курс не найден в БД");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(exchangeRate);
        resp.setContentType("application/json");
        PrintWriter printWriter =resp.getWriter();
        printWriter.print(json);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equals("PATCH"))
            this.doPatch(req,resp);
        else
            super.service(req, resp);
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException,ServletException {
        System.out.println(req.getMethod());
        String rateStr = req.getParameter("rate");
        String url = req.getPathInfo();
        String base=url.substring(1,4);
        String target=url.substring(4,7);
        if (!Validator.isDataValid(base,target,rateStr)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Отсутствует нужное поле формы");
            return;
        }

        ExchangeRepository exchangeRepository = new ExchangeRepository();
        if(exchangeRepository.getExchangeRateByCodes(base,target).isEmpty()){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,"Валютная пара отсутствует в базе данных");
            return;
        }

        exchangeRepository.update(new ExchangeRateDto(base,target,new BigDecimal(rateStr)));
    }

}
