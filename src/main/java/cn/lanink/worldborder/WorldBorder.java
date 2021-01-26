package cn.lanink.worldborder;

import cn.lanink.worldborder.command.WorldBorderCommand;
import cn.lanink.worldborder.entity.EntityText;
import cn.lanink.worldborder.form.WindowListener;
import cn.lanink.worldborder.listener.PlayerMoveListener;
import cn.lanink.worldborder.listener.PlayerTeleportListener;
import cn.lanink.worldborder.task.CheckPlayerPosTask;
import cn.lanink.worldborder.utils.Border;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class WorldBorder extends PluginBase {

    private static WorldBorder instance;
    private Config config;
    private Config borderConfig;
    private final Map<Level, Border> borders = new ConcurrentHashMap<>();
    private final Map<Level, Border> cacheBorder = new HashMap<>();

    private final Map<Player, EntityText> entityTexts = new HashMap<>();

    public static WorldBorder getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        this.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);

        this.loadBorderConfig();

        this.getServer().getCommandMap().register("", new WorldBorderCommand());

        this.getServer().getPluginManager().registerEvents(new WindowListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerTeleportListener(this), this);

        boolean cancelledMove = true;
        if (this.config.getInt("detectionMethod", 0) == 1) {
            this.getServer().getScheduler().scheduleRepeatingTask(this,
                    new CheckPlayerPosTask(this), 1, true);
            cancelledMove = false;
        }
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(this, cancelledMove), this);

        this.getLogger().info("§a 插件加载完成！");
    }

    @Override
    public void onDisable() {
        this.cacheBorder.clear();
        this.borders.clear();
        this.getLogger().info("§c 插件已卸载！");
    }

    public void loadBorderConfig() {
        this.borderConfig = new Config(this.getDataFolder() + "/border.yml", Config.YAML);
        this.borders.clear();

        if (!this.cacheBorder.isEmpty()) {
            for (Border border : this.cacheBorder.values()) {
                if (!border.isSetUp()) {
                    continue;
                }
                border.check();
                this.borderConfig.set(border.getLevel().getFolderName(), border.getSaveMap());
            }
            this.borderConfig.save();
        }

        for (String key : this.borderConfig.getAll().keySet()) {
            Level level = this.getServer().getLevelByName(key);
            if (level == null) {
                if (!this.getServer().loadLevel(key)) {
                    this.getLogger().error(" §c" + key + " 世界不存在！");
                    continue;
                }
                level = this.getServer().getLevelByName(key);
            }
            Border border = new Border(level, this.borderConfig.get(key, new HashMap<>()));
            this.borders.put(level, border);
        }
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    public Config getBorderConfig() {
        return this.borderConfig;
    }

    public Border getCacheBorder(Level level) {
        Border border = this.cacheBorder.get(level);
        if (border == null) {
            border = new Border(level);
            this.cacheBorder.put(level, border);
        }
        return border;
    }

    public Map<Level, Border> getCacheBorder() {
        return this.cacheBorder;
    }

    public Map<Level, Border> getBorders() {
        return this.borders;
    }

    public Map<Player, EntityText> getEntityTexts() {
        return this.entityTexts;
    }

}
