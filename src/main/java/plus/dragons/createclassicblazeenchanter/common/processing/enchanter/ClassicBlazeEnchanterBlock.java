package plus.dragons.createclassicblazeenchanter.common.processing.enchanter;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createclassicblazeenchanter.common.CCBERegistry;
import plus.dragons.createdragonsplus.common.advancements.AdvancementBehaviour;
import plus.dragons.createenchantmentindustry.common.fluids.experience.BlazeExperienceBlock;

public class ClassicBlazeEnchanterBlock extends BlazeExperienceBlock<ClassicBlazeEnchanterBlockEntity> {

    public ClassicBlazeEnchanterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        AdvancementBehaviour.setPlacedBy(level,pos,placer);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity == null)
            return InteractionResult.PASS;
        ItemStack extrtacted = blockEntity.extractItem(true, false);
        if (!extrtacted.isEmpty()) {
            player.getInventory().placeItemBackInInventory(extrtacted);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var result = super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (result.result() != InteractionResult.PASS)
            return result;
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity == null)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        var remainder = blockEntity.insertItem(stack, false);
        if (ItemStack.isSameItemSameComponents(stack, remainder) && remainder.getCount() == stack.getCount())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        player.setItemInHand(hand, remainder);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    protected MapCodec<ClassicBlazeEnchanterBlock> codec() {
        return simpleCodec(ClassicBlazeEnchanterBlock::new);
    }

    @Override
    public Class<ClassicBlazeEnchanterBlockEntity> getBlockEntityClass() {
        return ClassicBlazeEnchanterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ClassicBlazeEnchanterBlockEntity> getBlockEntityType() {
        return CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCKENTITY.get();
    }
}
