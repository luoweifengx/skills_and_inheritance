package luowei.fengxskillsandinter;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import luowei.fengxskillsandinter.entity.ModEntities;
import luowei.fengxskillsandinter.event.ManaCharge;
import luowei.fengxskillsandinter.item.ModItems;
import luowei.fengxskillsandinter.network.ModNetwork;
import luowei.fengxskillsandinter.spell.SpellRegistry;
//import luowei.fengxskillsandinter.villager.BlacksmithTrades;

public class FengxSkillsAndInheritance implements ModInitializer {
	public static final String MOD_ID = "fengx-skills-and-inheritance";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// 注册村民交易
		//BlacksmithTrades.registerTrades();
		// 注册物品
		ModItems.registerModItems();
		
		ModEntities.registerModEntities();

		ModNetwork.register();

		SpellRegistry.registerSpells();

		ManaCharge.manaCharge();
	}
}