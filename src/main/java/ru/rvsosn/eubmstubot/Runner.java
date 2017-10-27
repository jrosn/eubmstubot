package ru.rvsosn.eubmstubot;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.rvsosn.eubmstubot.eubmstu.EUBmstuApiExecutor;

public class Runner {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            EUBmstuApiExecutor apiExecutor = new EUBmstuApiExecutor(
                    new FirefoxDriver(),
                    System.getProperty("ru.rvsosn.eubmstubot.eulogin"),
                    System.getProperty("ru.rvsosn.eubmstubot.eupassword"));
            apiExecutor.warmingCache();

            telegramBotsApi.registerBot(new EuBmstuBot(apiExecutor));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
