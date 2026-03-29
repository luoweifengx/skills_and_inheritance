package luowei.fengxskillsandinter.client.renderer;

import java.util.function.Function;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import net.minecraft.entity.Entity;

/**
 * 投射物通用渲染基类：根据实体速度计算 yaw/pitch，并在 render 中对模型应用旋转；
 * 渲染层默认 {@link RenderLayer#getEntityCutout}，子类可覆盖 {@link #getProjectileRenderLayer}。
 */
public abstract class BaseProjectileEntityRenderer<E extends Entity>
        extends EntityRenderer<E, ProjectileEntityRenderState> {

    protected final EntityModel<ProjectileEntityRenderState> model;
    protected final Identifier texture;

    protected BaseProjectileEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer modelLayer, Identifier texture,
            Function<ModelPart, ? extends EntityModel<ProjectileEntityRenderState>> modelFactory) {
        super(context);
        this.texture = texture;
        this.model = modelFactory.apply(context.getPart(modelLayer));
    }

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }

    @Override
    public void updateRenderState(E entity, ProjectileEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);

        Vec3d v = entity.getVelocity();
        if (v.lengthSquared() > 1.0E-6) {
            double h = Math.sqrt(v.x * v.x + v.z * v.z);
            float vyaw = (float) Math.toDegrees(Math.atan2(v.x, v.z));
            float vpitch = (float) Math.toDegrees(Math.atan2(v.y, h));
            state.yaw = vyaw;
            state.pitch = vpitch;
        } else {
            state.yaw = entity.getYaw(tickProgress);
            // 有速度时 pitch 用 atan2(v.y,h)，与实体上存的 Minecraft pitch（由 SpellCastUtil：atan2(-d.y,h)）差一个符号；
            // render 里是 rotationX(-state.pitch)，这里取负才能与有速度分支视觉一致。
            state.pitch = -entity.getPitch(tickProgress);
        }
    }

    @Override
    public void render(ProjectileEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-state.pitch));
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180f));

        applyModelOffset(matrices);

        this.model.setAngles(state);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(getProjectileRenderLayer());
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }

    /**
     * 让子类决定模型额外的平移/缩放偏移（例如 Spark 的 translate）。
     */
    protected void applyModelOffset(MatrixStack matrices) {
        // default: none
    }

    /** 薄壳投射物可改为 {@link RenderLayer#getEntityCutoutNoCull}，见 {@link BubbleShotEntityRenderer}。 */
    protected RenderLayer getProjectileRenderLayer() {
        return RenderLayer.getEntityCutout(texture);
    }
}

