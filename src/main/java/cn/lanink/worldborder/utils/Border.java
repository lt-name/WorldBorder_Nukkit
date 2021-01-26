package cn.lanink.worldborder.utils;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author lt_name
 */
public class Border {

    private final Level level;
    private Double minX;
    private Double maxX;
    private Double minZ;
    private Double maxZ;

    public Border(@NotNull Level level) {
        this(level, null, null, null, null);
    }

    public Border(@NotNull Level level, @NotNull Map<String, Double> map) {
        this(level, map.get("minX"), map.get("maxX"), map.get("minZ"), map.get("maxZ"));
    }

    public Border(@NotNull Level level, Double minX, Double maxX, Double minZ, Double maxZ) {
        this.level = Objects.requireNonNull(level);
        this.minX = minX;
        this.maxX = maxX;
        this.minZ = minZ;
        this.maxZ = maxZ;
        if (this.isSetUp()) {
            this.check();
        }
    }

    /**
     * @return 是否设置完成
     */
    public boolean isSetUp() {
        return this.getLevel() != null &&
                this.getMinX() != null && this.getMaxX() != null &&
                this.getMinZ() != null && this.getMaxZ() != null;
    }

    public void check() {
        Double minXCache = this.minX;
        Double maxXCache = this.maxX;
        Double minZCache = this.minZ;
        Double maxZCache = this.maxZ;

        this.minX = Math.min(minXCache, maxXCache);
        this.maxX = Math.max(minXCache, maxXCache);
        this.minZ = Math.min(minZCache, maxZCache);
        this.maxZ = Math.max(minZCache, maxZCache);
    }

    /**
     * 是否在边界外
     *
     * @param vector3 位置
     * @return 是否在边界外
     */
    public boolean isOutside(@NotNull Vector3 vector3) {
        return this.isSetUp() &&
                vector3.x < this.minX || vector3.x > this.maxX ||
                vector3.z < this.minZ || vector3.z > this.maxZ;
    }

    /**
     * 获取弹回的移动参数
     *
     * @param vector3 位置
     * @return 移动参数
     */
    public Vector3 getReboundMotion(@NotNull Vector3 vector3) {
        Vector3 motion = null;
        if (this.isSetUp()) {
            if (vector3.x < this.minX) {
                motion = new Vector3(this.minX - vector3.x, 0, 0);
            } else if (vector3.x > this.maxX) {
                motion = new Vector3(this.maxX - vector3.x, 0, 0);
            } else if (vector3.z < this.minZ) {
                motion = new Vector3(0, 0, this.minZ - vector3.z);
            } else if (vector3.z > this.maxZ) {
                motion = new Vector3(0, 0, this.maxZ - vector3.z);
            }
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

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public Double getMinX() {
        return this.minX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public Double getMaxX() {
        return this.maxX;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    public Double getMinZ() {
        return this.minZ;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    public Double getMaxZ() {
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
