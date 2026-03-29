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

public class LuminousDrillModel extends EntityModel<ProjectileEntityRenderState> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "luminous_drill"),
            "main"
    );

    private final ModelPart bone;

    public LuminousDrillModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild("bone",
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-0.5F, -0.5F, 1.5F, 1.0F, 1.0F, 15.0F, new Dilation(0.0F)),
                ModelTransform.of(0.5F, 23.5F, -8.5F, 0.0F, 0.0F, 0.0F)
        );

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState state) {
        // no animation for now
    }
}