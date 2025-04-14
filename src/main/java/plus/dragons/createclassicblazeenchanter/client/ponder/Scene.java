/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createclassicblazeenchanter.client.ponder;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import plus.dragons.createclassicblazeenchanter.common.processing.enchanter.ClassicBlazeEnchanterBlockEntity;
import plus.dragons.createclassicblazeenchanter.common.processing.enchanter.ClassicEnchanterBehavior;
import plus.dragons.createdragonsplus.common.processing.blaze.BlazeBlock;
import plus.dragons.createenchantmentindustry.client.ponder.CEIPonderScenes;
import plus.dragons.createenchantmentindustry.common.registry.CEIBlocks;
import plus.dragons.createenchantmentindustry.common.registry.CEIFluids;
import plus.dragons.createenchantmentindustry.common.registry.CEIItems;

public class Scene {
    public static void basic(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("classic_blaze_enchanter.intro", "Introduction to Classic Blaze Enchanter");
        scene.configureBasePlate(0, 0, 5);
        scene.world().showSection(util.select().everywhere(), Direction.DOWN);
        scene.idle(5);
        scene.overlay().showText(60)
                .text("This is a Classic Blaze Enchanter. It can continuously enchant items according to Enchanting Template")
                .placeNearTarget()
                .pointAt(util.vector().centerOf(2, 2, 1));
        scene.idle(65);

        scene.overlay().showText(60)
                .text("Pass in Liquid Experience to get it started")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(util.vector().centerOf(2, 2, 1));
        scene.idle(5);
        scene.world().setKineticSpeed(util.select().position(4, 1, 3), 256.0F);
        scene.idle(10);
        scene.world().modifyBlockEntity(util.grid().at(1, 1, 2), FluidTankBlockEntity.class,
                be -> be.getControllerBE().getTankInventory().fill(new FluidStack(CEIFluids.EXPERIENCE.get(), 64000), IFluidHandler.FluidAction.EXECUTE));
        scene.world().modifyBlockEntity(util.grid().at(2, 2, 1), ClassicBlazeEnchanterBlockEntity.class,
                be -> be.getNormalTank().fill(new FluidStack(CEIFluids.EXPERIENCE.get(), 4000), IFluidHandler.FluidAction.EXECUTE));
        scene.world().modifyBlock(util.grid().at(2, 2, 1),
                bs -> bs.setValue(BlazeBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.KINDLED), false);
        scene.idle(10);
        scene.world().setKineticSpeed(util.select().position(0, 1, 3), 256.0F);
        scene.idle(40);

        Vec3 slotVec = util.vector().of(2.0f, 2.5f, 1.5f);
        scene.overlay().showFilterSlotInput(slotVec, Direction.WEST, 90);
        scene.overlay().showText(160)
                .text("Before you can start enchanting with it, you need to set Enchanting Template via filter slot. " +
                        "Enchantment on the Enchanting Template will be enchanted to item and will not consume the Enchanting Template or enchantment")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(slotVec);
        scene.idle(5);
        var template = CEIItems.ENCHANTING_TEMPLATE.asStack();
        scene.overlay().showText(100)
                .text("Set Enchanting Template with enchantments:")
                .colored(PonderPalette.INPUT)
                .independent(0);
        scene.overlay().showText(100)
                .text("Sweeping Edge Ⅰ")
                .colored(PonderPalette.INPUT)
                .independent(25);
        scene.overlay().showText(100)
                .text("Arthropods Ⅰ")
                .colored(PonderPalette.INPUT)
                .independent(40);
        CEIPonderScenes.enchant(scene, template, Enchantments.SWEEPING_EDGE, 1);
        CEIPonderScenes.enchant(scene, template, Enchantments.BANE_OF_ARTHROPODS, 1);
        scene.overlay().showControls(slotVec, Pointing.RIGHT, 100).withItem(template).rightClick();
        scene.world().modifyBlockEntity(util.grid().at(2, 2, 1), ClassicBlazeEnchanterBlockEntity.class, be -> {
            ClassicEnchanterBehavior enchanter = be.getBehaviour(ClassicEnchanterBehavior.TYPE);
            enchanter.setFilter(template);
        });
        scene.idle(175);

        scene.overlay().showText(70)
                .text("If there are multiple enchantments on the Enchanting Template, one of the available enchantments will be randomly selected for enchanting")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(slotVec);
        scene.idle(25);
        scene.overlay().showText(47)
                .text("Output: Diamond Sword")
                .colored(PonderPalette.OUTPUT);
        scene.overlay().showControls(util.vector().topOf(2, 2, 1), Pointing.RIGHT, 45).withItem(Items.DIAMOND_SWORD.getDefaultInstance()).rightClick();
        scene.world().modifyBlockEntity(util.grid().at(2, 2, 1), ClassicBlazeEnchanterBlockEntity.class, be -> be.insertItem(Items.DIAMOND_SWORD.getDefaultInstance(), false));
        scene.idle(50);
        scene.overlay().showText(47)
                .text("Output: Diamond Sword with enchantment:")
                .colored(PonderPalette.OUTPUT);
        scene.overlay().showText(47)
                .text("SWEEPING EDGE Ⅰ")
                .colored(PonderPalette.OUTPUT)
                .independent(25);
        scene.idle(50);
        scene.overlay().showText(47)
                .text("Output: Diamond Sword with enchantments:")
                .colored(PonderPalette.OUTPUT);
        scene.overlay().showText(47)
                .text("SWEEPING EDGE Ⅰ")
                .colored(PonderPalette.OUTPUT)
                .independent(25);
        scene.overlay().showText(47)
                .text("Bane of Arthropods Ⅰ")
                .colored(PonderPalette.OUTPUT)
                .independent(40);
        scene.idle(50);

        scene.overlay().showText(70)
                .text("Classic Blaze Enchanter enters the Super Enchanting mode in the same way as the Blaze Enchanter, requiring Cake O' Enchanting")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(2, 2, 1));
        scene.idle(10);
        scene.world().modifyBlockEntity(util.grid().at(2, 2, 1), ClassicBlazeEnchanterBlockEntity.class, be -> {
            be.getSpecialTank().setFluid(new FluidStack(CEIFluids.EXPERIENCE.get(), 4000));
            be.extractItem(false, false);
        });
        scene.world().modifyBlock(util.grid().at(2, 2, 1),
                bs -> bs.setValue(BlazeBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SEETHING), false);
        scene.idle(75);

        var template2 = CEIItems.SUPER_ENCHANTING_TEMPLATE.asStack();
        scene.overlay().showText(70)
                .text("Set Enchanting Template with enchantment:")
                .colored(PonderPalette.INPUT);
        scene.overlay().showText(70)
                .text("Smite Ⅴ")
                .colored(PonderPalette.INPUT)
                .independent(25);
        CEIPonderScenes.enchant(scene, template2, Enchantments.SMITE, 5);
        scene.overlay().showControls(slotVec, Pointing.RIGHT, 45).withItem(template2).rightClick();
        scene.world().modifyBlockEntity(util.grid().at(2, 2, 1), ClassicBlazeEnchanterBlockEntity.class, be -> {
            ClassicEnchanterBehavior enchanter = be.getBehaviour(ClassicEnchanterBehavior.TYPE);
            enchanter.setFilter(template);
        });
        scene.overlay().showText(70)
                .text("If the item already has an enchantment of the same type and level, Super Enchanting increases the enchantment by one level, up to one level beyond level cap")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(2, 2, 1));
        scene.idle(75);
        scene.overlay().showText(47)
                .text("Output: Diamond Sword")
                .colored(PonderPalette.OUTPUT);
        scene.overlay().showControls(util.vector().topOf(2, 2, 1), Pointing.RIGHT, 45).withItem(Items.DIAMOND_SWORD.getDefaultInstance()).rightClick();
        scene.world().modifyBlockEntity(util.grid().at(2, 2, 1), ClassicBlazeEnchanterBlockEntity.class, be -> be.insertItem(Items.DIAMOND_SWORD.getDefaultInstance(), false));
        scene.world().setBlock(util.grid().at(1, 2, 1), Blocks.LIGHTNING_ROD.defaultBlockState(), true);
        scene.idle(50);
        scene.world().createEntity(level -> {
            var lightning = EntityType.LIGHTNING_BOLT.create(level);
            lightning.moveTo(Vec3.atBottomCenterOf(util.grid().at(1, 2, 1)));
            return lightning;
        });
        scene.world().setBlock(util.grid().at(1, 1, 1), CEIBlocks.SUPER_EXPERIENCE_BLOCK.getDefaultState(), false);
        scene.overlay().showText(47)
                .text("Output: Diamond Sword with enchantment:")
                .colored(PonderPalette.OUTPUT);
        scene.overlay().showText(47)
                .text("SMITE Ⅴ")
                .colored(PonderPalette.OUTPUT)
                .independent(25);
        scene.idle(50);
        scene.world().createEntity(level -> {
            var lightning = EntityType.LIGHTNING_BOLT.create(level);
            lightning.moveTo(Vec3.atBottomCenterOf(util.grid().at(1, 2, 1)));
            return lightning;
        });
        scene.overlay().showText(47)
                .text("Output: Diamond Sword with enchantment:")
                .colored(PonderPalette.OUTPUT);
        scene.overlay().showText(47)
                .text("SMITE Ⅵ")
                .colored(PonderPalette.OUTPUT)
                .independent(25);
        scene.idle(50);
        scene.overlay().showText(160)
                .text("Super Enchanting invites lightning strikes, just like any other Super Enchanting Blaze. " +
                        "If the lightning strike is avoided by covering it with block, then the enchanting has a chance of causing the level of the enchantment to drop")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector().centerOf(2, 2, 1));
        scene.idle(160);
    }
}
