package cn.lanink.worldborder.task;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.border.Border;
import cn.lanink.worldborder.border.Borders;
import cn.nukkit.Player;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lt_name
 */
public class CheckPlayerPosTask extends PluginTask<WorldBorder> {

    public static final ConcurrentHashMap<Player, Integer> playerOutsizeTime = new ConcurrentHashMap<>();

    public CheckPlayerPosTask(WorldBorder owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for (Borders borders : this.getOwner().getBorders().values()) {
            for (Player player : borders.getLevel().getPlayers().values()) {
                if (borders.isInside(player) == null) {
                    Border lastBorder = this.owner.getPlayerLastInBorder().get(player);
                    if (lastBorder != null) {
                        //玩家最后一次在的边界允许玩家离开 不回弹
                        if (lastBorder.canLeave(player)) {
                            continue;
                        }
                    }else {
                        lastBorder = new ArrayList<>(borders.getBorders()).get(new Random().nextInt(borders.getBorders().size()));
                        playerOutsizeTime.put(player, 200);
                    }
                    int newValue = playerOutsizeTime.getOrDefault(player, 0) + 1;
                    if (newValue > 100) { //在边界外活动超过100tick 直接传送到最近边界中心
                        Position position = new Position(0, 255, 0, player.getLevel());
                        if (lastBorder.getBorderType() == Border.BorderType.ROUND) {
                            position.setX(lastBorder.getMinX());
                            position.setZ(lastBorder.getMinZ());
                        }else {
                            position.setX(lastBorder.getMinX() + (lastBorder.getMaxX() - lastBorder.getMinX())/2);
                            position.setZ(lastBorder.getMinZ() + (lastBorder.getMaxZ() - lastBorder.getMinZ())/2);
                        }
                        for (int y = 255; y > 0; y--) {
                            position.setY(y);
                            if (!position.getLevelBlock().canPassThrough()) {
                                break;
                            }
                        }
                        player.teleport(position);
                    }else {
                        playerOutsizeTime.put(player, newValue);
                        Vector3 motion = lastBorder.getReboundMotion(player);
                        if (motion != null) {
                            player.setMotion(motion);
                        }
                    }
                }else {
                    playerOutsizeTime.remove(player);
                }
            }
        }
    }

}
