package plus.dragons.createclassicblazeenchanter.common.processing.enchanter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import plus.dragons.createclassicblazeenchanter.common.CCBECommon;
import plus.dragons.createclassicblazeenchanter.common.CCBERegistry;
import plus.dragons.createenchantmentindustry.client.model.CEIPartialModels;

import java.util.function.Supplier;

import static plus.dragons.createclassicblazeenchanter.common.processing.enchanter.ClassicBlazeEnchanterRenderer.BOOK_MATERIAL;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = CCBECommon.ID)
public class ClassicBlazeEnchanterItemRenderer extends CustomRenderedItemModelRenderer {
    private final Supplier<BookModel> bookModelSupplier;
    private BookModel bookModel;

    public ClassicBlazeEnchanterItemRenderer() {
        this.bookModelSupplier = () -> new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @SubscribeEvent
    public static void register(RegisterClientExtensionsEvent event) {
        event.registerItem(
                SimpleCustomRenderer.create(CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK.asItem(), new ClassicBlazeEnchanterItemRenderer()),
                CCBERegistry.CLASSIC_BLAZE_ENCHANTER_BLOCK.asItem());
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        renderer.render(CEIPartialModels.BLAZE_ITEM.get(), light);
        poseStack.pushPose();
        poseStack.translate(0, -0.3, 0);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
        poseStack.scale(1.1f,1.1f,1.1f);
        VertexConsumer vertexconsumer = BOOK_MATERIAL.buffer(bufferSource, RenderType::entitySolid);
        if(bookModel == null){
            bookModel = bookModelSupplier.get();
            float page0 = Mth.frac(0.25f) * 1.6f - 0.3f;
            float page1 = Mth.frac(0.75f) * 1.6f - 0.3f;
            bookModel.setupAnim(0, Mth.clamp(page0, 0.0f, 1.0f), Mth.clamp(page1, 0.0f, 1.0f), 1);
        }
        this.bookModel.render(poseStack, vertexconsumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();
    }
}
