package ru.rvsosn.eubmstubot;

import one.util.streamex.StreamEx;
import org.telegram.telegrambots.api.methods.ActionType;
import org.telegram.telegrambots.api.methods.send.SendChatAction;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.rvsosn.eubmstubot.eubmstu.EUBmstuApiExecutor;
import ru.rvsosn.eubmstubot.eubmstu.GetAllGroupsInLastSessionTask;
import ru.rvsosn.eubmstubot.eubmstu.GetGroupInLastSessionTask;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class EuBmstuBot extends TelegramLongPollingBot {
    final String HELLO =
            "Добро пожаловать!\n\n" +
                    "Отправь мне название группы и я тебе кину скрин с результатами сессии";
    private final EUBmstuApiExecutor executor;

    public EuBmstuBot(EUBmstuApiExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            try {
                handleMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return System.getProperty("ru.rvsosn.eubmstubot.botusername");
    }

    @Override
    public String getBotToken() {
        return System.getProperty("ru.rvsosn.eubmstubot.botkey");
    }

    private void handleMessage(Message message) throws TelegramApiException {
        String normalized = message.getText().trim().toLowerCase();
        if (normalized.equals("/start")) {
            new SendMessageBuilder()
                    .setChatId(message.getChatId())
                    .setText(HELLO)
                    .send(this);
        } else {
            Set<String> groupNames = executor.executeTask(new GetAllGroupsInLastSessionTask()).getGroupNames();
            Optional<String> foundedGroupName = StreamEx.of(groupNames)
                    .findAny(g -> g.toLowerCase().contains(normalized.toLowerCase()));

            if (foundedGroupName.isPresent()) {
                new SendMessageBuilder()
                        .setChatId(message.getChatId())
                        .setText("Сейчас кину результаты последней сессии для группы " + foundedGroupName.get())
                        .send(this);
                new ChatActionBuilder()
                        .setChatId(message.getChatId())
                        .setActionType(ActionType.UPLOADPHOTO)
                        .send(this);
                Path screenshotFile = executor.executeTask(new GetGroupInLastSessionTask(foundedGroupName.get())).getScreenshotFile();
                new SendPhotoBuilder()
                        .setChatId(message.getChatId())
                        .setImageFile(screenshotFile)
                        .send(this);
            } else {
                new SendMessageBuilder()
                        .setChatId(message.getChatId())
                        .setText("У-у-ужас, я группу не смог найти :(")
                        .send(this);
            }
        }
    }
}

class SendMessageBuilder {
    private long chatId;
    private String text;

    public SendMessageBuilder setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public SendMessageBuilder setText(String text) {
        this.text = text;
        return this;
    }

    public void send(EuBmstuBot bot) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        bot.sendMessage(sendMessage);
    }
}

class ChatActionBuilder {
    private long chatId;
    private ActionType type;

    public ChatActionBuilder setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public ChatActionBuilder setActionType(ActionType type) {
        this.type = type;
        return this;
    }

    public void send(EuBmstuBot bot) throws TelegramApiException {
        SendChatAction chatAction = new SendChatAction();
        chatAction.setChatId(chatId);
        chatAction.setAction(type);
        bot.sendChatAction(chatAction);
    }
}

class SendPhotoBuilder {
    private long chatId;
    private Path imageFile;

    public SendPhotoBuilder setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    public SendPhotoBuilder setImageFile(Path file) {
        this.imageFile = file;
        return this;
    }

    public void send(EuBmstuBot bot) throws TelegramApiException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setNewPhoto(imageFile.toFile());
        bot.sendPhoto(sendPhoto);
    }
}
