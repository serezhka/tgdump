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
        parameters.apiId = 94575;
        parameters.apiHash = "a3406de8d171bb422bb6ddf3bbd800e2";
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
        //return new TdApi.ProxySocks5("p2.telegram-s.org", 433, null, null);
        //return new TdApi.ProxySocks5("185.211.245.136", 1080, "24181345", "rJ5jhFGl");
        //return new TdApi.ProxySocks5("4.tgsocks.cf", 1080, null, null);
        //return new TdApi.ProxySocks5("proxy.t-systems.ru", 3128, null, null);
        return new TdApi.ProxyEmpty();
    }
}
