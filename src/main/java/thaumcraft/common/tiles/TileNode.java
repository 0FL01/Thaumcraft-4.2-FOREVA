package thaumcraft.common.tiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.common.blocks.BlockTaintFibres;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.items.ItemCompassStone;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class TileNode
extends TileThaumcraft
implements ITickable, INode, IAspectContainer, IWandable {

    public String id = "";
    private AspectList aspects = new AspectList();
    private AspectList aspectsBase = new AspectList();
    private NodeType nodeType = NodeType.NORMAL;
    private NodeModifier nodeModifier = null;
    private boolean dirty = false;
    private long lastActive = 0L;
    private int count = 0;
    private int regeneration = -1;
    private int wait = 0;
    private byte nodeLock = 0;
    private boolean catchUp = false;
    public long fuel = 0;
    public boolean balanced = false;
    public Entity drainEntity = null;
    public RayTraceResult drainCollision = null;
    public int drainColor = 0xFFFFFF;
    public Color targetColor = new Color(0xFFFFFF);
    public Color color = new Color(0xFFFFFF);
    public int drainBeamAge = 0;

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.readFromNBT(nbttagcompound);
        this.id = nbttagcompound.getString("nodeId");
        this.lastActive = nbttagcompound.getLong("lastActive");
        String drainer = nbttagcompound.getString("drainer");
        if (drainer != null && !drainer.isEmpty() && this.world != null) {
            this.drainEntity = this.world.getPlayerEntityByName(drainer);
            if (this.drainEntity != null) {
                if (this.drainCollision == null) {
                    this.drainBeamAge = 0;
                }
                this.drainCollision = new RayTraceResult(
                        new Vec3d(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D),
                        EnumFacing.UP,
                        this.pos);
            } else {
                clearDrainVisual();
            }
        } else {
            clearDrainVisual();
        }
        this.drainColor = nbttagcompound.hasKey("draincolor") ? nbttagcompound.getInteger("draincolor") : 0xFFFFFF;
        this.targetColor = new Color(this.drainColor);
        AspectList al = new AspectList();
        NBTTagList tlist = nbttagcompound.getTagList("AspectsBase", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (!rs.hasKey("key")) continue;
            al.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
        }
        short oldBase = nbttagcompound.getShort("nodeVisBase");
        if (oldBase > 0 && al.size() == 0) {
            this.aspectsBase = new AspectList();
            for (Aspect aspect : this.aspects.getAspects()) {
                this.aspectsBase.merge(aspect, oldBase);
            }
        } else if (al.size() > 0) {
            this.aspectsBase = al.copy();
        } else {
            this.aspectsBase = this.aspects.copy();
        }
        byte type = nbttagcompound.getByte("type");
        byte mod = nbttagcompound.getByte("modifier");
        this.nodeType = NodeType.values()[type];
        this.nodeModifier = mod >= 0 ? NodeModifier.values()[mod] : null;
        this.fuel = nbttagcompound.getLong("fuel");
        this.regeneration = getRegenerationInterval();
        this.catchUp = this.regeneration > 0 && this.lastActive > 0L
                && System.currentTimeMillis() > this.lastActive + (long) this.regeneration * 75L;
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        this.aspects.writeToNBT(nbttagcompound);
        nbttagcompound.setString("nodeId", this.id);
        nbttagcompound.setLong("lastActive", this.lastActive);
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("AspectsBase", tlist);
        for (Aspect aspect : this.aspectsBase.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("key", aspect.getTag());
            tag.setInteger("amount", this.aspectsBase.getAmount(aspect));
            tlist.appendTag(tag);
        }
        nbttagcompound.setByte("type", (byte)this.nodeType.ordinal());
        nbttagcompound.setByte("modifier", this.nodeModifier != null ? (byte)this.nodeModifier.ordinal() : -1);
        nbttagcompound.setLong("fuel", this.fuel);
        if (this.drainEntity instanceof EntityPlayer) {
            nbttagcompound.setString("drainer", this.drainEntity.getName());
        }
        nbttagcompound.setInteger("draincolor", this.drainColor);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(
                this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1).grow(8.0D, 8.0D, 8.0D);
    }

    // IAspectContainer
    public AspectList getAspects() { return this.aspects; }
    public AspectList getAspectsBase() { return this.aspectsBase; }
    public void setAspects(AspectList aspects) {
        this.aspects = aspects.copy();
        this.aspectsBase = aspects.copy();
    }
    public boolean doesContainerAccept(Aspect tag) { return true; }
    public int addToContainer(Aspect tt, int am) {
        int out = 0;
        if (this.aspects.getAmount(tt) + am > this.aspectsBase.getAmount(tt)) {
            out = this.aspects.getAmount(tt) + am - this.aspectsBase.getAmount(tt);
        }
        this.aspects.add(tt, am - out);
        this.markDirty();
        return out;
    }
    public boolean takeFromContainer(Aspect tt, int am) {
        if (this.aspects.getAmount(tt) >= am) {
            this.aspects.remove(tt, am);
            this.markDirty();
            return true;
        }
        return false;
    }
    public boolean takeFromContainer(AspectList ot) {
        if (ot == null || !doesContainerContain(ot)) return false;
        for (Aspect aspect : ot.getAspects()) {
            if (aspect == null) continue;
            this.aspects.remove(aspect, ot.getAmount(aspect));
        }
        this.markDirty();
        return true;
    }
    public boolean doesContainerContainAmount(Aspect tag, int amt) {
        return this.aspects.getAmount(tag) >= amt;
    }
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect tt : ot.getAspects()) {
            if (this.aspects.getAmount(tt) < ot.getAmount(tt)) return false;
        }
        return true;
    }
    public int containerContains(Aspect tag) { return this.aspects.getAmount(tag); }

    // INode
    public NodeType getNodeType() { return this.nodeType; }
    public void setNodeType(NodeType type) { this.nodeType = type; }
    public NodeModifier getNodeModifier() { return this.nodeModifier; }
    public void setNodeModifier(NodeModifier mod) { this.nodeModifier = mod; this.regeneration = -1; }
    public int getNodeVisBase(Aspect aspect) { return this.aspectsBase.getAmount(aspect); }
    public void setNodeVisBase(Aspect aspect, short visBase) {
        if (this.aspectsBase.getAmount(aspect) < visBase) {
            this.aspectsBase.merge(aspect, visBase);
        } else {
            this.aspectsBase.reduce(aspect, this.aspectsBase.getAmount(aspect) - visBase);
        }
    }
    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }

    // IWandable
    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        return -1;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        if (wandstack != null && !wandstack.isEmpty() && wandstack.getItem() instanceof ItemWandCasting) {
            setActiveWandHand(player, wandstack);
            ((ItemWandCasting) wandstack.getItem()).setObjectInUse(wandstack, this.pos.getX(), this.pos.getY(), this.pos.getZ());
        }
        return wandstack;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {
        if (this.world == null || wandstack == null || wandstack.isEmpty() || !(wandstack.getItem() instanceof ItemWandCasting)) {
            clearDrainVisualOnServer();
            return;
        }

        RayTraceResult hit = rayTraceNodeTarget(player);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK || !this.pos.equals(hit.getBlockPos())) {
            player.stopActiveHand();
            clearDrainVisualOnServer();
            return;
        }

        if (count % 5 == 0) {
            ItemWandCasting wand = (ItemWandCasting) wandstack.getItem();
            boolean drained = tryDrainWandVis(wand, wandstack, player, hit);
            if (!drained) {
                clearDrainVisualOnServer();
            }
        }
    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {
        if (world != null && !world.isRemote) {
            clearDrainVisualAndSync();
        }
    }

    @Override
    public void update() {
        if (this.world == null) return;
        if (this.id == null || this.id.isEmpty()) {
            this.id = generateId();
        }
        this.count++;
        if (this.world.isRemote) {
            updateDrainBeamVisual();
            if (this.nodeType == NodeType.DARK && this.count % 50 == 0) {
                ItemCompassStone.sinisterNodes.put(new WorldCoordinates(this), System.currentTimeMillis());
            }
            return;
        }
        checkLock();
        if (this.regeneration < 0) {
            this.regeneration = getRegenerationInterval();
        }

        boolean changed = false;
        if (this.catchUp) {
            changed = handleCatchUpRecharge();
        }
        changed |= handleDischarge();
        if (this.wait > 0) {
            --this.wait;
        }
        if (this.regeneration > 0 && this.wait == 0 && this.count % this.regeneration == 0) {
            this.lastActive = System.currentTimeMillis();
            changed |= rechargeOneMissingAspect();
        }
        changed = handleTaintNode(changed);
        changed = handleDarkNode(changed);
        changed = handlePureNode(changed);

        if (changed) {
            nodeChange();
        }
    }

    private String generateId() {
        return this.world.provider.getDimension() + ":" + this.pos.getX() + ":" + this.pos.getY() + ":" + this.pos.getZ();
    }

    private static void setActiveWandHand(EntityPlayer player, ItemStack wandstack) {
        if (player == null) return;
        if (player.getHeldItemOffhand() == wandstack) {
            player.setActiveHand(EnumHand.OFF_HAND);
        } else {
            player.setActiveHand(EnumHand.MAIN_HAND);
        }
    }

    private RayTraceResult rayTraceNodeTarget(EntityPlayer player) {
        if (player == null || this.world == null) return null;
        double reach = 5.0D;
        if (player instanceof EntityPlayerMP) {
            reach = ((EntityPlayerMP) player).interactionManager.getBlockReachDistance();
        }
        Vec3d eyes = new Vec3d(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ);
        Vec3d look = player.getLook(1.0F);
        Vec3d end = eyes.add(look.x * reach, look.y * reach, look.z * reach);
        return this.world.rayTraceBlocks(eyes, end, false, true, false);
    }

    private boolean tryDrainWandVis(ItemWandCasting wand, ItemStack wandstack, EntityPlayer player, RayTraceResult hit) {
        int tap = 1;
        if (ResearchManager.isResearchComplete(player, "NODETAPPER1")) {
            ++tap;
        }
        if (ResearchManager.isResearchComplete(player, "NODETAPPER2")) {
            ++tap;
        }

        boolean preserve = shouldPreserveNode(wandstack, player);
        Aspect aspect = chooseRandomFilteredFromSource(wand.getAspectsWithRoom(wandstack), preserve);
        if (aspect == null) {
            return false;
        }

        int currentAmount = this.aspects.getAmount(aspect);
        if (tap > currentAmount) {
            tap = currentAmount;
        }
        if (preserve && tap == currentAmount) {
            --tap;
        }
        if (tap <= 0) {
            return false;
        }

        int remainder = ItemWandCasting.addVis(wandstack, aspect, tap, !this.world.isRemote);
        if (remainder >= tap) {
            return false;
        }

        this.drainColor = aspect.getColor();
        setDrainVisual(player, hit, this.drainColor);
        if (!this.world.isRemote && this.takeFromContainer(aspect, tap - remainder)) {
            syncDrainChange();
        }
        return true;
    }

    private void syncDrainChange() {
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
    }

    private boolean shouldPreserveNode(ItemStack wandstack, EntityPlayer player) {
        WandRod rod = ItemWandCasting.getRod(wandstack);
        WandCap cap = ItemWandCasting.getCap(wandstack);
        return !player.isSneaking()
                && ResearchManager.isResearchComplete(player, "NODEPRESERVE")
                && rod != null
                && cap != null
                && !"wood".equals(rod.getTag())
                && !"iron".equals(cap.getTag());
    }

    private Aspect chooseRandomFilteredFromSource(AspectList room, boolean preserve) {
        if (room == null || room.size() <= 0 || this.aspects == null || this.aspects.size() <= 0) {
            return null;
        }
        int min = preserve ? 1 : 0;
        List<Aspect> valid = new ArrayList<>();
        for (Aspect aspect : this.aspects.getAspects()) {
            if (aspect != null && room.getAmount(aspect) > 0 && this.aspects.getAmount(aspect) > min) {
                valid.add(aspect);
            }
        }
        if (valid.isEmpty()) {
            return null;
        }
        return valid.get(this.world.rand.nextInt(valid.size()));
    }

    private void setDrainVisual(EntityPlayer player, RayTraceResult hit, int color) {
        if (this.drainEntity != player || this.drainCollision == null) {
            this.drainBeamAge = 0;
        }
        this.drainEntity = player;
        this.drainCollision = hit;
        this.drainColor = color;
        this.targetColor = new Color(color);
    }

    private void clearDrainVisual() {
        this.drainEntity = null;
        this.drainCollision = null;
        this.drainBeamAge = 0;
    }

    private void clearDrainVisualAndSync() {
        boolean hadVisual = this.drainEntity != null || this.drainCollision != null;
        clearDrainVisual();
        if (hadVisual && this.world != null && !this.world.isRemote) {
            syncDrainChange();
        }
    }

    private void clearDrainVisualOnServer() {
        if (this.world != null && !this.world.isRemote) {
            clearDrainVisualAndSync();
        }
    }

    private void updateDrainBeamVisual() {
        if (this.drainEntity == null || this.drainCollision == null) {
            this.drainBeamAge = 0;
            return;
        }

        ++this.drainBeamAge;
        blendDrainColor();
    }

    private void blendDrainColor() {
        if (this.color == null) {
            this.color = new Color(0xFFFFFF);
        }
        if (this.targetColor == null) {
            this.targetColor = new Color(this.drainColor);
        }
        int red = (this.targetColor.getRed() + this.color.getRed() * 4) / 5;
        int green = (this.targetColor.getGreen() + this.color.getGreen() * 4) / 5;
        int blue = (this.targetColor.getBlue() + this.color.getBlue() * 4) / 5;
        this.color = new Color(red, green, blue);
    }

    private int getRegenerationInterval() {
        int interval = 600;
        if (this.nodeModifier == NodeModifier.BRIGHT) {
            interval = 400;
        } else if (this.nodeModifier == NodeModifier.PALE) {
            interval = 900;
        } else if (this.nodeModifier == NodeModifier.FADING) {
            interval = 0;
        }
        if (interval > 0) {
            if (this.getLock() == 1) {
                interval *= 2;
            } else if (this.getLock() == 2) {
                interval *= 20;
            }
        }
        return interval;
    }

    private boolean handleCatchUpRecharge() {
        this.catchUp = false;
        int inc = this.regeneration * 75;
        int amount = inc > 0 ? (int) ((System.currentTimeMillis() - this.lastActive) / (long) inc) : 0;
        boolean changed = false;
        for (int i = 0; i < Math.min(amount, this.aspectsBase.visSize()); i++) {
            changed |= rechargeOneMissingAspect();
        }
        return changed;
    }

    private boolean rechargeOneMissingAspect() {
        AspectList missing = new AspectList();
        for (Aspect aspect : this.aspectsBase.getAspects()) {
            if (aspect != null && this.aspects.getAmount(aspect) < this.getNodeVisBase(aspect)) {
                missing.add(aspect, 1);
            }
        }
        if (missing.size() <= 0) return false;
        Aspect aspect = missing.getAspects()[this.world.rand.nextInt(missing.size())];
        this.addToContainer(aspect, 1);
        return true;
    }

    private boolean handleTaintNode(boolean changed) {
        if (this.getNodeType() == NodeType.TAINTED && this.count % 50 == 0) {
            int x = this.pos.getX() + this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
            int z = this.pos.getZ() + this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
            if (ThaumcraftWorldGenerator.biomeTaint != null) {
                Biome biome = this.world.getBiome(new BlockPos(x, 0, z));
                if (!isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint)) {
                    Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeTaint);
                }
            }

            if (Config.hardNode && this.world.rand.nextBoolean()) {
                x = this.pos.getX() + this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
                int y = this.pos.getY() + this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
                z = this.pos.getZ() + this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
                BlockTaintFibres.spreadFibres(this.world, new BlockPos(x, y, z));
            }
        }

        if (this.getNodeType() == NodeType.PURE || this.getNodeType() == NodeType.TAINTED || this.count % 100 != 0) {
            return changed;
        }
        if (ThaumcraftWorldGenerator.biomeTaint == null) {
            return changed;
        }
        Biome biome = this.world.getBiome(this.pos);
        if (!isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint) || this.world.rand.nextInt(500) != 0) {
            return changed;
        }
        this.setNodeType(NodeType.TAINTED);
        return true;
    }

    private boolean handleDarkNode(boolean changed) {
        int dim = this.world.provider.getDimension();
        int dimBlacklist = ThaumcraftWorldGenerator.getDimBlacklist(dim);
        Biome nodeBiome = this.world.getBiome(this.pos);
        int biomeBlacklist = ThaumcraftWorldGenerator.getBiomeBlacklist(Biome.getIdForBiome(nodeBiome));
        if (dim == -1 || dim == 1 || dimBlacklist == 0 || dimBlacklist == 2 || biomeBlacklist == 0 || biomeBlacklist == 2) {
            return changed;
        }
        if (this.getNodeType() != NodeType.DARK || this.count % 50 != 0) {
            return changed;
        }
        if (ThaumcraftWorldGenerator.biomeEerie == null) {
            return changed;
        }

        int x = this.pos.getX() + this.world.rand.nextInt(12) - this.world.rand.nextInt(12);
        int z = this.pos.getZ() + this.world.rand.nextInt(12) - this.world.rand.nextInt(12);
        Biome biome = this.world.getBiome(new BlockPos(x, 0, z));
        if (!isSameBiome(biome, ThaumcraftWorldGenerator.biomeEerie)) {
            Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeEerie);
        }

        if (Config.hardNode && this.world.rand.nextBoolean()
                && this.world.getClosestPlayer(
                        (double) this.pos.getX() + 0.5D,
                        (double) this.pos.getY() + 0.5D,
                        (double) this.pos.getZ() + 0.5D,
                        24.0D,
                        false) != null) {
            EntityGiantBrainyZombie entity = new EntityGiantBrainyZombie(this.world);
            AxisAlignedBB nearby = new AxisAlignedBB(this.pos).grow(10.0D, 6.0D, 10.0D);
            if (this.world.getEntitiesWithinAABB(EntityGiantBrainyZombie.class, nearby).size() <= 3) {
                double spawnX = (double) this.pos.getX() + (this.world.rand.nextDouble() - this.world.rand.nextDouble()) * 5.0D;
                double spawnY = this.pos.getY() + this.world.rand.nextInt(3) - 1;
                double spawnZ = (double) this.pos.getZ() + (this.world.rand.nextDouble() - this.world.rand.nextDouble()) * 5.0D;
                entity.setLocationAndAngles(spawnX, spawnY, spawnZ, this.world.rand.nextFloat() * 360.0F, 0.0F);
                if (entity.getCanSpawnHere()) {
                    this.world.spawnEntity(entity);
                    this.world.playEvent(2004, this.pos, 0);
                    entity.spawnExplosionParticle();
                }
            }
        }
        return changed;
    }

    private boolean handlePureNode(boolean changed) {
        int dim = this.world.provider.getDimension();
        int dimBlacklist = ThaumcraftWorldGenerator.getDimBlacklist(dim);
        if (dim == -1 || dim == 1 || dimBlacklist == 0 || dimBlacklist == 2) {
            return changed;
        }
        if (this.getNodeType() != NodeType.PURE || this.count % 50 != 0) {
            return changed;
        }
        if (ThaumcraftWorldGenerator.biomeMagicalForest == null) {
            return changed;
        }

        int x = this.pos.getX() + this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
        int z = this.pos.getZ() + this.world.rand.nextInt(8) - this.world.rand.nextInt(8);
        Biome biome = this.world.getBiome(new BlockPos(x, 0, z));
        int biomeId = Biome.getIdForBiome(biome);
        int biomeBlacklist = ThaumcraftWorldGenerator.getBiomeBlacklist(biomeId);
        if (biomeBlacklist == 0 || biomeBlacklist == 2) {
            return changed;
        }
        if (isSameBiome(biome, ThaumcraftWorldGenerator.biomeMagicalForest)) {
            return changed;
        }

        if (isSameBiome(biome, ThaumcraftWorldGenerator.biomeTaint)) {
            Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeMagicalForest);
        } else if (this.world.getBlockState(this.pos).getBlock() == ConfigBlocks.blockMagicalLog) {
            Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeMagicalForest);
        }
        return changed;
    }

    private static boolean isSameBiome(Biome first, Biome second) {
        return first == second || first != null && second != null
                && Biome.getIdForBiome(first) == Biome.getIdForBiome(second);
    }

    private void nodeChange() {
        this.regeneration = -1;
        this.markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
    }

    public byte getLock() {
        return this.nodeLock;
    }

    private void checkLock() {
        if ((this.count <= 1 || this.count % 50 == 0)
                && this.pos.getY() > 0
                && this.world.getBlockState(this.pos).getBlock() == ConfigBlocks.blockAiry) {
            byte oldLock = this.nodeLock;
            this.nodeLock = 0;
            if (!this.world.isAirBlock(this.pos.down())
                    && this.world.getBlockState(this.pos.down()).getBlock() == ConfigBlocks.blockStoneDevice) {
                int meta = this.world.getBlockState(this.pos.down()).getBlock()
                        .getMetaFromState(this.world.getBlockState(this.pos.down()));
                if (meta == 9) {
                    this.nodeLock = 1;
                } else if (meta == 10) {
                    this.nodeLock = 2;
                }
            }
            if (oldLock != this.nodeLock) {
                this.regeneration = -1;
            }
        }
    }

    private boolean handleDischarge() {
        if (this.world.getBlockState(this.pos).getBlock() != ConfigBlocks.blockAiry || this.getLock() == 1) {
            return false;
        }
        if (this.getNodeModifier() == NodeModifier.FADING) {
            return false;
        }

        boolean shiny = this.getNodeType() == NodeType.HUNGRY || this.getNodeModifier() == NodeModifier.BRIGHT;
        int interval = this.getNodeModifier() == null ? 2 : (shiny ? 1 : (this.getNodeModifier() == NodeModifier.PALE ? 3 : 2));
        if (this.count % interval != 0) {
            return false;
        }
        if (this.getNodeModifier() == NodeModifier.PALE && this.world.rand.nextBoolean()) {
            return false;
        }

        int x = this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
        int y = this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
        int z = this.world.rand.nextInt(5) - this.world.rand.nextInt(5);
        if (x == 0 && y == 0 && z == 0) {
            return false;
        }

        TileEntity te = this.world.getTileEntity(this.pos.add(x, y, z));
        if (!(te instanceof INode) || this.world.getBlockState(this.pos.add(x, y, z)).getBlock() != ConfigBlocks.blockAiry) {
            return false;
        }
        if (te instanceof TileNode && ((TileNode) te).getLock() > 0) {
            return false;
        }

        INode node = (INode) te;
        int targetAverage = (node.getAspects().visSize() + node.getAspectsBase().visSize()) / 2;
        int thisAverage = (this.getAspects().visSize() + this.getAspectsBase().visSize()) / 2;
        if (targetAverage >= thisAverage || node.getAspects().size() <= 0) {
            return false;
        }

        Aspect aspect = node.getAspects().getAspects()[this.world.rand.nextInt(node.getAspects().size())];
        boolean updated = false;
        if (this.getAspects().getAmount(aspect) < this.getNodeVisBase(aspect) && node.takeFromContainer(aspect, 1)) {
            this.addToContainer(aspect, 1);
            updated = true;
        } else if (node.takeFromContainer(aspect, 1)) {
            int bound = 1 + (int) ((double) this.getNodeVisBase(aspect) / (shiny ? 1.5D : 1.0D));
            if (this.world.rand.nextInt(Math.max(1, bound)) == 0) {
                this.aspectsBase.add(aspect, 1);
                if (this.getNodeModifier() == NodeModifier.PALE && this.world.rand.nextInt(100) == 0) {
                    this.setNodeModifier(null);
                }
                if (this.world.rand.nextInt(3) == 0) {
                    node.setNodeVisBase(aspect, (short) (node.getNodeVisBase(aspect) - 1));
                }
            }
            updated = true;
        }

        if (!updated) {
            return false;
        }

        if (te instanceof TileNode) {
            TileNode targetNode = (TileNode) te;
            if (targetNode.regeneration < 0) {
                targetNode.regeneration = targetNode.getRegenerationInterval();
            }
            targetNode.wait = targetNode.regeneration / 2;
            te.markDirty();
            this.world.notifyBlockUpdate(te.getPos(), this.world.getBlockState(te.getPos()), this.world.getBlockState(te.getPos()), 3);
        }

        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockZap(
                        (float) (this.pos.getX() + x) + 0.5F,
                        (float) (this.pos.getY() + y) + 0.5F,
                        (float) (this.pos.getZ() + z) + 0.5F,
                        (float) this.pos.getX() + 0.5F,
                        (float) this.pos.getY() + 0.5F,
                        (float) this.pos.getZ() + 0.5F),
                new NetworkRegistry.TargetPoint(
                        this.world.provider.getDimension(),
                        this.pos.getX(),
                        this.pos.getY(),
                        this.pos.getZ(),
                        32.0));
        return true;
    }
}
