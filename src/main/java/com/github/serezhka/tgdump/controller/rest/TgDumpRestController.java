package com.github.serezhka.tgdump.controller.rest;

import com.github.serezhka.tgdump.telegram.TdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@RestController
@RequestMapping("/rest")
public class TgDumpRestController {

    private final TdClient tdClient;

    @Autowired
    public TgDumpRestController(TdClient tdClient) {
        this.tdClient = tdClient;
    }

    @RequestMapping("/{fileId}")
    public String file(@PathVariable("fileId") String fileId) {
        return String.valueOf(tdClient.getFile(fileId));

    }
}
