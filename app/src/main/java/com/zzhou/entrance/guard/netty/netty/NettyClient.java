package com.zzhou.entrance.guard.netty.netty;

import android.util.Log;

import com.google.gson.Gson;
import com.zzhou.entrance.guard.MyApplication;
import com.zzhou.entrance.guard.netty.HostUtils;
import com.zzhou.entrance.guard.netty.bean.NettyBaseInfo;
import com.zzhou.entrance.guard.util.LogUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;

/**
 * <desc>
 * Created by The Moss on 2018/8/30.
 */

public class NettyClient {

    private static NettyClient nettyClient = new NettyClient();

    private EventLoopGroup group;

    private NettyListener listener;

    private Channel channel;

    private boolean isConnect = false;

    private boolean isAuthor = false;

    private int reconnectNum = Integer.MAX_VALUE;

    private long reconnectIntervalTime = 5000;
    public final static String TAG = NettyClient.class.getName();
    private final Gson gson;
    private Bootstrap bootstrap;

    public NettyClient() {
        gson = new Gson();
    }

    public static NettyClient getInstance() {
        return nettyClient;
    }

    public synchronized NettyClient connect() {
        if (!isConnect) {
            group = new NioEventLoopGroup();
            bootstrap = new Bootstrap().group(group)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer(listener));
            try {
                String[] host = HostUtils.getHost(MyApplication.getInstance());
                if (host == null || host.length < 2) {
                    listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
                    reconnect();
                    return this;
                }
                String ip = host[0];
                int port = Integer.parseInt(host[1]);
//                String ip = UrlConstant.SOCKET_HOST;
//                int port = UrlConstant.SOCKET_PORT;
                if (!StringUtil.isNullOrEmpty(ip) && port > 0) {
                    ChannelFuture future = bootstrap.connect(ip, port).sync();
                    if (future != null && future.isSuccess()) {
                        channel = future.channel();
//                        channel.closeFuture().sync();
                        isConnect = true;
                    } else {
                        isConnect = false;
                        listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
                        reconnect();
                    }
                } else {
                    isConnect = false;
                    listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
                    reconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.onServiceStatusConnectChanged(NettyListener.STATUS_CONNECT_ERROR);
                reconnect();
            }
        }
        return this;
    }

    public void disconnect() {
        isConnect = false;
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    public void reconnect() {
        LogUtils.e("reconnect  reconnectNum  = " +reconnectNum + "  isConnect = " + isConnect);
//        if (reconnectNum > 0 && !isConnect) {
//            reconnectNum--;
            try {
                disconnect();
                Thread.sleep(reconnectIntervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connect();
//        }else {
//            reconnectNum = Integer.MAX_VALUE;
//            isConnect = false;
//            disconnect();
//            connect();
//        }
    }

    public Channel getChannel() {
        return channel;
    }

    /**
     * 发送消息
     *
     * @param vo
     * @param futureListener
     */
    public void sendMessage(NettyBaseInfo vo, FutureListener futureListener) {
        boolean flag = channel != null && isConnect;
        if (!flag) {
            Log.e(TAG, "------尚未连接");
            return;
        }
        final String s = gson.toJson(vo).replaceAll("\\\\n", "") + "\n";
        if (futureListener == null) {
            channel.writeAndFlush(s).addListener(new FutureListener() {

                @Override
                public void success() {
                    Log.e(TAG, "发送成功--->" + s);
                }

                @Override
                public void error() {
                    Log.e(TAG, "发送失败--->" + s);
                }
            });
        } else {
            channel.writeAndFlush(s).addListener(futureListener);
        }
    }

//    /**
//     * 设置重连次数
//     *
//     * @param reconnectNum 重连次数
//     */
//    public void setReconnectNum(int reconnectNum) {
//        this.reconnectNum = reconnectNum;
//    }

//    /**
//     * 设置重连时间间隔
//     *
//     * @param reconnectIntervalTime 时间间隔
//     */
//    public void setReconnectIntervalTime(long reconnectIntervalTime) {
//        this.reconnectIntervalTime = reconnectIntervalTime;
//    }

    public boolean getConnectStatus() {
        return isConnect;
    }

    /**
     * 设置连接状态
     *
     * @param status
     */
    public void setConnectStatus(boolean status) {
        this.isConnect = status;
    }

    public boolean isAuthor() {
        return isAuthor;
    }

    public void setAuthor(boolean author) {
        isAuthor = author;
    }

    public void setListener(NettyListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null ");
        }
        this.listener = listener;
    }


}
