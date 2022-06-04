package cn.lanink.worldborder.listener;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.border.Border;
import cn.lanink.worldborder.border.Borders;
import cn.lanink.worldborder.entity.EntityText;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;

/**
 * @author lt_name
 */
public class PlayerMoveListener implements Listener {

    private final WorldBorder worldBorder;
    private final boolean cancelled;

    public PlayerMoveListener(WorldBorder worldBorder, boolean cancelled) {
        this.worldBorder = worldBorder;
        this.cancelled = cancelled;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        Borders borders = this.worldBorder.getBorders(player.getLevel().getName());
        if (borders == null) {
            return;
        }
        Border border = borders.isInside(event.getTo());
        if (border == null) {
            border = this.worldBorder.getPlayerLastInBorder().get(player);
            if (border != null && border.canLeave(player)) {
                return;
            }

            event.setCancelled(this.cancelled);
            player.sendTitle("", "§c前面的区域\n以后再来探索吧！", 1, 30, 10);
            EntityText entityText = this.worldBorder.getEntityTexts().get(player);
            if (entityText == null) {
                EntityText text = new EntityText(player.getChunk(), EntityText.getDefaultNBT(player), player, 5);
                text.spawnTo(player);
                this.worldBorder.getEntityTexts().put(player, text);
            }else {
                entityText.resetSurvivalTime();
                entityText.setPosition(player.clone().add(0, 1, 0));
            }
        }else {
            this.worldBorder.getPlayerLastInBorder().put(player, border);
        }
    }

}
