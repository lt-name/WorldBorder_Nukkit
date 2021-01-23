package cn.lanink.worldborder.ui;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.window.FormWindowSimple;

public class UiListener implements Listener {

    @EventHandler
    public void onResponded(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        if (player == null || event.getWindow() == null || event.getResponse() == null) {
            return;
        }
        if (event.getWindow() instanceof FormWindowSimple) {
            if (event.getFormID() == UiCreate.ADMIN_MENU) {
                FormWindowSimple simple = (FormWindowSimple) event.getWindow();
                switch (simple.getResponse().getClickedButtonId()) {
                    case 0:
                        Server.getInstance().dispatchCommand(player, "boundary pos1");
                        break;
                    case 1:
                        Server.getInstance().dispatchCommand(player, "boundary pos2");
                        break;
                    case 2:
                        Server.getInstance().dispatchCommand(player, "boundary reload");
                        break;
                }
            }
        }
    }

}
