package com.github.serezhka.tgdump.controller;

import com.github.serezhka.tgdump.service.MessageService;
import com.github.serezhka.tgdump.telegram.TdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Controller
@RequestMapping("/")
public class TgDumpController {

    private final TdClient tdClient;
    private final MessageService messageService;

    @Autowired
    public TgDumpController(TdClient tdClient,
                            MessageService messageService) {
        this.tdClient = tdClient;
        this.messageService = messageService;
    }

    @RequestMapping
    public String index(Model model) {
        //model.addAttribute("tgConnectionState", tdClient.get)
        return "tgdump";
    }

    @RequestMapping("/test")
    public String test() {
        return "pug-bootstrap/layouts/jumbotron";
    }

    @RequestMapping("/chats")
    public String getChats(@RequestParam(value = "page", defaultValue = "1") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           Model model) {
        model.addAttribute("chats", messageService.getChatsOnline(PageRequest.of(page, size)));
        return "chats";
    }
}
