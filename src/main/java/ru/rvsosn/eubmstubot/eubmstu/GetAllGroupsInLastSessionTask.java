package ru.rvsosn.eubmstubot.eubmstu;

import one.util.streamex.StreamEx;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class GetAllGroupsInLastSessionTask extends AbstractTask<GetAllGroupsInLastSessionResult> {

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

    @Override
    public GetAllGroupsInLastSessionResult execute(EUBmstuApiExecutor executor, Context context) {
        WebDriver driver = context.getDriver();
        driver.findElement(EUElementPath.EU_MAIN_SESSIONS_BTN.getBy()).click();

        List<WebElement> elements = driver.findElements(EUElementPath.EU_SESSION_GROUPS.getBy());

        return new GetAllGroupsInLastSessionResult(normalize(elements));
    }

    private Map<String, String> normalize(List<WebElement> elements) {
        return StreamEx.of(elements)
                .mapToEntry(
                        webElement -> removeToMuchSpaces(webElement.getAttribute("innerHTML")),
                        webElement -> webElement.getAttribute("href"))
                .distinctKeys()
                .toImmutableMap();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
