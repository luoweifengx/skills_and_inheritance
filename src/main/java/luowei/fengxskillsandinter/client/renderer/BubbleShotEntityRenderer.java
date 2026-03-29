package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.client.model.BubbleShotModel;
import luowei.fengxskillsandinter.entity.BubbleShotEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * 泡泡弹实体渲染：{@code textures/entity/projectile/bubble_shot.png}（32×32，与模型 UV 一致）。
 */
public class BubbleShotEntityRenderer extends BaseProjectileEntityRenderer<BubbleShotEntity> {

    public static final Identifier TEXTURE =
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/projectile/bubbleshot.png");

    public BubbleShotEntityRenderer(EntityRendererFactory.Context context) {
        super(context, BubbleShotModel.LAYER_LOCATION, TEXTURE, BubbleShotModel::new);
    }

    @Override
    protected void applyModelOffset(MatrixStack matrices) {
        // 与 BubbleShotModel 根节点 ModelTransform.pivot(0, 22.5, 0) 对齐
        matrices.translate(0.0F, -22.5F / 16.0F, 0.0F);
    }

    @Override
    protected RenderLayer getProjectileRenderLayer() {
        return RenderLayer.getEntityCutoutNoCull(TEXTURE);
    }
}
