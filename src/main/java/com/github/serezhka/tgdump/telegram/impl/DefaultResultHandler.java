package com.github.serezhka.tgdump.telegram.impl;

import org.apache.log4j.Logger;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Component
public class DefaultResultHandler implements Client.ResultHandler {

    private static final Logger LOGGER = Logger.getLogger(DefaultResultHandler.class);

    @Override
    public void onResult(TdApi.Object object) {
        LOGGER.debug("Received result: " + object.toString().replaceAll("\\s{2,}|\\n|\\r", " "));
    }
}
