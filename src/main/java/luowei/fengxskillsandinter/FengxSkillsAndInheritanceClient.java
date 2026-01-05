package luowei.fengxskillsandinter;

import net.fabricmc.api.ClientModInitializer;
import luowei.fengxskillsandinter.client.ItemTooltipCallback;
import luowei.fengxskillsandinter.client.ResourceLoadDebug;

/**
 * 客户端初始化类
 */
public class FengxSkillsAndInheritanceClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        FengxSkillsAndInheritance.LOGGER.info("Initializing Client for Fengx Skills and Inheritance");
        
        // 注册资源加载调试
        ResourceLoadDebug.register();
        
        // 注册物品Tooltip
        ItemTooltipCallback.register();
        
        // HUD渲染通过Mixin实现（InGameHudMixin）
        
        FengxSkillsAndInheritance.LOGGER.info("Client initialized successfully!");
    }
}

