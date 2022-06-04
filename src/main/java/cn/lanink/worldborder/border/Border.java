package cn.lanink.worldborder.border;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author lt_name
 */
public class Border implements Cloneable {

    @Setter
    @Getter
    private Borders borders;
    @Setter
    @Getter
    private String name;
    @Getter
    private final BorderType borderType;
    private final Level level;
    private Double minX;
    private Double maxX;
    private Double minZ;
    private Double maxZ;
    @Setter
    @Getter
    private Double radius;

    public Border(@NotNull String name, @NotNull Level level) {
        this(name, level, null, null, null, null);
    }

    public Border(@NotNull String name, @NotNull BorderType borderType, @NotNull Level level) {
        this(name, borderType, level, null, null, null, null);
    }

    public Border(@NotNull String name, @NotNull Level level, Double minX, Double maxX, Double minZ, Double maxZ) {
        this(name, BorderType.SQUARE, level, minX, maxX, minZ, maxZ);
    }

    public Border(@NotNull String name, @NotNull BorderType borderType, @NotNull Level level, Double minX, Double maxX, Double minZ, Double maxZ) {
        this.name = name;
        this.borderType = borderType;
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
        if (this.borderType == BorderType.ROUND) {
            return this.getLevel() != null && this.getRadius() != null &&
                    this.getMinX() != null && this.getMinZ() != null;
        }else {
            return this.getLevel() != null &&
                    this.getMinX() != null && this.getMaxX() != null &&
                    this.getMinZ() != null && this.getMaxZ() != null;
        }
    }

    public void check() {
        if (this.borderType != BorderType.SQUARE) {
            return;
        }
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
     * 玩家是否可以通过边界
     *
     * @return true 可以通过 false 无法通过
     */
    public boolean canThrough(Player player) {
        return this.canJoin(player) && this.canLeave(player);
    }

    /**
     * 玩家是否可以进入边界范围内
     *
     * @return true 可以通过 false 无法通过
     */
    public boolean canJoin(Player player) {
        //TODO 完善这个判断
        return true;
    }

    /**
     * 玩家是否可以离开边界范围内
     *
     * @return true 可以通过 false 无法通过
     */
    public boolean canLeave(Player player) {
        //TODO 完善这个判断
        return player.isOp();
    }

    /**
     * 是否在边界里面
     *
     * @param vector3 位置
     * @return 是否在边界里面
     */
    public boolean isInside(@NotNull Vector3 vector3) {
        if (!this.isSetUp()) {
            return true;
        }
        if (this.borderType == BorderType.SQUARE) {
            return vector3.x > this.minX && vector3.x < this.maxX &&
                    vector3.z > this.minZ && vector3.z < this.maxZ;
        }else {
            return new Vector2(this.minX, this.minZ).distance(vector3.x, vector3.z) < this.radius;
        }
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
            switch (this.borderType) {
                case SQUARE:
                    if (vector3.x < this.minX) {
                        motion = new Vector3(this.minX - vector3.x, 0, 0);
                    } else if (vector3.x > this.maxX) {
                        motion = new Vector3(this.maxX - vector3.x, 0, 0);
                    } else if (vector3.z < this.minZ) {
                        motion = new Vector3(0, 0, this.minZ - vector3.z);
                    } else if (vector3.z > this.maxZ) {
                        motion = new Vector3(0, 0, this.maxZ - vector3.z);
                    }
                    break;
                case ROUND:
                    motion = new Vector3(this.minX - vector3.x, 0, this.minZ - vector3.z);
                    while (Math.abs(motion.getX()) > 4) {
                        motion.x /= 2;
                    }
                    while (Math.abs(motion.getZ()) > 4) {
                        motion.z /= 2;
                    }
                    break;
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
    public LinkedHashMap<String, Object> getSaveMap() {
        if (!this.isSetUp()) {
            return new LinkedHashMap<>();
        }
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("minX", this.getMinX());
        map.put("maxX", this.getMaxX());
        map.put("minZ", this.getMinZ());
        map.put("maxZ", this.getMaxZ());
        if (this.getRadius() != null) {
            map.put("radius", this.getRadius());
        }
        map.put("BorderType", this.borderType == BorderType.SQUARE ? "square" : "round");
        return map;
    }

    @Override
    public Border clone() {
        try {
            return (Border) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public enum BorderType {
        SQUARE,
        ROUND
    }

}
