package cn.lanink.worldborder.utils;

import cn.lanink.worldborder.WorldBorder;
import cn.nukkit.utils.Config;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

/**
 * @author LT_Name
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static void checkOldConfig() {
        WorldBorder worldBorder = WorldBorder.getInstance();
        File oldConfigFile = new File(worldBorder.getDataFolder() + "/border.yml");
        if (oldConfigFile.exists()) {
            worldBorder.getLogger().info("检测到旧版配置，正在转换...");
            Config oldConfig = new Config(oldConfigFile, Config.YAML);
            for (String key : oldConfig.getAll().keySet()) {
                try {
                    Config newConfig = worldBorder.getWorldConfig(key);
                    HashMap<String, Double> pos = oldConfig.get(key, new HashMap<>());
                    //生成一个不重复的名字
                    String newName;
                    do {
                        newName = key + "OldConfig" + new Random().nextInt(10000);
                    }while (newConfig.get(newName) != null);
                    LinkedHashMap<String, Double> map = new LinkedHashMap<>();
                    map.put("minX", pos.get("minX"));
                    map.put("maxX", pos.get("maxX"));
                    map.put("minZ", pos.get("minZ"));
                    map.put("maxZ", pos.get("maxZ"));
                    newConfig.set(newName, map);
                    newConfig.save();
                } catch (Exception e) {
                    worldBorder.getLogger().error("旧配置世界 " + key + " 边界设置转换失败！", e);
                }
            }
            worldBorder.getLogger().info("旧配置文件转换完成！");
            if (oldConfigFile.delete()) {
                worldBorder.getLogger().info("旧配置文件删除成功！");
            }else {
                worldBorder.getLogger().error("旧配置文件删除失败！请手动删除" + oldConfigFile.getName());
            }
        }
    }

}
