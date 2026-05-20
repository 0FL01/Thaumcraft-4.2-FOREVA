package thaumcraft.common.tiles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import thaumcraft.api.IScribeTools;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.items.ItemResearchNotes;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.research.ResearchNoteData;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.lib.utils.InventoryUtils;

public class TileResearchTable
extends TileThaumcraft
implements IInventory, ITickable {

    private ItemStack[] stackList = new ItemStack[2];
    public AspectList bonusAspects = new AspectList();
    private int nextRecalc = 0;
    private ResearchNoteData data = null;

    public TileResearchTable() {
        for (int i = 0; i < stackList.length; i++) {
            stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public int getSizeInventory() { return this.stackList.length; }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < this.stackList.length ? this.stackList[index] : ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (!this.stackList[index].isEmpty()) {
            if (this.stackList[index].getCount() <= count) {
                ItemStack stack = this.stackList[index];
                this.stackList[index] = ItemStack.EMPTY;
                this.markDirty();
                return stack;
            }
            ItemStack stack = this.stackList[index].splitStack(count);
            if (this.stackList[index].getCount() == 0) {
                this.stackList[index] = ItemStack.EMPTY;
            }
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (!this.stackList[index].isEmpty()) {
            ItemStack stack = this.stackList[index];
            this.stackList[index] = ItemStack.EMPTY;
            this.markDirty();
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stackList[index] = stack;
        if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }
        this.markDirty();
    }

    @Override
    public boolean hasCustomName() { return false; }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return this.world.getTileEntity(this.pos) == this
                && player.getDistanceSq(
                (double) this.pos.getX() + 0.5D,
                (double) this.pos.getY() + 0.5D,
                (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() { return 0; }

    @Override
    public void clear() {
        for (int i = 0; i < stackList.length; i++) {
            stackList[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public String getName() { return "Research Table"; }

    @Override
    public ITextComponent getDisplayName() { return new TextComponentString(getName()); }

    @Override
    public boolean isEmpty() {
        for (ItemStack s : stackList) {
            if (!s.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        NBTTagList list = compound.getTagList("Inventory", 10);
        this.stackList = new ItemStack[this.getSizeInventory()];
        for (int i = 0; i < this.stackList.length; i++) {
            this.stackList[i] = ItemStack.EMPTY;
        }
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            if (slot >= 0 && slot < this.stackList.length) {
                this.stackList[slot] = new ItemStack(item);
            }
        }

        this.bonusAspects = new AspectList();
        NBTTagList bonus = compound.getTagList("bonusAspects", 10);
        for (int i = 0; i < bonus.tagCount(); i++) {
            NBTTagCompound tag = bonus.getCompoundTagAt(i);
            Aspect aspect = Aspect.getAspect(tag.getString("tag"));
            if (aspect != null) {
                this.bonusAspects.merge(aspect, 1);
            }
        }
        this.nextRecalc = compound.getInteger("nextRecalc");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < this.stackList.length; i++) {
            if (!this.stackList[i].isEmpty()) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                this.stackList[i].writeToNBT(item);
                list.appendTag(item);
            }
        }
        compound.setTag("Inventory", list);

        NBTTagList bonus = new NBTTagList();
        for (Aspect aspect : this.bonusAspects.getAspects()) {
            if (aspect == null) continue;
            int amount = this.bonusAspects.getAmount(aspect);
            if (amount <= 0) continue;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("tag", aspect.getTag());
            bonus.appendTag(tag);
        }
        compound.setTag("bonusAspects", bonus);
        compound.setInteger("nextRecalc", this.nextRecalc);
    }

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote && this.nextRecalc++ > 600) {
            this.nextRecalc = 0;
            this.recalculateBonus();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            this.markDirty();
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        gatherResults();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        switch (index) {
            case 0:
                return stack.getItem() instanceof IScribeTools;
            case 1:
                return stack.getItem() instanceof ItemResearchNotes && stack.getMetadata() < 64;
            default:
                return false;
        }
    }

    public void duplicate(EntityPlayer player) {
        if (player == null || this.world == null || this.world.isRemote) return;

        ItemStack notesStack = getStackInSlot(1);
        if (notesStack.isEmpty() || !(notesStack.getItem() instanceof ItemResearchNotes) || notesStack.getMetadata() != 64) {
            return;
        }

        ResearchNoteData data = ResearchManager.getData(notesStack);
        if (data == null || data.key == null || data.key.isEmpty()) return;

        ResearchItem research = ResearchCategories.getResearch(data.key);
        if (research == null || research.tags == null) return;

        if (!playerHasItem(player, Items.FEATHER) || !playerHasItem(player, Items.PAPER)) return;

        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        if (knowledge == null) return;

        for (Aspect aspect : research.tags.getAspects()) {
            if (aspect == null) continue;
            int needed = research.tags.getAmount(aspect) + data.copies;
            if (knowledge.getAspectPoolFor(aspect) < needed) {
                return;
            }
        }

        for (Aspect aspect : research.tags.getAspects()) {
            if (aspect == null) continue;
            int cost = research.tags.getAmount(aspect) + data.copies;
            if (knowledge.addAspectPool(aspect, -cost) && player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(
                        new PacketAspectPool(aspect.getTag(), (short) (-cost), knowledge.getAspectPoolFor(aspect)),
                        (EntityPlayerMP) player);
            }
        }

        InventoryUtils.consumeInventoryItem(player, Items.FEATHER, 0);
        InventoryUtils.consumeInventoryItem(player, Items.PAPER, 0);

        data.copies++;
        ResearchManager.updateData(notesStack, data);
        setInventorySlotContents(1, notesStack);

        ItemStack duplicate = notesStack.copy();
        duplicate.setCount(1);
        if (!player.inventory.addItemStackToInventory(duplicate)) {
            player.dropItem(duplicate, false);
        }

        this.world.addBlockEvent(this.pos, ConfigBlocks.blockTable, 1, 1);
        markDirty();
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
    }

    private void gatherResults() {
        this.data = null;
        ItemStack notesStack = getStackInSlot(1);
        if (!notesStack.isEmpty() && notesStack.getItem() instanceof ItemResearchNotes) {
            this.data = ResearchManager.getData(notesStack);
        }
    }

    public void placeAspect(int q, int r, Aspect aspect, EntityPlayer player) {
        if (player == null || this.world == null || this.world.isRemote) return;
        if (this.data == null) {
            gatherResults();
        }
        if (!ResearchManager.consumeInkFromTable(getStackInSlot(0), false)) {
            return;
        }
        ItemStack notesStack = getStackInSlot(1);
        if (notesStack.isEmpty()
                || !(notesStack.getItem() instanceof ItemResearchNotes)
                || this.data == null
                || notesStack.getMetadata() >= 64) {
            return;
        }
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        if (knowledge == null) return;

        boolean researcher1 = ResearchManager.isResearchComplete(player, "RESEARCHER1");
        boolean researcher2 = ResearchManager.isResearchComplete(player, "RESEARCHER2");
        HexUtils.Hex hex = new HexUtils.Hex(q, r);
        String hexKey = hex.toString();
        if (!this.data.hexes.containsKey(hexKey) || !this.data.hexEntries.containsKey(hexKey)) {
            return;
        }
        ResearchManager.HexEntry current = this.data.hexEntries.get(hexKey);
        if (current == null) {
            return;
        }
        ResearchManager.HexEntry next;
        if (aspect != null) {
            if (!knowledge.hasDiscoveredAspect(aspect)) {
                return;
            }
            int poolAmount = knowledge.getAspectPoolFor(aspect);
            int bonusAmount = this.bonusAspects.getAmount(aspect);
            if (poolAmount <= 0 && bonusAmount <= 0) {
                return;
            }
            next = new ResearchManager.HexEntry(aspect, 2);
            boolean refundSkip = researcher2 && this.world.rand.nextFloat() < 0.1F;
            if (refundSkip) {
                this.world.playSound(null, this.pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategory.BLOCKS, 0.2F, 0.9F + player.world.rand.nextFloat() * 0.2F);
            } else {
                if (poolAmount <= 0) {
                    this.bonusAspects.remove(aspect, 1);
                    this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
                    this.markDirty();
                } else if (knowledge.addAspectPool(aspect, -1) && player instanceof EntityPlayerMP) {
                    PacketHandler.INSTANCE.sendTo(
                            new PacketAspectPool(aspect.getTag(), (short) -1, knowledge.getAspectPoolFor(aspect)),
                            (EntityPlayerMP) player);
                }
            }
        } else {
            float chance = this.world.rand.nextFloat();
            if (current != null
                    && current.aspect != null
                    && ((researcher1 && chance < 0.25F) || (researcher2 && chance < 0.5F))) {
                this.world.playSound(null, this.pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategory.BLOCKS, 0.2F, 0.9F + player.world.rand.nextFloat() * 0.2F);
                if (knowledge.addAspectPool(current.aspect, 1) && player instanceof EntityPlayerMP) {
                    PacketHandler.INSTANCE.sendTo(
                            new PacketAspectPool(current.aspect.getTag(), (short) 1, knowledge.getAspectPoolFor(current.aspect)),
                            (EntityPlayerMP) player);
                }
            }
            next = new ResearchManager.HexEntry(null, 0);
        }
        this.data.hexEntries.put(hexKey, next);
        this.data.hexes.put(hexKey, hex);
        ResearchManager.updateData(notesStack, this.data);
        ResearchManager.consumeInkFromTable(getStackInSlot(0), true);
        if (ResearchManager.checkResearchCompletion(notesStack, this.data, player.getName())) {
            notesStack.setItemDamage(64);
            this.world.addBlockEvent(this.pos, ConfigBlocks.blockTable, 1, 1);
        }
        this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        this.markDirty();
    }

    private void recalculateBonus() {
        if (this.world == null) return;
        if (!this.world.isDaytime()
                && this.world.getLight(this.pos.up()) < 4
                && !this.world.canSeeSky(this.pos.up())
                && this.world.rand.nextInt(20) == 0) {
            grantBonusAspect(Aspect.ENTROPY);
        }
        float worldHeight = (float) this.world.getHeight();
        if ((float) this.pos.getY() > worldHeight * 0.5F && this.world.rand.nextInt(20) == 0) {
            grantBonusAspect(Aspect.AIR);
        }
        if ((float) this.pos.getY() > worldHeight * 0.66F && this.world.rand.nextInt(20) == 0) {
            grantBonusAspect(Aspect.AIR);
        }
        if ((float) this.pos.getY() > worldHeight * 0.75F && this.world.rand.nextInt(20) == 0) {
            grantBonusAspect(Aspect.AIR);
        }

        for (int x = -8; x <= 8; ++x) {
            for (int z = -8; z <= 8; ++z) {
                for (int y = -8; y <= 8; ++y) {
                    BlockPos scanPos = this.pos.add(x, y, z);
                    if (scanPos.getY() <= 0 || scanPos.getY() >= this.world.getHeight()) continue;
                    IBlockState state = this.world.getBlockState(scanPos);
                    Block block = state.getBlock();
                    int md = block.getMetaFromState(state);
                    Material mat = state.getMaterial();

                    if (block == ConfigBlocks.blockCustomOre && md == 1) {
                        if (this.bonusAspects.getAmount(Aspect.AIR) < 1 && this.world.rand.nextInt(20) == 0) {
                            grantBonusAspect(Aspect.AIR);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCrystal && md == 0) {
                        if (this.bonusAspects.getAmount(Aspect.AIR) < 1 && this.world.rand.nextInt(10) == 0) {
                            grantBonusAspect(Aspect.AIR);
                            return;
                        }
                    } else if (mat == Material.LAVA || mat == Material.FIRE || block == ConfigBlocks.blockCustomOre && md == 2) {
                        if (this.bonusAspects.getAmount(Aspect.FIRE) < 1 && this.world.rand.nextInt(20) == 0) {
                            grantBonusAspect(Aspect.FIRE);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCrystal && md == 1) {
                        if (this.bonusAspects.getAmount(Aspect.FIRE) < 1 && this.world.rand.nextInt(10) == 0) {
                            grantBonusAspect(Aspect.FIRE);
                            return;
                        }
                    } else if (mat == Material.GROUND || block == ConfigBlocks.blockCustomOre && md == 4) {
                        if (this.bonusAspects.getAmount(Aspect.EARTH) < 1 && this.world.rand.nextInt(20) == 0) {
                            grantBonusAspect(Aspect.EARTH);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCrystal && md == 3) {
                        if (this.bonusAspects.getAmount(Aspect.EARTH) < 1 && this.world.rand.nextInt(10) == 0) {
                            grantBonusAspect(Aspect.EARTH);
                            return;
                        }
                    } else if (mat == Material.WATER) {
                        if (this.bonusAspects.getAmount(Aspect.WATER) < 1 && this.world.rand.nextInt(15) == 0) {
                            grantBonusAspect(Aspect.WATER);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCustomOre && md == 3) {
                        if (this.bonusAspects.getAmount(Aspect.WATER) < 1 && this.world.rand.nextInt(20) == 0) {
                            grantBonusAspect(Aspect.WATER);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCrystal && md == 2) {
                        if (this.bonusAspects.getAmount(Aspect.WATER) < 1 && this.world.rand.nextInt(10) == 0) {
                            grantBonusAspect(Aspect.WATER);
                            return;
                        }
                    } else if (mat == Material.CIRCUITS || mat == Material.ICE || block == ConfigBlocks.blockCustomOre && md == 5) {
                        if (this.bonusAspects.getAmount(Aspect.ORDER) < 1 && this.world.rand.nextInt(20) == 0) {
                            grantBonusAspect(Aspect.ORDER);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCrystal && md == 4) {
                        if (this.bonusAspects.getAmount(Aspect.ORDER) < 1 && this.world.rand.nextInt(10) == 0) {
                            grantBonusAspect(Aspect.ORDER);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCustomOre && md == 6) {
                        if (this.bonusAspects.getAmount(Aspect.ENTROPY) < 1 && this.world.rand.nextInt(20) == 0) {
                            grantBonusAspect(Aspect.ENTROPY);
                            return;
                        }
                    } else if (block == ConfigBlocks.blockCrystal && md == 5 && this.bonusAspects.getAmount(Aspect.ENTROPY) < 1 && this.world.rand.nextInt(10) == 0) {
                        grantBonusAspect(Aspect.ENTROPY);
                        return;
                    }

                    if ((block == net.minecraft.init.Blocks.BOOKSHELF && this.world.rand.nextInt(300) == 0)
                            || (block == ConfigBlocks.blockJar && md == 1 && this.world.rand.nextInt(200) == 0)) {
                        Aspect[] aspects = Aspect.aspects.values().toArray(new Aspect[0]);
                        if (aspects.length > 0) {
                            grantBonusAspect(aspects[this.world.rand.nextInt(aspects.length)]);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void grantBonusAspect(Aspect aspect) {
        if (aspect == null || this.bonusAspects.getAmount(aspect) > 0) {
            return;
        }
        this.bonusAspects.merge(aspect, 1);
    }

    private boolean playerHasItem(EntityPlayer player, net.minecraft.item.Item item) {
        NonNullList<ItemStack> main = player.inventory.mainInventory;
        for (ItemStack stack : main) {
            if (!stack.isEmpty() && stack.getItem() == item) return true;
        }
        return false;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(this.pos.add(-1, 0, -1), this.pos.add(2, 2, 2));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        if (this.world != null && this.world.isRemote) {
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            if (this.world != null && this.world.isRemote) {
                this.world.playSound(this.pos.getX(), this.pos.getY(), this.pos.getZ(),
                        TCSounds.LEARN, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
            return true;
        }
        return super.receiveClientEvent(id, type);
    }
}
