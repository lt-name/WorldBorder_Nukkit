package cn.lanink.worldborder;

import cn.lanink.worldborder.border.Border;
import cn.lanink.worldborder.border.Borders;
import cn.lanink.worldborder.command.WorldBorderCommand;
import cn.lanink.worldborder.entity.EntityText;
import cn.lanink.worldborder.form.FormListener;
import cn.lanink.worldborder.listener.PlayerMoveListener;
import cn.lanink.worldborder.listener.PlayerTeleportListener;
import cn.lanink.worldborder.listener.SetBorderListener;
import cn.lanink.worldborder.task.CheckPlayerPosTask;
import cn.lanink.worldborder.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class WorldBorder extends PluginBase {

    private static WorldBorder instance;

    private Config config;

    private final HashMap<String, Config> worldConfigCache = new HashMap<>();

    @Getter
    private final Map<String, Borders> borders = new ConcurrentHashMap<>();

    private final Map<Player, EntityText> entityTexts = new HashMap<>();

    @Getter
    private final ConcurrentHashMap<Player, Border> playerLastInBorder = new ConcurrentHashMap<>();

    @Getter
    private final HashMap<Player, Border> playerSet = new HashMap<>();

    public static WorldBorder getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        if (instance != null) {
            throw new RuntimeException("重复调用 onLoad 方法！");
        }
        instance = this;
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);

        this.getServer().getCommandMap().register("", new WorldBorderCommand());

        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
        this.getServer().getPluginManager().registerEvents(new SetBorderListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);

        boolean cancelledMove = true;
        if (this.config.getBoolean("启用回弹")) {
            this.getServer().getScheduler().scheduleRepeatingTask(this,
                    new CheckPlayerPosTask(this), 1, true);
            cancelledMove = false;
        }
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this, cancelledMove), this);

        Utils.checkOldConfig();

        this.loadWorldConfig();

        this.getLogger().info("§a插件加载完成！");
    }

    @Override
    public void onDisable() {
        this.borders.clear();
        this.playerSet.clear();
        this.playerLastInBorder.clear();
        for (EntityText entityText : this.entityTexts.values()) {
            entityText.close();
        }
        this.worldConfigCache.clear();
        this.getLogger().info("§c插件已卸载！");
    }

    public void reloadWorldConfig() {
        this.worldConfigCache.clear();
        this.borders.clear();
        this.loadWorldConfig();
    }

    public void loadWorldConfig() {
        int count = 0;
        File[] s = new File(this.getDataFolder() + "/worlds").listFiles();
        if (s != null && s.length > 0) {
            for (File file : s) {
                try {
                    String fileName = file.getName().split("\\.")[0];
                    Config worldConfig = this.getWorldConfig(fileName);
                    Borders borders = new Borders(fileName, worldConfig);
                    if (borders.getBorders().isEmpty()) {
                        continue;
                    }
                    this.borders.put(fileName, borders);
                    count++;
                } catch (Exception e) {
                    this.getLogger().error("加载配置 " + file.getName() + " 时出现错误！", e);
                }
            }
        }
        this.getLogger().info("世界边界配置加载完成，共加载" + count + "个世界配置文件！");
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    public Config getWorldConfig(Level level) {
        return this.getWorldConfig(level.getName());
    }

    public Config getWorldConfig(String levelName) {
        if (!this.worldConfigCache.containsKey(levelName)) {
            this.worldConfigCache.put(levelName,
                    new Config(this.getDataFolder() + "/worlds/" + levelName + ".yml", Config.YAML));
        }
        return this.worldConfigCache.get(levelName);
    }

    public Borders getBorders(Level level) {
        return this.getBorders(level.getName());
    }

    public Borders getBorders(String levelName) {
        return this.borders.get(levelName);
    }

    public Map<Player, EntityText> getEntityTexts() {
        return this.entityTexts;
    }

}
