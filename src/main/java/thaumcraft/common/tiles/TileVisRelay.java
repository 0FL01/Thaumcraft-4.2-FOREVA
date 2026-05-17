package thaumcraft.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.lib.TCSounds;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class TileVisRelay extends TileVisNode implements IWandable {
    public static final HashMap<Integer, WeakReference<TileVisRelay>> nearbyPlayers = new HashMap<>();

    public byte orientation = 1;
    public byte color = -1;

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public boolean isSource() {
        return false;
    }

    @Override
    public byte getAttunement() {
        return color;
    }

    @Override
    public void update() {
        super.update();
        if (this.world == null || this.world.isRemote || this.ticksExisted() % 20 != 0) return;
        List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(this.pos).grow(5.0D));
        for (EntityPlayer player : players) {
            WeakReference<TileVisRelay> currentRef = nearbyPlayers.get(player.getEntityId());
            TileVisRelay current = currentRef == null ? null : currentRef.get();
            if (current == null || current.isInvalid() || player.getDistanceSq(this.pos) < player.getDistanceSq(current.getPos())) {
                nearbyPlayers.put(player.getEntityId(), new WeakReference<>(this));
            }
        }
    }

    private int ticksExisted() {
        return this.world == null ? 0 : (int)(this.world.getTotalWorldTime() & Integer.MAX_VALUE);
    }

    @Override
    public void triggerConsumeEffect(Aspect aspect) {
        this.markDirty();
        if (this.world != null) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = nbt.getByte("orientation");
        this.color = nbt.hasKey("color") ? nbt.getByte("color") : -1;
        this.attunement = this.color;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("orientation", this.orientation);
        nbt.setByte("color", this.color);
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int event) {
        if (world != null && !world.isRemote) {
            cycleColor();
        }
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
    }

    private void cycleColor() {
        if (this.world == null) return;
        this.color++;
        if (this.color > 5) this.color = -1;
        this.removeThisNode();
        this.attunement = this.color;
        this.nodeRefresh = true;
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        this.world.playSound(null, this.pos, TCSounds.CRYSTAL, SoundCategory.BLOCKS, 0.2F, 1.0F);
    }
}
