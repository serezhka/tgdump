package com.github.serezhka.tgdump.controller.websocket;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * @author Sergei Fedorov (serezhka@xakep.ru)
 */
@ChannelHandler.Sharable
public class UniqueConnectionFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {

    private static final Logger LOGGER = Logger.getLogger(UniqueConnectionFilter.class);

    private boolean connected = false;

    @Override
    protected boolean accept(ChannelHandlerContext ctx, InetSocketAddress socketAddress) {
        if (connected) {
            LOGGER.info("Client " + socketAddress + " connection rejected!");
            return false;
        } else {
            LOGGER.info("Client " + socketAddress + " connection accepted!");
            connected = true;
            ctx.channel().closeFuture().addListener((ChannelFutureListener) future -> {
                LOGGER.info("Client " + socketAddress + " disconnected!");
                connected = false;
            });
            return true;
        }
    }
}
