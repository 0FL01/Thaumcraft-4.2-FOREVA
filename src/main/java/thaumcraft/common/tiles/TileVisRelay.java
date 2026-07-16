package thaumcraft.common.tiles;

import java.awt.Color;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.TCSounds;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class TileVisRelay extends TileVisNode implements IWandable {
    public static final HashMap<Integer, WeakReference<TileVisRelay>> nearbyPlayers = new HashMap<>();
    public static final int[] colors = new int[]{0xFFFF7E, 16727041, 37119, 40960, 0xEECCFF, 0x555577};

    public byte orientation = 1;
    public byte color = -1;
    protected Object beam1;
    protected int pulse;
    public float pRed = 0.5F;
    public float pGreen = 0.5F;
    public float pBlue = 0.5F;
    protected int px;
    protected int py;
    protected int pz;
    protected boolean parentLoaded;

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
        this.drawEffect();
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

    protected void drawEffect() {
        if (this.world != null && this.world.isRemote) {
            if (this.parentLoaded) {
                if (this.px == 0 && this.py == 0 && this.pz == 0) {
                    this.setParent(null);
                    this.parentLoaded = false;
                    this.parentChanged();
                } else {
                    net.minecraft.util.math.BlockPos parentPos = this.pos.add(-this.px, -this.py, -this.pz);
                    if (this.world.isBlockLoaded(parentPos)) {
                        TileEntity tile = this.world.getTileEntity(parentPos);
                        this.setParent(tile instanceof TileVisNode
                                ? new WeakReference<>((TileVisNode) tile) : null);
                        this.parentLoaded = false;
                        this.parentChanged();
                    }
                }
            }

            if (VisNetHandler.isNodeValid(this.getParent())) {
                TileVisNode parentNode = this.getParent().get();
                double parentOffsetX = 0.0D;
                double parentOffsetY = 0.0D;
                double parentOffsetZ = 0.0D;
                if (parentNode instanceof TileVisRelay) {
                    EnumFacing parentFacing = EnumFacing.byIndex(((TileVisRelay) parentNode).orientation);
                    parentOffsetX = parentFacing.getXOffset() * 0.05D;
                    parentOffsetY = parentFacing.getYOffset() * 0.05D;
                    parentOffsetZ = parentFacing.getZOffset() * 0.05D;
                }
                EnumFacing facing = EnumFacing.byIndex(this.orientation);
                this.beam1 = Thaumcraft.proxy.beamPower(this.world,
                        parentNode.getPos().getX() + 0.5D - parentOffsetX,
                        parentNode.getPos().getY() + 0.5D - parentOffsetY,
                        parentNode.getPos().getZ() + 0.5D - parentOffsetZ,
                        this.pos.getX() + 0.5D - facing.getXOffset() * 0.05D,
                        this.pos.getY() + 0.5D - facing.getYOffset() * 0.05D,
                        this.pos.getZ() + 0.5D - facing.getZOffset() * 0.05D,
                        this.pRed, this.pGreen, this.pBlue, this.pulse > 0, this.beam1);
            } else {
                this.beam1 = null;
            }

            this.pRed = Math.min(1.0F, this.pRed + 0.025F);
            this.pGreen = Math.min(1.0F, this.pGreen + 0.025F);
            this.pBlue = Math.min(1.0F, this.pBlue + 0.025F);
        }
        if (this.pulse > 0) {
            --this.pulse;
        }
    }

    private int ticksExisted() {
        return this.world == null ? 0 : (int)(this.world.getTotalWorldTime() & Integer.MAX_VALUE);
    }

    @Override
    public void triggerConsumeEffect(Aspect aspect) {
        this.addPulse(aspect);
    }

    protected void addPulse(Aspect aspect) {
        int primal = getPulseColorIndex(aspect);
        if (this.world != null && !this.world.isRemote && primal >= 0 && primal < colors.length && this.pulse == 0) {
            this.pulse = 5;
            this.world.addBlockEvent(this.pos, this.world.getBlockState(this.pos).getBlock(), 0, primal);
        }
    }

    private static int getPulseColorIndex(Aspect aspect) {
        if (aspect == Aspect.AIR) return 0;
        if (aspect == Aspect.FIRE) return 1;
        if (aspect == Aspect.WATER) return 2;
        if (aspect == Aspect.EARTH) return 3;
        if (aspect == Aspect.ORDER) return 4;
        if (aspect == Aspect.ENTROPY) return 5;
        return -1;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 0 && type >= 0 && type < colors.length) {
            if (this.world != null && this.world.isRemote) {
                Color pulseColor = new Color(colors[type]);
                this.pulse = 5;
                this.pRed = pulseColor.getRed() / 255.0F;
                this.pGreen = pulseColor.getGreen() / 255.0F;
                this.pBlue = pulseColor.getBlue() / 255.0F;
                WeakReference<TileVisNode> relayParent = this.getParent();
                while (VisNetHandler.isNodeValid(relayParent)
                        && relayParent.get() instanceof TileVisRelay
                        && ((TileVisRelay) relayParent.get()).pulse == 0) {
                    TileVisRelay relay = (TileVisRelay) relayParent.get();
                    relay.pRed = this.pRed;
                    relay.pGreen = this.pGreen;
                    relay.pBlue = this.pBlue;
                    relay.pulse = 5;
                    relayParent = relay.getParent();
                }
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }

    @Override
    public void parentChanged() {
        if (this.world != null && this.world.isRemote) {
            this.world.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
            this.world.markBlockRangeForRenderUpdate(this.pos, this.pos);
        }
    }

    @Override
    public void invalidate() {
        this.beam1 = null;
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        this.beam1 = null;
        super.onChunkUnload();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt) {
        this.orientation = nbt.getByte("orientation");
        this.color = nbt.hasKey("color") ? nbt.getByte("color") : -1;
        this.attunement = this.color;
        this.px = nbt.getByte("px");
        this.py = nbt.getByte("py");
        this.pz = nbt.getByte("pz");
        this.parentLoaded = true;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt) {
        nbt.setByte("orientation", this.orientation);
        nbt.setByte("color", this.color);
        if (VisNetHandler.isNodeValid(this.getParent())) {
            TileVisNode parentNode = this.getParent().get();
            nbt.setByte("px", (byte) (this.pos.getX() - parentNode.getPos().getX()));
            nbt.setByte("py", (byte) (this.pos.getY() - parentNode.getPos().getY()));
            nbt.setByte("pz", (byte) (this.pos.getZ() - parentNode.getPos().getZ()));
        } else {
            nbt.setByte("px", (byte) 0);
            nbt.setByte("py", (byte) 0);
            nbt.setByte("pz", (byte) 0);
        }
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
