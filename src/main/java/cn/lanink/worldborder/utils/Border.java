package cn.lanink.worldborder.utils;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

/**
 * @author lt_name
 */
public class Border {

    private final Level level;
    private double minX;
    private double maxX;
    private double minZ;
    private double maxZ;

    public Border(Level level, double minX, double maxX, double minZ, double maxZ) {
        this.level = level;
        this.minX = Math.min(minX, maxX);
        this.maxX = Math.max(minX, maxX);
        this.minZ = Math.min(minZ, maxZ);
        this.maxZ = Math.max(minZ, maxZ);
    }

    /**
     * 是否在边界外
     *
     * @param vector3 位置
     * @return 是否在边界外
     */
    public boolean isOutside(Vector3 vector3) {
        return vector3.x < this.minX || vector3.x > this.maxX ||
                vector3.z < this.minZ || vector3.z > this.maxZ;
    }

    public double getMinX() {
        return this.minX;
    }

    public double getMaxX() {
        return this.maxX;
    }

    public double getMinZ() {
        return this.minZ;
    }

    public double getMaxZ() {
        return this.maxZ;
    }

}