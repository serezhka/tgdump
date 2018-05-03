package com.github.serezhka.tgdump.repository.online;

import com.github.serezhka.tgdump.entity.Chat;
import com.github.serezhka.tgdump.telegram.TdClient;
import com.github.serezhka.tgdump.util.converter.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Repository
public class OnlineMessageRepository {

    private final TdClient tdClient;

    @Autowired
    public OnlineMessageRepository(TdClient tdClient) {
        this.tdClient = tdClient;
    }

    public Page<Chat> getChats(Pageable pageable) {
        long[] chatIds = tdClient.getChats(pageable.getOffset(), pageable.getPageSize()).chatIds;
        List<Chat> chats = LongStream.of(chatIds)
                .mapToObj(tdClient::getChat)
                .map(EntityConverter::chatToEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(chats);
    }
}
