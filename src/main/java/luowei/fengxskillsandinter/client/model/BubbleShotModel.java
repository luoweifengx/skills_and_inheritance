package luowei.fengxskillsandinter.client.model;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Bubble Shot 模型（与 Blockbench 导出 BubbleShotModel2 几何一致，32×32 贴图）。
 */
public class BubbleShotModel extends EntityModel<ProjectileEntityRenderState> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "bubble_shot"), "main");

    public BubbleShotModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("bone",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F))
                        .uv(0, 7).cuboid(-3.0F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(11, 7).cuboid(2.0F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new Dilation(0.0F))
                        .uv(0, 14).cuboid(-2.0F, -1.5F, -3.0F, 4.0F, 3.0F, 1.0F, new Dilation(0.0F))
                        .uv(11, 14).cuboid(-2.0F, -1.5F, 2.0F, 4.0F, 3.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 22.5F, 0.0F, 0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState state) {
        // no animation for now
    }
}