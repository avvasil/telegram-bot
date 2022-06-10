--liquibase formatted sql

--changeset avvasil:1
CREATE TABLE notification_task (
                                   id bigint primary key,
                                   chat_id bigint,
                                   msg_text varchar,
                                   notification_time timestamp
);