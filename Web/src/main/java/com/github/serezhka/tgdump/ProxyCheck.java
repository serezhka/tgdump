package com.github.serezhka.tgdump;

import java.io.IOException;
import java.net.Socket;

public class ProxyCheck {
    public static void main(String[] args) throws IOException {
        new Socket("127.0.0.1", 8118);
    }
}
