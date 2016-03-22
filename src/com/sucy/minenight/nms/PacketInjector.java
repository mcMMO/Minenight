/**
 * MineNight
 * com.sucy.minenight.nms.PacketInjector
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Steven Sucy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.minenight.nms;

import com.sucy.minenight.util.reflect.Reflection;
import io.netty.channel.Channel;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PacketInjector {

    private Field    playerCon;
    private Class<?> PlayerConnection;
    private Field    network;
    private Method   handle;

    private Class<?> NetworkManager;
    private Field    k;

    public PacketInjector()
    {
        try
        {
            String nms = Reflection.getNMSPackage();
            playerCon = Class.forName(nms + "EntityPlayer")
                .getField("playerConnection");

            PlayerConnection = Class.forName(nms + "PlayerConnection");
            network = PlayerConnection.getField("networkManager");

            NetworkManager = Class.forName(nms + "NetworkManager");
            k = NetworkManager.getField("channel");

            handle = Class.forName(Reflection.getCraftPackage() + "entity.CraftPlayer")
                .getMethod("getHandle");
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    public void addPlayer(Player p)
    {
        try
        {
            Channel ch = getChannel(getNetworkManager(handle.invoke(p)));
            if (ch.pipeline().get("PacketInjector") == null)
            {
                PacketHandler h = new PacketHandler(p);
                ch.pipeline().addBefore("packet_handler", "PacketInjector", h);
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    public void removePlayer(Player p)
    {
        try
        {
            Channel ch = getChannel(getNetworkManager(handle.invoke(p)));
            if (ch.pipeline().get("PacketInjector") != null)
            {
                ch.pipeline().remove("PacketInjector");
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private Object getNetworkManager(Object ep)
        throws Exception
    {
        Object con = playerCon.get(ep);
        return network.get(con);
    }

    private Channel getChannel(Object networkManager)
        throws Exception
    {
        Channel ch = null;
        try {
            ch = (Channel)k.get(networkManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ch;
    }
}
