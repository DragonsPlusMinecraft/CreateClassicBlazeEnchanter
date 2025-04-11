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

package plus.dragons.createclassicblazeenchanter.config;

import net.minecraft.Util;
import net.minecraft.util.Unit;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import plus.dragons.createenchantmentindustry.config.CEIClientConfig;
import plus.dragons.createenchantmentindustry.config.CEIServerConfig;

public class CCBEConfig {
    private static final CCBEClientConfig CLIENT_CONFIG = new CCBEClientConfig();
    private static final CCBEServerConfig SERVER_CONFIG = new CCBEServerConfig();
    private static ModConfigSpec CLIENT_SPEC;
    private static ModConfigSpec SERVER_SPEC;

    public CCBEConfig(ModContainer modContainer) {
        CLIENT_SPEC = Util.make(new ModConfigSpec.Builder().configure(builder -> {
            CLIENT_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> modContainer.registerConfig(Type.CLIENT, spec));
        SERVER_SPEC = Util.make(new ModConfigSpec.Builder().configure(builder -> {
            SERVER_CONFIG.registerAll(builder);
            return Unit.INSTANCE;
        }).getValue(), spec -> modContainer.registerConfig(Type.SERVER, spec));
    }


    public static CCBEClientConfig client() {
        return CLIENT_CONFIG;
    }

    public static CCBEServerConfig server() {
        return SERVER_CONFIG;
    }

    @SubscribeEvent
    public void onLoad(ModConfigEvent.Loading event) {
        var spec = event.getConfig().getSpec();
        if (SERVER_SPEC == spec) {
            SERVER_CONFIG.onLoad();
        } else if (CLIENT_SPEC == spec) {
            CLIENT_CONFIG.onLoad();
        }
    }

    @SubscribeEvent
    public void onReload(ModConfigEvent.Reloading event) {
        var spec = event.getConfig().getSpec();
        if (SERVER_SPEC == spec) {
            SERVER_CONFIG.onReload();
        } else if (CLIENT_SPEC == spec) {
            CLIENT_CONFIG.onReload();
        }
    }
}
