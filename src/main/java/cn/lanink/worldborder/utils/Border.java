package cn.lanink.worldborder.utils;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.LinkedHashMap;
import java.util.Map;

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

    /**
     * 获取弹回的移动参数
     *
     * @param vector3 位置
     * @return 移动参数
     */
    public Vector3 getReboundMotion(Vector3 vector3) {
        Vector3 motion = null;
        if (vector3.x < this.minX) {
            motion = new Vector3(this.minX - vector3.x, 0, 0);
        }else if (vector3.x > this.maxX) {
            motion = new Vector3(this.maxX - vector3.x, 0, 0);
        }else if (vector3.z < this.minZ) {
            motion = new Vector3(0, 0, this.minZ - vector3.z);
        }else if (vector3.z > this.maxZ) {
            motion = new Vector3(0, 0, this.maxZ - vector3.z);
        }
        if (motion != null) {
            if (motion.getX() != 0) {
                if (Math.abs(motion.getX()) < 0.5) {
                    motion.x *= 2;
                }else if (Math.abs(motion.getX()) > 2) {
                    motion.x /= 4;
                }
            }
            if (motion.getZ() != 0) {
                if (Math.abs(motion.getZ()) < 0.5) {
                    motion.z *= 2;
                }else if (Math.abs(motion.getZ()) > 2) {
                    motion.z /= 4;
                }
            }
        }
        return motion;
    }

    public Level getLevel() {
        return this.level;
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

    /**
     * 获取保存配置用map
     *
     * @return map
     */
    public Map<String, Double> getSaveMap() {
        LinkedHashMap<String, Double> map = new LinkedHashMap<>();
        map.put("minX", this.getMinX());
        map.put("maxX", this.getMaxX());
        map.put("minZ", this.getMinZ());
        map.put("maxZ", this.getMaxZ());
        return map;
    }

}
