package thaumcraft.common.lib.events;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerEntity {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
    }

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
    }

    @SubscribeEvent
    public void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
    }

    @SubscribeEvent
    public void onPlayerLoadFromFile(PlayerEvent.LoadFromFile event) {
    }

    @SubscribeEvent
    public void onPlayerSaveToFile(PlayerEvent.SaveToFile event) {
    }

    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
    }
}
