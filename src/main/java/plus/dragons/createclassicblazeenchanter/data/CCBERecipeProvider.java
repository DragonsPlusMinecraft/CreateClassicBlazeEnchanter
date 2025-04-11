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

package plus.dragons.createclassicblazeenchanter.data;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;
import plus.dragons.createenchantmentindustry.common.registry.CEIItems;

import static com.simibubi.create.AllBlocks.BLAZE_BURNER;
import static plus.dragons.createclassicblazeenchanter.common.CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK;
import static plus.dragons.createdragonsplus.common.registry.CDPItems.BLAZE_UPGRADE_SMITHING_TEMPLATE;

public class CCBERecipeProvider extends RecipeProvider {
    private static final String ANDESITE = "andesite";
    private static final String COPPER = "copper";
    private static final String BRASS = "brass";
    private static final String TRAIN = "train";

    public CCBERecipeProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(BLAZE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(BLAZE_BURNER),
                        Ingredient.of(CEIItems.ENCHANTING_TEMPLATE),
                        RecipeCategory.MISC,
                        CLASSIC_BLAZE_ENCHANTER_BLOCK.asItem())
                .unlocks("has_blaze_burner", has(BLAZE_BURNER))
                .save(output, CLASSIC_BLAZE_ENCHANTER_BLOCK.getId().withPrefix("smithing/"));
    }
}
