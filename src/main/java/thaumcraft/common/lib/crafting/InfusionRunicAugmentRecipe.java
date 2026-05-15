package thaumcraft.common.lib.crafting;

import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.events.EventHandlerRunic;

public class InfusionRunicAugmentRecipe extends InfusionRecipe {
    private ItemStack[] components;

    public InfusionRunicAugmentRecipe() {
        super("RUNICAUGMENTATION", null, 0, null, null, new ItemStack[]{
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(ConfigItems.itemResource, 1, 14)
        });
        this.components = new ItemStack[]{
                new ItemStack(Items.ENDER_PEARL),
                new ItemStack(ConfigItems.itemResource, 1, 14)
        };
    }

    public InfusionRunicAugmentRecipe(ItemStack input) {
        this();
        this.components = this.getComponents(input);
    }

    @Override
    public boolean matches(ArrayList<ItemStack> input, ItemStack central, World world, EntityPlayer player) {
        if (this.research.length() > 0 && !ThaumcraftApiHelper.isResearchComplete(player.getName(), this.research)) {
            return false;
        }
        if (!(central.getItem() instanceof IRunicArmor)) {
            return false;
        }

        ArrayList<ItemStack> available = new ArrayList<ItemStack>();
        for (ItemStack stack : input) {
            available.add(stack.copy());
        }
        for (ItemStack component : this.getComponents(central)) {
            boolean found = false;
            for (int i = 0; i < available.size(); i++) {
                ItemStack compare = available.get(i).copy();
                if (component.getMetadata() == Short.MAX_VALUE) {
                    compare.setItemDamage(Short.MAX_VALUE);
                }
                if (!InfusionRecipe.areItemStacksEqual(compare, component, true)) {
                    continue;
                }
                available.remove(i);
                found = true;
                break;
            }
            if (!found) {
                return false;
            }
        }
        return available.size() == 0;
    }

    @Override
    public Object getRecipeOutput(ItemStack input) {
        if (input == null) {
            return null;
        }
        ItemStack output = input.copy();
        int hardening = EventHandlerRunic.getHardening(input) + 1;
        output.setTagInfo("RS.HARDEN", new NBTTagByte((byte) hardening));
        return output;
    }

    @Override
    public AspectList getAspects(ItemStack input) {
        AspectList out = new AspectList();
        int amount = (int) (32.0D * Math.pow(2.0D, EventHandlerRunic.getFinalCharge(input)));
        if (amount > 0) {
            out.add(Aspect.ARMOR, amount / 2);
            out.add(Aspect.MAGIC, amount / 2);
            out.add(Aspect.ENERGY, amount);
        }
        return out;
    }

    @Override
    public int getInstability(ItemStack input) {
        return 5 + EventHandlerRunic.getFinalCharge(input) / 2;
    }

    public ItemStack[] getComponents(ItemStack input) {
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        out.add(new ItemStack(Items.ENDER_PEARL));
        out.add(new ItemStack(ConfigItems.itemResource, 1, 14));
        int charge = EventHandlerRunic.getFinalCharge(input);
        if (charge > 0) {
            for (int i = 0; i < charge; i++) {
                out.add(new ItemStack(ConfigItems.itemResource, 1, 14));
            }
        }
        return out.toArray(new ItemStack[0]);
    }

    @Override
    public ItemStack[] getComponents() {
        return this.components;
    }
}
