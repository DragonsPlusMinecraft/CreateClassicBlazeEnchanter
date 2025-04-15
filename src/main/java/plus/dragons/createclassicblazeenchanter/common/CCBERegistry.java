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

package plus.dragons.createclassicblazeenchanter.common;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static plus.dragons.createclassicblazeenchanter.common.CCBECommon.REGISTRATE;
import static plus.dragons.createenchantmentindustry.common.registry.CEIBlocks.BLAZE_FORGER;

import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import plus.dragons.createclassicblazeenchanter.common.processing.enchanter.*;
import plus.dragons.createdragonsplus.common.processing.blaze.BlazeBlock;
import plus.dragons.createenchantmentindustry.common.registry.CEICreativeModeTabs;

@EventBusSubscriber(modid = CCBECommon.ID, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("removal")
public class CCBERegistry {
    public static final BlockEntry<ClassicBlazeEnchanterBlock> CLASSIC_BLAZE_ENCHANTER_BLOCK = REGISTRATE
            .block("classic_blaze_enchanter", ClassicBlazeEnchanterBlock::new)
            .initialProperties(SharedProperties::softMetal)
            .properties(p -> p.mapColor(MapColor.COLOR_GRAY).lightLevel(BlazeBlock::getLight))
            .transform(pickaxeOnly())
            .addLayer(() -> RenderType::cutoutMipped)
            .tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag, AllTags.AllBlockTags.FAN_PROCESSING_CATALYSTS_SMOKING.tag)
            .blockstate((ctx, prov) -> prov.horizontalBlock(
                    ctx.getEntry(),
                    prov.models().getExistingFile(Create.asResource("block/blaze_burner/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(),
                    Create.asResource("block/blaze_burner/block_with_blaze")))
            .build()
            .register();

    public static final BlockEntityEntry<ClassicBlazeEnchanterBlockEntity> CLASSIC_BLAZE_ENCHANTER_BLOCKENTITY = REGISTRATE
            .blockEntity("classic_blaze_enchanter", ClassicBlazeEnchanterBlockEntity::new)
            .visual(() -> ClassicEnchanterBlockVisual::new)
            .renderer(() -> ClassicBlazeEnchanterRenderer::new)
            .validBlock(CLASSIC_BLAZE_ENCHANTER_BLOCK)
            .register();

    public static final RegistryEntry<ArmInteractionPointType, ClassicBlazeEnchanterArmInteractionPoint.Type> CLASSIC_BLAZE_ENCHANTER_ARM_INTERACTION = REGISTRATE.armInteractionPoint("classic_blaze_enchanter",
            ClassicBlazeEnchanterArmInteractionPoint.Type::new).register();

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CEICreativeModeTabs.BASE.getKey()) {
            event.insertAfter(BLAZE_FORGER.asStack(), CLASSIC_BLAZE_ENCHANTER_BLOCK.asStack(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                CLASSIC_BLAZE_ENCHANTER_BLOCKENTITY.get(), ClassicBlazeEnchanterBlockEntity::getFluidHandler);
    }

    public static void register(IEventBus modBus) {}
}
