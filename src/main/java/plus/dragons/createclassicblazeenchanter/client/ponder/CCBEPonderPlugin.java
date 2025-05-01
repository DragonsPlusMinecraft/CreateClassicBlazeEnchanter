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

import static plus.dragons.createenchantmentindustry.client.ponder.CEIPonderTags.EXPERIENCE_APPLIANCES;
import static plus.dragons.createenchantmentindustry.client.ponder.CEIPonderTags.SUPER_EXPERIENCE_APPLIANCES;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import plus.dragons.createclassicblazeenchanter.common.CCBECommon;
import plus.dragons.createclassicblazeenchanter.common.CCBERegistry;

public class CCBEPonderPlugin implements PonderPlugin {
    @Override
    public String getModId() {
        return CCBECommon.ID;
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.addToTag(EXPERIENCE_APPLIANCES).add(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK.getId());
        helper.addToTag(SUPER_EXPERIENCE_APPLIANCES).add(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK.getId());
        helper.addToTag(AllCreatePonderTags.ARM_TARGETS).add(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK.getId());
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderScene.register(helper);
    }

    static class PonderScene {
        static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
            PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> entryHelper = helper.withKeyFunction(DeferredHolder::getId);
            entryHelper.forComponents(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK)
                    .addStoryBoard("classic_blaze_enchanter", Scene::basic, EXPERIENCE_APPLIANCES, SUPER_EXPERIENCE_APPLIANCES)
                    .addStoryBoard("automate", Scene::automate, AllCreatePonderTags.ARM_TARGETS);
        }
    }
}
