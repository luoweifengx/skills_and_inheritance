// Made with Blockbench 5.0.7
// Adapted for Minecraft 1.21.5 Fabric (Yarn mappings)

package luowei.fengxskillsandinter.client.model;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.Identifier;

/**
 * Blockbench 导出的火花投射物模型
 */
public class SparkProjectileModel extends EntityModel<ProjectileEntityRenderState> {

    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "spark_projectile"), "main");

    public SparkProjectileModel(ModelPart root) {
        super(root);
    }

    /**
     * 供 EntityModelLayerRegistry 使用的 TexturedModelData 提供方法
     */
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        var root = modelData.getRoot();
        root.addChild("bone",
                ModelPartBuilder.create()
						.uv(0, 0).cuboid(-1.0F, -1.0F, -10.6667F, 2.0F, 2.0F, 16.0F, Dilation.NONE)
                        .uv(20, 18).cuboid(-1.0F, -4.0F, 0.3333F, 2.0F, 8.0F, 2.0F, Dilation.NONE)
                        .uv(0, 18).cuboid(-4.0F, -1.0F, 0.3333F, 8.0F, 2.0F, 2.0F, Dilation.NONE),
                ModelTransform.of(0.0F, 23.0F, 2.6667F, 0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(ProjectileEntityRenderState state) {
        // 投射物模型通常不需要动画，保持默认姿态
    }
}
