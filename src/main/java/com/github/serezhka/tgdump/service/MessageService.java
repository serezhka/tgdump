package com.github.serezhka.tgdump.service;

import com.github.serezhka.tgdump.entity.Chat;
import com.github.serezhka.tgdump.repository.MessageRepository;
import com.github.serezhka.tgdump.repository.online.OnlineMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final OnlineMessageRepository onlineMessageRepository;

    public MessageService(MessageRepository messageRepository,
                          OnlineMessageRepository onlineMessageRepository) {
        this.messageRepository = messageRepository;
        this.onlineMessageRepository = onlineMessageRepository;
    }

    public Stream<Chat> getChats() {
        return null;
    }

    public Page<Chat> getChats(Pageable pageable) {
        return null;
    }

    public Page<Chat> getChatsOnline(Pageable pageable) {
        return onlineMessageRepository.getChats(pageable);
    }
}
