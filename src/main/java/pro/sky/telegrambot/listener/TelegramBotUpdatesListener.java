package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.services.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationTaskService notificationTaskService;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {

            logger.info("Processing update: {}", update);

            if (update.message() == null && update.editedMessage() != null) {
                if (Objects.equals(update.editedMessage().text(), "/start")) {
                    SendMessage welcomeMsgEditedMsg = new SendMessage(update.editedMessage().chat().id(),
                            "Приветствуем Вас в нашем боте! Он напомнит Вам о событии, " +
                                    "которое нужно ввести в формате 01.01.2022 20:00 МОЁ НАПОМИНАНИЕ");
                    telegramBot.execute(welcomeMsgEditedMsg);
                } else {
                    if (!notificationTaskService.createNotificationTask(update.editedMessage().text(),
                            update.editedMessage().chat().id())) {
                        SendMessage errorMsgEditedMsg = new SendMessage(update.editedMessage().chat().id(),
                                "Неверный формат запроса");
                        telegramBot.execute(errorMsgEditedMsg);
                    }
                }
            }
            if (update.message() != null && update.editedMessage() == null) {
                if (Objects.equals(update.message().text(), "/start")) {
                    SendMessage welcomeMsg = new SendMessage(update.message().chat().id(),
                            "Приветствуем Вас в нашем боте! Он напомнит Вам о событии, " +
                                    "которое нужно ввести в формате 01.01.2022 20:00 МОЁ НАПОМИНАНИЕ");
                    telegramBot.execute(welcomeMsg);
                } else {
                    if (!notificationTaskService.createNotificationTask(update.message().text(),
                            update.message().chat().id())) {
                        SendMessage errorMsg = new SendMessage(update.message().chat().id(),
                                "Неверный формат запроса");
                        telegramBot.execute(errorMsg);
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
