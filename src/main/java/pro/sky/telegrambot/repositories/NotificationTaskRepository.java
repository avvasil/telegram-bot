package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.models.NotificationTask;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {

   Collection<NotificationTask> findNotificationTaskByNotificationTime(LocalDateTime timeStamp);

}
