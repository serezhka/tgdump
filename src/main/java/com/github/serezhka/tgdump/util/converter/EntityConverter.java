package com.github.serezhka.tgdump.util.converter;

import com.github.serezhka.tgdump.entity.Chat;
import com.github.serezhka.tgdump.entity.File;
import com.github.serezhka.tgdump.entity.Message;
import org.drinkless.tdlib.TdApi;

import java.util.Optional;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
public class EntityConverter {

    public static Chat chatToEntity(TdApi.Chat chat) {
        Message lastMessage = new Message();
        lastMessage.setMessageId(chat.lastMessage.id);
        switch (chat.lastMessage.content.getConstructor()) {
            case TdApi.MessageText.CONSTRUCTOR:
                lastMessage.setText(((TdApi.MessageText) chat.lastMessage.content).text.text);
                break;
        }
        lastMessage.setDate(chat.lastMessage.date);

        File chatPhoto = new File();
        Optional.ofNullable(chat.photo).map(photo -> photo.small).ifPresent(small -> {
            Optional.ofNullable(small.local).map(local -> local.path).ifPresent(chatPhoto::setLocalPath);
            Optional.ofNullable(small.remote).map(local -> local.id).ifPresent(chatPhoto::setRemoteId);
        });

        Chat entity = new Chat();
        entity.setChatId(chat.id);
        entity.setTitle(chat.title);
        entity.setChatPhoto(chatPhoto);
        entity.setLastMessage(lastMessage);
        return entity;
    }
}
