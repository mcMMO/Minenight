package com.sucy.minenight.nms;

import com.sucy.minenight.hologram.display.line.HologramLine;

public abstract interface NMSEntityBase
{
    public abstract HologramLine getLine();

    public abstract void setPos(double paramDouble1, double paramDouble2, double paramDouble3);

    public abstract boolean isDespawned();

    public abstract void despawn();

    public abstract int id();
}
