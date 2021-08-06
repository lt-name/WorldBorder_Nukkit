package cn.lanink.worldborder.border;

import cn.lanink.worldborder.WorldBorder;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author LT_Name
 */
public class Borders {

    @Getter
    private final Level level;
    @Getter
    private final HashSet<Border> borders = new HashSet<>();

    public Borders(@NotNull String levelName, @NotNull Config config) {
        if (!Server.getInstance().loadLevel(levelName)) {
            throw new RuntimeException("世界：" + levelName + " 无法加载！");
        }
        this.level = Server.getInstance().getLevelByName(levelName);

        for (String name : config.getAll().keySet()) {
            HashMap<String, Object> map = config.get(name, new HashMap<>());
            Border.BorderType borderType = Border.BorderType.SQUARE;
            if ("round".equals(map.get("BorderType"))) {
                borderType = Border.BorderType.ROUND;
            }
            Border border = new Border(name, borderType, this.level,
                    (Double) map.get("minX"), (Double) map.get("maxX"), (Double) map.get("minZ"), (Double) map.get("maxZ"));
            if (map.containsKey("radius")) {
                border.setRadius((Double) map.get("radius"));
            }
            this.addBorder(border);
        }
    }

    public Borders(@NotNull Level level) {
        this.level = level;
    }

    /**
     * 是否在边界里面
     *
     * @param vector3 位置
     * @return 是否在边界外
     */
    public Border isInside(@NotNull Vector3 vector3) {
        for (Border border : this.borders) {
            if (border.isInside(vector3)) {
                return border;
            }
        }
        return null;
    }

    public void addBorder(@NotNull Border border) {
        Border oldBorder = this.getBorderByName(border.getName());
        if (oldBorder != null) {
            this.borders.remove(oldBorder);
        }
        this.borders.add(border);
        border.setBorders(this);
    }

    public Border getBorderByName(String name) {
        for (Border border : this.borders) {
            if (border.getName().equals(name)) {
                return border;
            }
        }
        return null;
    }

    public void saveConfig() {
        Config worldConfig = WorldBorder.getInstance().getWorldConfig(this.level);
        for (String key : worldConfig.getAll().keySet()) {
            worldConfig.remove(key);
        }
        for (Border border : this.borders) {
            if (border.isSetUp()) {
                worldConfig.set(border.getName(), border.getSaveMap());
            }
        }
        worldConfig.save();
    }

}
