package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import javax.print.Doc;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot {

    public static final String DOMEN = "https://v2.vost.pw";
    public static List<String> anime = new ArrayList<>();
    public static final String STOPCOMMAND = "stop";

    @Override
    public String getBotUsername() {
        // Укажите имя вашего бота
        return "ListOfAnimeBot";
    }

    @Override
    public String getBotToken() {
        // Укажите токен вашего бота
        return "6363995416:AAG5l_jHZN8FeT5VYPD6nMDOY5vlLeiWLKQ";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Получение сообщения от пользователя
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        Document doc = getAnimeSchedule("/api/schedule");

        if (doc == null) {
            return;
        }

        commandName(doc, chatId, message);
        saveLog(update, chatId, message);
    }

    public void saveLog(Update update, long chatId, String message) {
        String filePath = "C:\\Users\\rpashayev\\Downloads\\TelegramBot\\log.txt";

        try {
            // Создаем объект File для представления файла по указанному пути
            File file = new File(filePath);

            // Создаем объект FileWriter для записи в файл
            FileWriter fileWriter = new FileWriter(file, true);

            // Создаем объект BufferedWriter для более эффективной записи данных
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Записываем текст в файл
            bufferedWriter.write(update.getMessage().getFrom().getFirstName() + " (@" + update.getMessage().getFrom().getUserName() + ") " + chatId + ": " + message + "\n");

            // Закрываем BufferedWriter
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Document getAnimeSchedule(String url) {
        Document doc;
        try {
            // Получаем HTML страницу
            doc = Jsoup.connect(DOMEN + url).get();
        } catch (IOException e) {
            e.printStackTrace();
            doc = null;
        }
        return doc;
    }

    private void sendListOfAnime(Document doc, long chatId, String listName) {
        try {
            // Отправка ответного сообщения с расписанием аниме
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            //sendMessage.setText(schedule);
            Element element = doc.getElementById(listName);

            //sendLink(doc, listName);

            // Проверяем, что элемент не является null перед извлечением содержимого
            if (element != null) {
                sendListMessage(element.text(), sendMessage, listName);
            }

            //execute(sendMessage);
            Message message = execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: 12.07.2023 Получение ссылки на конкретный тайтл
    public String sendLink(Document doc, String listName, String anime) {
        Element element = doc.getElementById(listName);
        String link = "";
        anime = anime.trim();
        //Elements elements = doc.getElementsByClass("epizode s1");
        String regex = "<a\\s+href=\"([^\"]+)\">(.*?)<\\/a>";
        // Создайте объект Pattern на основе регулярного выражения
        Pattern pattern = Pattern.compile(regex);
        // Создайте объект Matcher для поиска соответствий
        Matcher matcher = pattern.matcher(element.html());
        // Проверяем, есть ли соответствие
        while (matcher.find()) {
            // Получаем найденную ссылку
            String title = matcher.group(2);
            String[] newTitle = title.split("~ \\(\\d{2}:\\d{2}\\)");
            String text = newTitle[0].trim();
            if (text.equals(anime)) {
                link = matcher.group(1);
                break;
            }
            //System.out.println(title + "\n" + link + "\n");
        }
        return link;
    }

    public void sendListMessage(String text, SendMessage sendMessage, String listName) {
        String[] newText = text.split("~ \\(\\d{2}:\\d{2}\\)");

        String list = "";
        for (int i = 0; i < newText.length; i++) {
            //list += "[" + ((i + 1) + ". " + newText[i] + "\n") + "]" + "(" + DOMEN + sendLink(listName, newText[i]) + ")";
            list += (i + 1) + ". " + newText[i] + "\n";
        }
        sendMessage.setText(list);
        //sendMessage.enableMarkdown(true);    //разрешает отправлять предложения ввиде гиперссылки

    }

    public void commandName(Document doc, long chatId, String message) {
        if (message.equalsIgnoreCase("/monday")) {
            sendListOfAnime(doc, chatId, "raspisMon");
        } else if (message.equalsIgnoreCase("/tuesday")) {
            sendListOfAnime(doc, chatId, "raspisTue");
        } else if (message.equalsIgnoreCase("/wednesday")) {
            sendListOfAnime(doc, chatId, "raspisWed");
        } else if (message.equalsIgnoreCase("/thursday")) {
            sendListOfAnime(doc, chatId, "raspisThu");
        } else if (message.equalsIgnoreCase("/friday")) {
            sendListOfAnime(doc, chatId, "raspisFri");
        } else if (message.equalsIgnoreCase("/saturday")) {
            sendListOfAnime(doc, chatId, "raspisSat");
        } else if (message.equalsIgnoreCase("/sunday")) {
            sendListOfAnime(doc, chatId, "raspisSun");
        } else if (message.equalsIgnoreCase("/start")) {
            // Отправка ответного сообщения
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(returnText(1));

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if (message.equalsIgnoreCase("/receivenotifications")) {
            runnable(chatId);
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(returnText(2));

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void runnable(long chatId) {
        Runnable myTask = () -> {
            try {
                while (!("start" == STOPCOMMAND)) {
                    Document doc = getAnimeSchedule("/api/schedule");
                    // Задержка на 30 минут
                    TimeUnit.SECONDS.sleep(10);
                    // Ваш код, который должен выполниться после задержки
                    infiniteLoop(doc, chatId, "raspis raspis_fixed", "start");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        // Создаем объект потока и передаем ему задачу
        Thread thread = new Thread(myTask);
        // Запускаем поток
        thread.start();
    }

    public void infiniteLoop(Document doc, long chatId, String listName, String command) {
        try {
            // Отправка ответного сообщения с расписанием аниме
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            //sendMessage.setText(schedule);
            Elements elements = doc.getElementsByClass(listName);

            List<String> animeNames = new ArrayList<>();
            String regex = "([^/]+)\\s/\\s[^\\[]+";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(elements.text());

            while (matcher.find()) {
                String name = matcher.group(1).trim();
                animeNames.add(getAnimeNameWithoutNumbers(name));
                if (animeNames.size() == 6) {
                    break;
                }
            }

            if (!(animeNames.equals(anime))) {
                anime = animeNames;
                String list = "Последние обновления\n";
                for (int i = 0; i < animeNames.size(); i++) {
                    list += (i + 1) + ". " + animeNames.get(i) + "\n";
                }
                sendMessage.setText(list);
                Message message = execute(sendMessage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAnimeNameWithoutNumbers(String name) {
        return name.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ").trim();
    }

    public String returnText(int num) {
        if (num == 1) {
            return "Добро пожаловать!\n" +
                    "Выберите день недели, чтобы получить список аниме на этот день:\n" +
                    "\n" +
                    "Понедельник -" + " /monday\n" +
                    "Вторник -" + " /tuesday\n" +
                    "Среда -" + " /wednesday\n" +
                    "Четверг -" + " /thursday\n" +
                    "Пятница -" + " /friday\n" +
                    "Суббота -" + " /saturday\n" +
                    "Воскресенье -" + " /sunday\n\n" +
                    "Получать уведомления - " + " /receivenotifications\n\n" +
                    "Просто нажмите на кнопку с соответствующим днем недели, чтобы получить список аниме для выбранного дня. Приятного просмотра!";
        } else {
            return "Комманда не определена!\n" +
                    "Выберите день недели, чтобы получить список аниме на этот день:\n" +
                    "\n" +
                    "Понедельник -" + " /monday\n" +
                    "Вторник -" + " /tuesday\n" +
                    "Среда -" + " /wednesday\n" +
                    "Четверг -" + " /thursday\n" +
                    "Пятница -" + " /friday\n" +
                    "Суббота -" + " /saturday\n" +
                    "Воскресенье -" + " /sunday\n\n" +
                    "Получать уведомления - " + " /receivenotifications\n\n" +
                    "Просто нажмите на кнопку с соответствующим днем недели, чтобы получить список аниме для выбранного дня. Приятного просмотра!";
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        MyTelegramBot bot = new MyTelegramBot();
        bot.run();
    }

    public void run() throws TelegramApiException {
        // Запуск бота
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
