package cn.lanink.worldborder.command.worldborder;

import cn.lanink.worldborder.border.Border;
import cn.lanink.worldborder.border.Borders;
import cn.lanink.worldborder.command.BaseSubCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;

import java.util.HashMap;

/**
 * @author LT_Name
 */
public class AddBorderCommand extends BaseSubCommand {

    public AddBorderCommand(String name) {
        super(name);
    }

    @Override
    public boolean canUser(CommandSender sender) {
        return sender.isPlayer() && sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        HashMap<Player, Border> playerSet = this.worldBorder.getPlayerSet();
        if (playerSet.containsKey(player)) {
            playerSet.remove(player);
            player.sendTitle("", "已取消添加边界！");
        }else {
            if (args.length > 2) {
                Borders borders = this.worldBorder.getBorders(player.getLevel());
                if (borders != null && borders.getBorderByName(args[2]) != null) {
                    player.sendTitle("", "已存在名称为 " + args[2] + " 的边界！");
                    return true;
                }
                player.sendMessage("请通过放置/破坏方块的方式设置坐标");
                Border border;
                if ("圆形".equals(args[1])) {
                    border = new Border(args[2], Border.BorderType.ROUND, player.getLevel());
                    player.sendTitle("", "请设置圆心");
                }else {
                    border = new Border(args[2], Border.BorderType.SQUARE, player.getLevel());
                    player.sendTitle("", "请设置坐标一");
                }
                playerSet.put(player, border);
            }else {
                player.sendTitle("", "请输入边界形状和边界名称");
            }
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[] {
                CommandParameter.newEnum("边界形状", new String[]{"方形", "圆形"}),
                CommandParameter.newType("名称", CommandParamType.TEXT)
        };
    }
}
