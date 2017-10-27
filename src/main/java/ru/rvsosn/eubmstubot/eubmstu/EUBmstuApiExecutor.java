package ru.rvsosn.eubmstubot.eubmstu;

import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class EUBmstuApiExecutor {
    private final Context context;
    private String defaultPageUrl;
    private Map<ITask, Object> resultCache;

    public EUBmstuApiExecutor(WebDriver driver, String euLogin, String euPassword) {
        this.context = new Context(driver, euLogin, euPassword);
        this.resultCache = new HashMap<>();
    }

    /**
     * Выполняет задачу
     */
    public synchronized <T> T executeTask(ITask<? extends T> task) {
        Object cachedResult = resultCache.get(task);
        if (cachedResult != null) {
            //noinspection unchecked
            return (T) cachedResult;
        }

        init();
        T result = task.execute(this, context);
        resultCache.put(task, result);
        return result;
    }

    public void warmingCache() {
        executeTask(new GetAllGroupsInLastSessionTask());
    }

    /**
     * Инициализирует "запускатор".
     */
    private void init() {
        WebDriver driver = context.getDriver();

        if (defaultPageUrl == null) {
            // Переходим на страницу авторизации WebVPN
            String WEBVPN_ADDR = "https://webvpn.bmstu.ru/+CSCOE+/logon.html";
            driver.get(WEBVPN_ADDR);

            // Заполняем необходимые поля и жмякаем на кнопочку
            driver.findElement(EUElementPath.AUTH_LOGIN.getBy())
                    .sendKeys(context.getEuLogin());
            driver.findElement(EUElementPath.AUTH_PASSWORD.getBy())
                    .sendKeys(context.getEuPassword());
            driver.findElement(EUElementPath.AUTH_LOGIN_BTN.getBy())
                    .click();

            // Перешли в WebVPN меню
            String EU_ADDR = "eu.bmstu.ru";
            driver.findElement(EUElementPath.AFTER_AUTH_MENU_EU_ADDR.getBy())
                    .sendKeys(EU_ADDR);
            driver.findElement(EUElementPath.AFTER_AUTH_MENU_BTN.getBy())
                    .click();

            defaultPageUrl = driver.getCurrentUrl();
        }

        driver.get(defaultPageUrl);
    }

    public void close() {
        context.getDriver().close();
    }
}
