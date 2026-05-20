package thaumcraft.common.entities.ai.interact;

import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityGolemBobber;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIFish extends EntityAIBase {
    private EntityGolemBase theGolem;
    private float quality;
    private float distance;
    private World theWorld;
    private int maxDelay = 1;
    private int mod = 1;
    private int count = 0;
    private Vec3d target = null;
    private EntityGolemBobber bobber = null;

    // Weighted loot tables (replaces WeightedRandomFishable which doesn't exist in 1.12.2)
    private static final List<WeightedLoot> LOOT_CRAP = new ArrayList<>();
    private static final List<WeightedLoot> LOOT_RARE = new ArrayList<>();
    private static final List<WeightedLoot> LOOT_FISH = new ArrayList<>();

    static {
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.LEATHER_BOOTS), 10, 0.9F));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.ROTTEN_FLESH), 10));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.BONE), 10));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.STRING), 10));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.BOWL), 5));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.FISHING_ROD), 2, 0.9F));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.STICK), 10));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.BONE), 5));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.ARROW, 10), 5));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Blocks.TRIPWIRE_HOOK), 10));
        LOOT_CRAP.add(new WeightedLoot(new ItemStack(Items.GUNPOWDER), 10));

        LOOT_RARE.add(new WeightedLoot(new ItemStack(Blocks.WATERLILY), 1));
        LOOT_RARE.add(new WeightedLoot(new ItemStack(Items.NAME_TAG), 1));
        LOOT_RARE.add(new WeightedLoot(new ItemStack(Items.SADDLE), 1));
        LOOT_RARE.add(new WeightedLoot(new ItemStack(Items.BOW), 1, 0.25F));
        LOOT_RARE.add(new WeightedLoot(new ItemStack(Items.FISHING_ROD), 1, 0.25F));
        LOOT_RARE.add(new WeightedLoot(new ItemStack(Items.ENCHANTED_BOOK), 1));

        LOOT_FISH.add(new WeightedLoot(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.COD.getMetadata()), 60));
        LOOT_FISH.add(new WeightedLoot(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.SALMON.getMetadata()), 25));
        LOOT_FISH.add(new WeightedLoot(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.CLOWNFISH.getMetadata()), 2));
        LOOT_FISH.add(new WeightedLoot(new ItemStack(Items.FISH, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata()), 13));
    }

    public AIFish(EntityGolemBase golem) {
        this.theGolem = golem;
        this.theWorld = golem.world;
        this.setMutexBits(3);
        this.distance = MathHelper.floor(theGolem.getRange() / 2.0F);
    }

    @Override
    public boolean shouldExecute() {
        if (this.target != null || this.count > 0) return false;
        if (theGolem.ticksExisted % Config.golemDelay > 0) return false;
        if (!theGolem.getNavigator().noPath()) return false;

        if (this.bobber != null) {
            this.bobber.setDead();
        }

        Vec3d vv = this.findWater();
        if (vv == null) return false;

        this.target = new Vec3d(vv.x, vv.y, vv.z);
        this.quality = 0.0F;
        int x = (int) this.target.x;
        int y = (int) this.target.y;
        int z = (int) this.target.z;

        for (int a = 2; a <= 5; a++) {
            EnumFacing dir = EnumFacing.VALUES[a];
            BlockPos p = new BlockPos(x + dir.getXOffset(), y + dir.getYOffset(), z + dir.getZOffset());
            if (theWorld.getBlockState(p).getMaterial() == Material.WATER && theWorld.isAirBlock(p.up())) {
                this.quality += 3.0E-5F;
                if (theWorld.canBlockSeeSky(p.up())) {
                    this.quality += 3.0E-5F;
                }
                for (int depth = 1; depth <= 3; depth++) {
                    BlockPos dp = new BlockPos(x + dir.getXOffset(), y - depth + dir.getYOffset(), z + dir.getZOffset());
                    if (theWorld.getBlockState(dp).getMaterial() == Material.WATER) {
                        this.quality += 1.5E-5F;
                    }
                }
            }
        }

        theWorld.playSound(null, theGolem.getPosition(),
            SoundEvent.REGISTRY.getObject(new ResourceLocation("random.bow")),
            SoundCategory.NEUTRAL, 0.5F,
            0.4F / (theWorld.rand.nextFloat() * 0.4F + 0.8F));

        this.bobber = new EntityGolemBobber(theWorld, theGolem, x, y, z);
        return theWorld.spawnEntity(this.bobber);
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.bobber != null && !this.bobber.isDead && this.target != null && this.count-- > 0;
    }

    @Override
    public void updateTask() {
        if (this.target != null) {
            theGolem.getLookHelper().setLookPosition(this.target.x + 0.5, this.target.y + 1.0, this.target.z + 0.5, 30.0F, 30.0F);
            float chance = this.quality + (float) theGolem.getGolemStrength() * 1.5E-4F;
            if (theWorld.rand.nextFloat() < chance) {
                theGolem.startRightArmTimer();
                int qq = 1;
                if (theGolem.getUpgradeAmount(0) > 0 && theWorld.rand.nextInt(10) < theGolem.getUpgradeAmount(0)) {
                    qq++;
                }
                for (int a = 0; a < qq; a++) {
                    ItemStack fs = this.getFishingResult();
                    // Smelt upgrade
                    if (theGolem.getUpgradeAmount(2) > 0) {
                        ItemStack sr = FurnaceRecipes.instance().getSmeltingResult(fs);
                        if (sr != null && !sr.isEmpty()) fs = sr.copy();
                    }
                    EntityItem entityitem = new EntityItem(theWorld, this.target.x + 0.5, this.target.y + 1.0, this.target.z + 0.5, fs);
                    if (theGolem.getUpgradeAmount(2) > 0) {
                        entityitem.setNoDespawn();
                    }
                    entityitem.setPickupDelay(20);
                    double d1 = theGolem.posX + theWorld.rand.nextFloat() - theWorld.rand.nextFloat() - (this.target.x + 0.5);
                    double d3 = theGolem.posY - (this.target.y + 1.0);
                    double d5 = theGolem.posZ + theWorld.rand.nextFloat() - theWorld.rand.nextFloat() - (this.target.z + 0.5);
                    double d7 = MathHelper.sqrt(d1 * d1 + d3 * d3 + d5 * d5);
                    double d9 = 0.1;
                    entityitem.motionX = d1 * d9;
                    entityitem.motionY = d3 * d9 + MathHelper.sqrt(d7) * 0.08;
                    entityitem.motionZ = d5 * d9;
                    theWorld.spawnEntity(entityitem);
                }
                if (this.bobber != null) {
                    this.bobber.playSound(SoundEvent.REGISTRY.getObject(new ResourceLocation("random.splash")), 0.15F, 1.0F + (theWorld.rand.nextFloat() - theWorld.rand.nextFloat()) * 0.4F);
                    theWorld.setEntityState(this.bobber, EntityGolemBobber.STATUS_SPLASH_CATCH);
                    this.bobber.setDead();
                }
                this.target = null;
            }
        }
    }

    @Override
    public void resetTask() {
        if (this.bobber != null) {
            this.bobber.setDead();
        }
        this.target = null;
        this.count = -1;
    }

    @Override
    public void startExecuting() {
        this.count = 300 + theWorld.rand.nextInt(200);
        theGolem.startRightArmTimer();
    }

    private Vec3d findWater() {
        Random rand = theGolem.getRNG();
        BlockPos home = theGolem.getHomePosition();
        int var2 = 0;
        while ((float) var2 < this.distance * 2.0F) {
            int x = (int) ((float) (home.getX() + rand.nextInt((int) (1.0F + this.distance * 2.0F))) - this.distance);
            int y = (int) ((float) (home.getY() + rand.nextInt((int) (1.0F + this.distance))) - this.distance / 2.0F);
            int z = (int) ((float) (home.getZ() + rand.nextInt((int) (1.0F + this.distance * 2.0F))) - this.distance);
            BlockPos pos = new BlockPos(x, y, z);
            if (theWorld.getBlockState(pos).getMaterial() == Material.WATER && theWorld.isAirBlock(pos.up())) {
                return new Vec3d(x, y, z);
            }
            var2++;
        }
        return null;
    }

    private ItemStack getFishingResult() {
        Random rand = theWorld.rand;
        float f = rand.nextFloat();
        float f1 = 0.1F - (float) theGolem.getUpgradeAmount(5) * 0.025F;
        float f2 = 0.05F + (float) theGolem.getUpgradeAmount(4) * 0.0125F;

        // Adjust quality based on surrounding water
        if (this.target != null) {
            int x = (int) this.target.x;
            int y = (int) this.target.y;
            int z = (int) this.target.z;
            for (int a = 2; a <= 5; a++) {
                EnumFacing dir = EnumFacing.VALUES[a];
                BlockPos p = new BlockPos(x + dir.getXOffset(), y + dir.getYOffset(), z + dir.getZOffset());
                if (theWorld.getBlockState(p).getMaterial() == Material.WATER && theWorld.isAirBlock(p.up())) {
                    f1 -= 0.005F;
                    f2 += 0.00125F;
                    if (theWorld.canBlockSeeSky(p.up())) {
                        f1 -= 0.005F;
                        f2 += 0.00125F;
                    }
                    for (int depth = 1; depth <= 3; depth++) {
                        BlockPos dp = new BlockPos(x + dir.getXOffset(), y - depth + dir.getYOffset(), z + dir.getZOffset());
                        if (theWorld.getBlockState(dp).getMaterial() == Material.WATER) {
                            f2 += 0.001F;
                        }
                    }
                }
            }
        }

        f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
        f2 = MathHelper.clamp(f2, 0.0F, 1.0F);

        if (f < f1) {
            return getRandomLoot(LOOT_CRAP, rand);
        }
        f -= f1;
        if (f < f2) {
            return getRandomLoot(LOOT_RARE, rand);
        }
        return getRandomLoot(LOOT_FISH, rand);
    }

    private static ItemStack getRandomLoot(List<WeightedLoot> lootTable, Random rand) {
        int totalWeight = 0;
        for (WeightedLoot wl : lootTable) totalWeight += wl.weight;
        int roll = rand.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedLoot wl : lootTable) {
            cumulative += wl.weight;
            if (roll < cumulative) {
                ItemStack result = wl.stack.copy();
                if (rand.nextFloat() < wl.enchantChance) {
                    result = enchantItem(result, rand);
                }
                return result;
            }
        }
        return lootTable.get(0).stack.copy();
    }

    private static ItemStack enchantItem(ItemStack stack, Random rand) {
        try {
            return net.minecraft.enchantment.EnchantmentHelper.addRandomEnchantment(rand, stack, 30, false);
        } catch (Exception e) {
            return stack;
        }
    }

    private static class WeightedLoot {
        final ItemStack stack;
        final int weight;
        final float enchantChance;

        WeightedLoot(ItemStack stack, int weight) {
            this(stack, weight, 0.0F);
        }

        WeightedLoot(ItemStack stack, int weight, float enchantChance) {
            this.stack = stack;
            this.weight = weight;
            this.enchantChance = enchantChance;
        }
    }
}
