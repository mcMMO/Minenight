/**
 * MineNight
 * com.sucy.minenight.nms.PacketHandler
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

import com.sucy.minenight.hologram.display.LineData;
import com.sucy.minenight.util.reflect.Reflection;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

public class PacketHandler extends ChannelDuplexHandler
{
    private Player p;

    public PacketHandler(final Player p) {
        this.p = p;
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception
    {
        if (msg.getClass().getSimpleName().startsWith("PacketPlayOutSpawnEntity"))
        {
            int id = (Integer) Reflection.getValue(msg, "a");
            NMSEntityBase entity = NMS.getManager().getEntity(id);
            if (entity != null)
            {
                LineData hologram = entity.getLine().getParent();
                if (!hologram.getVisibility().isVisibleTo(p))
                {
                    promise.cancel(true);
                }
            }
        }
        super.write(ctx, msg, promise);
    }
    /*
    @Override
    public void channelRead(ChannelHandlerContext c, Object m) throws Exception {
        if (m.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInResourcePackStatus")) {
            String s = Reflection.getValue(m, "b").toString();
            if (s.equals("DECLINED")) {
            }
            if (s.equals("FAILED_DOWNLOAD")) {
            }
            if (s.equals("ACCEPTED")) {
            }
            if (s.equals("SUCCESSFULLY_LOADED")) {
                this.p.sendMessage("You have our texture pack installed"));
                return;
            }
        }
        else
        {
            super.channelRead(c, m);
        }
        super.channelRead(c, m);
    }
    */
}
