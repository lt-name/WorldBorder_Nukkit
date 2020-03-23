package name.WorldBoundary;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import updata.AutoData;

import java.util.LinkedHashMap;

public class WorldBoundary extends PluginBase implements Listener {

    private LinkedHashMap<Level, LinkedHashMap<String, Integer>> levels = new LinkedHashMap<Level, LinkedHashMap<String, Integer>>();
    private LinkedHashMap<Level, LinkedHashMap<String, Integer>> cache = new LinkedHashMap<Level, LinkedHashMap<String, Integer>>();
    private Config config;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("AutoUpData") != null) {
            getLogger().info("§e 检查更新中...");
            if (AutoData.defaultUpData(this, getFile(), "lt-name", "WorldBoundary_Nukkit")) {
                return;
            }
        }
        saveDefaultConfig();
        this.config = getConfig();
        for (String key : this.config.getAll().keySet()) {
            LinkedHashMap xz = (LinkedHashMap)config.get(key);
            this.levels.put(getServer().getLevelByName(key), xz);
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("§a 插件加载完成！");
    }

/*    @Override
    public void onDisable() {
        for (Level level : levels.keySet()) {
            this.config.set(level.getName(), this.levels.get(level));
            this.config.save();
        }
        getLogger().info("§c 插件已卸载！");
    }*/

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("boundary")) {
            if (sender instanceof Player) {
                if (args.length > 0) {
                    Player player = ((Player) sender).getPlayer();
                    switch (args[0]) {
                        case "点一":
                            if (!this.cache.containsKey(player.getLevel())) {
                                this.cache.put(player.getLevel(), new LinkedHashMap<String, Integer>());
                            }
                            this.cache.get(player.getLevel()).put("x1", player.getFloorX());
                            this.cache.get(player.getLevel()).put("z1", player.getFloorZ());
                            sender.sendMessage("§a 当前位置已保存为点一");
                            break;
                        case "点二":
                            if (!this.cache.containsKey(player.getLevel())) {
                                this.cache.put(player.getLevel(), new LinkedHashMap<String, Integer>());
                            }
                            this.cache.get(player.getLevel()).put("x2", player.getFloorX());
                            this.cache.get(player.getLevel()).put("z2", player.getFloorZ());
                            sender.sendMessage("§a 当前位置已保存为点二");
                            break;
                        case "保存":
                            if (this.cache.containsKey(player.getLevel())) {
                                this.levels.put(player.getLevel(), this.cache.get(player.getLevel()));
                                this.config.set(player.getLevel().getName(), this.cache.get(player.getLevel()));
                                this.config.save();
                                sender.sendMessage(player.getLevel().getName() + "世界的设置已加载");
                            }else {
                                sender.sendMessage("当前世界还未设置！");
                            }
                            break;
                        default:
                            sender.sendMessage("请使用以下命令圈出一个方形");
                            sender.sendMessage("§c警告：请按顺序设置！");
                            sender.sendMessage("/boundary 点一 设置当前位置为点一");
                            sender.sendMessage("/boundary 点二 设置当前位置为点二");
                            sender.sendMessage("/boundary 保存 保存当前世界设置");
                    }
                }else {
                    sender.sendMessage("/boundary help 查看帮助！");
                }
            }else {
                sender.sendMessage("§c 请在游戏内执行命令！");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPME(PlayerMoveEvent event) {
        if (event.isCancelled()) { return; }
        Player player = event.getPlayer();
        if (player != null) {
            if (getServer().isOp(player.getName())) { return; }
            if (this.levels.containsKey(player.getLevel())) {
                int x = player.getFloorX();
                int z = player.getFloorZ();
                int x1 = this.levels.get(player.getLevel()).get("x1");
                int z1 = this.levels.get(player.getLevel()).get("z1");
                int x2 = this.levels.get(player.getLevel()).get("x2");
                int z2 = this.levels.get(player.getLevel()).get("z2");
                if ((x < x1 || x < x2) && (x > x2 || x > x1) &&
                        (z < z1 || z < z2) && (z > z2 || z > z1)) {
                    return;
                }
                player.sendMessage("§e >> §c 请在规定范围内活动！");
                event.setCancelled();
            }
        }
    }

    @EventHandler
    public void onPTE(PlayerTeleportEvent event) {
        if (event.isCancelled()) { return; }
        Level level = event.getTo().level;
        if (level != null) {
            if (this.levels.containsKey(level)) {
                int x = event.getTo().getFloorX();
                int z = event.getTo().getFloorZ();
                int x1 = this.levels.get(level).get("x1");
                int z1 = this.levels.get(level).get("z1");
                int x2 = this.levels.get(level).get("x2");
                int z2 = this.levels.get(level).get("z2");
                if ((x<x1 || x<x2) && (x>x2 || x>x1) &&
                        (z<z1 || z<z2) && (z>z2 || z>z1)) {
                    return;
                }
                event.getPlayer().sendMessage("§e >> §c 请勿传送到规定范围外！");
                event.setCancelled();
            }
        }
    }

}
