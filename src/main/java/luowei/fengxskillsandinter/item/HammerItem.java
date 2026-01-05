package luowei.fengxskillsandinter.item;

import net.minecraft.item.Item;

/**
 * 锤子物品类
 */
public class HammerItem extends Item {
    
    private final String hammerType;
    
    public HammerItem(String type, Settings settings) {
        super(settings);
        this.hammerType = type;
    }
    
    public String getHammerType() {
        return hammerType;
    }
}

