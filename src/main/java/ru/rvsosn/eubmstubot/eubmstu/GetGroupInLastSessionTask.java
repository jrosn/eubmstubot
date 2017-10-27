package ru.rvsosn.eubmstubot.eubmstu;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;

public class GetGroupInLastSessionTask extends AbstractTask<GetGroupInLastSessionResult> {
    private final String groupName;

    public GetGroupInLastSessionTask(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public GetGroupInLastSessionResult execute(EUBmstuApiExecutor executor, Context context) {
        GetAllGroupsInLastSessionResult groupsInLastSession =
                executeOther(executor, context, new GetAllGroupsInLastSessionTask());

        String groupUrl = groupsInLastSession.getGroupUrl(groupName);

        // Temporary
        FirefoxDriver driver = (FirefoxDriver) context.getDriver();
        driver.get(groupUrl);
        File screenshot = driver.getScreenshotAs(OutputType.FILE);

        return new GetGroupInLastSessionResult(screenshot.toPath());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetGroupInLastSessionTask that = (GetGroupInLastSessionTask) o;

        return groupName.equals(that.groupName);
    }

    @Override
    public int hashCode() {
        return groupName.hashCode();
    }
}
