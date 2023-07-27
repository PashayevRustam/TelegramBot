package org.example;

import org.apache.tools.ant.dispatch.DispatchUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.tools.ant.dispatch.DispatchUtils.*;

public class MyRunnable implements Runnable {
    MyTelegramBot bot = new MyTelegramBot();
    Long chatId;

    public MyRunnable(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public void run() {
        System.out.println("Chat ID: " + chatId);
        try {
            while (true) {
                Document doc = bot.getAnimeSchedule("/api/schedule");
                // Задержка на 10 секунд
                TimeUnit.SECONDS.sleep(10);
                // Ваш код, который должен выполниться после задержки
                bot.infiniteLoop(doc, "raspis raspis_fixed");
                System.out.println("Chat ID 123123123: " + chatId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Chat ID поарошпа: " + chatId);
        }
    }
}
