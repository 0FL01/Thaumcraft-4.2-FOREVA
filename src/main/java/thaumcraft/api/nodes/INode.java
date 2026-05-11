package thaumcraft.api.nodes;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public interface INode
extends IAspectContainer {
    public String getId();

    public AspectList getAspectsBase();

    public NodeType getNodeType();

    public void setNodeType(NodeType var1);

    public void setNodeModifier(NodeModifier var1);

    public NodeModifier getNodeModifier();

    public int getNodeVisBase(Aspect var1);

    public void setNodeVisBase(Aspect var1, short var2);
}

