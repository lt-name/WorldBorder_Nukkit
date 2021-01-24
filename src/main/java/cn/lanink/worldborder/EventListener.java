package cn.lanink.worldborder;

import cn.lanink.worldborder.entity.EntityText;
import cn.lanink.worldborder.utils.Border;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.level.Level;

/**
 * @author lt_name
 */
public class EventListener implements Listener {

    private final WorldBorder worldBorder;

    public EventListener(WorldBorder worldBorder) {
        this.worldBorder = worldBorder;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) { return; }
        Player player = event.getPlayer();
        if (player == null || player.isOp()) {
            return;
        }
        Border border = this.worldBorder.getBorders().get(player.getLevel());
        if (border != null && border.isOutside(event.getTo())) {
            event.setCancelled(true);
            player.sendActionBar("§c请在规定范围内活动！");
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

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) { return; }
        Level level = event.getTo().level;
        if (level != null && !event.getPlayer().isOp()) {
            Border border = this.worldBorder.getBorders().get(level);
            if (border != null && border.isOutside(event.getTo())) {
                event.getPlayer().sendMessage("§e >> §c 请勿传送到规定范围外！");
                event.setCancelled(true);
            }
        }
    }

}
