package cn.lanink.worldborder.listener;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.border.Borders;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;

/**
 * @author lt_name
 */
public class PlayerTeleportListener implements Listener {

    private final WorldBorder worldBorder;

    public PlayerTeleportListener(WorldBorder worldBorder) {
        this.worldBorder = worldBorder;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Level level = event.getTo().getLevel();
        if (level != null && !event.getPlayer().isOp()) {
            Borders borders = this.worldBorder.getBorders().get(level.getName());
            if (borders != null && borders.isInside(event.getTo()) == null) {
                event.getPlayer().sendMessage("§e >> §c 无法传送到世界边界外！");
                event.setCancelled(true);
            }
        }
    }

}
