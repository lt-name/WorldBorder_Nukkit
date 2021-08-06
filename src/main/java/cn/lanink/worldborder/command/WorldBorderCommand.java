package cn.lanink.worldborder.command;

import cn.lanink.worldborder.command.worldborder.AddBorderCommand;
import cn.lanink.worldborder.command.worldborder.ReloadCommand;
import cn.lanink.worldborder.form.FormCreate;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

/**
 * @author lt_name
 */
public class WorldBorderCommand extends BaseCommand {

    public WorldBorderCommand() {
        super("WorldBorder", "WorldBorder管理命令");
        this.setPermission("worldborder.admin");

        this.addSubCommand(new AddBorderCommand("AddBorder"));
        this.addSubCommand(new ReloadCommand("Reload"));

        this.loadCommandBase();
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§e====WorldBorder====");
        sender.sendMessage("§a/WorldBorder §e打开GUI界面（推荐在游戏内使用GUI设置）");
        sender.sendMessage("§a/WorldBorder AddBorder <形状> <名称> §e在当前世界添加新的边界");
        sender.sendMessage("§a/WorldBorder Reload §e重载配置");
        sender.sendMessage("§e====WorldBorder====");
    }

    @Override
    public void sendUi(CommandSender sender) {
        FormCreate.sendAdminMenu((Player) sender);
    }

}
