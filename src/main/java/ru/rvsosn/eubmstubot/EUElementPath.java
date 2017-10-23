package ru.rvsosn.eubmstubot;

import org.openqa.selenium.By;

public enum EUElementPath {
    // Страница с авторизацией
    AUTH_LOGIN(By.xpath("//*[@id=\"username\"]")),
    AUTH_PASSWORD(By.xpath("//*[@id=\"password_input\"]")),
    AUTH_LOGIN_BTN(By.xpath("/html/body/div[1]/div/div/div[2]/div/div[2]/table/tbody/tr[2]/td/div/table/tbody/tr/td/div/form/table/tbody/tr[2]/td/div/table/tbody/tr[4]/td/input")),

    // Страница с выбором доступных действий после авторизации
    AFTER_AUTH_MENU_EU_ADDR(By.xpath("//*[@id=\"unicorn_form_url\"]")),
    AFTER_AUTH_MENU_BTN(By.xpath("//*[@id=\"browse_text\"]")),

    EU_MAIN_SESSIONS_BTN(By.xpath("//*[@id=\"canvas\"]/ul/li[2]/a")),

    EU_SESSION_GROUPS(By.cssSelector("#session-structure ul li > i > a"));

    private final By by;

    EUElementPath(By by) {
        this.by = by;
    }

    public By getBy() {
        return by;
    }
}
