package luowei.fengxskillsandinter.villager;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import luowei.fengxskillsandinter.item.ModItems;

/**
 * 铁匠村民交易配置类
 * 配置盔甲匠（改名为铁匠）的交易内容
 */
public class BlacksmithTrades {
    
    /**
     * 注册铁匠村民的交易
     */
    public static void registerTrades() {
        // 初级交易（等级1）
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 1, factories -> {
            // 1个新生诞礼 -> 1个木锤
            factories.add((entity, random) -> new TradeOffer(
                new TradedItem(ModItems.BIRTH_GIFT, 1),
                new ItemStack(ModItems.WOODEN_HAMMER, 1),
                12,  // 最大使用次数
                2,   // 经验奖励
                0.05f // 价格倍数
            ));
            
            // 可以添加更多初级交易
            // 例如：售卖新生诞礼
            factories.add((entity, random) -> new TradeOffer(
                new TradedItem(Items.EMERALD, 5),
                new ItemStack(ModItems.BIRTH_GIFT, 1),
                16,
                1,
                0.05f
            ));
        });
        
        // 中级交易（等级2）
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 2, factories -> {
            // 2个新生诞礼 -> 1个铁锤
            factories.add((entity, random) -> new TradeOffer(
                new TradedItem(ModItems.BIRTH_GIFT, 2),
                new ItemStack(ModItems.IRON_HAMMER, 1),
                8,
                5,
                0.05f
            ));
        });
        
        // 高级交易（等级3）
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.ARMORER, 3, factories -> {
            // 3个新生诞礼 -> 1个钻石锤
            factories.add((entity, random) -> new TradeOffer(
                new TradedItem(ModItems.BIRTH_GIFT, 3),
                new ItemStack(ModItems.DIAMOND_HAMMER, 1),
                6,
                10,
                0.05f
            ));
            
            // 铁匠经验之书
            factories.add((entity, random) -> new TradeOffer(
                new TradedItem(Items.EMERALD, 10),
                new ItemStack(ModItems.BLACKSMITH_EXPERIENCE_BOOK, 1),
                12,
                15,
                0.05f
            ));
        });
    }
}

