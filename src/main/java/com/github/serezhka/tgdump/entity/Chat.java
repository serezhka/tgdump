package com.github.serezhka.tgdump.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Entity
@Table(name = "chat", schema = "tgdump")
public class Chat extends AbstractEntity {

    private long chatId;
    private String title;
    private File chatPhoto;
    private Message lastMessage;

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public File getChatPhoto() {
        return chatPhoto;
    }

    public void setChatPhoto(File chatPhoto) {
        this.chatPhoto = chatPhoto;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
