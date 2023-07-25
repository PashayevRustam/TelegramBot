package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MyTask implements Runnable {
    private volatile boolean stopRequested = false;
    Document doc;
    long chatId;
    String anime;
    String sendText;

    public MyTask(Document doc, long chatId, String anime){
      this.doc = doc;
      this.chatId = chatId;
      this.anime = anime;
    }

    public void setAnime(String anime) {
        this.anime = anime;
    }

    public void setSendText(String sendText) {
        this.sendText = sendText;
    }

    public void setDoc(Document doc){
        this.doc = doc;
    }

    public String getSendText() {
        return sendText;
    }

    public void stop() {
        stopRequested = true;
    }

    @Override
    public void run() {
        while (!stopRequested) {
            // Бесконечный цикл с использованием TimeUnit
            try {
                TimeUnit.SECONDS.sleep(1); // Пауза на 1 секунду
                setDoc(getAnimeSchedule());
                infiniteLoop(this.doc, this.chatId, "raspis raspis_fixed", "start");
            } catch (InterruptedException e) {
                // Обработка прерывания, если необходимо
            }
        }
    }

    public Document getAnimeSchedule() {
        Document doc;
        try {
            // Получаем HTML страницу
            doc = Jsoup.connect("https://v2.vost.pw/api/schedule").get();
        } catch (IOException e) {
            e.printStackTrace();
            doc = null;
        }
        return doc;
    }

    public void infiniteLoop(Document doc, long chatId, String listName, String command) {
        try {
            // Отправка ответного сообщения с расписанием аниме
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            //sendMessage.setText(schedule);
            Elements elements = doc.getElementsByClass(listName);
            String[] newText = elements.text().split("\\[");

            if (!(newText[0].trim().equals(anime.trim()))) {
                setAnime(newText[0]);
                setSendText(newText[0] + " последнее обновление на сайте");
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
