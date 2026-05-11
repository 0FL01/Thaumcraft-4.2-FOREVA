package thaumcraft.codechicken.lib.render.uv;

import java.util.ArrayList;
import java.util.Iterator;
import thaumcraft.codechicken.lib.render.uv.UV;
import thaumcraft.codechicken.lib.render.uv.UVTransformation;

public class UVTransformationList
extends UVTransformation {
    private ArrayList<UVTransformation> transformations = new ArrayList();

    public UVTransformationList(UVTransformation ... transforms) {
        for (UVTransformation t : transforms) {
            if (t instanceof UVTransformationList) {
                this.transformations.addAll(((UVTransformationList)t).transformations);
                continue;
            }
            this.transformations.add(t);
        }
        this.compact();
    }

    @Override
    public void apply(UV uv) {
        for (int i = 0; i < this.transformations.size(); ++i) {
            this.transformations.get(i).apply(uv);
        }
    }

    @Override
    public UVTransformationList with(UVTransformation t) {
        if (t.isRedundant()) {
            return this;
        }
        if (t instanceof UVTransformationList) {
            this.transformations.addAll(((UVTransformationList)t).transformations);
        } else {
            this.transformations.add(t);
        }
        this.compact();
        return this;
    }

    public UVTransformationList prepend(UVTransformation t) {
        if (t.isRedundant()) {
            return this;
        }
        if (t instanceof UVTransformationList) {
            this.transformations.addAll(0, ((UVTransformationList)t).transformations);
        } else {
            this.transformations.add(0, t);
        }
        this.compact();
        return this;
    }

    private void compact() {
        ArrayList<UVTransformation> newList = new ArrayList<UVTransformation>(this.transformations.size());
        Iterator<UVTransformation> iterator = this.transformations.iterator();
        UVTransformation prev = null;
        while (iterator.hasNext()) {
            UVTransformation t = iterator.next();
            if (t.isRedundant()) continue;
            if (prev != null) {
                UVTransformation m = prev.merge(t);
                if (m == null) {
                    newList.add(prev);
                } else {
                    t = m.isRedundant() ? null : m;
                }
            }
            prev = t;
        }
        if (prev != null) {
            newList.add(prev);
        }
        if (newList.size() < this.transformations.size()) {
            this.transformations = newList;
        }
    }

    @Override
    public boolean isRedundant() {
        return this.transformations.size() == 0;
    }

    @Override
    public UVTransformation inverse() {
        UVTransformationList rev = new UVTransformationList(new UVTransformation[0]);
        for (int i = this.transformations.size() - 1; i >= 0; --i) {
            rev.with((UVTransformation)this.transformations.get(i).inverse());
        }
        return rev;
    }

    public String toString() {
        String s = "";
        for (UVTransformation t : this.transformations) {
            s = s + "\n" + t.toString();
        }
        return s.trim();
    }
}

