package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.entity.ChainsawEntity;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;

public class ChainsawEntityRenderer extends EntityRenderer<ChainsawEntity, EntityRenderState>{

    public ChainsawEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }
}
