package luowei.fengxskillsandinter.client.renderer;

import luowei.fengxskillsandinter.entity.ModEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class RendererRegistry {
    public static void registerRenderers() {
        EntityRendererRegistry.register(ModEntities.CHAINSAW, ChainsawEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.SPARK_PROJECTILE, SparkProjectileEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.DISC_BULLET_BIG, DiscBulletBigEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.BUBBLE_SHOT, BubbleShotEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.LUMINOUS_DRILL, LuminousDrillEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.NUKE, NukeEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.TELEPORT_PROJECTILE, ctx -> new EmptyEntityRenderer<>(ctx));
        EntityRendererRegistry.register(ModEntities.REGENERATION_FIELD_ENTITY, ctx -> new EmptyEntityRenderer<>(ctx));
        EntityRendererRegistry.register(ModEntities.BLACK_HOLE_ENTITY, ctx -> new EmptyEntityRenderer<>(ctx));
    }
}
