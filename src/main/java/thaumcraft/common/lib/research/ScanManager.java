package thaumcraft.common.lib.research;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectDiscovery;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedEntities;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedItems;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedPhenomena;

import java.util.Arrays;
import java.util.Random;

public class ScanManager implements IScanEventHandler {
    private static final long DEBUG_LOG_INTERVAL_MS = 1500L;
    private static long lastValidScanDebugLogMs = 0L;

    @Override
    public ScanResult scanPhenomena(ItemStack stack, World world, EntityPlayer player) {
        return stack == null || stack.isEmpty() ? null : scanItem(player, stack);
    }

    public static ScanResult scanEntity(EntityPlayer player, Entity entity) {
        if (player == null || entity == null) return null;
        ScanResult result = new ScanResult((byte)2, 0, 0, entity, null);
        return completeScan(player, result, "@") ? result : null;
    }

    public static ScanResult scanItem(EntityPlayer player, ItemStack stack) {
        if (player == null || stack == null || stack.isEmpty()) return null;
        ScanResult result = new ScanResult((byte)1, Item.getIdFromItem(stack.getItem()), stack.getMetadata(), null, null);
        return completeScan(player, result, "@") ? result : null;
    }

    public static ScanResult scanPhenomena(EntityPlayer player, String phenomenaKey) {
        if (player == null || phenomenaKey == null || phenomenaKey.isEmpty()) return null;
        ScanResult result = new ScanResult((byte)3, 0, 0, null, phenomenaKey);
        return completeScan(player, result, "@") ? result : null;
    }

    public static int generateItemHash(Item item, int meta) {
        if (item == null) return 0;
        ItemStack stack = new ItemStack(item, 1, meta);
        try {
            if (stack.isItemStackDamageable() || !item.getHasSubtypes()) {
                meta = -1;
            }
        } catch (Exception ignored) {
        }
        meta = normalizeGroupedMeta(item, meta);
        String name = item.getRegistryName() == null ? item.getTranslationKey(stack) : item.getRegistryName().toString();
        String hash = name + ":" + meta;
        if (!ThaumcraftApi.objectTags.containsKey(Arrays.asList(item, meta))) {
            for (Object keyObject : ThaumcraftApi.objectTags.keySet()) {
                if (!(keyObject instanceof java.util.List)) continue;
                java.util.List key = (java.util.List)keyObject;
                if (key.size() < 2 || key.get(0) != item || !(key.get(1) instanceof int[])) continue;
                int[] range = ((int[])key.get(1)).clone();
                Arrays.sort(range);
                if (Arrays.binarySearch(range, meta) < 0) continue;
                StringBuilder grouped = new StringBuilder(name);
                for (int value : range) grouped.append(':').append(value);
                return grouped.toString().hashCode();
            }
            if (meta == -1 && !ThaumcraftApi.objectTags.containsKey(Arrays.asList(item, -1))) {
                for (int index = 0; index < 16; index++) {
                    if (ThaumcraftApi.objectTags.containsKey(Arrays.asList(item, index))) {
                        hash = name + ":" + index;
                        break;
                    }
                }
            }
        }
        return hash.hashCode();
    }

    public static AspectList generateEntityAspects(Entity entity) {
        if (entity == null) return new AspectList();
        String key = getEntityKey(entity);
        AspectList tags = null;
        if (entity instanceof EntityPlayer) {
            String name = ((EntityPlayer)entity).getName();
            tags = new AspectList().add(Aspect.MAN, 4);
            if ("azanor".equalsIgnoreCase(name)) {
                tags.add(Aspect.ELDRITCH, 20);
            } else if ("direwolf20".equalsIgnoreCase(name)) {
                tags.add(Aspect.BEAST, 20);
            } else if ("pahimar".equalsIgnoreCase(name)) {
                tags.add(Aspect.EXCHANGE, 20);
            } else {
                Random random = new Random(("player_" + name).hashCode());
                Aspect[] values = Aspect.aspects.values().toArray(new Aspect[0]);
                if (values.length > 0) {
                    tags.add(values[random.nextInt(values.length)], 4);
                    tags.add(values[random.nextInt(values.length)], 4);
                    tags.add(values[random.nextInt(values.length)], 4);
                }
            }
        } else {
            for (ThaumcraftApi.EntityTags entityTags : ThaumcraftApi.scanEntities) {
                if (entityTags.entityName.equals(key) && matchesEntityTags(entity, entityTags)) {
                    tags = entityTags.aspects == null ? null : entityTags.aspects.copy();
                }
            }
        }
        return tags == null ? new AspectList() : tags;
    }

    public static boolean isValidScanTarget(EntityPlayer player, ScanResult scan, String prefix) {
        if (player == null || scan == null) return false;
        prefix = normalizePrefix(prefix);
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return false;
        if ("@".equals(prefix) && !isValidScanTarget(player, scan, "#")) return false;
        if (scan.type == 1) {
            Item item = Item.getItemById(scan.id);
            int meta = normalizeGroupedMeta(item, scan.meta);
            return item != null && !knowledge.hasScannedItem(prefix + generateItemHash(item, meta));
        }
        if (scan.type == 2) {
            if (scan.entity instanceof EntityItem) {
                ItemStack stack = getScannedItemStack((EntityItem)scan.entity);
                return !stack.isEmpty() && !knowledge.hasScannedItem(prefix + generateItemHash(stack.getItem(), stack.getMetadata()));
            }
            return scan.entity != null && !knowledge.hasScannedEntity(prefix + generateEntityHash(scan.entity));
        }
        return scan.type != 3 || !knowledge.hasScannedPhenomena(prefix + scan.phenomena);
    }

    public static boolean hasBeenScanned(EntityPlayer player, ScanResult scan) {
        if (player == null || scan == null) return false;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return false;
        if (scan.type == 1) {
            Item item = Item.getItemById(scan.id);
            int hash = generateItemHash(item, normalizeGroupedMeta(item, scan.meta));
            return knowledge.hasScannedItem("@" + hash) || knowledge.hasScannedItem("#" + hash);
        }
        if (scan.type == 2) {
            if (scan.entity instanceof EntityItem) {
                ItemStack stack = getScannedItemStack((EntityItem)scan.entity);
                int hash = generateItemHash(stack.getItem(), stack.getMetadata());
                return knowledge.hasScannedItem("@" + hash) || knowledge.hasScannedItem("#" + hash);
            }
            int hash = generateEntityHash(scan.entity);
            return knowledge.hasScannedEntity("@" + hash) || knowledge.hasScannedEntity("#" + hash);
        }
        return scan.type == 3 && (knowledge.hasScannedPhenomena("@" + scan.phenomena) || knowledge.hasScannedPhenomena("#" + scan.phenomena));
    }

    public static boolean completeScan(EntityPlayer player, ScanResult scan, String prefix) {
        if (player == null || scan == null) return false;
        prefix = normalizePrefix(prefix);
        if (!isValidScanTarget(player, scan, prefix)) return false;
        AspectList aspects = getScanAspects(scan, player.world);
        if (!validScan(aspects, player)) return false;

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return false;

        if (scan.type == 1) {
            Item item = Item.getItemById(scan.id);
            knowledge.scanItem(prefix + generateItemHash(item, normalizeGroupedMeta(item, scan.meta)));
        } else if (scan.type == 2) {
            if (scan.entity instanceof EntityItem) {
                ItemStack stack = getScannedItemStack((EntityItem)scan.entity);
                knowledge.scanItem(prefix + generateItemHash(stack.getItem(), stack.getMetadata()));
            } else {
                knowledge.scanEntity(prefix + generateEntityHash(scan.entity));
            }
        } else if (scan.type == 3) {
            knowledge.scanPhenomena(prefix + scan.phenomena);
        }

        if (!player.world.isRemote && aspects != null) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP)player;
                if (scan.type == 1 || scan.entity instanceof EntityItem) {
                    PacketHandler.INSTANCE.sendTo(new PacketSyncScannedItems(knowledge.getScannedItems()), mp);
                } else if (scan.type == 2) {
                    PacketHandler.INSTANCE.sendTo(new PacketSyncScannedEntities(knowledge.getScannedEntities()), mp);
                } else if (scan.type == 3) {
                    PacketHandler.INSTANCE.sendTo(new PacketSyncScannedPhenomena(knowledge.getScannedPhenomena()), mp);
                }
            }
            boolean scannedByThaumometerFallback = "#".equals(prefix) && !isValidScanTarget(player, scan, "@");
            AspectList awardedAspects = new AspectList();
            for (Aspect aspect : aspects.getAspects()) {
                if (aspect == null || !knowledge.hasDiscoveredParentAspects(aspect)) continue;
                int amount = aspects.getAmount(aspect);
                if (scannedByThaumometerFallback) amount = 0;
                if ("#".equals(prefix)) amount++;
                int awarded = checkAndSyncAspectKnowledge(player, aspect, amount);
                if (awarded > 0) {
                    awardedAspects.merge(aspect, awarded);
                }
            }
            Object clue = createScanClue(scan);
            if (clue != null) {
                ResearchManager.createClue(player.world, player, clue, awardedAspects);
            }
            ResearchManager.updateCache(player.getName(), knowledge);
        }
        return true;
    }

    public static int checkAndSyncAspectKnowledge(EntityPlayer player, Aspect aspect, int amount) {
        if (player == null || aspect == null) return 0;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return 0;
        int awarded = 0;
        if (!knowledge.hasDiscoveredAspect(aspect)) {
            knowledge.addDiscoveredAspect(aspect.getTag());
            amount += 2;
            awarded = amount;
            if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo((IMessage)new PacketAspectDiscovery(aspect.getTag()), (EntityPlayerMP)player);
            }
        }
        if (knowledge.getAspectPoolFor(aspect) >= Config.aspectTotalCap) {
            amount = (int)Math.sqrt(Math.max(0, amount));
        }
        if (amount > 1 && (float)knowledge.getAspectPoolFor(aspect) >= (float)Config.aspectTotalCap * 1.25F) {
            amount = 1;
        }
        if (amount > 0 && knowledge.addAspectPool(aspect, amount)) {
            awarded = amount;
            if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), (short)amount, knowledge.getAspectPoolFor(aspect)), (EntityPlayerMP)player);
            }
        }
        return awarded;
    }

    public static boolean validScan(AspectList aspects, EntityPlayer player) {
        if (player == null) return false;
        if (aspects == null || aspects.size() <= 0) return false;
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return false;
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null && !aspect.isPrimal() && !knowledge.hasDiscoveredParentAspects(aspect)) {
                return false;
            }
        }
        return true;
    }

    public static void notifyInvalidScan(AspectList aspects, EntityPlayer player) {
        if (player == null || player.world == null || !player.world.isRemote) return;
        if (aspects == null || aspects.size() <= 0) {
            logValidScanDebug(player, "reject-empty", null, null, aspects);
            Thaumcraft.proxy.notifyThaumometerUnknownObject();
            return;
        }
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return;
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect == null || aspect.isPrimal() || knowledge.hasDiscoveredParentAspects(aspect)) {
                continue;
            }
            Aspect missingParent = null;
            Aspect[] components = aspect.getComponents();
            if (components != null) {
                for (Aspect parent : components) {
                    if (parent == null || knowledge.hasDiscoveredAspect(parent)) {
                        continue;
                    }
                    missingParent = parent;
                    Thaumcraft.proxy.notifyThaumometerDiscoveryError(parent);
                    break;
                }
            }
            logValidScanDebug(player, "reject-missing-parent", aspect, missingParent, aspects);
            return;
        }
    }

    public static AspectList getScanAspects(ScanResult scan, World world) {
        if (scan == null) return new AspectList();
        if (scan.type == 1) {
            Item item = Item.getItemById(scan.id);
            int meta = normalizeGroupedMeta(item, scan.meta);
            return getObjectAspects(new ItemStack(item, 1, meta));
        }
        if (scan.type == 2) {
            if (scan.entity instanceof EntityItem) {
                return getObjectAspects(getScannedItemStack((EntityItem)scan.entity));
            }
            return generateEntityAspects(scan.entity);
        }
        if (scan.type == 3 && scan.phenomena != null && scan.phenomena.startsWith("NODE")) {
            return generateNodeAspects(world, scan.phenomena.substring(4));
        }
        return new AspectList();
    }

    private static int generateEntityHash(Entity entity) {
        if (entity == null) return 0;
        String hash = getEntityKey(entity);
        if (entity instanceof EntityPlayer) {
            hash = "player_" + ((EntityPlayer)entity).getName();
        }
        for (ThaumcraftApi.EntityTags entityTags : ThaumcraftApi.scanEntities) {
            if (entityTags.entityName.equals(hash) && entityTags.nbts != null && entityTags.nbts.length > 0 && matchesEntityTags(entity, entityTags)) {
                for (ThaumcraftApi.EntityTagsNBT nbt : entityTags.nbts) {
                    hash += nbt.name + String.valueOf(nbt.value);
                }
            }
        }
        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isChild()) hash += "CHILD";
        if (entity instanceof EntityCreeper && ((EntityCreeper)entity).getPowered()) hash += "POWERED";
        return hash.hashCode();
    }

    private static AspectList generateNodeAspects(World world, String nodeKey) {
        if (world == null || nodeKey == null) return new AspectList();
        String[] parts = nodeKey.startsWith(":") ? nodeKey.substring(1).split(":") : nodeKey.split(":");
        if (parts.length < 4) return new AspectList();
        try {
            int dim = Integer.parseInt(parts[0]);
            if (dim != world.provider.getDimension()) return new AspectList();
            BlockPos pos = new BlockPos(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof INode)) return new AspectList();
            INode node = (INode)tile;
            AspectList tags = new AspectList();
            AspectList nodeAspects = node.getAspects();
            if (nodeAspects != null) {
                for (Aspect aspect : nodeAspects.getAspectsSorted()) {
                    if (aspect != null) tags.merge(aspect, Math.max(4, nodeAspects.getAmount(aspect) / 10));
                }
            }
            NodeType type = node.getNodeType();
            if (type == NodeType.UNSTABLE) tags.merge(Aspect.ENTROPY, 4);
            else if (type == NodeType.HUNGRY) tags.merge(Aspect.HUNGER, 4);
            else if (type == NodeType.TAINTED) tags.merge(Aspect.TAINT, 4);
            else if (type == NodeType.PURE) tags.merge(Aspect.HEAL, 2).add(Aspect.ORDER, 2);
            else if (type == NodeType.DARK) tags.merge(Aspect.DEATH, 2).add(Aspect.DARKNESS, 2);
            return tags;
        } catch (NumberFormatException ignored) {
            return new AspectList();
        }
    }

    private static AspectList getObjectAspects(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return new AspectList();
        AspectList base = ThaumcraftCraftingManager.getObjectTags(stack);
        return ThaumcraftCraftingManager.getBonusTags(stack, base);
    }

    private static ItemStack getScannedItemStack(EntityItem item) {
        if (item == null || item.getItem().isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = item.getItem().copy();
        stack.setCount(1);
        stack.setItemDamage(normalizeGroupedMeta(stack.getItem(), stack.getMetadata()));
        return stack;
    }

    private static int normalizeGroupedMeta(Item item, int meta) {
        if (item == null) return meta;
        int[] grouped = ThaumcraftApi.groupedObjectTags.get(Arrays.asList(item, meta));
        return grouped != null && grouped.length > 0 ? grouped[0] : meta;
    }

    private static String normalizePrefix(String prefix) {
        return (prefix == null || prefix.isEmpty()) ? "@" : prefix;
    }

    private static Object createScanClue(ScanResult scan) {
        if (scan == null) return null;
        if (scan.type == 1) {
            Item item = Item.getItemById(scan.id);
            if (item == null) return null;
            return new ItemStack(item, 1, normalizeGroupedMeta(item, scan.meta));
        }
        if (scan.type == 2) {
            if (scan.entity instanceof EntityItem) {
                return getScannedItemStack((EntityItem) scan.entity);
            }
            return scan.entity == null ? null : getEntityKey(scan.entity);
        }
        return null;
    }

    private static String getEntityKey(Entity entity) {
        ResourceLocation key = EntityList.getKey(entity);
        return key == null ? "generic" : key.toString();
    }

    private static boolean matchesEntityTags(Entity entity, ThaumcraftApi.EntityTags entityTags) {
        if (entityTags.nbts == null || entityTags.nbts.length == 0) return true;
        NBTTagCompound nbt = new NBTTagCompound();
        entity.writeToNBT(nbt);
        for (ThaumcraftApi.EntityTagsNBT matcher : entityTags.nbts) {
            if (!nbt.hasKey(matcher.name)) return false;
            Object value = getNbtValue(nbt, matcher.name);
            if (value == null || !value.equals(matcher.value)) return false;
        }
        return true;
    }

    private static Object getNbtValue(NBTTagCompound nbt, String key) {
        byte type = nbt.getTagId(key);
        if (type == Constants.NBT.TAG_BYTE) return nbt.getByte(key);
        if (type == Constants.NBT.TAG_SHORT) return nbt.getShort(key);
        if (type == Constants.NBT.TAG_INT) return nbt.getInteger(key);
        if (type == Constants.NBT.TAG_LONG) return nbt.getLong(key);
        if (type == Constants.NBT.TAG_FLOAT) return nbt.getFloat(key);
        if (type == Constants.NBT.TAG_DOUBLE) return nbt.getDouble(key);
        if (type == Constants.NBT.TAG_STRING) return nbt.getString(key);
        return nbt.getTag(key);
    }

    private static void logValidScanDebug(EntityPlayer player, String reason, Aspect blockedAspect, Aspect missingParent, AspectList aspects) {
        long now = System.currentTimeMillis();
        if (now - lastValidScanDebugLogMs < DEBUG_LOG_INTERVAL_MS) {
            return;
        }
        lastValidScanDebugLogMs = now;

        Thaumcraft.log.info("[ThaumometerDebug] validScan {} for player {} blockedAspect={} missingParent={} aspects={}",
                reason,
                player == null ? "<null>" : player.getName(),
                blockedAspect == null ? "<none>" : blockedAspect.getTag(),
                missingParent == null ? "<none>" : missingParent.getTag(),
                aspects == null ? "<none>" : aspects.toString());
    }
}
