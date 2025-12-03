package luowei.fengxskillsandinter;

import net.fabricmc.api.ClientModInitializer;
import luowei.fengxskillsandinter.client.ItemTooltipCallback;

/**
 * 客户端初始化类
 */
public class FengxSkillsAndInheritanceClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        FengxSkillsAndInheritance.LOGGER.info("Initializing Client for Fengx Skills and Inheritance");
        
        // 注册物品Tooltip
        ItemTooltipCallback.register();
        
        FengxSkillsAndInheritance.LOGGER.info("Client initialized successfully!");
    }
}

