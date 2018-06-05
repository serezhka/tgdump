package com.github.serezhka.tgdump.telegram.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Component
public class DefaultExceptionHandler implements Consumer<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(DefaultExceptionHandler.class);

    @Override
    public void accept(Throwable e) {
        LOGGER.error(e.getMessage(), e);
    }
}
