package thaumcraft.common.items.wands;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.IArchitect;
import thaumcraft.api.IVisDiscountGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.blocks.BlockArcaneFurnace;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileInfusionMatrix;
import thaumcraft.common.tiles.TileInfusionPillar;
import thaumcraft.common.tiles.TilePedestal;
import thaumcraft.common.tiles.TileThaumatorium;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WandManager implements IWandTriggerManager {

    private static final Map<Integer, Long> cooldownClient = new HashMap<>();
    private static final Map<Integer, Long> cooldownServer = new HashMap<>();

    public static float getTotalVisDiscount(EntityPlayer player, Aspect aspect) {
        if (player == null) return 0.0F;

        int total = 0;
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (baubles != null) {
            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                ItemStack stack = baubles.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof IVisDiscountGear) {
                    total += ((IVisDiscountGear) stack.getItem()).getVisDiscount(stack, player, aspect);
                }
            }
        }

        for (ItemStack stack : player.inventory.armorInventory) {
            if (!stack.isEmpty() && stack.getItem() instanceof IVisDiscountGear) {
                total += ((IVisDiscountGear) stack.getItem()).getVisDiscount(stack, player, aspect);
            }
        }

        int exhaustionLevel = -1;
        if (Config.potionVisExhaust != null) {
            PotionEffect effect = player.getActivePotionEffect(Config.potionVisExhaust);
            if (effect != null) exhaustionLevel = Math.max(exhaustionLevel, effect.getAmplifier());
        }
        if (Config.potionInfectiousVisExhaust != null) {
            PotionEffect effect = player.getActivePotionEffect(Config.potionInfectiousVisExhaust);
            if (effect != null) exhaustionLevel = Math.max(exhaustionLevel, effect.getAmplifier());
        }
        if (exhaustionLevel >= 0) {
            total -= (exhaustionLevel + 1) * 10;
        }

        return (float) total / 100.0F;
    }

    /**
     * Try to consume vis from any wand in the player's inventory.
     */
    public static boolean consumeVisFromInventory(EntityPlayer player, AspectList cost) {
        if (player == null || cost == null) return false;

        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (baubles != null) {
            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                ItemStack stack = baubles.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemAmuletVis
                        && ((ItemAmuletVis) stack.getItem()).consumeAllVis(stack, player, cost, true, true)) {
                    return true;
                }
            }
        }

        for (int i = player.inventory.mainInventory.size() - 1; i >= 0; i--) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                if (wand.consumeAllVis(stack, player, cost, true, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void changeFocus(ItemStack wandStack, World world, EntityPlayer player, String focusKey) {
        if (wandStack == null || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting) || player == null) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack current = wand.getFocusItem(wandStack);
        if (focusKey == null) focusKey = "";

        TreeMap<String, FocusLocation> foci = new TreeMap<>();
        Map<Integer, PouchLocation> pouches = new HashMap<>();
        int pouchCount = 0;

        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (baubles != null) {
            for (int slot = 0; slot < baubles.getSlots(); slot++) {
                ItemStack stack = baubles.getStackInSlot(slot);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusPouch) {
                    PouchLocation pouch = new PouchLocation(++pouchCount, slot, true);
                    pouches.put(pouch.id, pouch);
                    addPouchFoci(foci, (ItemFocusPouch) stack.getItem(), stack, pouch);
                }
            }
        }

        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusBasic) {
                foci.put(((ItemFocusBasic) stack.getItem()).getSortingHelper(stack), FocusLocation.inventory(slot));
            } else if (!stack.isEmpty() && stack.getItem() instanceof ItemFocusPouch) {
                PouchLocation pouch = new PouchLocation(++pouchCount, slot, false);
                pouches.put(pouch.id, pouch);
                addPouchFoci(foci, (ItemFocusPouch) stack.getItem(), stack, pouch);
            }
        }

        if ("REMOVE".equals(focusKey) || foci.isEmpty()) {
            if (!current.isEmpty() && (addFocusToPouch(player, current.copy(), pouches) || player.inventory.addItemStackToInventory(current.copy()))) {
                wand.setFocus(wandStack, ItemStack.EMPTY);
                player.inventory.markDirty();
                playFocusSound(world, player, 0.9F);
            }
            return;
        }

        String selectedKey = focusKey;
        if (!foci.containsKey(selectedKey)) {
            selectedKey = foci.higherKey(selectedKey);
        }
        if (selectedKey == null || !foci.containsKey(selectedKey)) {
            selectedKey = foci.firstKey();
        }

        FocusLocation location = foci.get(selectedKey);
        ItemStack selected = fetchFocus(player, location, pouches);
        if (selected.isEmpty() || !(selected.getItem() instanceof ItemFocusBasic)) return;

        if (!current.isEmpty()) {
            if (!addFocusToPouch(player, current.copy(), pouches) && !player.inventory.addItemStackToInventory(current.copy())) {
                restoreFocus(player, location, selected.copy(), pouches);
                return;
            }
        }
        wand.setFocus(wandStack, selected.copy());
        player.inventory.markDirty();
        playFocusSound(world, player, 1.0F);
    }

    private static void addPouchFoci(TreeMap<String, FocusLocation> foci, ItemFocusPouch pouchItem, ItemStack pouchStack, PouchLocation pouch) {
        ItemStack[] inventory = pouchItem.getInventory(pouchStack);
        for (int slot = 0; slot < inventory.length; slot++) {
            ItemStack focus = inventory[slot];
            if (!focus.isEmpty() && focus.getItem() instanceof ItemFocusBasic) {
                foci.put(((ItemFocusBasic) focus.getItem()).getSortingHelper(focus), FocusLocation.pouch(pouch.id, slot));
            }
        }
    }

    private static ItemStack fetchFocus(EntityPlayer player, FocusLocation location, Map<Integer, PouchLocation> pouches) {
        if (location == null) return ItemStack.EMPTY;
        if (!location.inPouch) {
            ItemStack focus = player.inventory.mainInventory.get(location.slot);
            player.inventory.mainInventory.set(location.slot, ItemStack.EMPTY);
            return focus.copy();
        }
        PouchLocation pouch = pouches.get(location.pouchId);
        if (pouch == null) return ItemStack.EMPTY;
        ItemStack pouchStack = getPouchStack(player, pouch);
        if (pouchStack.isEmpty() || !(pouchStack.getItem() instanceof ItemFocusPouch)) return ItemStack.EMPTY;
        ItemFocusPouch pouchItem = (ItemFocusPouch) pouchStack.getItem();
        ItemStack[] inventory = pouchItem.getInventory(pouchStack);
        if (location.slot < 0 || location.slot >= inventory.length) return ItemStack.EMPTY;
        ItemStack focus = inventory[location.slot];
        inventory[location.slot] = ItemStack.EMPTY;
        pouchItem.setInventory(pouchStack, inventory);
        setPouchStack(player, pouch, pouchStack);
        return focus.copy();
    }

    private static void restoreFocus(EntityPlayer player, FocusLocation location, ItemStack focus, Map<Integer, PouchLocation> pouches) {
        if (location == null || focus.isEmpty()) return;
        if (!location.inPouch) {
            player.inventory.mainInventory.set(location.slot, focus);
            return;
        }
        PouchLocation pouch = pouches.get(location.pouchId);
        if (pouch == null) return;
        ItemStack pouchStack = getPouchStack(player, pouch);
        if (pouchStack.isEmpty() || !(pouchStack.getItem() instanceof ItemFocusPouch)) return;
        ItemFocusPouch pouchItem = (ItemFocusPouch) pouchStack.getItem();
        ItemStack[] inventory = pouchItem.getInventory(pouchStack);
        if (location.slot >= 0 && location.slot < inventory.length && inventory[location.slot].isEmpty()) {
            inventory[location.slot] = focus;
            pouchItem.setInventory(pouchStack, inventory);
            setPouchStack(player, pouch, pouchStack);
        }
    }

    private static boolean addFocusToPouch(EntityPlayer player, ItemStack focus, Map<Integer, PouchLocation> pouches) {
        if (focus.isEmpty()) return true;
        for (PouchLocation pouch : pouches.values()) {
            ItemStack pouchStack = getPouchStack(player, pouch);
            if (pouchStack.isEmpty() || !(pouchStack.getItem() instanceof ItemFocusPouch)) continue;
            ItemFocusPouch pouchItem = (ItemFocusPouch) pouchStack.getItem();
            ItemStack[] inventory = pouchItem.getInventory(pouchStack);
            for (int slot = 0; slot < inventory.length; slot++) {
                if (!inventory[slot].isEmpty()) continue;
                inventory[slot] = focus.copy();
                pouchItem.setInventory(pouchStack, inventory);
                setPouchStack(player, pouch, pouchStack);
                return true;
            }
        }
        return false;
    }

    private static ItemStack getPouchStack(EntityPlayer player, PouchLocation pouch) {
        if (pouch.bauble) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            return baubles == null ? ItemStack.EMPTY : baubles.getStackInSlot(pouch.slot);
        }
        return player.inventory.mainInventory.get(pouch.slot);
    }

    private static void setPouchStack(EntityPlayer player, PouchLocation pouch, ItemStack stack) {
        if (pouch.bauble) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            if (baubles != null) baubles.setStackInSlot(pouch.slot, stack);
        } else {
            player.inventory.mainInventory.set(pouch.slot, stack);
            player.inventory.markDirty();
        }
    }

    public static void toggleMisc(ItemStack wandStack, World world, EntityPlayer player) {
        if (wandStack == null || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemFocusBasic focus = wand.getFocus(wandStack);
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (!(focus instanceof IArchitect) || focusStack.isEmpty() || !focus.isUpgradedWith(focusStack, FocusUpgradeType.architect)) return;

        if (player != null && player.isSneaking()) {
            int dim = getAreaDim(wandStack) + 1;
            if (dim > 3) dim = 0;
            setAreaDim(wandStack, dim);
        } else {
            int max = focus.getMaxAreaSize(focusStack);
            int dim = getAreaDim(wandStack);
            int x = getAreaX(wandStack, max);
            int y = getAreaY(wandStack, max);
            int z = getAreaZ(wandStack, max);
            if (dim == 0) {
                x++;
                y++;
                z++;
            } else if (dim == 1) {
                x++;
            } else if (dim == 2) {
                z++;
            } else if (dim == 3) {
                y++;
            }
            setAreaX(wandStack, x > max ? 0 : x);
            setAreaY(wandStack, y > max ? 0 : y);
            setAreaZ(wandStack, z > max ? 0 : z);
        }
        if (world != null && player != null) {
            world.playSound(null, player.getPosition(), TCSounds.CAMERATICKS, SoundCategory.PLAYERS, 0.3F, 1.0F);
        }
    }

    public static int getAreaDim(ItemStack stack) {
        return stack != null && stack.hasTagCompound() ? stack.getTagCompound().getInteger("aread") : 0;
    }

    public static int getAreaX(ItemStack stack, int max) {
        return getClampedArea(stack, "areax", max);
    }

    public static int getAreaY(ItemStack stack, int max) {
        return getClampedArea(stack, "areay", max);
    }

    public static int getAreaZ(ItemStack stack, int max) {
        return getClampedArea(stack, "areaz", max);
    }

    public static void setAreaX(ItemStack stack, int area) {
        ItemWandCasting.ensureTag(stack).setInteger("areax", area);
    }

    public static void setAreaY(ItemStack stack, int area) {
        ItemWandCasting.ensureTag(stack).setInteger("areay", area);
    }

    public static void setAreaZ(ItemStack stack, int area) {
        ItemWandCasting.ensureTag(stack).setInteger("areaz", area);
    }

    public static void setAreaDim(ItemStack stack, int dim) {
        ItemWandCasting.ensureTag(stack).setInteger("aread", dim);
    }

    private static int getClampedArea(ItemStack stack, String key, int max) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
            return Math.min(max, Math.max(0, stack.getTagCompound().getInteger(key)));
        }
        return max;
    }

    private static void playFocusSound(World world, EntityPlayer player, float pitch) {
        if (world != null && player != null) {
            world.playSound(null, player.getPosition(), TCSounds.CAMERATICKS, SoundCategory.PLAYERS, 0.3F, pitch);
        }
    }

    public static boolean isOnCooldown(EntityLivingBase entityLiving) {
        if (entityLiving == null || entityLiving.world == null) return false;
        Map<Integer, Long> map = entityLiving.world.isRemote ? cooldownClient : cooldownServer;
        Long until = map.get(entityLiving.getEntityId());
        return until != null && until > System.currentTimeMillis();
    }

    public static float getCooldown(EntityLivingBase entityLiving) {
        if (entityLiving == null || entityLiving.world == null) return 0.0F;
        Long until = (entityLiving.world.isRemote ? cooldownClient : cooldownServer).get(entityLiving.getEntityId());
        return until == null ? 0.0F : Math.max(0.0F, (float)(until - System.currentTimeMillis()) / 1000.0F);
    }

    public static void setCooldown(EntityLivingBase entityLiving, int cooldown) {
        if (entityLiving == null || entityLiving.world == null) return;
        if (cooldown == 0) {
            cooldownClient.remove(entityLiving.getEntityId());
            cooldownServer.remove(entityLiving.getEntityId());
            return;
        }
        Map<Integer, Long> map = entityLiving.world.isRemote ? cooldownClient : cooldownServer;
        map.put(entityLiving.getEntityId(), System.currentTimeMillis() + (long) cooldown);
    }

    @Override
    public boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side, int event) {
        switch (event) {
            case 0:
                return createThaumonomicon(wand, player, world, x, y, z);
            case 1:
                return createCrucible(wand, player, world, x, y, z);
            case 2:
                if (!ResearchManager.isResearchComplete(player, "INFERNALFURNACE")) break;
                return createArcaneFurnace(wand, player, world, x, y, z);
            case 3:
                if (!ResearchManager.isResearchComplete(player, "INFUSION")) break;
                return createInfusionAltar(wand, player, world, x, y, z);
            case 4:
                if (!ResearchManager.isResearchComplete(player, "NODEJAR")) break;
                return createNodeJar(wand, player, world, x, y, z);
            case 5:
                if (!ResearchManager.isResearchComplete(player, "THAUMATORIUM")) break;
                return createThaumatorium(wand, player, world, x, y, z, side);
            case 6:
                if (!ResearchManager.isResearchComplete(player, "OCULUS")) break;
                return createOculus(wand, player, world, x, y, z, side);
            case 7:
                if (!ResearchManager.isResearchComplete(player, "ADVALCHEMYFURNACE")) break;
                return createAdvancedAlchemicalFurnace(wand, player, world, x, y, z, side);
            default:
                break;
        }
        return false;
    }

    public static boolean createCrucible(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z) {
        if (world.isRemote || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return false;
        world.playSound(null, new BlockPos(x, y, z), TCSounds.WAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
        world.setBlockToAir(new BlockPos(x, y, z));
        world.setBlockState(new BlockPos(x, y, z), ConfigBlocks.blockMetalDevice.getDefaultState().withProperty(thaumcraft.common.blocks.BlockMetalDevice.TYPE, 0), 3);
        return true;
    }

    public static boolean createThaumonomicon(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z) {
        if (world.isRemote || wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return false;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        if (wand.getFocus(wandStack) != null) return false;

        BlockPos pos = new BlockPos(x, y, z);
        world.setBlockToAir(pos);

        EntityItem entityItem = new EntitySpecialItem(world, x + 0.5, y + 0.3, z + 0.5, new ItemStack(ConfigItems.itemThaumonomicon));
        entityItem.motionY = 0.0;
        entityItem.motionX = 0.0;
        entityItem.motionZ = 0.0;
        world.spawnEntity(entityItem);

        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockSparkle(x, y, z, -9999),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 32.0));
        world.playSound(null, pos, TCSounds.WAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    public static boolean createThaumatorium(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z, int side) {
        if (wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return false;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        BlockPos center = new BlockPos(x, y, z);
        if (world.getBlockState(center.up()).getBlock() != ConfigBlocks.blockMetalDevice
                || world.getBlockState(center.up()).getValue(thaumcraft.common.blocks.BlockMetalDevice.TYPE) != 9
                || world.getBlockState(center.down()).getBlock() != ConfigBlocks.blockMetalDevice
                || world.getBlockState(center.down()).getValue(thaumcraft.common.blocks.BlockMetalDevice.TYPE) != 0) {
            if (world.getBlockState(center.down()).getBlock() == ConfigBlocks.blockMetalDevice
                    && world.getBlockState(center.down()).getValue(thaumcraft.common.blocks.BlockMetalDevice.TYPE) == 9
                    && world.getBlockState(center.down(2)).getBlock() == ConfigBlocks.blockMetalDevice
                    && world.getBlockState(center.down(2)).getValue(thaumcraft.common.blocks.BlockMetalDevice.TYPE) == 0) {
                center = center.down();
            } else {
                return false;
            }
        }
        if (!wand.consumeAllVisCrafting(wandStack, player,
                new AspectList().add(Aspect.FIRE, 15).add(Aspect.ORDER, 30).add(Aspect.WATER, 30), true)) {
            return false;
        }
        if (world.isRemote) {
            return false;
        }

        world.setBlockState(center, ConfigBlocks.blockMetalDevice.getDefaultState().withProperty(thaumcraft.common.blocks.BlockMetalDevice.TYPE, 10), 3);
        world.setBlockState(center.up(), ConfigBlocks.blockMetalDevice.getDefaultState().withProperty(thaumcraft.common.blocks.BlockMetalDevice.TYPE, 11), 3);
        TileEntity tile = world.getTileEntity(center);
        if (tile instanceof TileThaumatorium) {
            ((TileThaumatorium) tile).facing = EnumFacing.byIndex(side);
            tile.markDirty();
        }
        world.notifyBlockUpdate(center, world.getBlockState(center), world.getBlockState(center), 3);
        world.notifyBlockUpdate(center.up(), world.getBlockState(center.up()), world.getBlockState(center.up()), 3);

        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockSparkle(center.getX(), center.getY(), center.getZ(), -9999),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 32.0));
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXBlockSparkle(center.getX(), center.getY() + 1, center.getZ(), -9999),
                new NetworkRegistry.TargetPoint(world.provider.getDimension(), center.getX(), center.getY(), center.getZ(), 32.0));
        world.playSound(null, center, TCSounds.WAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    private static boolean createArcaneFurnace(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z) {
        if (wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return false;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        for (int xx = x - 2; xx <= x; xx++) {
            for (int yy = y - 2; yy <= y; yy++) {
                for (int zz = z - 2; zz <= z; zz++) {
                    if (!fitArcaneFurnace(world, xx, yy, zz)
                            || !wand.consumeAllVisCrafting(wandStack, player, new AspectList().add(Aspect.FIRE, 50).add(Aspect.EARTH, 50), true)) {
                        continue;
                    }
                    if (!world.isRemote) {
                        return replaceArcaneFurnace(world, xx, yy, zz);
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private static boolean fitArcaneFurnace(World world, int x, int y, int z) {
        Block obsidian = Blocks.OBSIDIAN;
        Block netherrack = Blocks.NETHERRACK;
        Block fence = Blocks.NETHER_BRICK_FENCE;
        Block lava = Blocks.LAVA;
        Block[][][] blueprint = new Block[][][]{
                {
                        {netherrack, obsidian, netherrack},
                        {obsidian, Blocks.AIR, obsidian},
                        {netherrack, obsidian, netherrack}
                },
                {
                        {netherrack, obsidian, netherrack},
                        {obsidian, lava, obsidian},
                        {netherrack, obsidian, netherrack}
                },
                {
                        {netherrack, obsidian, netherrack},
                        {obsidian, obsidian, obsidian},
                        {netherrack, obsidian, netherrack}
                }
        };

        boolean fenceFound = false;
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    BlockPos target = new BlockPos(x + xx, y - yy + 2, z + zz);
                    Block found = world.isAirBlock(target) ? Blocks.AIR : world.getBlockState(target).getBlock();
                    if (found == blueprint[yy][xx][zz]) continue;

                    boolean crossSlot = (xx == 1 || zz == 1) && xx != zz;
                    if (yy == 1 && !fenceFound && found == fence && crossSlot) {
                        fenceFound = true;
                        continue;
                    }
                    return false;
                }
            }
        }
        return fenceFound;
    }

    private static boolean replaceArcaneFurnace(World world, int x, int y, int z) {
        for (int yy = 0; yy < 3; yy++) {
            int step = 1;
            for (int zz = 0; zz < 3; zz++) {
                for (int xx = 0; xx < 3; xx++) {
                    BlockPos target = new BlockPos(x + xx, y + yy, z + zz);
                    int meta = step;
                    Block found = world.getBlockState(target).getBlock();
                    if (found == Blocks.LAVA || found == Blocks.FIRE) {
                        meta = 0;
                    } else if (found == Blocks.NETHER_BRICK_FENCE) {
                        meta = 10;
                    }
                    if (!world.isAirBlock(target)) {
                        world.setBlockState(target, ConfigBlocks.blockArcaneFurnace.getDefaultState().withProperty(BlockArcaneFurnace.TYPE, meta), 3);
                        world.notifyNeighborsOfStateChange(target, ConfigBlocks.blockArcaneFurnace, false);
                    }
                    step++;
                }
            }
        }

        for (int yy = 0; yy < 3; yy++) {
            for (int zz = 0; zz < 3; zz++) {
                for (int xx = 0; xx < 3; xx++) {
                    BlockPos target = new BlockPos(x + xx, y + yy, z + zz);
                    world.notifyBlockUpdate(target, world.getBlockState(target), world.getBlockState(target), 3);
                }
            }
        }
        world.playSound(null, new BlockPos(x, y, z), TCSounds.WAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    private static boolean createInfusionAltar(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z) {
        if (wandStack.isEmpty() || !(wandStack.getItem() instanceof ItemWandCasting)) return false;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        for (int xx = x - 2; xx <= x; xx++) {
            for (int yy = y - 2; yy <= y; yy++) {
                for (int zz = z - 2; zz <= z; zz++) {
                    if (!fitInfusionAltar(world, xx, yy, zz)
                            || !wand.consumeAllVisCrafting(wandStack, player,
                            new AspectList()
                                    .add(Aspect.FIRE, 25)
                                    .add(Aspect.EARTH, 25)
                                    .add(Aspect.ORDER, 25)
                                    .add(Aspect.AIR, 25)
                                    .add(Aspect.ENTROPY, 25)
                                    .add(Aspect.WATER, 25), true)) {
                        continue;
                    }
                    if (!world.isRemote) {
                        replaceInfusionAltar(world, xx, yy, zz);
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private static boolean fitInfusionAltar(World world, int x, int y, int z) {
        ItemStack br1 = new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 6);
        ItemStack br2 = new ItemStack(ConfigBlocks.blockCosmeticSolid, 1, 7);
        ItemStack bs = new ItemStack(ConfigBlocks.blockStoneDevice, 1, 2);
        ItemStack[][][] blueprint = new ItemStack[][][]{
                {
                        {null, null, null},
                        {null, bs, null},
                        {null, null, null}
                },
                {
                        {br1, null, br1},
                        {null, null, null},
                        {br1, null, br1}
                },
                {
                        {br2, null, br2},
                        {null, null, null},
                        {br2, null, br2}
                }
        };
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    BlockPos target = new BlockPos(x + xx, y - yy + 2, z + zz);
                    ItemStack expected = blueprint[yy][xx][zz];
                    if (expected == null) {
                        if (xx == 1 && zz == 1 && yy == 2) {
                            TileEntity tile = world.getTileEntity(target);
                            if (tile instanceof TilePedestal) {
                                continue;
                            }
                            return false;
                        }
                        if (world.isAirBlock(target)) continue;
                        return false;
                    }
                    ItemStack found = new ItemStack(world.getBlockState(target).getBlock(), 1, world.getBlockState(target).getBlock().getMetaFromState(world.getBlockState(target)));
                    if (found.isItemEqual(expected)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    private static void replaceInfusionAltar(World world, int x, int y, int z) {
        int[][][] blueprint = new int[][][]{
                {
                        {0, 0, 0},
                        {0, 9, 0},
                        {0, 0, 0}
                },
                {
                        {1, 0, 1},
                        {0, 0, 0},
                        {1, 0, 1}
                },
                {
                        {2, 0, 3},
                        {0, 0, 0},
                        {4, 0, 5}
                }
        };
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    int code = blueprint[yy][xx][zz];
                    if (code == 0) continue;
                    BlockPos target = new BlockPos(x + xx, y - yy + 2, z + zz);
                    if (code == 1) {
                        world.setBlockState(target, ConfigBlocks.blockStoneDevice.getDefaultState().withProperty(thaumcraft.common.blocks.BlockStoneDevice.TYPE, 4), 3);
                        world.notifyNeighborsOfStateChange(target, ConfigBlocks.blockStoneDevice, false);
                        continue;
                    }
                    if (code > 1 && code < 9) {
                        world.setBlockState(target, ConfigBlocks.blockStoneDevice.getDefaultState().withProperty(thaumcraft.common.blocks.BlockStoneDevice.TYPE, 3), 3);
                        TileEntity tile = world.getTileEntity(target);
                        if (tile instanceof TileInfusionPillar) {
                            ((TileInfusionPillar) tile).orientation = (byte) code;
                            tile.markDirty();
                        }
                        world.notifyBlockUpdate(target, world.getBlockState(target), world.getBlockState(target), 3);
                        world.notifyNeighborsOfStateChange(target, ConfigBlocks.blockStoneDevice, false);
                        continue;
                    }
                    TileEntity tile = world.getTileEntity(target);
                    if (tile instanceof TileInfusionMatrix) {
                        ((TileInfusionMatrix) tile).active = true;
                        tile.markDirty();
                    }
                    world.notifyBlockUpdate(target, world.getBlockState(target), world.getBlockState(target), 3);
                }
            }
        }
        world.playSound(null, new BlockPos(x, y, z), TCSounds.WAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    private static boolean createNodeJar(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z) {
        return false;
    }

    private static boolean createOculus(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z, int side) {
        return false;
    }

    private static boolean createAdvancedAlchemicalFurnace(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z, int side) {
        return false;
    }

    private static final class PouchLocation {
        final int id;
        final int slot;
        final boolean bauble;

        PouchLocation(int id, int slot, boolean bauble) {
            this.id = id;
            this.slot = slot;
            this.bauble = bauble;
        }
    }

    private static final class FocusLocation {
        final boolean inPouch;
        final int slot;
        final int pouchId;

        private FocusLocation(boolean inPouch, int slot, int pouchId) {
            this.inPouch = inPouch;
            this.slot = slot;
            this.pouchId = pouchId;
        }

        static FocusLocation inventory(int slot) {
            return new FocusLocation(false, slot, 0);
        }

        static FocusLocation pouch(int pouchId, int slot) {
            return new FocusLocation(true, slot, pouchId);
        }
    }
}
