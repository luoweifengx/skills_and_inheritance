package luowei.fengxskillsandinter.client.renderer;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;

/**
 * 无模型实体：不参与绘制（粒子由实体端逻辑产生）。
 */
public class EmptyEntityRenderer<E extends Entity> extends EntityRenderer<E, EntityRenderState> {

    public EmptyEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}
