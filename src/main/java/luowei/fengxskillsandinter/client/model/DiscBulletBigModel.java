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
 * Disc Bullet Big 占位模型（当前版本兼容）。
 */
public class DiscBulletBigModel extends EntityModel<ProjectileEntityRenderState> {
    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "disc_bullet_big"), "main");

    //private final ModelPart bone;

    public DiscBulletBigModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("bone",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-9.65F, -2.0F, -23.75F, 20.0F, 2.0F, 20.0F, new Dilation(0.0F))
                        .uv(126, 47).cuboid(-3.95F, -2.0F, -3.74F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(50, 29).cuboid(-6.0F, -2.0F, -1.0F, 7.0F, 2.0F, 2.0F, new Dilation(0.0F))
                        .uv(32, 57).cuboid(7.25F, -2.0F, -3.75F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(0, 59).cuboid(-12.7F, -2.0F, -6.75F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(45, 60).cuboid(-9.6F, -2.0F, -26.75F, 3.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(48, 23).cuboid(-4.0F, -2.0F, -26.65F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(46, 46).cuboid(-1.0F, -2.0F, -29.65F, 8.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(0, 53).cuboid(10.2F, -2.0F, -23.75F, 5.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(23, 35).cuboid(10.2F, -2.0F, -17.75F, 3.0F, 2.0F, 8.0F, new Dilation(0.0F))
                        .uv(46, 35).cuboid(-12.7F, -2.0F, -17.75F, 3.0F, 2.0F, 8.0F, new Dilation(0.0F))
                        .uv(23, 46).cuboid(13.0F, -2.0F, -15.75F, 3.0F, 2.0F, 8.0F, new Dilation(0.0F))
                        .uv(0, 23).cuboid(-15.7F, -2.0F, -20.05F, 3.0F, 2.0F, 9.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 24.0F, 14.75F, 0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState state) {
        // no animation for now
    }
}