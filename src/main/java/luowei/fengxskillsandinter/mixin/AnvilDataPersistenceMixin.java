package luowei.fengxskillsandinter.mixin;

import luowei.fengxskillsandinter.FengxSkillsAndInheritance;
import luowei.fengxskillsandinter.block.AnvilForgeHandler;
import luowei.fengxskillsandinter.block.AnvilWorldData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class AnvilDataPersistenceMixin {

    /**
     * 世界加载时读取铁砧数据
     */
    @Inject(at = @At("TAIL"), method = "loadWorld")
    private void onWorldLoad(CallbackInfo info) {
        MinecraftServer server = (MinecraftServer) (Object) this;

        // 等待世界完全加载后读取数据
        server.execute(() -> {
            for (ServerWorld world : server.getWorlds()) {
                try {
                    AnvilWorldData.loadAnvilData(world);
                    FengxSkillsAndInheritance.LOGGER.info("Loaded anvil data for world: " + world.getRegistryKey().getValue());
                } catch (Exception e) {
                    FengxSkillsAndInheritance.LOGGER.error("Failed to load anvil data for world: " + world.getRegistryKey().getValue(), e);
                }
            }
        });
    }

    /**
     * 世界保存时保存铁砧数据
     */
    @Inject(at = @At("HEAD"), method = "save")
    private void onWorldSave(CallbackInfo info) {
        MinecraftServer server = (MinecraftServer) (Object) this;

        // 保存所有世界的数据
        for (ServerWorld world : server.getWorlds()) {
            try {
                AnvilWorldData.saveAnvilData(world);
            } catch (Exception e) {
                FengxSkillsAndInheritance.LOGGER.error("Failed to save anvil data for world: " + world.getRegistryKey().getValue(), e);
            }
        }
    }

    /**
     * 服务器关闭时保存数据
     */
    @Inject(at = @At("HEAD"), method = "shutdown")
    private void onServerShutdown(CallbackInfo info) {
        MinecraftServer server = (MinecraftServer) (Object) this;

        // 最后一次保存数据
        for (ServerWorld world : server.getWorlds()) {
            try {
                AnvilWorldData.saveAnvilData(world);
                FengxSkillsAndInheritance.LOGGER.info("Saved anvil data on shutdown for world: " + world.getRegistryKey().getValue());
            } catch (Exception e) {
                FengxSkillsAndInheritance.LOGGER.error("Failed to save anvil data on shutdown for world: " + world.getRegistryKey().getValue(), e);
            }
        }
    }
}