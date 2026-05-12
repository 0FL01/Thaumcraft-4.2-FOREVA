package thaumcraft.common.entities.ai.interact;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.GolemHelper;
import thaumcraft.common.entities.golems.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AIUseItem extends EntityAIBase {

    private EntityGolemBase theGolem;
    private int xx;
    private int yy;
    private int zz;
    private float movementSpeed;
    private float distance;
    private World theWorld;
    private Block block = Blocks.AIR;
    private int blockMd = 0;
    private FakePlayer player;
    private int count = 0;
    private int color = -1;
    private PlayerInteractionManager im;
    int nextTick = 0;

    private static final GameProfile GAME_PROFILE = new GameProfile(
        UUID.nameUUIDFromBytes("thaumcraft:golem_ai".getBytes(java.nio.charset.StandardCharsets.UTF_8)),
        "[TCGolem]");

    public AIUseItem(EntityGolemBase golem) {
        this.theGolem = golem;
        this.theWorld = golem.world;
        this.setMutexBits(3);
        this.distance = MathHelper.floor(golem.getRange() / 3.0f);
        if (this.theWorld instanceof WorldServer) {
            this.player = FakePlayerFactory.get((WorldServer) this.theWorld, GAME_PROFILE);
        }
        try {
            this.nextTick = this.theGolem.ticksExisted + this.theWorld.rand.nextInt(6);
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean shouldExecute() {
        BlockPos home = this.theGolem.getHomePosition();
        EnumFacing facing = EnumFacing.VALUES[this.theGolem.homeFacing % EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        TileEntity tile = this.theGolem.world.getTileEntity(new BlockPos(cX, cY, cZ));
        boolean ignoreItem = tile == null || !(tile instanceof IInventory);

        int d = 5 - this.theGolem.ticksExisted;
        if (d < 1) d = 1;

        if ((this.theGolem.itemCarried == null || this.theGolem.itemCarried.isEmpty()) && !ignoreItem) return false;
        if (this.theGolem.ticksExisted < this.nextTick) return false;
        if (!this.theGolem.getNavigator().noPath()) return false;

        this.nextTick = this.theGolem.ticksExisted + d * 3;
        return this.findSomething();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.theWorld.getBlockState(new BlockPos(this.xx, this.yy, this.zz)).getBlock() == this.block
            && this.theWorld.getBlockState(new BlockPos(this.xx, this.yy, this.zz)).getBlock()
                .getMetaFromState(this.theWorld.getBlockState(new BlockPos(this.xx, this.yy, this.zz))) == this.blockMd
            && this.count-- > 0
            && !this.theGolem.getNavigator().noPath();
    }

    @Override
    public void updateTask() {
        this.theGolem.getLookHelper().setLookPosition(this.xx + 0.5, this.yy + 0.5, this.zz + 0.5, 30.0f, 30.0f);
        double dist = this.theGolem.getDistanceSq(this.xx + 0.5, this.yy + 0.5, this.zz + 0.5);
        if (dist <= 4.0) {
            this.click();
        }
    }

    @Override
    public void resetTask() {
        this.count = 0;
        this.theGolem.getNavigator().clearPath();
    }

    @Override
    public void startExecuting() {
        this.count = 200;
        this.theGolem.getNavigator().tryMoveToXYZ(this.xx + 0.5, this.yy + 0.5, this.zz + 0.5, this.theGolem.getAIMoveSpeed());
    }

    void click() {
        if (!(this.theWorld instanceof WorldServer)) return;
        BlockPos home = this.theGolem.getHomePosition();
        EnumFacing facing = EnumFacing.VALUES[this.theGolem.homeFacing % EnumFacing.VALUES.length];
        int cX = home.getX() - facing.getXOffset();
        int cY = home.getY() - facing.getYOffset();
        int cZ = home.getZ() - facing.getZOffset();
        TileEntity tile = this.theGolem.world.getTileEntity(new BlockPos(cX, cY, cZ));
        boolean ignoreItem = tile != null && tile instanceof IInventory;

        this.player.setPositionAndRotation(this.theGolem.posX, this.theGolem.posY, this.theGolem.posZ,
            this.theGolem.rotationYaw, this.theGolem.rotationPitch);
        this.player.setHeldItem(EnumHand.MAIN_HAND,
            this.theGolem.itemCarried != null ? this.theGolem.itemCarried.copy() : net.minecraft.item.ItemStack.EMPTY);

        java.util.List<Integer> sides = GolemHelper.getMarkedSides(this.theGolem, this.xx, this.yy, this.zz,
            this.theWorld.provider.getDimension(), (byte) this.color);
        for (Integer sideInt : sides) {
            int x = 0, y = 0, z = 0;
            if (this.theGolem.world.isAirBlock(new BlockPos(this.xx, this.yy, this.zz))) {
                EnumFacing s = EnumFacing.VALUES[sideInt % EnumFacing.VALUES.length];
                x = s.getOpposite().getXOffset();
                y = s.getOpposite().getYOffset();
                z = s.getOpposite().getZOffset();
            }
            if (this.im == null) {
                this.im = new PlayerInteractionManager(this.theWorld);
            }
            if ((this.theGolem.itemCarried == null || this.theGolem.itemCarried.isEmpty()) && ignoreItem) {
                this.resetTask();
                return;
            }
            try {
                if (this.theGolem.getToggles()[1]) {
                    // Left-click mode (break)
                    this.theGolem.startLeftArmTimer();
                    this.im.tryHarvestBlock(
                        new BlockPos(this.xx + x, this.yy + y, this.zz + z));
                } else {
                    // Right-click mode (place/use)
                    net.minecraft.util.math.BlockPos targetPos = new BlockPos(this.xx + x, this.yy + y, this.zz + z);
                    EnumFacing clickSide = EnumFacing.VALUES[sideInt % EnumFacing.VALUES.length];
                    net.minecraft.util.EnumActionResult result = this.im.processRightClickBlock(
                        this.player, this.theWorld, this.player.getHeldItem(EnumHand.MAIN_HAND),
                        EnumHand.MAIN_HAND, targetPos, clickSide.getOpposite(),
                        0.5f, 0.5f, 0.5f);
                    if (result == net.minecraft.util.EnumActionResult.PASS) {
                        result = this.im.processRightClick(
                            this.player, this.theWorld, this.player.getHeldItem(EnumHand.MAIN_HAND),
                            EnumHand.MAIN_HAND);
                    }
                    if (result == net.minecraft.util.EnumActionResult.SUCCESS) {
                        this.theGolem.startRightArmTimer();
                    }
                }
                net.minecraft.item.ItemStack after = this.player.getHeldItem(EnumHand.MAIN_HAND);
                this.theGolem.itemCarried = after.isEmpty() ? null : after.copy();
                if (this.theGolem.itemCarried == null || this.theGolem.itemCarried.isEmpty()) {
                    this.theGolem.itemCarried = null;
                }
                for (int a = 1; a < this.player.inventory.mainInventory.size(); ++a) {
                    net.minecraft.item.ItemStack slotStack = this.player.inventory.getStackInSlot(a);
                    if (slotStack.isEmpty()) continue;
                    if (this.theGolem.itemCarried == null) {
                        this.theGolem.itemCarried = slotStack.copy();
                    } else {
                        this.player.dropItem(slotStack, false);
                    }
                    this.player.inventory.setInventorySlotContents(a, net.minecraft.item.ItemStack.EMPTY);
                }
                this.theGolem.updateCarried();
                this.resetTask();
                return;
            } catch (Exception e) {
                this.resetTask();
                return;
            }
        }
    }

    boolean findSomething() {
        ArrayList<Byte> matchingColors = this.theGolem.getColorsMatching(this.theGolem.itemCarried);
        for (byte col : matchingColors) {
            ArrayList<Marker> markers = this.theGolem.getMarkers();
            for (Marker marker : markers) {
                if (marker.color != col && col != -1) continue;
                boolean isAir = this.theGolem.world.isAirBlock(new BlockPos(marker.x, marker.y, marker.z));
                if (this.theGolem.getToggles()[0] && !isAir) continue;
                if (!this.theGolem.getToggles()[0] && isAir) continue;
                EnumFacing opp = EnumFacing.VALUES[marker.side % EnumFacing.VALUES.length];
                if (!this.theGolem.world.isAirBlock(new BlockPos(
                    marker.x + opp.getXOffset(),
                    marker.y + opp.getYOffset(),
                    marker.z + opp.getZOffset()))) continue;
                this.color = col;
                this.xx = marker.x;
                this.yy = marker.y;
                this.zz = marker.z;
                this.block = this.theWorld.getBlockState(new BlockPos(this.xx, this.yy, this.zz)).getBlock();
                this.blockMd = this.theWorld.getBlockState(new BlockPos(this.xx, this.yy, this.zz)).getBlock()
                    .getMetaFromState(this.theWorld.getBlockState(new BlockPos(this.xx, this.yy, this.zz)));
                return true;
            }
        }
        return false;
    }
}
