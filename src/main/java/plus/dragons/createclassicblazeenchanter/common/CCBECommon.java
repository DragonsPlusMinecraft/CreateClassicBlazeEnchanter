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

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import plus.dragons.createclassicblazeenchanter.config.CCBEConfig;
import plus.dragons.createdragonsplus.common.CDPRegistrate;

@Mod(CCBECommon.ID)
public class CCBECommon {
    public static final String ID = "create_classic_blaze_enchanter";
    public static final CDPRegistrate REGISTRATE = new CDPRegistrate(ID);

    public CCBECommon(IEventBus modBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modBus);
        CCBERegistry.register(modBus);
        modBus.register(this);
        modBus.register(new CCBEConfig(modContainer));
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void register(final RegisterEvent event) {
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            CCBEAdvancements.register();
            CCBEAdvancements.BuiltinTriggersQuickDeploy.register();
        }
    }

    public static ResourceLocation asResource(String name) {
        return ResourceLocation.fromNamespaceAndPath(ID, name);
    }

    public static String asLocalization(String key) {
        return ID + "." + key;
    }
}
