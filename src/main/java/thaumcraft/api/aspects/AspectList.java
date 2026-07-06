package thaumcraft.api.aspects;

import java.io.Serializable;
import java.util.LinkedHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;

public class AspectList
implements Serializable {
    public LinkedHashMap<Aspect, Integer> aspects = new LinkedHashMap();

    public AspectList(ItemStack stack) {
        try {
            AspectList temp = ThaumcraftApiHelper.getObjectAspects(stack);
            if (temp != null) {
                for (Aspect tag : temp.getAspects()) {
                    this.add(tag, temp.getAmount(tag));
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public AspectList() {
    }

    public AspectList copy() {
        AspectList out = new AspectList();
        for (Aspect a : this.getAspects()) {
            out.add(a, this.getAmount(a));
        }
        return out;
    }

    public int size() {
        return this.aspects.size();
    }

    public int visSize() {
        int q = 0;
        for (Aspect as : this.aspects.keySet()) {
            q += this.getAmount(as);
        }
        return q;
    }

    public Aspect[] getAspects() {
        Aspect[] q = new Aspect[1];
        return this.aspects.keySet().toArray(q);
    }

    public Aspect[] getPrimalAspects() {
        AspectList t = new AspectList();
        for (Aspect as : this.aspects.keySet()) {
            if (!as.isPrimal()) continue;
            t.add(as, 1);
        }
        Aspect[] q = new Aspect[1];
        return t.aspects.keySet().toArray(q);
    }

    public Aspect[] getAspectsSorted() {
        try {
            Aspect[] out = this.aspects.keySet().toArray(new Aspect[0]);
            boolean change = false;
            block2: do {
                change = false;
                for (int a = 0; a < out.length - 1; ++a) {
                    Aspect e1 = out[a];
                    Aspect e2 = out[a + 1];
                    if (e1 == null || e2 == null || e1.getTag().compareTo(e2.getTag()) <= 0) continue;
                    out[a] = e2;
                    out[a + 1] = e1;
                    change = true;
                    continue block2;
                }
            } while (change);
            return out;
        }
        catch (Exception e) {
            return this.getAspects();
        }
    }

    public Aspect[] getAspectsSortedAmount() {
        try {
            Aspect[] out = this.aspects.keySet().toArray(new Aspect[1]);
            boolean change = false;
            block2: do {
                change = false;
                for (int a = 0; a < out.length - 1; ++a) {
                    Aspect eb;
                    int e1 = this.getAmount(out[a]);
                    int e2 = this.getAmount(out[a + 1]);
                    if (e1 <= 0 || e2 <= 0 || e2 <= e1) continue;
                    Aspect ea = out[a];
                    out[a] = eb = out[a + 1];
                    out[a + 1] = ea;
                    change = true;
                    continue block2;
                }
            } while (change);
            return out;
        }
        catch (Exception e) {
            return this.getAspects();
        }
    }

    public int getAmount(Aspect key) {
        if (key == null) {
            return 0;
        }
        return this.aspects.get(key) == null ? 0 : this.aspects.get(key);
    }

    public boolean reduce(Aspect key, int amount) {
        if (key == null) {
            return false;
        }
        if (this.getAmount(key) >= amount) {
            int am = this.getAmount(key) - amount;
            this.aspects.put(key, am);
            return true;
        }
        return false;
    }

    public AspectList remove(Aspect key, int amount) {
        if (key == null) {
            return this;
        }
        int am = this.getAmount(key) - amount;
        if (am <= 0) {
            this.aspects.remove(key);
        } else {
            this.aspects.put(key, am);
        }
        return this;
    }

    public AspectList remove(Aspect key) {
        if (key == null) {
            return this;
        }
        this.aspects.remove(key);
        return this;
    }

    public AspectList add(Aspect aspect, int amount) {
        if (aspect == null) {
            return this;
        }
        if (this.aspects.containsKey(aspect)) {
            int oldamount = this.aspects.get(aspect);
            amount += oldamount;
        }
        this.aspects.put(aspect, amount);
        return this;
    }

    public AspectList merge(Aspect aspect, int amount) {
        if (aspect == null) {
            return this;
        }
        int oldamount;
        if (this.aspects.containsKey(aspect) && amount < (oldamount = this.aspects.get(aspect).intValue())) {
            amount = oldamount;
        }
        this.aspects.put(aspect, amount);
        return this;
    }

    public AspectList add(AspectList in) {
        for (Aspect a : in.getAspects()) {
            this.add(a, in.getAmount(a));
        }
        return this;
    }

    public AspectList merge(AspectList in) {
        for (Aspect a : in.getAspects()) {
            this.merge(a, in.getAmount(a));
        }
        return this;
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.aspects.clear();
        NBTTagList tlist = nbttagcompound.getTagList("Aspects", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (!rs.hasKey("key")) continue;
            this.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound, String label) {
        this.aspects.clear();
        NBTTagList tlist = nbttagcompound.getTagList(label, 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (!rs.hasKey("key")) continue;
            this.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("Aspects", tlist);
        for (Aspect aspect : this.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound f = new NBTTagCompound();
            f.setString("key", aspect.getTag());
            f.setInteger("amount", this.getAmount(aspect));
            tlist.appendTag(f);
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound, String label) {
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag(label, tlist);
        for (Aspect aspect : this.getAspects()) {
            if (aspect == null) continue;
            NBTTagCompound f = new NBTTagCompound();
            f.setString("key", aspect.getTag());
            f.setInteger("amount", this.getAmount(aspect));
            tlist.appendTag(f);
        }
    }
}
