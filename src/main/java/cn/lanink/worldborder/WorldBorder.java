package cn.lanink.worldborder;

import cn.lanink.worldborder.entity.EntityText;
import cn.lanink.worldborder.ui.UiCreate;
import cn.lanink.worldborder.ui.UiListener;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;

import java.util.HashMap;

public class WorldBorder extends PluginBase implements Listener {

    private final HashMap<Level, HashMap<String, Integer>> levels = new HashMap<>();
    private final HashMap<Level, HashMap<String, Integer>> cache = new HashMap<>();
    private Config config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.config = getConfig();
        for (String key : this.config.getAll().keySet()) {
            if (getServer().getLevelByName(key) == null && !getServer().loadLevel(key)) {
                continue;
            }
            HashMap<String, Integer> pos = config.get(key, new HashMap<>());
            this.levels.put(getServer().getLevelByName(key), pos);
        }
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new UiListener(), this);
        getLogger().info("§a 插件加载完成！");
    }

    @Override
    public void onDisable() {
        this.cache.clear();
        this.levels.clear();
        getLogger().info("§c 插件已卸载！");
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
                                this.cache.get(player.getLevel()).put("x1", player.getFloorX());
                                this.cache.get(player.getLevel()).put("z1", player.getFloorZ());
                                sender.sendMessage("§a当前位置已保存为点一");
                                break;
                            case "pos2": case "点二":
                                if (!this.cache.containsKey(player.getLevel())) {
                                    this.cache.put(player.getLevel(), new HashMap<>());
                                }
                                this.cache.get(player.getLevel()).put("x2", player.getFloorX());
                                this.cache.get(player.getLevel()).put("z2", player.getFloorZ());
                                sender.sendMessage("§a当前位置已保存为点二");
                                break;
                            case "reload": case "重载":
                                if (this.cache.containsKey(player.getLevel())) {
                                    HashMap<String, Integer> pos = this.cache.get(player.getLevel());
                                    if (pos.get("x1") == null || pos.get("z1") == null ||
                                            pos.get("x2") == null || pos.get("z2") == null) {
                                        sender.sendMessage("§c当前世界还未设置完！");
                                        return true;
                                    }
                                    this.levels.put(player.getLevel(), pos);
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) { return; }
        Player player = event.getPlayer();
        if (player == null || player.isOp()) {
            return;
        }
        HashMap<String, Integer> pos = this.levels.get(player.getLevel());
        Location location = event.getTo();
        if (pos == null || location == null) {
            return;
        }
        int x = location.getFloorX();
        int z = location.getFloorZ();
        int x1 = pos.get("x1");
        int z1 = pos.get("z1");
        int x2 = pos.get("x2");
        int z2 = pos.get("z2");
        if ((x < x1 || x < x2) && (x > x2 || x > x1) &&
                (z < z1 || z < z2) && (z > z2 || z > z1)) {
            return;
        }
        event.setCancelled(true);
        player.sendActionBar("§c请在规定范围内活动！");
        EntityText text = new EntityText(player.getChunk(), EntityText.getDefaultNBT(player), player);
        text.spawnTo(player);
        getServer().getScheduler().scheduleDelayedTask(this, new Task() {
            @Override
            public void onRun(int i) {
                text.close();
            }
        }, 40);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) { return; }
        Level level = event.getTo().level;
        if (level != null && !event.getPlayer().isOp()) {
            if (this.levels.containsKey(level)) {
                int x = event.getTo().getFloorX();
                int z = event.getTo().getFloorZ();
                int x1 = this.levels.get(level).get("x1");
                int z1 = this.levels.get(level).get("z1");
                int x2 = this.levels.get(level).get("x2");
                int z2 = this.levels.get(level).get("z2");
                if ((x < x1 || x < x2) && (x > x2 || x > x1) &&
                        (z < z1 || z < z2) && (z > z2 || z > z1)) {
                    return;
                }
                event.getPlayer().sendMessage("§e >> §c 请勿传送到规定范围外！");
                event.setCancelled(true);
            }
        }
    }

}
