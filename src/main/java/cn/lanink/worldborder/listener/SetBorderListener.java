package cn.lanink.worldborder.listener;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.border.Border;
import cn.lanink.worldborder.border.Borders;
import cn.lanink.worldborder.form.FormCreate;
import cn.lanink.worldborder.task.CheckPlayerPosTask;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author LT_Name
 */
public class SetBorderListener implements Listener {

    private final WorldBorder worldBorder;

    public SetBorderListener(WorldBorder worldBorder) {
        this.worldBorder = worldBorder;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.worldBorder.getPlayerSet().remove(player);
        this.worldBorder.getPlayerLastInBorder().remove(player);
        CheckPlayerPosTask.playerOutsizeTime.remove(player);
    }

    @EventHandler
    public void onEntityLevelChange(EntityLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (this.worldBorder.getPlayerSet().containsKey(player)) {
                this.worldBorder.getPlayerSet().remove(player);
                player.sendTitle("", "已取消添加边界！");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.onOperatingBlock(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (this.onOperatingBlock(event.getPlayer(), event.getBlock())) {
            event.setCancelled(true);
        }
    }

    public boolean onOperatingBlock(@NotNull Player player, @NotNull Block block) {
        if (this.worldBorder.getPlayerSet().containsKey(player)) {
            Border border = this.worldBorder.getPlayerSet().get(player);
            if (border.getBorderType() == Border.BorderType.SQUARE) {
                if (border.getMinX() == null || border.getMinZ() == null) {
                    border.setMinX(block.getFloorX() + 0.5);
                    border.setMinZ(block.getFloorZ() + 0.5);
                    player.sendTitle("", "坐标一设置完成，请继续设置坐标二！");
                } else {
                    border.setMaxX(block.getFloorX() + 0.5);
                    border.setMaxZ(block.getFloorZ() + 0.5);
                    border.check();
                    player.sendTitle("", "坐标二设置完成，添加边界成功！");
                    this.worldBorder.getPlayerSet().remove(player);
                    Borders borders = this.worldBorder.getBorders(player.getLevel());
                    if (borders == null) {
                        borders = new Borders(player.getLevel());
                        this.worldBorder.getBorders().put(player.getLevel().getName(), borders);
                    }
                    borders.addBorder(border);
                    borders.saveConfig();
                }
            }else {
                border.setMinX(block.getFloorX() + 0.5);
                border.setMinZ(block.getFloorZ() + 0.5);
                border.setMaxX(block.getFloorX() + 0.5);
                border.setMaxZ(block.getFloorZ() + 0.5);
                FormCreate.sendSetBorderRadius(player, border);
            }
            return true;
        }
        return false;
    }

}
