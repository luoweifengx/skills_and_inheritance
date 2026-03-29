package luowei.fengxskillsandinter.client.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;

/**
 * Legacy Blockbench 模型草稿，保留用于坐标参考。
 */
public class liuming extends EntityModel<ProjectileEntityRenderState> {
    private final ModelPart bone;

    public liuming(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(
                "bone",
                ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -0.5F, 1.5F, 1.0F, 1.0F, 15.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, 23.5F, -8.5F, 0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState state) {
        // no animation for now
    }
}