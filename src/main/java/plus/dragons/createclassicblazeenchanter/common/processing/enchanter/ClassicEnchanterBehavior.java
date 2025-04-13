package plus.dragons.createclassicblazeenchanter.common.processing.enchanter;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import plus.dragons.createclassicblazeenchanter.config.CCBEConfig;
import plus.dragons.createclassicblazeenchanter.util.CCBELang;
import plus.dragons.createenchantmentindustry.common.fluids.experience.ExperienceHelper;
import plus.dragons.createenchantmentindustry.common.processing.enchanter.EnchantingTemplateItem;
import plus.dragons.createenchantmentindustry.common.registry.CEIItems;
import plus.dragons.createenchantmentindustry.util.CEILang;

import java.util.List;

public class ClassicEnchanterBehavior extends FilteringBehaviour implements IHaveGoggleInformation {
    public static final BehaviourType<ClassicEnchanterBehavior> TYPE = new BehaviourType<>();
    private final ClassicBlazeEnchanterBlockEntity enchanter;

    public ClassicEnchanterBehavior(ClassicBlazeEnchanterBlockEntity enchanter, ValueBoxTransform transform) {
        super(enchanter, transform);
        this.enchanter = enchanter;
    }

    public boolean canProcess(ItemStack stack) {
        if(filter.item().is(CEIItems.SUPER_ENCHANTING_TEMPLATE) && !enchanter.special) return false;
        if(stack.is(Items.ENCHANTED_BOOK)||stack.getItem() instanceof EnchantingTemplateItem) return false;
        return test(stack);
    }

    public ItemStack getResult(ItemStack stack) {
        var result = stack.copy();
        var availableEnchantment = filterAvailableEnchantment(stack);
        var apply = WeightedRandom.getRandomItem(enchanter.getLevel().random, availableEnchantment.stream().map(entry->new EnchantmentInstance(entry.getKey(),entry.getIntValue())).toList()).get();
        var applyLevel = apply.level;
        if(enchanter.special){
            var stackEnchantment = EnchantmentHelper.getEnchantmentsForCrafting(stack);
            var level = stackEnchantment.getLevel(apply.enchantment);
            if(applyLevel==level){
                applyLevel += 1;
                if(applyLevel>apply.enchantment.value().getMaxLevel()+1)
                    applyLevel-=1;
            }
            if(enchanter.cursed){
                if(enchanter.getLevel().random.nextFloat()<0.25){
                    applyLevel = apply.level - 1;
                }
            }
        }
        var removedEnchantments = new ItemEnchantments.Mutable(stack.getOrDefault(EnchantmentHelper.getComponentType(stack), ItemEnchantments.EMPTY));
        removedEnchantments.set(apply.enchantment, 0);
        EnchantmentHelper.setEnchantments(result, removedEnchantments.toImmutable());
        result.enchant(apply.enchantment,applyLevel);
        return result;
    }

    public int getExperienceCost(ItemStack stack) {
        return filterAvailableEnchantment(stack).stream().map(this::enchantmentToCost).max(Integer::compareTo).orElse(0);
    }

    private List<Object2IntMap.Entry<Holder<Enchantment>>> filterAvailableEnchantment(ItemStack stack){
        var stackEnchantment = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        var targetEnchantment = EnchantmentHelper.getEnchantmentsForCrafting(filter.item());
        return targetEnchantment.entrySet().stream()
                .filter(entry->{
                    if(!stack.supportsEnchantment(entry.getKey())) return false;
                    int level = stackEnchantment.getLevel(entry.getKey());
                    if((level >= entry.getIntValue() && !enchanter.special)||(enchanter.special && level>entry.getIntValue())) return false;
                    var removedIdentical = stackEnchantment.keySet().stream().filter(e -> !e.value().equals(entry.getKey().value())).toList();
                    if(!EnchantmentHelper.isEnchantmentCompatible(removedIdentical, entry.getKey())) return false;
                    return true;
                }).toList();
    }


    @Override
    public boolean test(ItemStack stack) {
        return !filterAvailableEnchantment(stack).isEmpty();
    }

    public int getMaxExperienceCost() {
        return (int) Math.ceil(EnchantmentHelper.getEnchantmentsForCrafting(filter.item()).entrySet().stream().map(this::enchantmentToCost).max(Integer::compareTo).get()
                * (enchanter.special? CCBEConfig.server().classicBlazeForgerSuperEnchantingCostCoefficient.get() : CCBEConfig.server().classicBlazeForgerNormalEnchantingCostCoefficient.get()));
    }

    private int enchantmentToCost(Object2IntMap.Entry<Holder<Enchantment>> enchantment){
        var enchantingLevel = enchanter.special? 60 : 30;
        int levelCost = Math.ceilDiv(enchantingLevel, 20);
        int experienceCost = 0;
        for (int i = 0; i < levelCost; i++) {
            experienceCost += ExperienceHelper.getExperienceForNextLevel(enchantingLevel - i);
        }
        return experienceCost + (enchanter.special ? enchantment.getKey().value().getMinCost(enchantment.getIntValue()) : enchantment.getKey().value().getMaxCost(enchantment.getIntValue()));
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt,registries,clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt,registries,clientPacket);
    }


    @Override
    public boolean setFilter(ItemStack stack) {
        if(stack.isEmpty() || stack.getItem() instanceof EnchantingTemplateItem && !EnchantmentHelper.getEnchantmentsForCrafting(stack).isEmpty()) return super.setFilter(stack);
        return false;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!filter.isEmpty()) {
            if(EnchantmentHelper.getEnchantmentsForCrafting(filter.item()).size()>1)
                CCBELang.translate("gui.goggles.classic_enchanting.available_targets").forGoggles(tooltip);
            else
                CCBELang.translate("gui.goggles.classic_enchanting.available_target").forGoggles(tooltip);
            var style = enchanter.special
                    ? (enchanter.cursed ? ChatFormatting.RED : ChatFormatting.BLUE)
                    : ChatFormatting.GOLD;

            EnchantmentHelper.getEnchantmentsForCrafting(filter.item()).entrySet().stream().forEach(enchantment->{
                MutableComponent add = Component.literal("     ").append(Enchantment.getFullname(enchantment.getKey(),enchantment.getIntValue()).copy().withStyle(style));
                Component sign = null;
                if(enchanter.special){
                    if(enchantment.getIntValue()<=enchantment.getKey().value().getMaxLevel())
                        sign = enchanter.cursed? Component.literal(" +/-?") : Component.literal(" +");
                }
                if(sign!=null) add = add.append(sign.copy());
                tooltip.add(add.withStyle(style));
            });

            int cost = getMaxExperienceCost();
            LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
            CEILang.translate("gui.goggles.enchanting.cost", CEILang.number(cost).add(mb).style(style))
                    .forGoggles(tooltip);
            return true;
        }
        return false;
    }
}
