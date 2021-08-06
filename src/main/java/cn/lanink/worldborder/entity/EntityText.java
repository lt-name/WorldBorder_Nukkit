package cn.lanink.worldborder.entity;

import cn.lanink.worldborder.WorldBorder;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * @author lt_name
 */
public class EntityText extends Entity {

    private Player player;
    private final int setSurvivalTime;
    private int survivalTime;

    @Override
    public int getNetworkId() {
        return 64;
    }

    @Deprecated
    public EntityText(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.setSurvivalTime = 0;
        this.close();
    }

    public EntityText(@NotNull FullChunk chunk, @NotNull CompoundTag nbt, @NotNull Player player) {
        this(chunk, nbt, player, 5);
    }

    public EntityText(@NotNull FullChunk chunk, @NotNull CompoundTag nbt, @NotNull Player player, int survivalTime) {
        super(chunk, nbt);
        this.player = player;
        this.setSurvivalTime = survivalTime;
        this.survivalTime = survivalTime;
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        this.setNameTag("§e╭──────────────────╮\n" +
                        "§e|      §c前面的区域       §e|\n" +
                        "§e|    §c以后再来探索吧！    §e|\n" +
                        "§e╰──────────────────╯");
        this.setPosition(new Vector3(player.x, player.y + 1, player.z));
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(20);
        this.setHealth(20.0F);
        this.setImmobile(true);
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (currentTick%20 == 0) {
            this.survivalTime--;
            if (this.survivalTime <= 0) {
                this.close();
            }
        }
        return super.onUpdate(currentTick);
    }

    @Override
    public void close() {
        super.close();
        if (this.player != null) {
            WorldBorder.getInstance().getEntityTexts().remove(this.player);
        }
    }

    public void resetSurvivalTime() {
        this.survivalTime = this.setSurvivalTime;
        if (this.isClosed()) {
            EntityText text = new EntityText(this.getChunk(), this.namedTag, this.player, 5);
            text.spawnTo(this.player);
            WorldBorder.getInstance().getEntityTexts().put(player, text);
        }
    }

}
