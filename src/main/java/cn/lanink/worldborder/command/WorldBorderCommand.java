package cn.lanink.worldborder.command;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.form.WindowCreate;
import cn.lanink.worldborder.utils.Border;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

/**
 * @author lt_name
 */
public class WorldBorderCommand extends Command {

    private final WorldBorder worldBorder = WorldBorder.getInstance();

    public WorldBorderCommand() {
        super("worldborder", "边界设置", "/worldborder help");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            if (player.hasPermission("worldborder.admin")) {
                if (args.length > 0) {
                    Border cacheBorder;
                    switch (args[0]) {
                        case "pos1":
                            cacheBorder = this.worldBorder.getCacheBorder(player.getLevel());
                            cacheBorder.setMinX(player.getX());
                            cacheBorder.setMinZ(player.getZ());
                            sender.sendMessage("§a当前位置已保存为点一");
                            break;
                        case "pos2":
                            cacheBorder = this.worldBorder.getCacheBorder(player.getLevel());
                            cacheBorder.setMaxX(player.getX());
                            cacheBorder.setMaxZ(player.getZ());
                            sender.sendMessage("§a当前位置已保存为点二");
                            break;
                        case "delete":
                            this.worldBorder.getCacheBorder().remove(player.getLevel());
                            if (this.worldBorder.getBorders().containsKey(player.getLevel())) {
                                this.worldBorder.getBorders().remove(player.getLevel());
                                this.worldBorder.getBorderConfig().remove(player.getLevel().getFolderName());
                                this.worldBorder.getBorderConfig().save();
                            }
                            sender.sendMessage("§a已删除当前世界的边界设置！");
                            break;
                        case "reload":
                            this.worldBorder.loadBorderConfig();
                            sender.sendMessage("§a已保存所有缓存设置并重载配置");
                            break;
                        default:
                            sender.sendMessage("§e请使用以下命令圈出一个方形");
                            sender.sendMessage("§c警告：请按顺序设置！");
                            sender.sendMessage("§a/worldborder §e打开ui界面");
                            sender.sendMessage("§a/worldborder pos1 §e设置当前位置为点一");
                            sender.sendMessage("§a/worldborder pos2 §e设置当前位置为点二");
                            sender.sendMessage("§a/worldborder delete §e删除当前位置的边界设置");
                            sender.sendMessage("§a/worldborder reload §e保存所有缓存设置并重载配置");
                            break;
                    }
                } else {
                    WindowCreate.sendAdminMenu(player);
                }
            }else {
                sender.sendMessage("§c你没有权限使用此命令！");
            }
        }else {
            sender.sendMessage("§c请在游戏内执行命令！");
        }
        return true;
    }

}
