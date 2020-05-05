package cn.lanink.worldboundary.ui;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;

public class UiCreate {

    public static final String PLUGIN_NAME = "§l§7[§1W§2o§3r§4l§5d§6B§ao§cu§bn§dd§9a§6r§2y§7]";
    public static final int ADMIN_MENU = 795938203;

    public static void sendAdminMenu(Player player) {
        FormWindowSimple simple = new FormWindowSimple(PLUGIN_NAME, "§a当前设置世界：" + player.getLevel().getName());
        simple.addButton(new ElementButton("§e设置pos1", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e设置pos2", new ElementButtonImageData("path", "textures/ui/World")));
        simple.addButton(new ElementButton("§e重载当前世界设置", new ElementButtonImageData("path", "textures/ui/refresh_light")));
        player.showFormWindow(simple, ADMIN_MENU);
    }

}
