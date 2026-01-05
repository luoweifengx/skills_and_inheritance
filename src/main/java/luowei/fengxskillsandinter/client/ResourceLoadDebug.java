package luowei.fengxskillsandinter.client;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import luowei.fengxskillsandinter.FengxSkillsAndInheritance;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 资源加载调试类
 * 用于检查材质和模型是否正确加载
 */
public class ResourceLoadDebug {
    
    public static void register() {
        ResourceManagerHelper.get(net.minecraft.resource.ResourceType.CLIENT_RESOURCES)
            .registerReloadListener(new SimpleResourceReloadListener<Void>() {
                @Override
                public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor) {
                    return CompletableFuture.runAsync(() -> {
                        // 检查材质文件是否存在
                        Identifier ironHammerTexture = Identifier.of(
                            FengxSkillsAndInheritance.MOD_ID, 
                            "textures/item/iron_hammer.png"
                        );
                        Identifier woodenHammerTexture = Identifier.of(
                            FengxSkillsAndInheritance.MOD_ID, 
                            "textures/item/wooden_hammer.png"
                        );
                        Identifier diamondHammerTexture = Identifier.of(
                            FengxSkillsAndInheritance.MOD_ID, 
                            "textures/item/diamond_hammer.png"
                        );
                        
                        // 检查模型文件是否存在
                        Identifier ironHammerModel = Identifier.of(
                            FengxSkillsAndInheritance.MOD_ID, 
                            "models/item/iron_hammer.json"
                        );
                        Identifier woodenHammerModel = Identifier.of(
                            FengxSkillsAndInheritance.MOD_ID, 
                            "models/item/wooden_hammer.json"
                        );
                        Identifier diamondHammerModel = Identifier.of(
                            FengxSkillsAndInheritance.MOD_ID, 
                            "models/item/diamond_hammer.json"
                        );
                        
                        // 检查材质
                        if (manager.getResource(ironHammerTexture).isPresent()) {
                            FengxSkillsAndInheritance.LOGGER.info("✓ 找到材质: {}", ironHammerTexture);
                        } else {
                            FengxSkillsAndInheritance.LOGGER.error("✗ 未找到材质: {}", ironHammerTexture);
                        }
                        
                        if (manager.getResource(woodenHammerTexture).isPresent()) {
                            FengxSkillsAndInheritance.LOGGER.info("✓ 找到材质: {}", woodenHammerTexture);
                        } else {
                            FengxSkillsAndInheritance.LOGGER.error("✗ 未找到材质: {}", woodenHammerTexture);
                        }
                        
                        if (manager.getResource(diamondHammerTexture).isPresent()) {
                            FengxSkillsAndInheritance.LOGGER.info("✓ 找到材质: {}", diamondHammerTexture);
                        } else {
                            FengxSkillsAndInheritance.LOGGER.error("✗ 未找到材质: {}", diamondHammerTexture);
                        }
                        
                        // 检查模型
                        if (manager.getResource(ironHammerModel).isPresent()) {
                            FengxSkillsAndInheritance.LOGGER.info("✓ 找到模型: {}", ironHammerModel);
                        } else {
                            FengxSkillsAndInheritance.LOGGER.error("✗ 未找到模型: {}", ironHammerModel);
                        }
                        
                        if (manager.getResource(woodenHammerModel).isPresent()) {
                            FengxSkillsAndInheritance.LOGGER.info("✓ 找到模型: {}", woodenHammerModel);
                        } else {
                            FengxSkillsAndInheritance.LOGGER.error("✗ 未找到模型: {}", woodenHammerModel);
                        }
                        
                        if (manager.getResource(diamondHammerModel).isPresent()) {
                            FengxSkillsAndInheritance.LOGGER.info("✓ 找到模型: {}", diamondHammerModel);
                        } else {
                            FengxSkillsAndInheritance.LOGGER.error("✗ 未找到模型: {}", diamondHammerModel);
                        }
                    }, executor);
                }
                
                @Override
                public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor) {
                    return CompletableFuture.completedFuture(null);
                }
                
                @Override
                public Identifier getFabricId() {
                    return Identifier.of(FengxSkillsAndInheritance.MOD_ID, "resource_debug");
                }
            });
    }
}




