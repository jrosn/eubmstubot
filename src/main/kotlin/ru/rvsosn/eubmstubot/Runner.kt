package ru.rvsosn.eubmstubot

import org.openqa.selenium.firefox.FirefoxDriver
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException
import ru.rvsosn.eubmstubot.eubmstu.EUBmstuApiExecutor

fun main(args: Array<String>) {
    ApiContextInitializer.init()
    val telegramBotsApi = TelegramBotsApi()
    try {
        val apiExecutor = EUBmstuApiExecutor(
                FirefoxDriver(),
                System.getProperty("ru.rvsosn.eubmstubot.eulogin"),
                System.getProperty("ru.rvsosn.eubmstubot.eupassword"))
        apiExecutor.warmingCache()

        telegramBotsApi.registerBot(EUBmstuBot(apiExecutor))
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }

}