package thaumcraft.common.lib.utils;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXVisDrain;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {

    public static final Map<List<Object>, ItemStack> specialMiningResult = new HashMap<>();
    public static final Map<List<Object>, Float> specialMiningChance = new HashMap<>();
    private static final Map<WorldCoordinates, Long> effectBuffer = new HashMap<>();

    public static void generateVisEffect(World world, BlockPos from, BlockPos to, int color) {
        if (world.isRemote) return;

        int dim = world.provider.getDimension();
        WorldCoordinates key = new WorldCoordinates(from.getX(), from.getY(), from.getZ(), dim);
        long now = System.currentTimeMillis();
        Long last = effectBuffer.get(key);

        // Rate limit: max one packet per 500ms per source position
        if (last != null && now - last < 500L) {
            return;
        }
        effectBuffer.put(key, now + 500L + (long)(Math.random() * 100.0));

        // Cleanup stale entries (older than 10 seconds)
        if (effectBuffer.size() > 1000) {
            effectBuffer.values().removeIf(v -> now - v > 10000L);
        }

        PacketHandler.INSTANCE.sendToAllAround(
            new PacketFXVisDrain(from, to, color),
            new NetworkRegistry.TargetPoint(dim, from.getX() + 0.5, from.getY() + 0.5, from.getZ() + 0.5, 64.0));
    }

    public static void setPrivateFinalValue(Class<?> cls, Object instance, Object value, String... fieldNames) {
    }

    public static void addSpecialMiningResult(ItemStack in, ItemStack out, float chance) {
        if (in == null || in.isEmpty() || out == null || out.isEmpty()) {
            return;
        }
        List<Object> key = Arrays.asList(in.getItem(), in.getItemDamage());
        specialMiningResult.put(key, out.copy());
        specialMiningChance.put(key, chance);
    }

    public static ItemStack findSpecialMiningResult(ItemStack is, float chance, Random rand) {
        if (is == null || is.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack dropped = is.copy();
        List<Object> key = Arrays.asList(is.getItem(), is.getItemDamage());
        Float rate = specialMiningChance.get(key);
        ItemStack replacement = specialMiningResult.get(key);
        if (rate != null && replacement != null && rand.nextFloat() <= chance * rate) {
            dropped = replacement.copy();
            dropped.setCount(dropped.getCount() * is.getCount());
        }
        return dropped;
    }

    public static void resetFloatCounter(EntityPlayerMP player) {
        try {
            ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, player.connection, 0,
                    "floatingTickCount", "field_147365_f");
        } catch (Exception ignored) {}
    }

    public static ItemStack generateLoot(int rarity, Random random) {
        ItemStack loot = ItemStack.EMPTY;
        if (rarity > 0 && random.nextFloat() < 0.025F * (float) rarity) {
            loot = genGear(rarity, random);
            if (loot.isEmpty()) {
                loot = generateLoot(rarity, random);
            }
        } else {
            List<WeightedRandomLoot> table = rarity >= 2 ? WeightedRandomLoot.lootBagRare
                    : rarity == 1 ? WeightedRandomLoot.lootBagUncommon : WeightedRandomLoot.lootBagCommon;
            if (!table.isEmpty()) {
                loot = ((WeightedRandomLoot) WeightedRandom.getRandomItem(random, table)).item.copy();
            }
        }
        if (loot.isEmpty()) {
            return fallbackCoins(random, rarity);
        }
        if (loot.getItem() == Items.BOOK) {
            loot = EnchantmentHelper.addRandomEnchantment(random, loot,
                    (int) (5.0F + (float) rarity * 0.75F * (float) random.nextInt(18)), false);
        }
        return loot.copy();
    }

    private static ItemStack fallbackCoins(Random random, int rarity) {
        if (ConfigItems.itemResource != null) {
            return new ItemStack(ConfigItems.itemResource, 1 + random.nextInt(rarity >= 2 ? 4 : 2), ItemResource.META_COIN);
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack genGear(int rarity, Random random) {
        int quality = random.nextInt(2);
        if (random.nextFloat() < 0.2F) quality++;
        if (random.nextFloat() < 0.15F) quality++;
        if (random.nextFloat() < 0.1F) quality++;
        if (random.nextFloat() < 0.095F) quality++;
        if (random.nextFloat() < 0.095F) quality++;
        Item item = getGearItemForSlot(random.nextInt(5), quality);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        int maxDamage = item.getMaxDamage();
        ItemStack stack = new ItemStack(item, 1, maxDamage > 0 ? random.nextInt(1 + maxDamage / 6) : 0);
        if (random.nextInt(4) < rarity) {
            stack = EnchantmentHelper.addRandomEnchantment(random, stack,
                    (int) (5.0F + (float) rarity * 0.75F * (float) random.nextInt(18)), false);
        }
        return stack.copy();
    }

    private static Item getGearItemForSlot(int slot, int quality) {
        switch (slot) {
            case 4:
                if (quality == 0) return Items.LEATHER_HELMET;
                if (quality == 1) return Items.CHAINMAIL_HELMET;
                if (quality == 2) return Items.IRON_HELMET;
                if (quality == 3) return Items.GOLDEN_HELMET;
                if (quality == 4) return ConfigItems.itemHelmThaumium;
                if (quality == 5) return Items.DIAMOND_HELMET;
                if (quality == 6) return ConfigItems.itemHelmVoid;
                break;
            case 3:
                if (quality == 0) return Items.LEATHER_CHESTPLATE;
                if (quality == 1) return Items.CHAINMAIL_CHESTPLATE;
                if (quality == 2) return Items.IRON_CHESTPLATE;
                if (quality == 3) return Items.GOLDEN_CHESTPLATE;
                if (quality == 4) return ConfigItems.itemChestThaumium;
                if (quality == 5) return Items.DIAMOND_CHESTPLATE;
                if (quality == 6) return ConfigItems.itemChestVoid;
                break;
            case 2:
                if (quality == 0) return Items.LEATHER_LEGGINGS;
                if (quality == 1) return Items.CHAINMAIL_LEGGINGS;
                if (quality == 2) return Items.IRON_LEGGINGS;
                if (quality == 3) return Items.GOLDEN_LEGGINGS;
                if (quality == 4) return ConfigItems.itemLegsThaumium;
                if (quality == 5) return Items.DIAMOND_LEGGINGS;
                if (quality == 6) return ConfigItems.itemLegsVoid;
                break;
            case 1:
                if (quality == 0) return Items.LEATHER_BOOTS;
                if (quality == 1) return Items.CHAINMAIL_BOOTS;
                if (quality == 2) return Items.IRON_BOOTS;
                if (quality == 3) return Items.GOLDEN_BOOTS;
                if (quality == 4) return ConfigItems.itemBootsThaumium;
                if (quality == 5) return Items.DIAMOND_BOOTS;
                if (quality == 6) return ConfigItems.itemBootsVoid;
                break;
            case 0:
                if (quality == 0) return Items.WOODEN_SWORD;
                if (quality == 1) return Items.STONE_SWORD;
                if (quality == 2) return Items.IRON_SWORD;
                if (quality == 3) return Items.GOLDEN_SWORD;
                if (quality == 4) return ConfigItems.itemSwordThaumium;
                if (quality == 5) return Items.DIAMOND_SWORD;
                if (quality == 6) return ConfigItems.itemSwordVoid;
                break;
            default:
                break;
        }
        return null;
    }

    /** Set the biome at a given x,z position in the world (used for taint spread in Eldritch dimension). */
    public static void setBiomeAt(World world, int x, int z, Biome biome) {
        setBiomeAt(world, new BlockPos(x, 0, z), biome, true);
    }

    /** TC6-compatible biome mutation overload used by addons and coremod mixins. */
    public static void setBiomeAt(World world, BlockPos pos, Biome biome) {
        setBiomeAt(world, pos, biome, true);
    }

    /** TC6-compatible biome mutation overload used by addons and coremod mixins. */
    public static void setBiomeAt(World world, BlockPos pos, Biome biome, boolean sync) {
        if (world == null || pos == null || biome == null) return;
        int biomeId = Biome.getIdForBiome(biome);
        if (biomeId < 0) return;
        int x = pos.getX();
        int z = pos.getZ();
        Chunk chunk = world.getChunk(pos);
        if (chunk == null) return;
        byte[] biomes = chunk.getBiomeArray();
        int index = ((z & 15) << 4) | (x & 15);
        biomes[index] = (byte) (biomeId & 255);
        chunk.setBiomeArray(biomes);
        chunk.markDirty();
        world.markBlockRangeForRenderUpdate(new BlockPos(x, 0, z), new BlockPos(x, world.getActualHeight(), z));
        if (sync && !world.isRemote) {
            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketBiomeChange(x, z, (short) biomeId),
                    new NetworkRegistry.TargetPoint(
                            world.provider.getDimension(),
                            x,
                            world.getHeight(new BlockPos(x, 0, z)).getY(),
                            z,
                            32.0));
        }
    }

    public static boolean resetBiomeAt(World world, BlockPos pos) {
        return resetBiomeAt(world, pos, true);
    }

    public static boolean resetBiomeAt(World world, BlockPos pos, boolean sync) {
        if (world == null || pos == null || world.getBiomeProvider() == null) return false;
        Biome[] biomes = world.getBiomeProvider().getBiomes(null, pos.getX(), pos.getZ(), 1, 1);
        if (biomes != null && biomes.length > 0 && biomes[0] != null) {
            Biome biome = biomes[0];
            if (biome != world.getBiome(pos)) {
                setBiomeAt(world, pos, biome, sync);
                return true;
            }
        }
        return false;
    }

    public static byte pack(boolean[] bits) {
        byte v = 0;
        for (int i = 0; i < Math.min(bits.length, 8); i++) {
            if (bits[i]) v |= (1 << i);
        }
        return v;
    }

    public static boolean[] unpack(byte v) {
        boolean[] bits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = (v & (1 << i)) != 0;
        }
        return bits;
    }

    public static boolean isWoodLog(net.minecraft.world.IBlockAccess world, BlockPos pos) {
        net.minecraft.block.Block block = world.getBlockState(pos).getBlock();
        if (block == net.minecraft.init.Blocks.AIR) return false;
        // Check if the block can sustain leaves (all vanilla and most mod logs do this)
        if (block.canSustainLeaves(world.getBlockState(pos), world, pos)) return true;
        return false;
    }
}
