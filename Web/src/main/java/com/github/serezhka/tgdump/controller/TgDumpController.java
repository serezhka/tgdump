package com.github.serezhka.tgdump.controller;

import com.github.serezhka.tgdump.telegram.TdClient;
import org.drinkless.tdlib.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Controller
@RequestMapping("/")
public class TgDumpController {

    private TdClient tdClient;

    @Autowired
    public TgDumpController(TdClient tdClient) {
        this.tdClient = tdClient;
    }

    @RequestMapping
    public String index() {
        return "tgdump";
    }

    @RequestMapping("/chats")
    public String getChats(@RequestParam(value = "offsetOrder", defaultValue = "" + Integer.MAX_VALUE) int offsetOrder,
                           @RequestParam(value = "offsetChatId", defaultValue = "0") int offsetChat,
                           @RequestParam(value = "limit", defaultValue = "10") int limit,
                           Model model) {
        List<TdApi.Chat> chats = LongStream.of(tdClient.getChats(offset, limit).chatIds)
                .mapToObj(chatId -> tdClient.getChat(chatId))
                .collect(Collectors.toList());
        model.addAttribute("chats", chats);
        return "chats";
    }

    @RequestMapping("/chat/{chatId}")
    public String getChat(@PathVariable("chatId") long chatId,
                          Model model) {
        model.addAttribute("chatMembers", tdClient.getChatMembers(chatId));
        return "chat-members";
    }
}
