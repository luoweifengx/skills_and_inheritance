package luowei.fengxskillsandinter.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import luowei.fengxskillsandinter.item.SpellItem;
import luowei.fengxskillsandinter.item.WandItem;
import luowei.fengxskillsandinter.spell.SpellRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;

public class WandScreenHandler extends ScreenHandler{
    
    private final Inventory inventory;
    private final ItemStack wandStack;
    private final Hand hand;
    /** 打开时写入的UUID，用于关闭时查找法杖（防止移动法杖导致刷物品） */
    private final String openId;

    public WandScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack wandStack, Hand hand, PlayerEntity player){
        super(ScreenHandlerType.GENERIC_9X1, syncId);//设置了ui为9x1的格子
        int capacity = WandItem.getCapacity(wandStack);
        this.inventory = new SimpleInventory(Math.max(capacity, 9));
        this.wandStack = wandStack;
        this.hand = hand;

        // 仅服务端：写入UUID以便关闭时追踪法杖
        if (!player.getWorld().isClient()) {
            this.openId = UUID.randomUUID().toString();
            WandItem.setWandUuid(wandStack, this.openId);
        } else {
            this.openId = null;
        }

        // 添加 inventory slots（法术槽位）- 这是关键！
        for (int i = 0; i < this.inventory.size(); i++) {
            this.addSlot(new SpellSlot(this.inventory, i, 8 + i * 18, 20));
        }
        
        // 添加玩家物品栏 slots（可选，如果需要玩家可以从物品栏拖拽法术）
        // 玩家物品栏（3行9列）
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 51 + i * 18));
            }
        }
        // 玩家快捷栏
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 109));
        }
        loadSpellIntoInventory(wandStack);
    }
    //加载
    private void loadSpellIntoInventory(ItemStack wandStack){
        List<String> spells = WandItem.getSpells(wandStack);//返回一个列表，存储着法术序列
        for(int i = 0; i < inventory.size(); i++){//可能会越界，如果完全取相同值则不会有此问题
            String spellId = spells.get(i);
            
            if(spellId != null && !spellId.isEmpty()){
                Item spellItem = SpellRegistry.getItem(spellId);
                if(spellItem != null){
                    this.inventory.setStack(i, new ItemStack(spellItem));
                }
                else{
                    this.inventory.setStack(i, ItemStack.EMPTY);
                }
            }
            //ItemStack spellItem = this.inventory.getStack(i);
            //spellItem.setCustomName(Text.literal(spells.get(i)));
            //this.inventory.setStack(i, spellItem);
        }
    }
    /** 将法术槽位数据保存到指定法杖（支持UUID查找，防止移动法杖刷物品） */
    private void saveSpellsToWand(ItemStack targetWand){
        List<String> spells = new ArrayList<>();
        for(int i = 0; i < inventory.size(); i++){
            ItemStack stack = inventory.getStack(i);
            if(!stack.isEmpty() && stack.getItem() instanceof SpellItem spellItem){
                spells.add(i, spellItem.getSpellId());
            }
            else{
                spells.add(i, "");
            }
        }
        WandItem.setSpells(targetWand, spells);
    }

    /** 在玩家背包中查找带有指定UUID的法杖 */
    private ItemStack findWandByUuid(PlayerEntity player, String uuid){
        for(int i = 0; i < player.getInventory().size(); i++){
            ItemStack stack = player.getInventory().getStack(i);
            if(WandItem.matchesWandUuid(stack, uuid)){
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onClosed(PlayerEntity player){
        super.onClosed(player);
        // 仅服务端：通过UUID查找法杖并保存，防止移动法杖导致刷物品
        if(openId != null && !player.getWorld().isClient()){
            ItemStack target = findWandByUuid(player, openId);
            if(!target.isEmpty()){
                saveSpellsToWand(target);
            }
        }
    }
    @Override
    public boolean canUse(PlayerEntity player){
        return true;
    }
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot){
        return ItemStack.EMPTY;
    }
}
