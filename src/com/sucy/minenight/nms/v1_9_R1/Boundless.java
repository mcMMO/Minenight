package com.sucy.minenight.nms.v1_9_R1;

import net.minecraft.server.v1_9_R1.AxisAlignedBB;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.MovingObjectPosition;
import net.minecraft.server.v1_9_R1.Vec3D;

/**
 * A bounding box that makes sure no collisions occur
 */
public class Boundless extends AxisAlignedBB
{
    public Boundless() { super(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D); }

    public MovingObjectPosition a(Vec3D arg0, Vec3D arg1) { return super.a(arg0, arg1); }

    public AxisAlignedBB a(AxisAlignedBB arg0) { return this; }

    public AxisAlignedBB a(BlockPosition arg0) { return this; }

    public AxisAlignedBB a(double arg0, double arg1, double arg2) { return this; }

    public AxisAlignedBB c(double arg0, double arg1, double arg2) { return this; }

    public AxisAlignedBB e(double arg0) { return this; }

    public AxisAlignedBB g(double arg0) { return this; }

    public AxisAlignedBB grow(double arg0, double arg1, double arg2) { return this; }

    public AxisAlignedBB shrink(double arg0) { return this; }

    public boolean a(double arg0, double arg1, double arg2, double arg3, double arg4, double arg5) { return false; }

    public boolean b(Vec3D arg0) { return false; }

    public boolean c(Vec3D arg0) { return false; }

    public boolean d(Vec3D arg0) { return false; }

    public boolean a(Vec3D arg0) { return false; }

    public boolean b(AxisAlignedBB arg0) { return false; }

    public double a(AxisAlignedBB arg0, double arg1) { return 0.0D; }

    public double b(AxisAlignedBB arg0, double arg1) { return 0.0D; }

    public double c(AxisAlignedBB arg0, double arg1) { return 0.0D; }

    public double a() { return 0.0D; }
}
