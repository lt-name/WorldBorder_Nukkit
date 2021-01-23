package cn.lanink.worldborder;

import cn.lanink.worldborder.ui.UiCreate;
import cn.lanink.worldborder.ui.UiListener;
import cn.lanink.worldborder.utils.Border;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WorldBorder extends PluginBase {

    private static WorldBorder instance;
    private Config config;
    private final ConcurrentHashMap<Level, Border> borders = new ConcurrentHashMap<>();
    private final HashMap<Level, HashMap<String, Double>> cache = new HashMap<>();

    public WorldBorder getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = getConfig();
        for (String key : this.config.getAll().keySet()) {
            Level level = this.getServer().getLevelByName(key);
            if (level == null) {
                if (!this.getServer().loadLevel(key)) {
                    continue;
                }
                level = this.getServer().getLevelByName(key);
            }
            HashMap<String, Double> pos = config.get(key, new HashMap<>());
            Border border = new Border(level, pos.get("x1"), pos.get("x2"), pos.get("z1"), pos.get("z2"));
            this.borders.put(level, border);
        }
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        this.getServer().getPluginManager().registerEvents(new UiListener(), this);
        this.getLogger().info("§a 插件加载完成！");
    }

    @Override
    public void onDisable() {
        this.cache.clear();
        this.borders.clear();
        this.getLogger().info("§c 插件已卸载！");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("boundary")) {
            if (sender instanceof Player) {
                Player player = ((Player) sender).getPlayer();
                if (player.isOp()) {
                    if (args.length > 0) {
                        switch (args[0]) {
                            case "pos1": case "点一":
                                if (!this.cache.containsKey(player.getLevel())) {
                                    this.cache.put(player.getLevel(), new HashMap<>());
                                }
                                this.cache.get(player.getLevel()).put("x1", player.getX());
                                this.cache.get(player.getLevel()).put("z1", player.getZ());
                                sender.sendMessage("§a当前位置已保存为点一");
                                break;
                            case "pos2": case "点二":
                                if (!this.cache.containsKey(player.getLevel())) {
                                    this.cache.put(player.getLevel(), new HashMap<>());
                                }
                                this.cache.get(player.getLevel()).put("x2", player.getX());
                                this.cache.get(player.getLevel()).put("z2", player.getZ());
                                sender.sendMessage("§a当前位置已保存为点二");
                                break;
                            case "reload": case "重载":
                                if (this.cache.containsKey(player.getLevel())) {
                                    HashMap<String, Double> pos = this.cache.get(player.getLevel());
                                    if (pos.get("x1") == null || pos.get("z1") == null ||
                                            pos.get("x2") == null || pos.get("z2") == null) {
                                        sender.sendMessage("§c当前世界还未设置完！");
                                        return true;
                                    }
                                    Border border = new Border(player.getLevel(), pos.get("x1"), pos.get("x2"), pos.get("z1"), pos.get("z2"));
                                    this.borders.put(player.getLevel(), border);
                                    this.config.set(player.getLevel().getName(), pos);
                                    this.config.save();
                                    sender.sendMessage(player.getLevel().getName() + "世界的设置已加载");
                                } else {
                                    sender.sendMessage("§c当前世界还未设置！");
                                }
                                break;
                            default:
                                sender.sendMessage("§e请使用以下命令圈出一个方形");
                                sender.sendMessage("§c警告：请按顺序设置！");
                                sender.sendMessage("§a/boundary §e打开ui界面");
                                sender.sendMessage("/boundary pos1 设置当前位置为点一");
                                sender.sendMessage("/boundary pos2 设置当前位置为点二");
                                sender.sendMessage("/boundary reload 重新加载当前世界设置");
                        }
                    } else {
                        UiCreate.sendAdminMenu(player);
                    }
                }else {
                    sender.sendMessage("§c你没有权限！");
                }
            }else {
                sender.sendMessage("§c请在游戏内执行命令！");
            }
            return true;
        }
        return false;
    }

    public ConcurrentHashMap<Level, Border> getBorders() {
        return this.borders;
    }

}
