package plus.dragons.createclassicblazeenchanter.common.processing.enchanter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createclassicblazeenchanter.config.CCBEConfig;
import plus.dragons.createdragonsplus.common.advancements.AdvancementBehaviour;
import plus.dragons.createdragonsplus.common.fluids.tank.ConfigurableFluidTank;
import plus.dragons.createdragonsplus.common.processing.blaze.BlazeBlockEntity;
import plus.dragons.createenchantmentindustry.common.fluids.experience.BlazeExperienceBlockEntity;
import plus.dragons.createenchantmentindustry.common.registry.CEIFluids;

import java.util.List;
import java.util.function.Consumer;

public class ClassicBlazeEnchanterBlockEntity extends BlazeExperienceBlockEntity {
    protected static final int ENCHANTING_TIME = 200;
    protected ItemStack heldItem = ItemStack.EMPTY;
    protected int processingTime;
    protected boolean special;
    protected boolean cursed;
    protected ClassicEnchanterBehavior enchanter;
    protected AdvancementBehaviour advancement;
    float flip;
    float oFlip;
    float flipT;
    float flipA;

    public ClassicBlazeEnchanterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public LerpedFloat headAngle(){
        return ((BlazeBlockEntity)this).headAngle;
    }

    public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
        if (side == Direction.DOWN)
            return tanks.getCapability();
        return null;
    }


    @Override
    protected ConfigurableFluidTank createNormalTank(Consumer<FluidStack> fluidUpdateCallback) {
        return new ConfigurableFluidTank(CCBEConfig.server().classicBlazeForgerFluidCapacity.get(), fluidUpdateCallback)
                .allowInsertion(fluidStack -> fluidStack.is(CEIFluids.EXPERIENCE));
    }

    @Override
    protected ConfigurableFluidTank createSpecialTank(Consumer<FluidStack> fluidUpdateCallback) {
        return new ConfigurableFluidTank(CCBEConfig.server().classicBlazeForgerFluidCapacity.get(), fluidUpdateCallback)
                .forbidInsertion();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        enchanter = new ClassicEnchanterBehavior(this,new EnchanterTransform());
        advancement = new AdvancementBehaviour(this);
        behaviours.add(enchanter);
        behaviours.add(advancement);
    }

    @Override
    public boolean isActive() {
        return processingTime > 0;
    }

    public ItemStack insertItem(ItemStack stack, boolean simulate) {
        assert level != null;
        if (!heldItem.isEmpty())
            return stack;
        var input = stack.copy();
        var inserted = input.split(1);
        if (!enchanter.canProcess(inserted)) {
            return stack;
        }
        if (simulate)
            return input;
        heldItem = inserted;
        notifyUpdate();
        return input;
    }

    public ItemStack extractItem(boolean forced, boolean simulate) {
        assert level != null;
        ItemStack extracted = ItemStack.EMPTY;
        if (forced || processingTime <= 0) {
            extracted = heldItem.copy();
            if (!simulate) {
                heldItem = ItemStack.EMPTY;
                processingTime = -1;
                notifyUpdate();
            }
        }
        return extracted;
    }

    @Override
    public void tick() {
        super.tick();
        boolean special = getHeatLevelFromBlock() == BlazeBurnerBlock.HeatLevel.SEETHING;
        if (this.special != special) {
            this.special = special;
        }
        var strikePos = getStrikePos();
        boolean cursed = special && !worldPosition.equals(strikePos);
        if (this.cursed != cursed) {
            this.cursed = cursed;
        }
        bookTick();
        if(heldItem.isEmpty()) return;
        if (level.isClientSide() && isVirtual()){
            if (enchanter.canProcess(heldItem)) {
                if (processingTime < 0) {
                    processingTime = ENCHANTING_TIME / 4;
                    return;
                }
                if (processingTime > 0) {
                    processingTime--;
                    return;
                }
                processingTime = -1;
                heldItem = enchanter.getResult(heldItem);
                return;
            }
        }
        if (!(level instanceof ServerLevel serverLevel))
            return;
        if (enchanter.canProcess(heldItem)) {
            var cost = enchanter.getExperienceCost(heldItem);
            if (cost > 0 && consumeExperience(enchanter.getMaxExperienceCost(), special, true)) {
                if (processingTime < 0) {
                    processingTime = ENCHANTING_TIME;
                    notifyUpdate();
                    return;
                }
                if (processingTime > 0) {
                    processingTime--;
                    notifyUpdate();
                    return;
                }
                if (special && !cursed && strikeLightning(serverLevel, strikePos)) {
                    serverLevel.destroyBlock(worldPosition, false);
                    serverLevel.setBlockAndUpdate(worldPosition, AllBlocks.LIT_BLAZE_BURNER.getDefaultState());
                    return;
                }
                processingTime = -1;
                heldItem = enchanter.getResult(heldItem);
                consumeExperience(enchanter.getMaxExperienceCost(), special, false);
                notifyUpdate();
                level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);
                spawnEnchantParticles();
            } else {
                if (processingTime != -1) {
                    processingTime = -1;
                    notifyUpdate();
                }
            }
        } else if (processingTime != -1) {
            processingTime = -1;
            notifyUpdate();
        }

    }

    @Override
    public @Nullable PartialModel getGogglesModel(BlazeBurnerBlock.HeatLevel heatLevel) {
        return super.getGogglesModel(heatLevel);
    }

    @Override
    public void tickAnimation() {
        super.tickAnimation();
    }

    protected void bookTick() {
        if (level.random.nextInt(40) == 0) {
            float oFlipT = flipT;
            while (oFlipT == flipT) {
                flipT += (level.random.nextInt(4) - level.random.nextInt(4));
            }
        }
        oFlip = flip;
        float flipDiff = (flipT - flip) * 0.4F;
        flipDiff = Mth.clamp(flipDiff, -0.2F, 0.2F);
        flipA += (flipDiff - flipA) * 0.9F;
        flip += flipA;
    }

    protected void spawnEnchantParticles() {
        if (isVirtual())
            return;
        Vec3 vec = VecHelper.getCenterOf(worldPosition);
        vec = vec.add(0, 1, 0);
        ParticleOptions particle = ParticleTypes.ENCHANT;
        for (int i = 0; i < 20; i++) {
            Vec3 m = VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1f);
            m = new Vec3(m.x, Math.abs(m.y), m.z);
            level.addAlwaysVisibleParticle(particle, vec.x, vec.y, vec.z, m.x, m.y, m.z);
        }
        level.playLocalSound(vec.x, vec.y, vec.z, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1f, level.random.nextFloat() * .1f + .9f, true);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        added |= enchanter.addToGoggleTooltip(tooltip, isPlayerSneaking);
        return added;
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("ProcessingTime", this.processingTime);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.processingTime = compound.getInt("ProcessingTime");
    }

    public LerpedFloat headAnimation() {
        return ((BlazeBlockEntity)this).headAnimation;
    }

    private static class EnchanterTransform extends ValueBoxTransform.Sided {
        private EnchanterTransform() {
        }

        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace((double)8.0F, (double)8.0F, (double)13.5F);
        }

        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack poseStack) {
            float yRot = AngleHelper.horizontalAngle(this.getSide()) + 180.0F;
            TransformStack.of(poseStack).rotateYDegrees(yRot);
        }

        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis().isHorizontal();
        }
    }
}
