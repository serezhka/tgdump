package com.github.serezhka.tgdump.telegram.impl;

import org.apache.log4j.Logger;
import org.drinkless.tdlib.Client;
import org.springframework.stereotype.Component;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Component
public class DefaultExceptionHandler implements Client.ExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(DefaultExceptionHandler.class);

    @Override
    public void onException(Throwable e) {
        LOGGER.error(e.getMessage(), e);
    }
}
