package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.client.model.SparkProjectileModel;
import luowei.fengxskillsandinter.entity.SparkProjectileEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * 火花投射物实体渲染器，使用 Blockbench 导出的 SparkProjectileModel
 */
public class SparkProjectileEntityRenderer
        extends BaseProjectileEntityRenderer<SparkProjectileEntity> {

    public static final Identifier TEXTURE = Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/projectile/spark_projectile.png");

    public SparkProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context, SparkProjectileModel.LAYER_LOCATION, TEXTURE, SparkProjectileModel::new);
    }

    @Override
    protected void applyModelOffset(MatrixStack matrices) {
        // 随仰角变化，模型在 x 轴上有对应偏移；沿用你原 Spark 的 translate。
        matrices.translate(0, -23f / 16f, -2.6667f / 16f);
    }
}
//随着仰角的改变，模型在x轴上的渲染会存在对应偏移
// matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));
// matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(state.pitch));