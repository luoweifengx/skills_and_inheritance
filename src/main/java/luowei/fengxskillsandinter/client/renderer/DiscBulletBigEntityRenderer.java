package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.client.model.DiscBulletBigModel;
import luowei.fengxskillsandinter.entity.DiscBulletBigEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

/**
 * 锯片投射物：绕飞行方向（局部 Z）逆时针旋转，并做与模型 pivot 对齐的平移。
 */
public class DiscBulletBigEntityRenderer extends EntityRenderer<DiscBulletBigEntity, DiscBulletBigRenderState> {

    public static final Identifier TEXTURE =
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/projectile/disc_bullet_big.png");

    private static final float ROLL_DEGREES_PER_TICK = 50.0f;

    private final EntityModel<ProjectileEntityRenderState> model;

    public DiscBulletBigEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new DiscBulletBigModel(context.getPart(DiscBulletBigModel.LAYER_LOCATION));
    }

    @Override
    public DiscBulletBigRenderState createRenderState() {
        return new DiscBulletBigRenderState();
    }

    @Override
    public void updateRenderState(DiscBulletBigEntity entity, DiscBulletBigRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        Vec3d v = entity.getVelocity();
        if (v.lengthSquared() > 1.0E-6) {
            double h = Math.sqrt(v.x * v.x + v.z * v.z);
            state.yaw = (float) Math.toDegrees(Math.atan2(v.x, v.z));
            state.pitch = (float) Math.toDegrees(Math.atan2(v.y, h));
        }
        state.rollDegrees = (entity.age + tickProgress) * ROLL_DEGREES_PER_TICK;
    }

    @Override
    public void render(DiscBulletBigRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-state.pitch));
        // 与飞行方向一致后，绕 Z 轴旋转（圆盘在局部 XY 平面内转动）
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.rollDegrees));
        matrices.translate(0.0F, -24.0F / 16.0F, 0);

        this.model.setAngles(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
