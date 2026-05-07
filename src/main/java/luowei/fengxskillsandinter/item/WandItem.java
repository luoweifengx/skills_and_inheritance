package luowei.fengxskillsandinter.item;

import java.util.ArrayList;
import java.util.List;

import luowei.fengxskillsandinter.screen.WandScreenHandler;
import luowei.fengxskillsandinter.util.ItemDataHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WandItem extends Item {
    //public static final String WAND_ID = "wand_id";
    public static final String DRAW_COUNT = "draw_count";
    public static final String CAPACITY = "capacity";
    public static final String CASTING_DELAY = "casting_delay";
    public static final String CASTING_DELAY_ENDS_AT = "casting_delay_ends_at";
    public static final String RECHARGE_DELAY = "recharge_delay";
    public static final String CURRENT_RECHARGE_DELAY = "current_recharge_delay";//用于中间的充能值计算
    public static final String CURRENT_CASTING_DELAY = "current_casting_delay";
    public static final String RECHARGE_DELAY_ENDS_AT = "recharge_delay_ends_at";
    // public static final String ON_CASTING_DELAY = "on_casting_delay";
    // public static final String ON_RECHARGE_DELAY = "on_recharge_delay";
    public static final String MAXINUM_MANA = "maxinum_mana";
    public static final String CURRENT_MANA = "current_mana";
    public static final String MANA_CHARGE_SPEED = "mana_charge_speed";

    //为防止移动槽位刷取物品的uuid设置
    public static final String WAND_UUID = "wand_uuid";

    public static final String SPELLS = "spells";
    public static final String SPELL_CACHE_POINTER = "spell_cache_pointer";
    
    private static final int DEFAULT_DRAW_COUNT = 1;
    private static final int DEFAULT_CAPACITY = 9;    
    private static final double DEFAULT_CASTING_DELAY = 0.7;
    private static final long DEFAULT_CASTING_DELAY_ENDS_AT = 0;
    private static final double DEFAULT_RECHARGE_DELAY = 1.0;
    private static final double DEFAULT_CURRENT_CASTING_DELAY = DEFAULT_CASTING_DELAY;
    private static final long DEFAULT_RECHARGE_DELAY_ENDS_AT = 0;
    private static final double DEFAULT_MAXINUM_MANA = 200;
    private static final double DEFAULT_CURRENT_MANA = DEFAULT_MAXINUM_MANA;
    private static final double DEFAULT_MANA_CHARGE_SPEED = 30;
    private static final int DEFAULT_SPELL_CACHE_POINTER = 0;

    // public static final boolean DEFAULT_ON_CASTING_DELAY = false;
    // public static final boolean DEFAULT_ON_RECHARGE_DELAY = false;
    //private int CAPACITY;

    public WandItem(Settings settings) {
        super(settings);
    }
    //初始化
    public static void init(ItemStack stack){
        // setOnCastingDelay(stack, DEFAULT_ON_CASTING_DELAY);
        // setOnRechargeDelay(stack, DEFAULT_ON_RECHARGE_DELAY);
        setCapacity(stack, DEFAULT_CAPACITY);
        setCastingDelay(stack, DEFAULT_CASTING_DELAY);
        setCurrentCastingDelay(stack, DEFAULT_CURRENT_CASTING_DELAY);
        setRechargeDelay(stack, DEFAULT_RECHARGE_DELAY);
        setSpells(stack, new ArrayList<>());
        setSpellCachePointer(stack, DEFAULT_SPELL_CACHE_POINTER);
        setDrawCount(stack, DEFAULT_DRAW_COUNT);
    }
    public static void setWand(ItemStack stack, List<Double> attributes){
        // setOnCastingDelay(stack, DEFAULT_ON_CASTING_DELAY);
        // setOnRechargeDelay(stack, DEFAULT_ON_RECHARGE_DELAY);
        setCapacity(stack, attributes.get(0).intValue());
        setCastingDelay(stack, attributes.get(1));
        setRechargeDelay(stack, attributes.get(2));
        setManaChargeSpeed(stack, attributes.get(3).doubleValue());
        //setSpells(stack, new ArrayList<>());
        setMaxinumMana(stack, attributes.get(4).doubleValue());
        setDrawCount(stack, attributes.get(5).intValue());
    }
    // //法杖id
    // public static void setWandId(ItemStack stack){
    //     if(!ItemDataHelper.contains(stack, WAND_ID)){
    //         String id = UUID.randomUUID().toString();
    //         ItemDataHelper.setString(stack, WAND_ID, id);
    //     }
        
    // }
    // public static String getWandId(ItemStack stack){
    //     setWandId(stack);
    //     return ItemDataHelper.getString(stack, WAND_ID);
    // }

    // //是否释放延迟
    // public static void setOnRechargeDelay(ItemStack stack, boolean onRechargeDelay){
    //     ItemDataHelper.setBoolean(stack, ON_RECHARGE_DELAY, onRechargeDelay);
    // }
    // public static boolean getOnRechargeDelay(ItemStack stack){
    //     if(!ItemDataHelper.contains(stack, ON_RECHARGE_DELAY)){
    //         setOnRechargeDelay(stack, DEFAULT_ON_RECHARGE_DELAY);
    //     }
    //     return ItemDataHelper.getBoolean(stack, ON_RECHARGE_DELAY);
    // }

    //充能延迟结束时间
    public static void setRechargeDelayEndsAt(ItemStack stack, long rechargeDelayEndsAt){
        ItemDataHelper.setLong(stack, RECHARGE_DELAY_ENDS_AT, rechargeDelayEndsAt);
    }
    public static long getRechargeDelayEndsAt(ItemStack stack){
        if(!ItemDataHelper.contains(stack, RECHARGE_DELAY_ENDS_AT)){
            setRechargeDelayEndsAt(stack, DEFAULT_RECHARGE_DELAY_ENDS_AT);
        }
        return ItemDataHelper.getLong(stack, RECHARGE_DELAY_ENDS_AT);
    }

    // //是否充能延迟
    // public static void setOnCastingDelay(ItemStack stack, boolean onCastingDelay){
    //     ItemDataHelper.setBoolean(stack, ON_CASTING_DELAY, onCastingDelay);
    // }
    // public static boolean getOnCastingDelay(ItemStack stack){
    //     if(!ItemDataHelper.contains(stack, ON_CASTING_DELAY)){
    //         setOnCastingDelay(stack, DEFAULT_ON_CASTING_DELAY);
    //     }
    //     return ItemDataHelper.getBoolean(stack, ON_CASTING_DELAY);
    // }

    //释放延迟结束时间
    public static void setCastingDelayEndsAt(ItemStack stack, long castingDelayEndsAt){
        ItemDataHelper.setLong(stack, CASTING_DELAY_ENDS_AT, castingDelayEndsAt);
    }
    public static long getCastingDelayEndsAt(ItemStack stack){
        if(!ItemDataHelper.contains(stack, CASTING_DELAY_ENDS_AT)){
            setCastingDelayEndsAt(stack, DEFAULT_CASTING_DELAY_ENDS_AT);
        }
        return ItemDataHelper.getLong(stack, CASTING_DELAY_ENDS_AT);
    }
    
    //遍历点数
    public static void setDrawCount(ItemStack stack, int drawCount){
        ItemDataHelper.setInt(stack, DRAW_COUNT, drawCount);
    }
    public static int getDrawCount(ItemStack stack){
        if(!ItemDataHelper.contains(stack, DRAW_COUNT)){
            setDrawCount(stack, DEFAULT_DRAW_COUNT);
        }
        return ItemDataHelper.getInt(stack, DRAW_COUNT);
    }

    //容量
    public static void setCapacity(ItemStack stack, int capacity){        
        ItemDataHelper.setInt(stack, CAPACITY, capacity);
    }
    public static int getCapacity(ItemStack stack){
        if(!ItemDataHelper.contains(stack, CAPACITY)){
            setCapacity(stack, DEFAULT_CAPACITY);
        }
        return ItemDataHelper.getInt(stack, CAPACITY);
    }

    //释放延迟
    public static void setRechargeDelay(ItemStack stack, double rechargeDelay){
        ItemDataHelper.setDouble(stack, RECHARGE_DELAY, rechargeDelay);
    }
    public static double getRechargeDelay(ItemStack stack){
        if(!ItemDataHelper.contains(stack, RECHARGE_DELAY)){
            setRechargeDelay(stack, DEFAULT_RECHARGE_DELAY);
        }
        return ItemDataHelper.getDouble(stack, RECHARGE_DELAY);
    }

    //充能延迟
    public static void setCastingDelay(ItemStack stack, double castingDelay){
        ItemDataHelper.setDouble(stack, CASTING_DELAY, castingDelay);
    }
    public static double getCastingDelay(ItemStack stack){
        if(!ItemDataHelper.contains(stack, CASTING_DELAY)){
            setCastingDelay(stack, DEFAULT_CASTING_DELAY);
        }
        return ItemDataHelper.getDouble(stack, CASTING_DELAY);
    }

    //缓存的充能延迟
    public static void setCurrentRechargeDelay(ItemStack stack, double currentRechargeDelay){
        ItemDataHelper.setDouble(stack, CURRENT_RECHARGE_DELAY, currentRechargeDelay);
    }
    public static double getCurrentRechargeDelay(ItemStack stack){
        if(!ItemDataHelper.contains(stack, CURRENT_RECHARGE_DELAY)){
            setCurrentRechargeDelay(stack, DEFAULT_RECHARGE_DELAY);
        }
        return ItemDataHelper.getDouble(stack, CURRENT_RECHARGE_DELAY);
    }

    //缓存的施法延迟（用于 HUD 进度条基准）
    public static void setCurrentCastingDelay(ItemStack stack, double currentCastingDelay){
        ItemDataHelper.setDouble(stack, CURRENT_CASTING_DELAY, currentCastingDelay);
    }
    public static double getCurrentCastingDelay(ItemStack stack){
        if(!ItemDataHelper.contains(stack, CURRENT_CASTING_DELAY)){
            setCurrentCastingDelay(stack, DEFAULT_CURRENT_CASTING_DELAY);
        }
        return ItemDataHelper.getDouble(stack, CURRENT_CASTING_DELAY);
    }

    //法术序列
    public static void setSpells(ItemStack stack, List<String> spells){
        //添加到nbt数据
        int capacity = getCapacity(stack);
        int previousSize = spells.size();
        List<String> newSpells = new ArrayList<>();
        for(int i = 0; i < capacity; i++){
            if(i < previousSize){
                newSpells.add(spells.get(i));
            }
            else{
                newSpells.add("");
            }
        }
        ItemDataHelper.setStringList(stack, SPELLS, newSpells);
        ItemDataHelper.setInt(stack, SPELL_CACHE_POINTER, DEFAULT_SPELL_CACHE_POINTER);
    }
    public static List<String> getSpells(ItemStack stack){
        if(!ItemDataHelper.contains(stack, SPELLS)){
            setSpells(stack, new ArrayList<>());
        }
        return ItemDataHelper.getStringList(stack, SPELLS);
    }

    //最大魔力
    public static void setMaxinumMana(ItemStack stack, double maxinumMana){
        ItemDataHelper.setDouble(stack, MAXINUM_MANA, maxinumMana);
    }
    public static double getMaxinumMana(ItemStack stack){
        if(!ItemDataHelper.contains(stack, MAXINUM_MANA)){
            setMaxinumMana(stack, DEFAULT_MAXINUM_MANA);
        }
        return ItemDataHelper.getDouble(stack, MAXINUM_MANA);
    }
    
    //魔力充能速度
    public static void setManaChargeSpeed(ItemStack stack, double manaChargeSpeed){
        ItemDataHelper.setDouble(stack, MANA_CHARGE_SPEED, manaChargeSpeed);
    }
    public static double getManaChargeSpeed(ItemStack stack){
        if(!ItemDataHelper.contains(stack, MANA_CHARGE_SPEED)){
            setManaChargeSpeed(stack, DEFAULT_MANA_CHARGE_SPEED);
        }
        return ItemDataHelper.getDouble(stack, MANA_CHARGE_SPEED);
    }

    //当前魔力
    public static void setCurrentMana(ItemStack stack, double currentMana){
        ItemDataHelper.setDouble(stack, CURRENT_MANA, currentMana);
    }
    public static double getCurrentMana(ItemStack stack){
        if(!ItemDataHelper.contains(stack, CURRENT_MANA)){
            setCurrentMana(stack, DEFAULT_CURRENT_MANA);
        }
        return ItemDataHelper.getDouble(stack, CURRENT_MANA);
    }
    
    //断点
    public static void setSpellCachePointer(ItemStack stack, int spellCachePointer){
        ItemDataHelper.setInt(stack, SPELL_CACHE_POINTER, spellCachePointer);
    }
    public static int getSpellCachePointer(ItemStack stack){
        if(!ItemDataHelper.contains(stack, SPELL_CACHE_POINTER)){
            setSpellCachePointer(stack, DEFAULT_SPELL_CACHE_POINTER);
        }
        return ItemDataHelper.getInt(stack, SPELL_CACHE_POINTER);
    }

    //法杖uuid（用于追踪移动后的法杖，防止刷物品）
    public static void setWandUuid(ItemStack stack, String wandUuid){
        ItemDataHelper.setString(stack, WAND_UUID, wandUuid);
    }
    /** 检查法杖是否带有指定UUID（用于查找，不会自动创建） */
    public static boolean matchesWandUuid(ItemStack stack, String uuid){
        if(!(stack.getItem() instanceof WandItem)) return false;
        if(!ItemDataHelper.contains(stack, WAND_UUID)) return false;
        return uuid.equals(ItemDataHelper.getString(stack, WAND_UUID));
    }

    //添加法术,index为插入位置，-1为末尾
    public static void addSpell(ItemStack stack, String spell, int index){
        List<String> spells = getSpells(stack);
        if(index == -1){
            spells.add(spell);
        }
        else{
            spells.add(index, spell);
        }
        setSpells(stack, spells);
    }

    //获取法术数量
    public static int getSpellCount(ItemStack stack){
        return getSpells(stack).size();
    }
    
    //物品行为
    //右键
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        ItemStack stack = player.getStackInHand(hand);
        if(!world.isClient){
            if(player.isSneaking()){
                int spellSlots = MathHelper.clamp(WandItem.getCapacity(stack), 1, WandScreenHandler.MAX_SPELL_SLOTS);
                int handOrd = hand.ordinal();

                player.openHandledScreen(new ExtendedScreenHandlerFactory<WandScreenHandler.WandScreenOpenData>() {
                    @Override
                    public WandScreenHandler.WandScreenOpenData getScreenOpeningData(ServerPlayerEntity serverPlayer) {
                        return new WandScreenHandler.WandScreenOpenData(spellSlots, handOrd);
                    }

                    @Override
                    public Text getDisplayName() {
                        return Text.literal("法术列表");
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity playerEntity) {
                        return new WandScreenHandler(syncId, inventory, new WandScreenHandler.WandScreenOpenData(spellSlots, handOrd));
                    }
                });
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.CONSUME;
    }
}
