package thaumcraft.common.entities.golems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;

import java.util.ArrayList;

public class ItemGolemBell extends Item {
    public ItemGolemBell() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);

    }

    public static int getGolemId(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("golemid")) {
            return stack.getTagCompound().getInteger("golemid");
        }
        return -1;
    }

    public static int getGolemHomeFace(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("golemhomeface")) {
            return stack.getTagCompound().getInteger("golemhomeface");
        }
        return -1;
    }

    public static BlockPos getGolemHomeCoords(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("golemhomex")) {
            return new BlockPos(stack.getTagCompound().getInteger("golemhomex"),
                    stack.getTagCompound().getInteger("golemhomey"),
                    stack.getTagCompound().getInteger("golemhomez"));
        }
        return null;
    }

    public static ArrayList<Marker> getMarkers(ItemStack stack) {
        ArrayList<Marker> markers = new ArrayList<>();
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("markers")) {
            NBTTagList tagList = stack.getTagCompound().getTagList("markers", 10);
            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                markers.add(new Marker(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"),
                        (byte) tag.getInteger("dim"), tag.getByte("side"), tag.getByte("color")));
            }
        }
        return markers;
    }

    public static void resetMarkers(ItemStack stack, World world, EntityPlayer player) {
        if (stack == null || stack.isEmpty() || world == null) return;
        int id = getGolemId(stack);
        Entity entity = id >= 0 ? world.getEntityByID(id) : null;
        if (entity instanceof EntityGolemBase) {
            getOrCreateTag(stack).setTag("markers", new NBTTagList());
            ((EntityGolemBase) entity).setMarkers(new ArrayList<Marker>());
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.7F, 1.0F + world.rand.nextFloat() * 0.1F);
        }
    }

    public static void changeMarkers(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
        Entity linked = null;
        ArrayList<Marker> markers = getMarkers(stack);
        boolean markMultipleColors = false;
        int id = getGolemId(stack);
        if (id > -1) {
            linked = world.getEntityByID(id);
            if (linked instanceof EntityGolemBase && ((EntityGolemBase) linked).getUpgradeAmount(4) > 0) {
                markMultipleColors = true;
            }
        }

        int index = -1;
        int color = 0;
        byte dim = (byte) world.provider.getDimension();
        byte sideIndex = (byte) side.getIndex();
        if (!markMultipleColors) {
            index = markers.indexOf(new Marker(pos.getX(), pos.getY(), pos.getZ(), dim, sideIndex, (byte) -1));
        } else {
            for (int testColor = -1; testColor < 16; testColor++) {
                index = markers.indexOf(new Marker(pos.getX(), pos.getY(), pos.getZ(), dim, sideIndex, (byte) testColor));
                color = testColor;
                if (index != -1) break;
            }
        }

        boolean changed = true;
        if (index >= 0) {
            markers.remove(index);
            if (markMultipleColors && !player.isSneaking() && ++color <= 15) {
                markers.add(new Marker(pos.getX(), pos.getY(), pos.getZ(), dim, sideIndex, (byte) color));
            }
        } else {
            markers.add(new Marker(pos.getX(), pos.getY(), pos.getZ(), dim, sideIndex, (byte) -1));
        }

        if (changed) {
            getOrCreateTag(stack).setTag("markers", writeMarkers(markers));
            if (id > -1) {
                if (linked instanceof EntityGolemBase) {
                    ((EntityGolemBase) linked).setMarkers(markers);
                } else {
                    clearLinkedGolem(stack);
                }
            }
        }
        world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.7F, 1.0F + world.rand.nextFloat() * 0.1F);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && side != null) {
            changeMarkers(stack, player, world, pos, side);
        }
        return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!(target instanceof EntityGolemBase)) {
            return false;
        }
        if (!target.world.isRemote) {
            EntityGolemBase golem = (EntityGolemBase) target;
            NBTTagCompound tag = getOrCreateTag(stack);
            clearLinkedGolem(stack);
            tag.setTag("markers", writeMarkers(golem.getMarkers()));
            tag.setInteger("golemid", target.getEntityId());
            BlockPos home = golem.getHomePosition();
            tag.setInteger("golemhomex", home.getX());
            tag.setInteger("golemhomey", home.getY());
            tag.setInteger("golemhomez", home.getZ());
            tag.setInteger("golemhomeface", golem.homeFacing);
            target.world.playSound(null, target.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.NEUTRAL, 0.7F, 1.0F + target.world.rand.nextFloat() * 0.1F);
            if (player.capabilities.isCreativeMode) {
                player.setHeldItem(hand, stack.copy());
            }
        }
        player.swingArm(hand);
        return true;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        if (entity instanceof EntityTravelingTrunk && !entity.isDead) {
            return pickupTrunk(player, (EntityTravelingTrunk) entity);
        }
        if (entity instanceof EntityGolemBase && !entity.isDead) {
            return pickupGolem(player, (EntityGolemBase) entity);
        }
        return false;
    }

    private static boolean pickupTrunk(EntityPlayer player, EntityTravelingTrunk trunk) {
        int upgrade = trunk.getUpgrade();
        if (upgrade == 3 && trunk.getOwnerId() != null && !trunk.getOwnerId().equals(player.getUniqueID())) {
            return false;
        }
        if (trunk.world.isRemote) {
            player.swingArm(EnumHand.MAIN_HAND);
            return true;
        }

        ItemStack dropped = new ItemStack(ConfigItems.itemTrunkSpawner);
        if (player.isSneaking()) {
            if (upgrade > -1 && trunk.world.rand.nextBoolean()) {
                trunk.entityDropItem(new ItemStack(ConfigItems.itemGolemUpgrade, 1, upgrade), 0.5F);
            }
        } else {
            if (trunk.hasCustomName()) {
                dropped.setStackDisplayName(trunk.getCustomNameTag());
            }
            NBTTagCompound tag = getOrCreateTag(dropped);
            tag.setByte("upgrade", (byte) upgrade);
            if (upgrade == 4) {
                tag.setTag("inventory", trunk.inventory.writeToNBT(new NBTTagList()));
            }
        }

        trunk.entityDropItem(dropped, 0.5F);
        if (upgrade != 4 || player.isSneaking()) {
            trunk.inventory.dropAllItems();
        }
        trunk.world.playSound(null, trunk.getPosition(), TCSounds.ZAP, SoundCategory.NEUTRAL, 0.5F, 1.0F);
        trunk.setDead();
        return true;
    }

    private static boolean pickupGolem(EntityPlayer player, EntityGolemBase golem) {
        if (golem.world.isRemote) {
            player.swingArm(EnumHand.MAIN_HAND);
            return true;
        }

        ItemStack dropped = new ItemStack(ConfigItems.itemGolemPlacer, 1, golem.golemType.ordinal());
        if (golem.advanced) {
            getOrCreateTag(dropped).setBoolean("advanced", true);
        }
        if (player.isSneaking()) {
            if (golem.getCore() > -1) {
                golem.entityDropItem(new ItemStack(ConfigItems.itemGolemCore, 1, golem.getCore()), 0.5F);
            }
            for (byte upgrade : golem.upgrades) {
                if (upgrade > -1 && golem.world.rand.nextBoolean()) {
                    golem.entityDropItem(new ItemStack(ConfigItems.itemGolemUpgrade, 1, upgrade), 0.5F);
                }
            }
        } else {
            if (golem.hasCustomName()) {
                dropped.setStackDisplayName(golem.getCustomNameTag());
            }
            NBTTagCompound tag = getOrCreateTag(dropped);
            if (golem.decoration.length() > 0) {
                tag.setString("deco", golem.decoration);
            }
            if (golem.getCore() > -1) {
                tag.setByte("core", golem.getCore());
            }
            tag.setByteArray("upgrades", golem.upgrades);
            tag.setTag("markers", writeMarkers(golem.getMarkers()));
            if (golem.inventory != null) {
                tag.setTag("Inventory", golem.inventory.writeToNBT(new NBTTagList()));
            }
        }

        golem.entityDropItem(dropped, 0.5F);
        golem.dropStuff();
        golem.world.playSound(null, golem.getPosition(), TCSounds.ZAP, SoundCategory.NEUTRAL, 0.5F, 1.0F);
        golem.setDead();
        return true;
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    private static NBTTagList writeMarkers(ArrayList<Marker> markers) {
        NBTTagList tagList = new NBTTagList();
        for (Marker marker : markers) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("x", marker.x);
            tag.setInteger("y", marker.y);
            tag.setInteger("z", marker.z);
            tag.setInteger("dim", marker.dim);
            tag.setByte("side", marker.side);
            tag.setByte("color", marker.color);
            tagList.appendTag(tag);
        }
        return tagList;
    }

    private static void clearLinkedGolem(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        tag.removeTag("golemid");
        tag.removeTag("markers");
        tag.removeTag("golemhomex");
        tag.removeTag("golemhomey");
        tag.removeTag("golemhomez");
        tag.removeTag("golemhomeface");
    }
}
