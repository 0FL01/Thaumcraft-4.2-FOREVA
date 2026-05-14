package thaumcraft.common.lib.research;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;

import java.util.Arrays;

public class ScanManager implements IScanEventHandler {

    @Override
    public ScanResult scanPhenomena(ItemStack stack, World world, EntityPlayer player) {
        return stack == null || stack.isEmpty() ? null : scanItem(player, stack);
    }

    /**
     * Scan an entity with the thaumometer.
     * Returns the ScanResult if successful, null otherwise.
     */
    public static ScanResult scanEntity(EntityPlayer player, Entity entity) {
        if (player == null || entity == null) return null;

        // Get the entity's registered name
        ResourceLocation entityKey = EntityList.getKey(entity);
        if (entityKey == null) return null;
        String entityName = entityKey.toString();

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return null;

        // Check if already scanned
        if (knowledge.hasScannedEntity(entityName)) {
            return null;
        }

        // Calculate aspects for this entity
        AspectList aspects = getEntityAspects(entity);

        // Mark as scanned
        knowledge.scanEntity(entityName);

        // Discover aspects
        if (aspects != null) {
            for (Aspect aspect : aspects.getAspects()) {
                knowledge.addDiscoveredAspect(aspect.getTag());
            }
        }

        return new ScanResult((byte) 2, 0, 0, entity, null);
    }

    /**
     * Scan an item stack with the thaumometer.
     */
    public static ScanResult scanItem(EntityPlayer player, ItemStack stack) {
        if (player == null || stack.isEmpty()) return null;

        String registryName = stack.getItem().getRegistryName().toString();
        if (registryName == null) return null;

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return null;

        // Check if already scanned
        if (knowledge.hasScannedItem(registryName)) {
            return null;
        }

        // Get aspects from the object tag registry
        AspectList aspects = getObjectAspects(stack);

        // Mark as scanned
        knowledge.scanItem(registryName);

        // Discover aspects
        if (aspects != null) {
            for (Aspect aspect : aspects.getAspects()) {
                knowledge.addDiscoveredAspect(aspect.getTag());
            }
        }

        return new ScanResult((byte) 1, Item.getIdFromItem(stack.getItem()), stack.getMetadata(), null, null);
    }

    /**
     * Scan a phenomenon (e.g., flux, aura, etc.)
     */
    public static ScanResult scanPhenomena(EntityPlayer player, String phenomenaKey) {
        if (player == null || phenomenaKey == null) return null;

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return null;

        // Check if already scanned
        if (knowledge.hasScannedPhenomena(phenomenaKey)) {
            return null;
        }

        // Mark as scanned
        knowledge.scanPhenomena(phenomenaKey);

        return new ScanResult((byte) 3, 0, 0, null, phenomenaKey);
    }

    /**
     * Get the aspects for a given entity based on registered entity tags.
     */
    public static AspectList getEntityAspects(Entity entity) {
        if (entity == null) return new AspectList();

        ResourceLocation key = EntityList.getKey(entity);
        if (key == null) return new AspectList();
        String entityName = key.toString();

        // Look up in registered entity tags
        for (ThaumcraftApi.EntityTags et : ThaumcraftApi.scanEntities) {
            if (et.entityName.equals(entityName)) {
                return et.aspects.copy();
            }
        }

        return new AspectList();
    }

    /**
     * Get aspects from object tags registry.
     */
    private static AspectList getObjectAspects(ItemStack stack) {
        if (stack.isEmpty()) return new AspectList();
        // Look up in the object tags registry
        java.util.List<Object> key = Arrays.asList(stack.getItem(), stack.getMetadata());
        AspectList aspects = ThaumcraftApi.objectTags.get(key);
        if (aspects == null) {
            // Try wildcard meta
            key = Arrays.asList(stack.getItem(), Short.MAX_VALUE);
            aspects = ThaumcraftApi.objectTags.get(key);
        }
        return aspects != null ? aspects.copy() : new AspectList();
    }
}
