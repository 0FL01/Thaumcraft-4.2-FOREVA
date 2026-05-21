package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.Config;

import java.util.ArrayList;

public class PlayerNotifications {
    private static ArrayList<Notification> notificationList = new ArrayList<Notification>();
    private static ArrayList<AspectNotification> aspectList = new ArrayList<AspectNotification>();

    public static void addNotification(String text) {
        addNotification(text, null, 0xFFFFFF);
    }

    public static void addAspectNotification(Aspect aspect) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.world == null || aspect == null) {
            return;
        }
        long time = System.nanoTime() / 1000000L + mc.world.rand.nextInt(1000);
        float x = 0.4F + mc.world.rand.nextFloat() * 0.2F;
        float y = 0.4F + mc.world.rand.nextFloat() * 0.2F;
        aspectList.add(new AspectNotification(aspect, x, y, time, time + 1500L));
    }

    public static void addNotification(String text, Aspect aspect) {
        if (aspect == null) {
            addNotification(text, (ResourceLocation) null);
            return;
        }
        addNotification(text, aspect.getImage(), aspect.getColor());
    }

    public static void addNotification(String text, ResourceLocation image) {
        addNotification(text, image, 0xFFFFFF);
    }

    public static void addNotification(String text, ResourceLocation image, int color) {
        long time = System.nanoTime() / 1000000L;
        long timeBonus = notificationList.isEmpty() ? (long) (Config.notificationDelay / 2) : 0L;
        notificationList.add(new Notification(text, image, time + Config.notificationDelay + timeBonus, time + Config.notificationDelay / 4L, color));
    }

    public static ArrayList<Notification> getListAndUpdate(long time) {
        ArrayList<Notification> temp = new ArrayList<Notification>();
        boolean first = true;
        for (Notification notification : notificationList) {
            if (notification.expire >= time) {
                if (!first) {
                    temp.add(new Notification(notification.text, notification.image, time + Config.notificationDelay, notification.created, notification.color));
                } else {
                    temp.add(notification);
                }
            }
            first = false;
        }
        notificationList = temp;
        return temp;
    }

    public static ArrayList<AspectNotification> getAspectListAndUpdate(long time) {
        ArrayList<AspectNotification> temp = new ArrayList<AspectNotification>();
        for (AspectNotification notification : aspectList) {
            if (notification.expire >= time) {
                temp.add(notification);
            }
        }
        aspectList = temp;
        return temp;
    }

    public static class AspectNotification {
        public final Aspect aspect;
        public final float startX;
        public final float startY;
        public final long created;
        public final long expire;

        public AspectNotification(Aspect aspect, float startX, float startY, long created, long expire) {
            this.aspect = aspect;
            this.startX = startX;
            this.startY = startY;
            this.created = created;
            this.expire = expire;
        }
    }

    public static class Notification {
        public final String text;
        public final ResourceLocation image;
        public final long expire;
        public final long created;
        public final int color;

        public Notification(String text, ResourceLocation image, long expire, long created, int color) {
            this.text = text;
            this.image = image;
            this.expire = expire;
            this.created = created;
            this.color = color;
        }
    }
}
