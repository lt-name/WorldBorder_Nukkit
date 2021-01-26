package cn.lanink.worldborder.listener;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.entity.EntityText;
import cn.lanink.worldborder.utils.Border;
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
    private boolean cancelled;

    public PlayerMoveListener(WorldBorder worldBorder, boolean cancelled) {
        this.worldBorder = worldBorder;
        this.cancelled = cancelled;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.isOp()) {
            return;
        }
        Border border = this.worldBorder.getBorders().get(player.getLevel());
        if (border != null && border.isOutside(event.getTo())) {
            event.setCancelled(this.cancelled);
            player.sendActionBar("§c请在世界边界内活动！");
            EntityText entityText = this.worldBorder.getEntityTexts().get(player);
            if (entityText == null) {
                EntityText text = new EntityText(player.getChunk(), EntityText.getDefaultNBT(player), player, 5);
                text.spawnTo(player);
                this.worldBorder.getEntityTexts().put(player, text);
            }else {
                entityText.resetSurvivalTime();
                entityText.setPosition(player.clone().add(0, 1, 0));
            }
        }
    }

}
