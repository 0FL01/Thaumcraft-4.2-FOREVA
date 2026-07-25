package thaumcraft.api.research;

import net.minecraft.entity.player.EntityPlayer;

public interface IScanThing {
    boolean checkThing(EntityPlayer player, Object thing);
    String getResearchKey(EntityPlayer player, Object thing);

    default void onSuccess(EntityPlayer player, Object thing) {
    }
}
