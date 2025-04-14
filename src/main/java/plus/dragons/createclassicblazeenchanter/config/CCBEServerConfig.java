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

import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.config.ui.ConfigAnnotations;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CCBEServerConfig extends ConfigBase {
    public final ConfigBase.ConfigInt classicBlazeForgerFluidCapacity = i(4000, 1000,
            "classicBlazeForgerFluidCapacity",
            "The amount of liquid a Classic Blaze Enchanter can hold (mB).",
            ConfigAnnotations.RequiresRestart.SERVER.asComment());
    public final ConfigBase.ConfigFloat classicBlazeForgerNormalEnchantingCostCoefficient = f(1.0f, 0.01f,
            "classicBlazeForgerNormalEnchantingCostCoefficient",
            "Experience cost coefficient of Classic Blaze Enchanter regular enchanting.",
            ConfigAnnotations.RequiresRestart.SERVER.asComment());
    public final ConfigBase.ConfigFloat classicBlazeForgerSuperEnchantingCostCoefficient = f(1.0f, 0.01f,
            "classicBlazeForgerSuperEnchantingCostCoefficient",
            "Experience cost coefficient of Classic Blaze Enchanter super enchanting.",
            ConfigAnnotations.RequiresRestart.SERVER.asComment());
    public final ConfigBase.ConfigFloat classicBlazeForgerSuperEnchantingCurseLevelDroppingRate = f(0.25f, 0.01f,
            "classicBlazeForgerSuperEnchantingCurseLevelDroppingRate",
            "Probability that a cursed Classic Blaze Enchanter super enchants resulting in a drop in enchantment level",
            ConfigAnnotations.RequiresRestart.SERVER.asComment());

    @Override
    public void registerAll(ModConfigSpec.Builder builder) {
        super.registerAll(builder);
    }

    @Override
    public String getName() {
        return "server";
    }

    static class Comments {}
}
