const TGDUMP_HOST = "127.0.0.1";
const TGDUMP_PORT = "8091";
const TGDUMP_PATH = "/tgdump";

ws = new ReconnectingWebSocket("ws://" + TGDUMP_HOST + ":" + TGDUMP_PORT + TGDUMP_PATH);
ws.debug = true;
ws.maxReconnectInterval = 5000;

ws.onopen = function () {
    console.log("Websocket connection established with " + TGDUMP_HOST + " on port " + TGDUMP_PORT);
    onWebsocketConnected();
};

ws.onmessage = function (evt) {
    var update = JSON.parse(evt.data);
    if (update.AuthorizationStateWaitPhoneNumber) {
        console.log("Waiting phone number");
        onAuthorizationStateWaitPhoneNumber();
    } else if (update.AuthorizationStateWaitCode) {
        console.log("Waiting code");
        onAuthorizationStateWaitCode();
    } else if (update.AuthorizationStateReady) {
        console.log("Authenticated");
        onAuthorizationStateReady();
    } else if (update.ConnectionStateReady) {
        console.log("Connected");
        onTelegramConnected();
    } else {
        console.log(evt.data);
    }
};

ws.onerror = function (error) {
    console.error("Websocket error: " + error);
};

ws.onclose = function (event) {
    console.log("Websocket connection closed with code: " + event.code);
    onWebsocketDisconnected()
};

function getConnectionState() {
    sendCommand({
        command: "getConnectionState"
    });
}

function getAuthState() {
    sendCommand({
        command: "getAuthState"
    });
}

function setAuthenticationPhoneNumber(phoneNumber) {
    sendCommand({
        command: "setAuthenticationPhoneNumber",
        phoneNumber: phoneNumber
    });
}

function checkAuthenticationCode(code) {
    sendCommand({
        command: "checkAuthenticationCode",
        code: code
    });
}

function checkAuthenticationPassword(password) {
    sendCommand({
        command: "checkAuthenticationPassword",
        password: password
    });
}

function logout() {
    sendCommand({
        command: "logout"
    });
}

function onWebsocketConnected() {
    $("#ws_connection i").each(greenIndicator);
    $("#tg_auth").show();
    $("#tg_auth_state").text("Initializing");
}

function onWebsocketDisconnected() {
    $("#ws_connection i").each(redIndicator);
    $("#tg_connection i").each(redIndicator);
    $("#tg_auth").hide();
}

function onAuthorizationStateWaitPhoneNumber() {
    $(".tg_auth").hide();
    $("#tg_auth_phone").show();
    $("#tg_auth_state").text("Waiting phone number");
}

function onAuthorizationStateWaitCode() {
    $(".tg_auth").hide();
    $("#tg_auth_code").show();
    $("#tg_auth_state").text("Waiting code");
}

function onAuthorizationStateReady() {
    $(".tg_auth").hide();
    $("#tg_auth_logout").show();
    $("#tg_auth_state").text("Logged-in");
}

function onTelegramConnected() {
    $("#tg_connection i").each(greenIndicator);
}

function greenIndicator() {
    $(this).removeClass("fa-unlink");
    $(this).addClass("fa-link");
    $(this).css("color", "green");
}

function redIndicator() {
    $(this).removeClass("fa-link");
    $(this).addClass("fa-unlink");
    $(this).css("color", "red");
}

/**
 * @see TdClient
 * @param command
 */
function sendCommand(command) {
    if (ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify(command));
    }
}

$(function () {
    setInterval(getConnectionState, 7000);
    setInterval(getAuthState, 7000);

    $("#tg_auth_phone form").on("submit", function (event) {
        $(".tg-auth").hide();
        setAuthenticationPhoneNumber($(this).find("input[name=phone]").val());
        event.preventDefault();
    });

    $("#tg_auth_code form").on("submit", function (event) {
        $(".tg-auth").hide();
        checkAuthenticationCode($(this).find("input[name=code]").val());
        event.preventDefault();
    });

    $("#tg_auth_logout form").on("submit", function (event) {
        $(".tg-auth").hide();
        logout();
        event.preventDefault();
    });
});