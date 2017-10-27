package ru.rvsosn.eubmstubot.eubmstu;

import org.openqa.selenium.WebDriver;

public class Context {
    private final WebDriver driver;
    private final String euLogin;
    private final String euPassword;

    public Context(WebDriver driver, String euLogin, String euPassword) {
        this.driver = driver;
        this.euLogin = euLogin;
        this.euPassword = euPassword;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getEuLogin() {
        return euLogin;
    }

    public String getEuPassword() {
        return euPassword;
    }
}
