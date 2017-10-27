package ru.rvsosn.eubmstubot.eubmstu;

import java.nio.file.Path;

public class GetGroupInLastSessionResult {
    private final Path screenshotFile;

    GetGroupInLastSessionResult(Path screenshotFile) {
        this.screenshotFile = screenshotFile;
    }

    public Path getScreenshotFile() {
        return screenshotFile;
    }
}
