package com.github.serezhka.tgdump;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@EnableCaching
@SpringBootApplication
public class App {

    static {
        System.loadLibrary("tdjni");
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
