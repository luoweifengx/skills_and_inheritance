package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.entity.NukeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.EntityRendererFactory;
import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.client.model.NukeModel;

public class NukeEntityRenderer extends BaseProjectileEntityRenderer<NukeEntity>{
    public static final Identifier TEXTURE = Identifier.of(FengxSkillsAndInheritance.MOD_ID, "textures/entity/projectile/nuke.png");

    public NukeEntityRenderer(EntityRendererFactory.Context context) {
        super(context, NukeModel.LAYER_LOCATION, TEXTURE, NukeModel::new);
    }

    @Override
    protected void applyModelOffset(MatrixStack matrices) {
        // 随仰角变化，模型在 x 轴上有对应偏移；沿用你原 Spark 的 translate。
        matrices.translate(0, -23f / 16f, -2.6667f / 16f);
    }
}
