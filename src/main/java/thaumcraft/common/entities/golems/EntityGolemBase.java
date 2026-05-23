package thaumcraft.common.entities.golems;

import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.TCSounds;

public class EntityGolemBase extends net.minecraft.entity.monster.EntityGolem implements net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData {

    public thaumcraft.common.entities.InventoryMob inventory;
    public net.minecraft.item.ItemStack itemCarried;
    public net.minecraftforge.fluids.FluidStack fluidCarried;
    public net.minecraft.item.ItemStack itemWatched = null;
    public thaumcraft.api.aspects.Aspect essentia;
    public int essentiaAmount;
    public boolean advanced = false;
    public int homeFacing = 0;
    public boolean paused = false;
    public boolean inactive = false;
    public boolean flag = false;
    public byte[] colors = null;
    public byte[] upgrades = null;
    public String decoration = "";
    public float bootup = -1.0f;
    public EnumGolemType golemType = EnumGolemType.WOOD;
    public int regenTimer = 0;
    protected java.util.ArrayList<Marker> markers = new java.util.ArrayList<>();
    public int action = 0;
    public int leftArm = 0;
    public int rightArm = 0;
    public int healing = 0;

    private static final net.minecraft.network.datasync.DataParameter<net.minecraft.item.ItemStack> CARRIED =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.ITEM_STACK);
    private static final net.minecraft.network.datasync.DataParameter<String> OWNER =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.STRING);
    private static final net.minecraft.network.datasync.DataParameter<Byte> TOGGLES =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.BYTE);
    private static final net.minecraft.network.datasync.DataParameter<Integer> GOLEM_TYPE =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.VARINT);
    private static final net.minecraft.network.datasync.DataParameter<String> DECORATION =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.STRING);
    private static final net.minecraft.network.datasync.DataParameter<Byte> CORE =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.BYTE);
    private static final net.minecraft.network.datasync.DataParameter<String> COLORS_STR =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.STRING);
    private static final net.minecraft.network.datasync.DataParameter<String> UPGRADES_STR =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.STRING);
    private static final net.minecraft.network.datasync.DataParameter<Byte> HEALTH_PERCENT =
        net.minecraft.network.datasync.EntityDataManager.createKey(EntityGolemBase.class, net.minecraft.network.datasync.DataSerializers.BYTE);

    public EntityGolemBase(net.minecraft.world.World world) {
        super(world);
        this.stepHeight = 1.0f;
        this.colors = new byte[]{-1};
        this.upgrades = new byte[]{-1};
        this.setSize(0.4f, 0.95f);
        this.enablePersistence();
    }

    public EntityGolemBase(net.minecraft.world.World world, EnumGolemType type, boolean adv) {
        this(world);
        this.golemType = type;
        this.advanced = adv;
        this.upgrades = new byte[this.golemType.upgrades + (this.advanced ? 1 : 0)];
        for (int a = 0; a < this.upgrades.length; a++) this.upgrades[a] = -1;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CARRIED, net.minecraft.item.ItemStack.EMPTY);
        this.dataManager.register(OWNER, "");
        this.dataManager.register(TOGGLES, (byte) 0);
        this.dataManager.register(GOLEM_TYPE, 0);
        this.dataManager.register(DECORATION, "");
        this.dataManager.register(CORE, (byte) -1);
        this.dataManager.register(COLORS_STR, "");
        this.dataManager.register(UPGRADES_STR, "");
        this.dataManager.register(HEALTH_PERCENT, (byte) 20);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getAttributeMap().registerAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6);
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.action > 0) this.action--;
        if (this.leftArm > 0) this.leftArm--;
        if (this.rightArm > 0) this.rightArm--;
        if (this.healing > 0) this.healing--;

        int xx = net.minecraft.util.math.MathHelper.floor(this.posX);
        int yy = net.minecraft.util.math.MathHelper.floor(this.posY);
        int zz = net.minecraft.util.math.MathHelper.floor(this.posZ);
        net.minecraft.util.math.BlockPos below = new net.minecraft.util.math.BlockPos(xx, yy - 1, zz);
        net.minecraft.block.state.IBlockState belowState = yy > 0
                ? this.world.getBlockState(below)
                : net.minecraft.init.Blocks.AIR.getDefaultState();
        this.inactive = yy > 0
                && belowState.getBlock() == thaumcraft.common.config.ConfigBlocks.blockCosmeticSolid
                && belowState.getBlock().getMetaFromState(belowState) == 10;

        if (!this.world.isRemote) {
            if (this.regenTimer > 0) {
                this.regenTimer--;
            } else {
                this.regenTimer = this.golemType.regenDelay;
                if (this.decoration.contains("F")) this.regenTimer = (int)((float)this.regenTimer * 0.66f);
                if (this.getHealth() < this.getMaxHealth()) {
                    this.world.setEntityState(this, (byte) 5);
                    this.heal(1.0f);
                }
            }
            // Too far from home teleport
            if (this.getDistanceSq(this.getHomePosition()) >= 2304.0 || this.isEntityInsideOpaqueBlock()) {
                net.minecraft.util.math.BlockPos home = this.getHomePosition();
                for (int dy = 1; dy >= -1; dy--) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            net.minecraft.util.math.BlockPos p = new net.minecraft.util.math.BlockPos(home.getX() + dx, home.getY() - 1 + dy, home.getZ() + dz);
                            if (this.world.isAirBlock(p) && this.world.getBlockState(p.down()).isSideSolid(this.world, p.down(), net.minecraft.util.EnumFacing.UP)) {
                                this.setLocationAndAngles(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, this.rotationYaw, this.rotationPitch);
                                this.getNavigator().clearPath();
                                return;
                            }
                        }
                    }
                }
            }
        } else if (this.bootup > 0.0f && this.getCore() > -1) {
            this.bootup *= this.bootup / 33.1f;
            this.world.playSound(
                    this.posX, this.posY, this.posZ,
                    TCSounds.CAMERATICKS,
                    net.minecraft.util.SoundCategory.NEUTRAL,
                    this.bootup * 0.2f,
                    this.bootup,
                    false);
        }
    }

    // -- Core setup ---
    public boolean setupGolem() {
        if (!this.world.isRemote) {
            this.dataManager.set(GOLEM_TYPE, this.golemType.ordinal());
        }

        PathNavigate nav = this.getNavigator();
        if (nav instanceof PathNavigateGround) {
            boolean light = this.isLightGolem();
            ((PathNavigateGround) nav).setCanSwim(light);
            this.setPathPriority(PathNodeType.WATER, light ? 0.0F : -1.0F);
        }
        this.isImmuneToFire = this.getGolemType().fireResist;

        int bonus = this.decoration.contains("H") ? 5 : 0;
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getGolemType().health + bonus);
        int damage = 2 + this.getGolemStrength() + this.getUpgradeAmount(1);
        if (this.decoration.contains("M")) damage += 2;
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(damage);

        this.tasks.taskEntries.clear();
        // AI registration by core type
        if (this.getCore() > -1) {
            this.tasks.addTask(0, new thaumcraft.common.entities.ai.combat.AIAvoidCreeperSwell(this));
        }
        switch (this.getCore()) {
            case 0: // Gather
                this.tasks.addTask(0, new thaumcraft.common.entities.ai.inventory.AIHomeReplace(this));
                this.tasks.addTask(1, new thaumcraft.common.entities.ai.inventory.AIHomePlace(this));
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.inventory.AIHomeDrop(this));
                this.tasks.addTask(3, new thaumcraft.common.entities.ai.inventory.AIFillTake(this));
                this.tasks.addTask(4, new thaumcraft.common.entities.ai.inventory.AIFillGoto(this));
                break;
            case 1: // Empty
                this.tasks.addTask(0, new thaumcraft.common.entities.ai.inventory.AIHomeReplace(this));
                this.tasks.addTask(1, new thaumcraft.common.entities.ai.inventory.AIEmptyPlace(this));
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.inventory.AIEmptyDrop(this));
                this.tasks.addTask(3, new thaumcraft.common.entities.ai.inventory.AIEmptyGoto(this));
                this.tasks.addTask(4, new thaumcraft.common.entities.ai.inventory.AIHomeTake(this));
                break;
            case 2: // Pickup
                this.tasks.addTask(0, new thaumcraft.common.entities.ai.inventory.AIHomeReplace(this));
                this.tasks.addTask(1, new thaumcraft.common.entities.ai.inventory.AIHomePlace(this));
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.inventory.AIItemPickup(this));
                break;
            case 3: // Harvest
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.interact.AIHarvestCrops(this));
                break;
            case 4: // Attack
                if (this.decoration.contains("R")) this.tasks.addTask(2, new thaumcraft.common.entities.ai.combat.AIDartAttack(this));
                this.tasks.addTask(3, new thaumcraft.common.entities.ai.combat.AIGolemAttackOnCollide(this));
                this.targetTasks.addTask(1, new thaumcraft.common.entities.ai.combat.AIHurtByTarget(this, false));
                this.targetTasks.addTask(2, new thaumcraft.common.entities.ai.combat.AINearestAttackableTarget(this, 0, true));
                break;
            case 5: // Fluids
                this.tasks.addTask(1, new thaumcraft.common.entities.ai.fluid.AILiquidEmpty(this));
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.fluid.AILiquidGather(this));
                this.tasks.addTask(3, new thaumcraft.common.entities.ai.fluid.AILiquidGoto(this));
                break;
            case 6: // Essentia
                this.tasks.addTask(1, new thaumcraft.common.entities.ai.fluid.AIEssentiaEmpty(this));
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.fluid.AIEssentiaGather(this));
                this.tasks.addTask(3, new thaumcraft.common.entities.ai.fluid.AIEssentiaGoto(this));
                break;
            case 7: // Lumber
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.interact.AIHarvestLogs(this));
                break;
            case 8: // Use
                this.tasks.addTask(0, new thaumcraft.common.entities.ai.inventory.AIHomeReplace(this));
                this.tasks.addTask(0, new thaumcraft.common.entities.ai.interact.AIUseItem(this));
                this.tasks.addTask(4, new thaumcraft.common.entities.ai.inventory.AIHomeTake(this));
                break;
            case 9: // Butcher
                if (this.decoration.contains("R")) this.tasks.addTask(2, new thaumcraft.common.entities.ai.combat.AIDartAttack(this));
                this.tasks.addTask(3, new thaumcraft.common.entities.ai.combat.AIGolemAttackOnCollide(this));
                this.targetTasks.addTask(1, new thaumcraft.common.entities.ai.combat.AINearestButcherTarget(this));
                break;
            case 10: // Sort
                this.tasks.addTask(0, new thaumcraft.common.entities.ai.inventory.AIHomeReplace(this));
                this.tasks.addTask(1, new thaumcraft.common.entities.ai.inventory.AISortingPlace(this));
                this.tasks.addTask(3, new thaumcraft.common.entities.ai.inventory.AISortingGoto(this));
                this.tasks.addTask(4, new thaumcraft.common.entities.ai.inventory.AIHomeTakeSorting(this));
                break;
            case 11: // Fish
                this.tasks.addTask(2, new thaumcraft.common.entities.ai.interact.AIFish(this));
                break;
        }
        if (this.getCore() > -1) {
            this.tasks.addTask(5, new thaumcraft.common.entities.ai.misc.AIOpenDoor(this));
            this.tasks.addTask(6, new thaumcraft.common.entities.ai.misc.AIReturnHome(this));
            this.tasks.addTask(7, new net.minecraft.entity.ai.EntityAIWatchClosest(this, net.minecraft.entity.player.EntityPlayer.class, 6.0f));
            this.tasks.addTask(8, new net.minecraft.entity.ai.EntityAILookIdle(this));
        }
        return true;
    }

    public boolean setupGolemInventory() {
        if (!ItemGolemCore.hasInventory(this.getCore())) {
            this.inventory = null;
            return false;
        }
        int invSize;
        switch (this.getCore()) {
            case 5:
                invSize = 1 + this.getUpgradeAmount(2);
                break;
            default:
                invSize = 6 + this.getUpgradeAmount(2) * 6;
                break;
        }
        thaumcraft.common.entities.InventoryMob oldInventory = this.inventory;
        this.inventory = new thaumcraft.common.entities.InventoryMob(invSize);
        if (oldInventory != null) {
            int copySlots = Math.min(oldInventory.getSizeInventory(), this.inventory.getSizeInventory());
            for (int slot = 0; slot < copySlots; slot++) {
                this.inventory.setInventorySlotContents(slot, oldInventory.getStackInSlot(slot));
            }
        }
        byte[] oldcolors = this.colors == null ? new byte[0] : this.colors;
        this.colors = new byte[this.inventory.getSizeInventory()];
        for (int a = 0; a < this.inventory.getSizeInventory(); a++) {
            this.colors[a] = (a < oldcolors.length) ? oldcolors[a] : -1;
        }
        return true;
    }

    public void setup(int side) {
        this.homeFacing = side;
        this.setHomePosAndDistance(new net.minecraft.util.math.BlockPos(
                net.minecraft.util.math.MathHelper.floor(this.posX),
                net.minecraft.util.math.MathHelper.floor(this.posY),
                net.minecraft.util.math.MathHelper.floor(this.posZ)), 32);
        this.setupGolem();
        this.setupGolemInventory();
    }

    // --- NBT ---
    @Override
    public void writeEntityToNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("HomeX", this.getHomePosition().getX());
        nbt.setInteger("HomeY", this.getHomePosition().getY());
        nbt.setInteger("HomeZ", this.getHomePosition().getZ());
        nbt.setByte("HomeFacing", (byte) this.homeFacing);
        nbt.setByte("GolemType", (byte) this.golemType.ordinal());
        nbt.setByte("Core", this.getCore());
        nbt.setString("Decoration", this.decoration);
        nbt.setByte("toggles", this.getTogglesValue());
        nbt.setBoolean("advanced", this.advanced);
        nbt.setByteArray("colors", this.colors);
        nbt.setByteArray("upgrades", this.upgrades);
        if (this.getCore() == 5 && this.fluidCarried != null) {
            this.fluidCarried.writeToNBT(nbt);
        }
        if (this.getCore() == 6 && this.essentia != null && this.essentiaAmount > 0) {
            nbt.setString("essentia", this.essentia.getTag());
            nbt.setByte("essentiaAmount", (byte) this.essentiaAmount);
        }
        net.minecraft.nbt.NBTTagCompound itemNBT = new net.minecraft.nbt.NBTTagCompound();
        if (this.itemCarried != null) this.itemCarried.writeToNBT(itemNBT);
        nbt.setTag("ItemCarried", itemNBT);
        nbt.setString("Owner", this.getOwnerName() != null ? this.getOwnerName() : "");
        net.minecraft.nbt.NBTTagList tl = new net.minecraft.nbt.NBTTagList();
        for (Marker m : this.markers) {
            net.minecraft.nbt.NBTTagCompound tc = new net.minecraft.nbt.NBTTagCompound();
            tc.setInteger("x", m.x); tc.setInteger("y", m.y); tc.setInteger("z", m.z);
            tc.setInteger("dim", m.dim); tc.setByte("side", m.side); tc.setByte("color", m.color);
            tl.appendTag(tc);
        }
        nbt.setTag("Markers", tl);
        if (this.inventory != null) nbt.setTag("Inventory", this.inventory.writeToNBT(new net.minecraft.nbt.NBTTagList()));
    }

    @Override
    public void readEntityFromNBT(net.minecraft.nbt.NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        int hx = nbt.getInteger("HomeX"), hy = nbt.getInteger("HomeY"), hz = nbt.getInteger("HomeZ");
        this.homeFacing = nbt.getByte("HomeFacing");
        this.setHomePosAndDistance(new net.minecraft.util.math.BlockPos(hx, hy, hz), 32);
        this.advanced = nbt.getBoolean("advanced");
        this.golemType = EnumGolemType.getType(nbt.getByte("GolemType"));
        this.setCore(nbt.getByte("Core"));
        if (this.getCore() == 5) {
            this.fluidCarried = net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT(nbt);
        }
        if (this.getCore() == 6) {
            String s = nbt.getString("essentia");
            if (s != null && !s.isEmpty()) {
                this.essentia = thaumcraft.api.aspects.Aspect.getAspect(s);
                if (this.essentia != null) this.essentiaAmount = nbt.getByte("essentiaAmount");
            }
        }
        this.setTogglesValue(nbt.getByte("toggles"));
        net.minecraft.nbt.NBTTagCompound itemNBT = nbt.getCompoundTag("ItemCarried");
        this.itemCarried = new net.minecraft.item.ItemStack(itemNBT);
        this.updateCarried();
        this.decoration = nbt.getString("Decoration");
        this.setGolemDecoration(this.decoration);
        String owner = nbt.getString("Owner");
        if (owner.length() > 0) this.setOwner(owner);
        net.minecraft.nbt.NBTTagList markersList = nbt.getTagList("Markers", 10);
        for (int i = 0; i < markersList.tagCount(); i++) {
            net.minecraft.nbt.NBTTagCompound tc = markersList.getCompoundTagAt(i);
            this.markers.add(new Marker(tc.getInteger("x"), tc.getInteger("y"), tc.getInteger("z"),
                (byte) tc.getInteger("dim"), tc.getByte("side"), tc.getByte("color")));
        }
        byte[] savedUpgrades = nbt.getByteArray("upgrades");
        this.upgrades = new byte[this.golemType.upgrades + (this.advanced ? 1 : 0)];
        java.util.Arrays.fill(this.upgrades, (byte) -1);
        System.arraycopy(savedUpgrades, 0, this.upgrades, 0, Math.min(savedUpgrades.length, this.upgrades.length));
        this.setupGolem();
        for (int slot = 0; slot < this.upgrades.length; slot++) {
            this.setUpgrade(slot, this.upgrades[slot]);
        }
        this.setupGolemInventory();
        if (nbt.hasKey("Inventory")) {
            net.minecraft.nbt.NBTTagList invList = nbt.getTagList("Inventory", 10);
            if (this.inventory != null) this.inventory.readFromNBT(invList);
        }
        byte[] oldcolors = nbt.getByteArray("colors");
        if (this.inventory != null) {
            this.colors = new byte[this.inventory.getSizeInventory()];
            for (int a = 0; a < this.inventory.getSizeInventory(); a++) {
                this.colors[a] = (a < oldcolors.length) ? oldcolors[a] : -1;
            }
        }
    }

    // --- Interaction ---
    @Override
    public boolean processInteract(net.minecraft.entity.player.EntityPlayer player, net.minecraft.util.EnumHand hand) {
        if (player.isSneaking()) return false;
        net.minecraft.item.ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty()) return this.customInteraction(player, stack, hand);
        if (this.getCore() == -1 && stack.getItem() instanceof thaumcraft.common.entities.golems.ItemGolemCore && stack.getItemDamage() != 100) {
            this.setCore((byte) stack.getItemDamage());
            this.setupGolem();
            this.setupGolemInventory();
            stack.shrink(1);
            this.world.playSound(null, this.getPosition(), TCSounds.UPGRADE, net.minecraft.util.SoundCategory.NEUTRAL, 0.5f, 1.0f);
            player.swingArm(hand);
            this.world.setEntityState(this, (byte) 7);
            return true;
        }
        if (stack.getItem() instanceof thaumcraft.common.entities.golems.ItemGolemUpgrade) {
            for (int a = 0; a < this.upgrades.length; a++) {
                if (this.getUpgrade(a) != -1 || this.getUpgradeAmount(stack.getItemDamage()) >= 2) continue;
                this.setUpgrade(a, (byte) stack.getItemDamage());
                this.setupGolem();
                this.setupGolemInventory();
                stack.shrink(1);
                this.world.playSound(null, this.getPosition(), TCSounds.UPGRADE, net.minecraft.util.SoundCategory.NEUTRAL, 0.5f, 1.0f);
                player.swingArm(hand);
                return true;
            }
            return false;
        }
        return this.customInteraction(player, stack, hand);
    }

    public boolean customInteraction(net.minecraft.entity.player.EntityPlayer player, net.minecraft.item.ItemStack stack,
                                     net.minecraft.util.EnumHand hand) {
        if (!stack.isEmpty() && stack.getItem() == thaumcraft.common.config.ConfigItems.itemGolemBell) {
            return false;
        }
        if (!stack.isEmpty() && stack.getItem() == thaumcraft.common.config.ConfigItems.itemGolemDecoration) {
            boolean added = this.addDecoration(ItemGolemDecoration.getDecoChar(stack.getItemDamage()), stack);
            if (added) {
                player.swingArm(hand);
            }
            return added;
        }
        if (!stack.isEmpty() && stack.getItem() == net.minecraft.init.Items.WHEAT) {
            if (!this.world.isRemote) {
                stack.shrink(1);
                this.heal(5.0F);
                this.world.setEntityState(this, (byte) 5);
                this.world.playSound(null, this.getPosition(), net.minecraft.init.SoundEvents.ENTITY_GENERIC_EAT,
                        net.minecraft.util.SoundCategory.NEUTRAL, 0.3F, 0.9F + this.rand.nextFloat() * 0.2F);
            }
            player.swingArm(hand);
            return true;
        }
        if (this.getCore() > -1 && thaumcraft.common.entities.golems.ItemGolemCore.hasGUI(this.getCore())) {
            if (!stack.isEmpty() && stack.getItem() instanceof thaumcraft.common.items.wands.ItemWandCasting) {
                return false;
            }
            if (!this.world.isRemote) {
                player.openGui(thaumcraft.common.Thaumcraft.instance, thaumcraft.common.CommonProxy.GUI_GOLEM, this.world, this.getEntityId(), 0, 0);
            }
            return true;
        }
        return false;
    }

    protected boolean addDecoration(String type, net.minecraft.item.ItemStack stack) {
        if (type == null || type.isEmpty() || this.decoration.contains(type)) {
            return false;
        }
        if (("F".equals(type) || "H".equals(type)) && (this.decoration.contains("F") || this.decoration.contains("H"))) {
            return false;
        }
        if (("G".equals(type) || "V".equals(type)) && (this.decoration.contains("G") || this.decoration.contains("V"))) {
            return false;
        }
        if (("B".equals(type) || "P".equals(type)) && (this.decoration.contains("B") || this.decoration.contains("P"))) {
            return false;
        }
        this.decoration = this.decoration + type;
        if (!this.world.isRemote) {
            this.setGolemDecoration(this.decoration);
            stack.shrink(1);
            this.world.playSound(null, this.getPosition(), TCSounds.CAMERACLACK,
                    net.minecraft.util.SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
        this.setupGolem();
        return true;
    }

    // --- Data watcher accessors ---
    public String getOwnerName() { return this.dataManager.get(OWNER); }
    public void setOwner(String name) { this.dataManager.set(OWNER, name); }
    public byte getCore() { return this.dataManager.get(CORE); }
    public void setCore(byte core) { this.dataManager.set(CORE, core); }
    public EnumGolemType getGolemType() { return EnumGolemType.getType(this.dataManager.get(GOLEM_TYPE)); }
    public String getGolemDecoration() { return this.dataManager.get(DECORATION); }
    public void setGolemDecoration(String s) { this.dataManager.set(DECORATION, s); }
    public boolean[] getToggles() { return thaumcraft.common.lib.utils.Utils.unpack(this.dataManager.get(TOGGLES)); }
    public byte getTogglesValue() { return this.dataManager.get(TOGGLES); }
    public void setToggle(int index, boolean val) {
        boolean[] f = this.getToggles();
        f[index] = val;
        this.dataManager.set(TOGGLES, thaumcraft.common.lib.utils.Utils.pack(f));
    }
    public void setTogglesValue(byte v) { this.dataManager.set(TOGGLES, v); }

    public int getGolemStrength() { return this.getGolemType().strength + this.getUpgradeAmount(1); }
    public int getCarryLimit() { return this.golemType.carry + Math.min(16, Math.max(4, this.golemType.carry)) * this.getUpgradeAmount(1); }
    public int getFluidCarryLimit() { return net.minecraft.util.math.MathHelper.floor(Math.sqrt(this.getCarryLimit())) * 1000; }

    public boolean isLightGolem() {
        EnumGolemType type = this.getGolemType();
        return type == EnumGolemType.STRAW
            || type == EnumGolemType.WOOD
            || type == EnumGolemType.FLESH
            || type == EnumGolemType.TALLOW;
    }

    public byte getUpgrade(int slot) {
        char[] chars = this.dataManager.get(UPGRADES_STR).toCharArray();
        if (slot < chars.length) {
            byte t = Byte.parseByte("" + chars[slot], 16);
            return t == 15 ? -1 : t;
        }
        return -1;
    }

    public int getUpgradeAmount(int type) {
        int a = 0;
        for (byte b : this.upgrades) { if (type == b) a++; }
        return a;
    }

    public void setUpgrade(int slot, byte upgrade) {
        this.upgrades[slot] = upgrade;
        StringBuilder sb = new StringBuilder();
        for (byte c : this.upgrades) sb.append(Integer.toHexString(c < 0 ? 15 : c));
        this.dataManager.set(UPGRADES_STR, sb.toString());
    }

    public short getColors(int slot) {
        char[] chars = this.dataManager.get(COLORS_STR).toCharArray();
        if (slot < chars.length) {
            if (("" + chars[slot]).equals("h")) return -1;
            return Short.parseShort("" + chars[slot], 16);
        }
        return -1;
    }

    public void setColors(int slot, int color) {
        this.colors[slot] = (byte) color;
        StringBuilder sb = new StringBuilder();
        for (byte c : this.colors) sb.append(c == -1 ? "h" : Integer.toHexString(c));
        this.dataManager.set(COLORS_STR, sb.toString());
    }

    // --- Carried item ---
    public net.minecraft.item.ItemStack getCarriedForDisplay() {
        return this.dataManager.get(CARRIED);
    }

    public void setCarried(net.minecraft.item.ItemStack stack) {
        this.itemCarried = stack;
        this.updateCarried();
    }

    public net.minecraft.item.ItemStack getCarried() {
        if (this.itemCarried != null && this.itemCarried.getCount() <= 0) this.setCarried(net.minecraft.item.ItemStack.EMPTY);
        return this.itemCarried;
    }

    public int getCarrySpace() {
        if (this.itemCarried == null || this.itemCarried.isEmpty()) return this.getCarryLimit();
        return Math.min(this.getCarryLimit() - this.itemCarried.getCount(), this.itemCarried.getMaxStackSize() - this.itemCarried.getCount());
    }

    public void updateCarried() {
        if (this.itemCarried != null && !this.itemCarried.isEmpty()) {
            this.dataManager.set(CARRIED, this.itemCarried.copy());
        } else if (this.getCore() == 5 && this.fluidCarried != null) {
            net.minecraft.block.Block fluidBlock = this.fluidCarried.getFluid().getBlock();
            if (fluidBlock != null) {
                this.dataManager.set(CARRIED, new net.minecraft.item.ItemStack(fluidBlock, 1, this.fluidCarried.amount));
            } else {
                this.dataManager.set(CARRIED, net.minecraft.item.ItemStack.EMPTY);
            }
        } else if (this.getCore() == 6) {
            net.minecraft.item.ItemStack display = thaumcraft.common.config.ConfigBlocks.blockJar == null
                    ? net.minecraft.item.ItemStack.EMPTY
                    : new net.minecraft.item.ItemStack(thaumcraft.common.config.ConfigBlocks.blockJar);
            if (!display.isEmpty() && display.getItem() instanceof thaumcraft.api.aspects.IEssentiaContainerItem
                    && this.essentia != null && this.essentiaAmount > 0) {
                int amount = (int)(64.0F * ((float)this.essentiaAmount / (float)Math.max(1, this.getCarryLimit())));
                ((thaumcraft.api.aspects.IEssentiaContainerItem) display.getItem())
                        .setAspects(display, new thaumcraft.api.aspects.AspectList().add(this.essentia, amount));
            }
            this.dataManager.set(CARRIED, display);
        } else {
            this.dataManager.set(CARRIED, net.minecraft.item.ItemStack.EMPTY);
        }
    }

    public boolean hasSomething() {
        if (this.itemCarried != null && !this.itemCarried.isEmpty()) return true;
        if (this.inventory != null && this.inventory.hasSomething()) return true;
        return false;
    }

    // --- Configuration toggles ---
    public boolean checkOreDict() { return (this.getUpgradeAmount(5) > 0); }
    public boolean ignoreDamage() { return (this.getUpgradeAmount(5) > 0); }
    public boolean ignoreNBT() { return (this.getUpgradeAmount(5) > 0); }

    // --- Color matching ---
    public java.util.ArrayList<Byte> getColorsMatching(net.minecraft.item.ItemStack match) {
        java.util.ArrayList<Byte> result = new java.util.ArrayList<>();
        if (match == null || match.isEmpty()) return result;
        for (Marker m : this.getMarkers()) {
            if (m.dim == this.world.provider.getDimension()) {
                Byte color = m.color;
                if (!result.contains(color)) result.add(color);
            }
        }
        if (result.isEmpty()) result.add((byte)-1);
        return result;
    }

    public void startRightArmTimer() {
        if (this.rightArm == 0) {
            this.rightArm = 5;
            this.world.setEntityState(this, (byte)8);
        }
    }

    // --- Markers ---
    public void setMarkers(java.util.ArrayList<Marker> markers) { this.markers = markers; }
    public java.util.ArrayList<Marker> getMarkers() { this.validateMarkers(); return this.markers; }
    protected void validateMarkers() {
        java.util.ArrayList<Marker> newMarkers = new java.util.ArrayList<>();
        for (Marker m : this.markers) { if (m.dim == this.world.provider.getDimension()) newMarkers.add(m); }
        this.markers = newMarkers;
    }

    // --- Health sync ---
    @Override
    public void heal(float amount) {
        super.heal(amount);
        if (!this.world.isRemote) this.dataManager.set(HEALTH_PERCENT, (byte) this.getHealth());
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(health);
        if (!this.world.isRemote) this.dataManager.set(HEALTH_PERCENT, (byte) this.getHealth());
    }

    public float getHealthPercentage() {
        return (float) this.dataManager.get(HEALTH_PERCENT) / this.getMaxHealth();
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 4) this.action = 6;
        else if (id == 5) {
            this.healing = 5;
            this.refreshClientHealingState();
        }
        else if (id == 6) this.leftArm = 5;
        else if (id == 8) this.rightArm = 5;
        else if (id == 7) this.bootup = 33.0f;
        else super.handleStatusUpdate(id);
    }

    @Override
    public void notifyDataManagerChange(net.minecraft.network.datasync.DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (CORE.equals(key) && this.world != null && this.world.isRemote && this.getCore() >= 0 && this.bootup < 0.0F) {
            this.bootup = 33.0F;
        }
    }

    private void refreshClientHealingState() {
        int bonus = this.getGolemDecoration().contains("H") ? 5 : 0;
        this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH)
                .setBaseValue(this.getGolemType().health + bonus);
    }

    public int getActionTimer() {
        return 3 - Math.abs(this.action - 3);
    }

    // --- Target validation ---
    public boolean isWithinHomeDistanceFromPosition(int x, int y, int z) {
        float range = this.getRange();
        return this.getHomePosition().distanceSq(x, y, z) < range * range;
    }

    public float getRange() {
        float dmod = 16 + this.getUpgradeAmount(3) * 4;
        if (this.decoration.contains("G")) dmod += Math.max(dmod * 0.1f, 1.0f);
        if (this.advanced) dmod += Math.max(dmod * 0.2f, 2.0f);
        return dmod;
    }

    public boolean isValidTarget(net.minecraft.entity.Entity target) {
        if (!target.isEntityAlive()) return false;
        if (target instanceof net.minecraft.entity.player.EntityPlayer && ((net.minecraft.entity.player.EntityPlayer)target).getName().equals(this.getOwnerName())) return false;
        if (!this.isWithinHomeDistanceFromPosition(
                net.minecraft.util.math.MathHelper.floor(target.posX),
                net.minecraft.util.math.MathHelper.floor(target.posY),
                net.minecraft.util.math.MathHelper.floor(target.posZ))) return false;
        if (this.getCore() == 9) {
            if (this.isValidAnimalTarget(target, true)) return true;
        } else {
            if (this.canAttackCreepers() && this.getUpgradeAmount(4) > 0 && target instanceof net.minecraft.entity.monster.EntityCreeper) return true;
            if (this.canAttackHostiles() && target instanceof net.minecraft.entity.monster.IMob && !(target instanceof net.minecraft.entity.monster.EntityCreeper)) return true;
            if (this.canAttackAnimals() && this.getUpgradeAmount(4) > 0 && this.isValidAnimalTarget(target, false)) return true;
            if (this.canAttackPlayers() && this.getUpgradeAmount(4) > 0 && target instanceof net.minecraft.entity.player.EntityPlayer) return true;
        }
        return false;
    }

    private boolean isValidAnimalTarget(net.minecraft.entity.Entity target, boolean excludeChildren) {
        if (!(target instanceof net.minecraft.entity.passive.EntityAnimal)
                && !(target instanceof net.minecraft.entity.passive.IAnimals)) return false;
        if (target instanceof net.minecraft.entity.monster.IMob) return false;
        if (target instanceof net.minecraft.entity.passive.EntityTameable
                && ((net.minecraft.entity.passive.EntityTameable) target).isTamed()) return false;
        if (target instanceof net.minecraft.entity.monster.EntityGolem) return false;
        return !excludeChildren
                || !(target instanceof net.minecraft.entity.passive.EntityAnimal)
                || !((net.minecraft.entity.passive.EntityAnimal) target).isChild();
    }

    public boolean canAttackHostiles() { return !this.getToggles()[1]; }
    public boolean canAttackAnimals() { return !this.getToggles()[2]; }
    public boolean canAttackPlayers() { return !this.getToggles()[3]; }
    public boolean canAttackCreepers() { return !this.getToggles()[4]; }

    // --- Combat ---
    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity target) {
        float f = (float) this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int knockback = 0;
        if (target instanceof net.minecraft.entity.EntityLivingBase) {
            net.minecraft.entity.EntityLivingBase livingTarget = (net.minecraft.entity.EntityLivingBase) target;
            f += net.minecraft.enchantment.EnchantmentHelper.getModifierForCreature(
                    this.getHeldItemMainhand(), livingTarget.getCreatureAttribute());
            knockback += net.minecraft.enchantment.EnchantmentHelper.getKnockbackModifier(this);
        }
        boolean flag = target.attackEntityFrom(net.minecraft.util.DamageSource.causeMobDamage(this), f);
        if (flag) {
            if (this.decoration.contains("V")) thaumcraft.common.lib.utils.EntityUtils.setRecentlyHit(target, 100);
            if (knockback > 0) {
                target.addVelocity(
                        -net.minecraft.util.math.MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F,
                        0.1D,
                        net.minecraft.util.math.MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F);
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
            int fire = net.minecraft.enchantment.EnchantmentHelper.getFireAspectModifier(this) + this.getUpgradeAmount(2);
            if (fire > 0) target.setFire(fire * 4);
            if (target instanceof net.minecraft.entity.EntityLivingBase) {
                net.minecraft.enchantment.EnchantmentHelper.applyThornEnchantments((net.minecraft.entity.EntityLivingBase) target, this);
            }
            net.minecraft.enchantment.EnchantmentHelper.applyArthropodEnchantments(this, target);
        }
        return flag;
    }

    @Override
    public int getTotalArmorValue() {
        int armor = super.getTotalArmorValue() + this.getGolemType().armor;
        if (this.decoration.contains("V")) armor++;
        if (this.decoration.contains("P")) armor += 4;
        return Math.min(armor, 20);
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        this.paused = false;
        if (source == net.minecraft.util.DamageSource.IN_WALL) return false;
        if (source.isFireDamage() && this.getGolemType().fireResist) return false;
        if (this.getGolemType() == EnumGolemType.THAUMIUM && source == net.minecraft.util.DamageSource.MAGIC) amount *= 0.5f;
        net.minecraft.entity.Entity attacker = source.getTrueSource();
        int thornUpgrades = this.getUpgradeAmount(5);
        if (attacker != null && attacker.getEntityId() != this.getEntityId() && thornUpgrades > 0) {
            attacker.attackEntityFrom(net.minecraft.util.DamageSource.causeThornsDamage(this),
                    thornUpgrades * 2 + this.rand.nextInt(2 * thornUpgrades));
            attacker.playSound(net.minecraft.init.SoundEvents.ENCHANT_THORNS_HIT, 0.5F, 1.0F);
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void setFire(int seconds) {
        if (!this.getGolemType().fireResist) {
            super.setFire(seconds);
        }
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public void onDeath(net.minecraft.util.DamageSource cause) {
        if (!this.world.isRemote) {
            Thaumcraft.log.info("[Thaumcraft] {} was killed by {} ({})", this, cause.getTrueSource(), cause.getDamageType());
        }
        super.onDeath(cause);
    }

    // --- Misc ---
    @Override
    public float getAIMoveSpeed() {
        if (this.paused || this.inactive) return 0.0f;
        float speed = (float)this.golemType.speed * (this.decoration.contains("B") ? 1.1f : 1.0f);
        if (this.decoration.contains("P")) speed *= 0.88f;
        speed *= 1.0f + (float) this.getUpgradeAmount(0) * 0.15f;
        if (this.advanced) speed *= 1.1f;
        return speed;
    }

    public int getAttackSpeed() {
        return 20 - (this.advanced ? 2 : 0);
    }

    public void startActionTimer() {
        if (this.action == 0) {
            this.action = 6;
            this.world.setEntityState(this, (byte)4);
        }
    }

    public void startLeftArmTimer() {
        if (this.leftArm == 0) {
            this.leftArm = 5;
            this.world.setEntityState(this, (byte)6);
        }
    }

    public void attackEntityWithRangedAttack(net.minecraft.entity.EntityLivingBase target) {
        thaumcraft.common.entities.projectile.EntityDart dart = new thaumcraft.common.entities.projectile.EntityDart(this.world, this);
        double dx = target.posX - this.posX;
        double dy = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - dart.posY;
        double dz = target.posZ - this.posZ;
        float speed = 1.6F;
        float inaccuracy = 7.0F - (float)this.getUpgradeAmount(3) * 1.75F;
        dart.shoot(dx, dy, dz, speed, inaccuracy);
        double dmg = this.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        dart.setDamage(dmg * 0.4D);
        this.playSound(TCSounds.GOLEMIRONSHOOT, 0.5F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.6F));
        this.world.spawnEntity(dart);
        this.startLeftArmTimer();
    }

    @Override
    public int getMaxSpawnedInChunk() { return 0; }
    @Override protected boolean canDespawn() { return false; }

    @Override
    protected void dropFewItems(boolean wasRecentlyHit, int looting) {
        this.dropStuff();
    }

    public void dropStuff() {
        if (!this.world.isRemote && this.itemCarried != null) {
            this.entityDropItem(this.itemCarried, 0.5f);
        }
    }

    // --- Spawn data ---
    @Override
    public void writeSpawnData(io.netty.buffer.ByteBuf buf) {
        buf.writeByte(this.getCore());
        buf.writeBoolean(this.advanced);
        buf.writeByte(this.upgrades.length);
        for (byte b : this.upgrades) buf.writeByte(b);
    }

    @Override
    public void readSpawnData(io.netty.buffer.ByteBuf buf) {
        try {
            this.setCore(buf.readByte());
            this.advanced = buf.readBoolean();
            if (this.getCore() >= 0) {
                this.bootup = 0.0F;
            }
            this.upgrades = new byte[buf.readByte()];
            for (int a = 0; a < this.upgrades.length; a++) this.upgrades[a] = buf.readByte();
        } catch (Exception e) {}
    }
}
