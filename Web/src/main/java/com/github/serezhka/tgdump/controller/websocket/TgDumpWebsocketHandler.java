package com.github.serezhka.tgdump.controller.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.serezhka.tgdump.telegram.TdClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.catalina.core.StandardService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@Component
@ChannelHandler.Sharable
public class TgDumpWebsocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger LOGGER = Logger.getLogger(StandardService.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Class<? extends ServerSocketChannel> serverSocketChannelClass;
    private final TdClient tdClient;
    private final ObjectMapper objectMapper;

    private ChannelHandlerContext ctx;

    @Value("${tgdump.websocket.port}")
    private int port;

    @Value("${tgdump.websocket.path}")
    private String path;

    @Autowired
    public TgDumpWebsocketHandler(EventLoopGroup bossGroup,
                                  EventLoopGroup workerGroup,
                                  Class<? extends ServerSocketChannel> serverSocketChannelClass,
                                  TdClient tdClient,
                                  ObjectMapper objectMapper) {
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
        this.serverSocketChannelClass = serverSocketChannelClass;
        this.tdClient = tdClient;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {

        tdClient.addUpdatesListener(this::send);

        new Thread(() -> {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            try {
                serverBootstrap.group(bossGroup, workerGroup)
                        .channel(serverSocketChannelClass)
                        .localAddress(new InetSocketAddress(port))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(final SocketChannel ch) {
                                ch.pipeline().addLast(
                                        new UniqueConnectionFilter(),
                                        new HttpRequestDecoder(4096, 8192, 8192, false),
                                        new HttpObjectAggregator(65536),
                                        new HttpResponseEncoder(),
                                        new WebSocketServerProtocolHandler(path),
                                        TgDumpWebsocketHandler.this);
                            }
                        });

                serverBootstrap.bind().sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws IOException {
        JsonNode message = objectMapper.readTree(msg.content().retain().toString(UTF_8));
        LOGGER.info("Received websocket message: " + message);
        switch (message.get("command").asText()) {
            case "getAuthState":
                send(tdClient.getAuthorizationState());
                break;
            case "getConnectionState":
                send(tdClient.getConnectionState());
                break;
            case "setAuthenticationPhoneNumber":
                tdClient.setAuthenticationPhoneNumber(message.get("phoneNumber").asText());
                break;
            case "checkAuthenticationCode":
                tdClient.checkAuthenticationCode(message.get("code").asText());
                break;
            case "checkAuthenticationPassword":
                tdClient.checkAuthenticationPassword(message.get("password").asText());
                break;
            case "logout":
                tdClient.logout();
                break;
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.ctx = null;
    }

    private void send(Object object) {
        if (ctx != null) {
            try {
                ctx.writeAndFlush(new TextWebSocketFrame(objectMapper.writeValueAsString(object)));
            } catch (JsonProcessingException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
