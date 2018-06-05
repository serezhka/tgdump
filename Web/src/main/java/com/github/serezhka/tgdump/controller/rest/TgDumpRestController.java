package com.github.serezhka.tgdump.controller.rest;

import com.github.serezhka.tgdump.telegram.TdClient;
import org.drinkless.tdlib.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Paths;

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

    @RequestMapping("/file/{fileId}")
    public String file(@PathVariable("fileId") String fileId) {
        TdApi.File file = tdClient.getFile(fileId);
        File localFile = Paths.get(file.local.path).toFile();
        return String.valueOf(file);
    }
}
