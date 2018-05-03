package com.github.serezhka.tgdump.telegram.impl;

import com.github.serezhka.tgdump.telegram.TdClient;
import org.apache.log4j.Logger;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.Log;
import org.drinkless.tdlib.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Component
public class TdClientImpl implements TdClient {

    private static final Logger LOGGER = Logger.getLogger(TdClientImpl.class);

    private final Client.ResultHandler defaultResultHandler;
    private final Client.ExceptionHandler defaultExceptionHandler;
    private final TdApi.TdlibParameters tdlibParameters;

    private Client client;
    private UpdateHandler updatesHandler;
    private TdApi.AuthorizationState authorizationState;
    private TdApi.ConnectionState connectionState;

    @Autowired
    public TdClientImpl(Client.ResultHandler defaultResultHandler,
                        Client.ExceptionHandler defaultExceptionHandler,
                        TdApi.TdlibParameters tdlibParameters,
                        TdApi.Proxy tdapiProxy) {

        this.defaultResultHandler = defaultResultHandler;
        this.defaultExceptionHandler = defaultExceptionHandler;
        this.tdlibParameters = tdlibParameters;

        Log.setVerbosityLevel(3);
        if (!Log.setFilePath("log")) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        updatesHandler = new UpdateHandler();
        updatesHandler.addUpdatesListener(defaultResultHandler);
        client = Client.create(updatesHandler, defaultExceptionHandler, defaultExceptionHandler);
        client.send(new TdApi.SetProxy(tdapiProxy), defaultResultHandler);
    }

    @Override
    public void addUpdatesListener(Client.ResultHandler updatesListener) {
        updatesHandler.addUpdatesListener(updatesListener);
    }

    @Override
    public TdApi.AuthorizationState getAuthorizationState() {
        return authorizationState;
    }

    @Override
    public TdApi.ConnectionState getConnectionState() {
        return connectionState;
    }

    @Override
    public void setAuthenticationPhoneNumber(String phoneNumber) {
        client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, false, false), defaultResultHandler);
    }

    @Override
    public void checkAuthenticationCode(String code) {
        client.send(new TdApi.CheckAuthenticationCode(code, "", ""), defaultResultHandler);
    }

    @Override
    public void checkAuthenticationPassword(String password) {
        client.send(new TdApi.CheckAuthenticationPassword(password), defaultResultHandler);
    }

    @Override
    public void logout() {
        client.send(new TdApi.LogOut(), defaultResultHandler);
    }

    @Override
    public TdApi.Chats getChats(long offset, int limit) {
        return (TdApi.Chats) syncSend(new TdApi.GetChats(Long.MAX_VALUE, offset, limit));
    }

    @Override
    public TdApi.Chat getChat(long chatId) {
        return (TdApi.Chat) syncSend(new TdApi.GetChat(chatId));
    }

    @Override
    public TdApi.File getFile(String remoteFileId) {
        TdApi.File file = (TdApi.File) syncSend(new TdApi.GetRemoteFile(remoteFileId, null));
        if (!file.local.isDownloadingCompleted && !file.local.isDownloadingActive) {
            file = (TdApi.File) syncSend(new TdApi.DownloadFile(file.id, 1));
        }
        return file;
    }

    private void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            this.authorizationState = authorizationState;
        }
        switch (this.authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                client.send(new TdApi.SetTdlibParameters(tdlibParameters), defaultResultHandler);
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                client.send(new TdApi.CheckDatabaseEncryptionKey(), defaultResultHandler);
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                client = Client.create(updatesHandler, defaultExceptionHandler, defaultExceptionHandler);
                break;
        }
    }

    private TdApi.Object syncSend(TdApi.Function query) {
        final AtomicReference<TdApi.Object> notifier = new AtomicReference<>();
        client.send(query, result -> {
            defaultResultHandler.onResult(result);
            synchronized (notifier) {
                notifier.set(result);
                notifier.notify();
            }
        }, defaultExceptionHandler);
        synchronized (notifier) {
            while (notifier.get() == null) {
                try {
                    notifier.wait();
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return notifier.get();
    }

    private class UpdateHandler implements Client.ResultHandler {

        private List<Client.ResultHandler> updatesListeners = new ArrayList<>();

        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;
                case TdApi.UpdateConnectionState.CONSTRUCTOR:
                    connectionState = ((TdApi.UpdateConnectionState) object).state;
            }

            updatesListeners.forEach(handler -> handler.onResult(object));
        }

        private void addUpdatesListener(Client.ResultHandler updatesListener) {
            updatesListeners.add(updatesListener);
        }
    }
}
