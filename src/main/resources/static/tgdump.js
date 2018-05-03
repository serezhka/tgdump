const TGDUMP_HOST = "127.0.0.1";
const TGDUMP_PORT = "8091";
const TGDUMP_PATH = "/tgdump";

ws = new ReconnectingWebSocket("ws://" + TGDUMP_HOST + ":" + TGDUMP_PORT + TGDUMP_PATH);
ws.debug = true;
ws.maxReconnectInterval = 5000;


ws.onopen = function () {
    console.log("Websocket connection established with " + TGDUMP_HOST + " on port " + TGDUMP_PORT);
    $("#ws_indicator").css("color", "green");
    $("#tg_auth").show();
    $("#tg_auth_state").text("Initializing");
};

ws.onmessage = function (evt) {
    var update = JSON.parse(evt.data);
    if (update.AuthorizationStateWaitPhoneNumber) {
        $(".tg_auth").hide();
        $("#tg_auth_phone").show();
        $("#tg_auth_state").text("Waiting phone number");
        console.log("Waiting phone number");
    } else if (update.AuthorizationStateWaitCode) {
        $(".tg_auth").hide();
        $("#tg_auth_code").show();
        $("#tg_auth_state").text("Waiting code");
        console.log("Waiting code");
    } else if (update.AuthorizationStateReady) {
        $(".tg_auth").hide();
        $("#tg_auth_logout").show();
        $("#tg_auth_state").text("Logged-in");
        console.log("Authenticated");
    } else {
        console.log(evt.data);
    }
};

ws.onerror = function (error) {
    console.error("Websocket error: " + error);
};

ws.onclose = function (event) {
    console.log("Websocket connection closed with code: " + event.code);
    $("#ws_indicator").css("color", "red");
    $("#tg_auth").hide();
};

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
    setInterval(getAuthState, 5000);

    $("#tg_auth_phone form").on("submit", function (event) {
        $(".tg-auth").hide();
        setAuthenticationPhoneNumber($(this).find("input[name=phone]").val());
        event.preventDefault();
    });

    $("#tg_auth_code form").on("submit", function (event) {
        $("#tg-auth").hide();
        checkAuthenticationCode($(this).find("input[name=code]").val());
        event.preventDefault();
    });

    $("#tg_auth_logout form").on("submit", function (event) {
        $("#tg-auth").hide();
        logout();
        event.preventDefault();
    });
});