package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationTaskService {

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    Logger logger = LoggerFactory.getLogger(NotificationTaskService.class);
    String date = "";
    String task = "";

    public boolean createNotificationTask(String txt, Long chatId) {

        logger.info("Processing parsing: {}", txt);

        String regex = "([\\d.:\\s]{16})(\\s)([\\W+]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(txt);

        if (matcher.matches()) {
            date = matcher.group(1);
            task = matcher.group(3);

            LocalDateTime dateAndTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            if (dateAndTime.isAfter(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))) {
                NotificationTask notificationTask = new NotificationTask();
                notificationTask.setChatId(chatId);
                notificationTask.setMsgText(txt);
                notificationTask.setNotificationTime(dateAndTime);

                notificationTaskRepository.save(notificationTask);
            }
            else
            {
            SendMessage errorMsg = new SendMessage(chatId,
                    "Вы ввели неверные дату/время.");
            telegramBot.execute(errorMsg);
            }
        }
        else return false;
        return true;
    }
}



