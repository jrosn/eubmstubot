package ru.rvsosn.eubmstubot;

import one.util.streamex.StreamEx;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.ActionType;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Runner {
    private static ConcurrentHashMap<String, Path> screenshots = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        System.setProperty("webdriver.gecko.driver", "/home/rvsosn/Downloads/geckodriver");

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> new EUBmstu().updateScreenshotsOfAllGroups(screenshots), 0, 20, TimeUnit.MINUTES);

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new MyBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static class MyBot extends TelegramLongPollingBot {

        @Override
        public void onUpdateReceived(Update update) {
            try {
                Message message = update.getMessage();
                if (message.getText().equals("/start"))
                    return;

                if (!message.getText().isEmpty()) {
                    sendScreenshot(message.getChatId(), message.getText().trim());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        private void sendScreenshot(long chatId, String group) throws TelegramApiException {
            SendChatAction sendChatAction = new SendChatAction();
            sendChatAction.setAction(ActionType.TYPING);
            sendChatAction.setChatId(chatId);
            sendApiMethod(sendChatAction);

            Optional<String> founded = StreamEx.ofKeys(screenshots)
                    .findAny(g -> g.toLowerCase().contains(group.toLowerCase()));

            if (founded.isPresent()) {
                SendPhoto sendScreenshot = new SendPhoto();
                sendScreenshot.setChatId(chatId);
                sendScreenshot.setNewPhoto(screenshots.get(founded.get()).toFile());
                sendPhoto(sendScreenshot);
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("Я не нашел группу :(");
                sendApiMethod(sendMessage);
            }
        }

        @Override
        public String getBotUsername() {
            return System.getProperty("ru.rvsosn.eubmstu.botusername");
        }

        @Override
        public String getBotToken() {
            return System.getProperty("ru.rvsosn.eubmstubot.botkey");
        }
    }
}
