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

import com.simibubi.create.foundation.advancement.AllTriggers;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import plus.dragons.createclassicblazeenchanter.util.CCBEAdvancement;
import plus.dragons.createdragonsplus.common.advancements.CDPAdvancement;
import plus.dragons.createdragonsplus.common.advancements.criterion.BuiltinTrigger;
import plus.dragons.createdragonsplus.util.CodeReference;

public class CCBEAdvancements {
    public static final List<CDPAdvancement> ENTRIES = new ArrayList();

    public static final CCBEAdvancement START = null,

            LEGACY_IGNITION = create("legacy_ignition", b -> b.icon(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK)
                    .title("Legacy Ignition")
                    .description("It's from the last era. Obtain a Classic Blaze Enchanter")
                    .whenIconCollected()),
            //.after(EXPERIENCED_ENGINEER)),

            THOUSANDFOLD_EVERCHANT = create("thousandfold_everchant", b -> b.icon(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK)
                    .title("Thousandfold Everchant")
                    .description("Classic Blaze Enchanter enchants 1,000 times")
                    .whenStatReach(Stats.CUSTOM.get(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_STAT.get()), MinMaxBounds.Ints.atLeast(1000))
                    .special(CDPAdvancement.TaskType.EXPERT));
    //.after(LEGACY_IGNITION));

    private static CCBEAdvancement create(String id, UnaryOperator<CDPAdvancement.Builder> b) {
        return new CCBEAdvancement(id, b);
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (CDPAdvancement advancement : ENTRIES) {
            advancement.provideLang(consumer);
        }
    }

    public static void register() {}

    @CodeReference(value = { AllTriggers.class }, source = { "create" }, license = { "mit" })
    public static class BuiltinTriggersQuickDeploy {
        private static final Map<ResourceLocation, BuiltinTrigger> triggers = new IdentityHashMap();

        public static BuiltinTrigger add(ResourceLocation id) {
            BuiltinTrigger instance = new BuiltinTrigger();
            triggers.put(id, instance);
            return instance;
        }

        public static void register() {
            triggers.entrySet().forEach((set) -> Registry.register(BuiltInRegistries.TRIGGER_TYPES, (ResourceLocation) set.getKey(), (BuiltinTrigger) set.getValue()));
        }
    }
}
