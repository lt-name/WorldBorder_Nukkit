package cn.lanink.worldborder.task;

import cn.lanink.worldborder.WorldBorder;
import cn.lanink.worldborder.utils.Border;
import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.PluginTask;

/**
 * @author lt_name
 */
public class CheckPlayerPosTask extends PluginTask<WorldBorder> {

    public CheckPlayerPosTask(WorldBorder owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for (Border border : this.getOwner().getBorders().values()) {
            if (border.getLevel() == null) {
                continue;
            }
            for (Player player : border.getLevel().getPlayers().values()) {
                Vector3 motion = border.getReboundMotion(player);
                if (motion != null) {
                    player.setMotion(motion);
                }
            }
        }
    }

}
