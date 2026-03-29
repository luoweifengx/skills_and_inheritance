package luowei.fengxskillsandinter;

import luowei.fengxskillsandinter.client.input.CastKey;
import luowei.fengxskillsandinter.client.ItemTooltipCallback;
import luowei.fengxskillsandinter.client.hud.SpellHudRenderer;
import luowei.fengxskillsandinter.client.model.BubbleShotModel;
import luowei.fengxskillsandinter.client.model.DiscBulletBigModel;
import luowei.fengxskillsandinter.client.model.LuminousDrillModel;
import luowei.fengxskillsandinter.client.model.NukeModel;
import luowei.fengxskillsandinter.client.model.SparkProjectileModel;
import luowei.fengxskillsandinter.client.renderer.RendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;

/**
 * 客户端初始化类
 */
public class FengxSkillsAndInheritanceClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        FengxSkillsAndInheritance.LOGGER.info("Initializing Client for Fengx Skills and Inheritance");

        // 注册火花投射物模型层
        EntityModelLayerRegistry.registerModelLayer(SparkProjectileModel.LAYER_LOCATION, SparkProjectileModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(DiscBulletBigModel.LAYER_LOCATION, DiscBulletBigModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(BubbleShotModel.LAYER_LOCATION, BubbleShotModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(LuminousDrillModel.LAYER_LOCATION, LuminousDrillModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(NukeModel.LAYER_LOCATION, NukeModel::getTexturedModelData);

        //注册实体渲染器
        RendererRegistry.registerRenderers();
        ItemTooltipCallback.register();
        
        // HUD渲染
        
        FengxSkillsAndInheritance.LOGGER.info("Client initialized successfully!");

        CastKey.register();
        SpellHudRenderer.register();
    }
}

