package ru.rvsosn.eubmstubot;

import org.openqa.selenium.firefox.FirefoxDriver;
import ru.rvsosn.eubmstubot.eubmstu.EUBmstuApiExecutor;
import ru.rvsosn.eubmstubot.eubmstu.GetAllGroupsInLastSessionResult;
import ru.rvsosn.eubmstubot.eubmstu.GetAllGroupsInLastSessionTask;
import ru.rvsosn.eubmstubot.eubmstu.GetGroupInLastSessionTask;

import java.util.ArrayList;

public class Runner {

    public static void main(String[] args) {
        EUBmstuApiExecutor apiExecutor = new EUBmstuApiExecutor(
                new FirefoxDriver(),
                System.getProperty("ru.rvsosn.eubmstubot.eulogin"),
                System.getProperty("ru.rvsosn.eubmstubot.eupassword"));

        GetAllGroupsInLastSessionResult result = apiExecutor.executeTask(new GetAllGroupsInLastSessionTask());

        for (int i = 0; i < 100; i++) {
            apiExecutor.executeTask(new GetGroupInLastSessionTask(new ArrayList<>(result.getGroupNames()).get(0)));
        }

        apiExecutor.close();
    }
}
