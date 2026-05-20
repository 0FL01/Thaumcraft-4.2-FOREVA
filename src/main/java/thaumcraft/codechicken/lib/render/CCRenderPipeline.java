package thaumcraft.codechicken.lib.render;

import java.util.ArrayList;
import thaumcraft.codechicken.lib.render.CCRenderState;

public class CCRenderPipeline {
    private ArrayList<CCRenderState.VertexAttribute> attribs = new ArrayList();
    private ArrayList<CCRenderState.IVertexOperation> ops = new ArrayList();
    private ArrayList<PipelineNode> nodes = new ArrayList();
    private ArrayList<CCRenderState.IVertexOperation> sorted = new ArrayList();
    private PipelineNode loading;
    private PipelineBuilder builder = new PipelineBuilder();

    public void setPipeline(CCRenderState.IVertexOperation ... ops) {
        this.ops.clear();
        if (ops == null) {
            this.rebuild();
            return;
        }
        for (int i = 0; i < ops.length; ++i) {
            if (ops[i] != null) {
                this.ops.add(ops[i]);
            }
        }
        this.rebuild();
    }

    public void reset() {
        this.ops.clear();
        this.unbuild();
    }

    private void unbuild() {
        for (int i = 0; i < this.attribs.size(); ++i) {
            this.attribs.get((int)i).active = false;
        }
        this.attribs.clear();
        this.sorted.clear();
    }

    public void rebuild() {
        int i;
        if (this.ops.isEmpty() || CCRenderState.model == null) {
            return;
        }
        while (this.nodes.size() < CCRenderState.operationCount()) {
            this.nodes.add(new PipelineNode());
        }
        this.unbuild();
        if (CCRenderState.useNormals) {
            this.addAttribute(CCRenderState.normalAttrib);
        }
        if (CCRenderState.baseColour != -1 || CCRenderState.alphaOverride >= 0) {
            this.addAttribute(CCRenderState.colourAttrib);
        } else if (CCRenderState.hasColour) {
            CCRenderState.setColour(-1);
        }
        for (i = 0; i < this.ops.size(); ++i) {
            CCRenderState.IVertexOperation op = this.ops.get(i);
            if (op == null) {
                continue;
            }
            this.loading = this.nodes.get(op.operationID());
            boolean loaded = op.load();
            if (loaded) {
                this.loading.op = op;
            }
            if (!(op instanceof CCRenderState.VertexAttribute)) continue;
            if (loaded) {
                this.attribs.add((CCRenderState.VertexAttribute)op);
                continue;
            }
            ((CCRenderState.VertexAttribute)op).active = false;
        }
        for (i = 0; i < this.nodes.size(); ++i) {
            this.nodes.get(i).add();
        }
    }

    public void addRequirement(int opRef) {
        this.loading.deps.add(this.nodes.get(opRef));
    }

    public void addDependency(CCRenderState.VertexAttribute attrib) {
        this.loading.deps.add(this.nodes.get(attrib.operationID()));
        this.addAttribute(attrib);
    }

    public void addAttribute(CCRenderState.VertexAttribute attrib) {
        if (!attrib.active) {
            this.ops.add(attrib);
            attrib.active = true;
        }
    }

    public void operate() {
        for (int i = 0; i < this.sorted.size(); ++i) {
            this.sorted.get(i).operate();
        }
    }

    public PipelineBuilder builder() {
        this.ops.clear();
        return this.builder;
    }

    private class PipelineNode {
        public ArrayList<PipelineNode> deps = new ArrayList();
        public CCRenderState.IVertexOperation op;

        private PipelineNode() {
        }

        public void add() {
            if (this.op == null) {
                return;
            }
            for (int i = 0; i < this.deps.size(); ++i) {
                this.deps.get(i).add();
            }
            this.deps.clear();
            CCRenderPipeline.this.sorted.add(this.op);
            this.op = null;
        }
    }

    public class PipelineBuilder {
        public PipelineBuilder add(CCRenderState.IVertexOperation op) {
            CCRenderPipeline.this.ops.add(op);
            return this;
        }

        public PipelineBuilder add(CCRenderState.IVertexOperation ... ops) {
            for (int i = 0; i < ops.length; ++i) {
                CCRenderPipeline.this.ops.add(ops[i]);
            }
            return this;
        }

        public void build() {
            CCRenderPipeline.this.rebuild();
        }

        public void render() {
            CCRenderPipeline.this.rebuild();
            CCRenderState.render();
        }
    }
}
