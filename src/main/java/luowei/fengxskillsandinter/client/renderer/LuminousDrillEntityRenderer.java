package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.client.model.LuminousDrillModel;
import luowei.fengxskillsandinter.entity.LuminousDrillEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * 光钻：速度为 0 时由实体 yaw/pitch（与 {@link SpellCastUtil#applyFacingFromDirection} 一致）驱动朝向，
 * 见 {@link BaseProjectileEntityRenderer#updateRenderState}。
 */
public class LuminousDrillEntityRenderer extends BaseProjectileEntityRenderer<LuminousDrillEntity> {

    public static final Identifier TEXTURE =
            Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/projectile/luminous_drill.png");

    public LuminousDrillEntityRenderer(EntityRendererFactory.Context context) {
        super(context, LuminousDrillModel.LAYER_LOCATION, TEXTURE, LuminousDrillModel::new);
    }

    @Override
    protected void applyModelOffset(MatrixStack matrices) {
        // 与 LuminousDrillModel 根 pivot (0.5, 23.5, -8.5) 对齐：取负再 /16
        matrices.translate(-0.5f / 16.0f, -23.5f / 16.0f, 8.5f / 16.0f);
    }
}
