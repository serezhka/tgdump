package com.github.serezhka.tgdump.config;

import org.drinkless.tdlib.TdApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Configuration
public class TdlibConfig {

    @Bean
    public TdApi.TdlibParameters tdlibParameters() {
        TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
        parameters.apiId = 0;
        parameters.apiHash = "";
        parameters.systemLanguageCode = "en";
        parameters.deviceModel = "Desktop";
        parameters.systemVersion = "Unknown";
        parameters.applicationVersion = "1.0";
        parameters.databaseDirectory = "./temp";
        parameters.filesDirectory = "./temp";
        parameters.enableStorageOptimizer = true;
        return parameters;
    }

    @Bean
    public TdApi.Proxy tdlibProxy() {
        return new TdApi.ProxySocks5("127.0.0.1", 9150, null, null);
        //return new TdApi.ProxySocks5("proxy.t-systems.ru", 3128, null, null);
        //return new TdApi.ProxyEmpty();
    }
}
