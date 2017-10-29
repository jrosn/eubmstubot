package ru.rvsosn.eubmstubot.eubmstu;

import org.openqa.selenium.*;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetGroupInLastSessionTask extends AbstractTask<GetGroupInLastSessionResult> {
    private final String groupName;

    public GetGroupInLastSessionTask(String groupName) {
        this.groupName = groupName;
    }

    private static final int LEFT_INDENT = 10;
    private static final int RIGHT_INDENT = 10;
    private static final int TOP_INDENT = 20;
    private static final int BOTTOM_INDENT = 10;
    private static final int WHITE_COLOR = 255 * 65536 + 255 * 256 + 255;
    private static final int LEFT_TEXT_INDENT = LEFT_INDENT;
    private static final int TOP_TEXT_INDENT = 15;

    private static Path captureElementBitmap(WebDriver driver, WebElement element, String description) throws IOException {
        // Делаем скриншот страницы
        Path screeshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
        BufferedImage img = ImageIO.read(screeshotFile.toFile());

        System.out.println(img.getWidth() + " " + img.getHeight());

        // Получаем размеры элемента
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();

        // Прямоугольник с размерами элемента
        Point p = element.getLocation();

        // Режем
        System.out.println(p.getX() + " " + p.getY());
        BufferedImage dest = img.getSubimage(p.getX(), p.getY(), width, height);
        dest = normalizeScreenshot(description, dest);
        ImageIO.write(dest, "png", screeshotFile.toFile());
        return screeshotFile;
    }

    private static BufferedImage normalizeScreenshot(String text, BufferedImage source) {
        BufferedImage target = new BufferedImage(source.getWidth() + LEFT_INDENT + RIGHT_INDENT, source.getHeight() + TOP_INDENT + BOTTOM_INDENT, source.getType());
        for (int w = 0; w < target.getWidth(); w++) {
            for (int h = 0; h < target.getHeight(); h++) {
                target.setRGB(w, h, WHITE_COLOR);
            }
        }

        for (int w = 0; w < source.getWidth(); w++) {
            for (int h = 0; h < source.getHeight(); h++) {
                target.setRGB(w + LEFT_INDENT, h + TOP_INDENT, source.getRGB(w, h));
            }
        }

        Graphics gr = target.getGraphics();
        gr.setFont(Font.getFont(Font.SANS_SERIF));
        gr.setColor(Color.BLACK);
        gr.drawString(text, LEFT_TEXT_INDENT, TOP_TEXT_INDENT);

        return target;
    }

    @Override
    public GetGroupInLastSessionResult execute(EUBmstuApiExecutor executor, Context context) {
        GetAllGroupsInLastSessionResult groupsInLastSession =
                executeOther(executor, context, new GetAllGroupsInLastSessionTask());

        String groupUrl = groupsInLastSession.getGroupUrl(groupName);
        context.getDriver().get(groupUrl);

        try {
            Path screenshot = captureElementBitmap(
                    context.getDriver(),
                    context.getDriver().findElement(By.xpath("/html/body/div[1]/div[7]/div/div[2]/span[2]/div/table")),
                    String.format("%s (%s)", groupName, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            );
            return new GetGroupInLastSessionResult(screenshot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
