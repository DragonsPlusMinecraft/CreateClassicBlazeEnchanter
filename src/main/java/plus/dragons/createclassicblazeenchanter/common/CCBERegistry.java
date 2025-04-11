package plus.dragons.createclassicblazeenchanter.common;

import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import plus.dragons.createclassicblazeenchanter.common.processing.enchanter.ClassicBlazeEnchanterBlock;
import plus.dragons.createclassicblazeenchanter.common.processing.enchanter.ClassicBlazeEnchanterBlockEntity;
import plus.dragons.createclassicblazeenchanter.common.processing.enchanter.ClassicBlazeEnchanterRenderer;
import plus.dragons.createdragonsplus.common.processing.blaze.BlazeBlock;
import plus.dragons.createdragonsplus.common.processing.blaze.BlazeBlockVisual;
import plus.dragons.createenchantmentindustry.common.registry.CEIBlockEntities;

import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static plus.dragons.createclassicblazeenchanter.common.CCBECommon.REGISTRATE;

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
            .visual(() -> BlazeBlockVisual::new)
            .renderer(() -> ClassicBlazeEnchanterRenderer::new)
            .validBlock(CLASSIC_BLAZE_ENCHANTER_BLOCK)
            .register();

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK,
                CLASSIC_BLAZE_ENCHANTER_BLOCKENTITY.get(), ClassicBlazeEnchanterBlockEntity::getFluidHandler);
    }

    public static void register(IEventBus modBus) {
        modBus.register(CEIBlockEntities.class);
    }
}
