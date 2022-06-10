package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.models.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SchedulerService {

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;

    private final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    public SchedulerService(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }


    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        LocalDateTime timeStamp = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> actualTasks = new ArrayList<>(notificationTaskRepository.findNotificationTaskByNotificationTime(timeStamp));

        if (actualTasks.isEmpty()) {
            logger.info("No tasks available");
        }
            else
            {
                for (NotificationTask actualTask : actualTasks) {
                    logger.info("Processing scheduler task: {}", actualTask);
                    SendMessage notificationTaskMsg = new SendMessage(actualTask.getChatId(), actualTask.getMsgText());
                    telegramBot.execute(notificationTaskMsg);
                }
        }
    }
}