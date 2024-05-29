package servlets;

import com.google.gson.Gson;
import dtos.ExchangeDto;
import models.ExchangeRate;
import repositories.ExchangeRepository;
import validators.Validator;

import javax.imageio.plugins.tiff.ExifGPSTagSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrency = req.getParameter("from");
        String targetCurrency = req.getParameter("to");
        String amount = req.getParameter("amount");

        if(!Validator.isDataValid(baseCurrency,targetCurrency,amount)){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Недостаточно данных в запросе");
            return;
        }
        ExchangeDto exchangeDto;
        ExchangeRepository exchangeRepository = new ExchangeRepository();
        Optional<ExchangeRate> abExchange = exchangeRepository.getExchangeRateByCodes(baseCurrency,targetCurrency);
        if(!abExchange.isEmpty()) {
            System.out.println("Обменный курс найде по валютной паре AB");
            ExchangeRate exchangeRate = abExchange.get();
            exchangeDto =  new ExchangeDto(exchangeRate.getBaseCurrency(),
                    exchangeRate.getTargetCurrency(),exchangeRate.getRate(), BigDecimal.valueOf(Long.parseLong(amount)),
                    BigDecimal.valueOf(Long.parseLong(amount)).multiply(exchangeRate.getRate()).setScale(2, RoundingMode.HALF_DOWN));
        }else{
            Optional<ExchangeRate> baExchange = exchangeRepository.getExchangeRateByCodes(targetCurrency,baseCurrency);
            if (!baExchange.isEmpty()){
                System.out.println("Обменный курс найден по валютной паре BA");
                ExchangeRate exchangeRate = baExchange.get();
                BigDecimal abRate = BigDecimal.ONE.divide(exchangeRate.getRate(),8, RoundingMode.HALF_DOWN);
                exchangeDto = new ExchangeDto(exchangeRate.getTargetCurrency(),
                        exchangeRate.getBaseCurrency(),abRate, BigDecimal.valueOf(Long.parseLong(amount)),
                        BigDecimal.valueOf(Long.parseLong(amount)).multiply(abRate).setScale(2,RoundingMode.HALF_DOWN));
            }else{
                Optional<ExchangeRate> usdAexchangeOpt = exchangeRepository.getExchangeRateByCodes("USD",baseCurrency);
                Optional<ExchangeRate> usdBexchangeOpt = exchangeRepository.getExchangeRateByCodes("USD",targetCurrency);
                if(usdAexchangeOpt.isEmpty() || usdAexchangeOpt.isEmpty()){
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND,"Невозможно получить обменный курс ," +
                            " в следствие отсутствия отсутсвия необходимых данных");
                    System.out.println("Обменный курс не найден");
                    return;
                }
                System.out.println("Обменный курс найден по валютной паре USD-A,USD-B");
                ExchangeRate usdAexchange = usdAexchangeOpt.get();
                ExchangeRate usdBexchange = usdBexchangeOpt.get();
                BigDecimal abRate = (usdBexchange.getRate().divide(usdAexchange.getRate(),8, RoundingMode.HALF_DOWN));
                exchangeDto = new ExchangeDto(usdAexchange.getTargetCurrency(),
                        usdBexchange.getTargetCurrency(),abRate, BigDecimal.valueOf(Long.parseLong(amount)),
                        BigDecimal.valueOf(Long.parseLong(amount)).multiply(abRate).setScale(2,RoundingMode.HALF_DOWN));
            }
        }
        Gson gson = new Gson();
        String json= gson.toJson(exchangeDto);
        resp.setContentType("application/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);
    }
}
