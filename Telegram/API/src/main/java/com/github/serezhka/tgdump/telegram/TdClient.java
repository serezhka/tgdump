package com.github.serezhka.tgdump.telegram;

import org.drinkless.tdlib.TdApi;

import java.util.function.Consumer;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
public interface TdClient {

    void addUpdatesListener(Consumer<TdApi.Object> updatesListener);

    TdApi.AuthorizationState getAuthorizationState();

    TdApi.ConnectionState getConnectionState();

    void setAuthenticationPhoneNumber(String phoneNumber);

    void checkAuthenticationCode(String code);

    void checkAuthenticationPassword(String password);

    void logout();

    TdApi.Chats getChats(long offset, int limit);

    TdApi.Chat getChat(long chatId);

    TdApi.ChatMembers getChatMembers(long chatId);

    TdApi.File getFile(String remoteFileId);
}
