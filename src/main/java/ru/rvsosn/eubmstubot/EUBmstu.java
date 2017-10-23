package ru.rvsosn.eubmstubot;

import one.util.streamex.StreamEx;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.concurrent.TimeUnit.SECONDS;

public class EUBmstu {
    private static final String WEBVPN_ADDR = "https://webvpn.bmstu.ru/+CSCOE+/logon.html";
    private static final String EU_ADDR = "eu.bmstu.ru";

    private final String login;
    private final String password;

    EUBmstu() {
        this.login = System.getProperty("ru.rvsosn.eubmstubot.eulogin");
        this.password = System.getProperty("ru.rvsosn.eubmstubot.eupassword");
    }

    private static List<WebElement> getAllGroups(WebDriver driver) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(30, SECONDS)
                .pollingEvery(5, SECONDS)
                .ignoring(NoSuchElementException.class);

        return wait.until(d -> d.findElements(EUElementPath.EU_SESSION_GROUPS.getBy()));
    }

    private static String removeToMuchSpaces(String str) {
        StringBuilder builder = new StringBuilder();

        boolean isLastSpace = true;
        for (char i : str.toCharArray()) {
            if (!Character.isWhitespace(i)) {
                builder.append(i);
                isLastSpace = false;
            } else if (!isLastSpace) {
                builder.append(' ');
                isLastSpace = true;
            }
        }

        return builder.toString().trim();
    }

    public Map<String, Path> updateScreenshotsOfAllGroups(ConcurrentHashMap<String, Path> screenshots) {
        System.setProperty("webdriver.gecko.driver", "/home/rvsosn/Downloads/geckodriver");

        FirefoxDriver driver = new FirefoxDriver();

        driver.get(WEBVPN_ADDR);

        driver.findElement(EUElementPath.AUTH_LOGIN.getBy())
                .sendKeys(login);
        driver.findElement(EUElementPath.AUTH_PASSWORD.getBy())
                .sendKeys(password);
        driver.findElement(EUElementPath.AUTH_LOGIN_BTN.getBy())
                .click();
        driver.findElement(EUElementPath.AFTER_AUTH_MENU_EU_ADDR.getBy())
                .sendKeys(EU_ADDR);
        driver.findElement(EUElementPath.AFTER_AUTH_MENU_BTN.getBy())
                .click();
        driver.findElement(EUElementPath.EU_MAIN_SESSIONS_BTN.getBy())
                .click();

        List<EUSessionGroup> groups = StreamEx.of(getAllGroups(driver))
                .map(webElement -> {
                    String name = removeToMuchSpaces(webElement.getAttribute("innerHTML"));
                    String addr = webElement.getAttribute("href");
                    return new EUSessionGroup(name, addr);
                })
                .toList();

        StreamEx.of(groups)
                .forEach(group -> {
                    driver.navigate().to(group.addr);
                    driver.manage().window().maximize();
                    screenshots.put(group.name, driver.getScreenshotAs(OutputType.FILE).toPath());
                });

        driver.quit();

        return screenshots;
    }

    static class EUSessionGroup {
        private String name;
        private String addr;

        private EUSessionGroup(String name, String addr) {
            this.name = name;
            this.addr = addr;
        }

        @Override
        public String toString() {
            return String.format("%s %s", name, addr);
        }
    }
}
