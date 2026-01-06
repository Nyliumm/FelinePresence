package com.keurdeloup;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FelinePresence implements ModInitializer {
	public static final String MOD_ID = "felinepresence";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Feline Presence enchantment - makes creepers avoid the player like they avoid cats
	// This is data-driven via src/main/resources/data/keurdeloup/enchantment/feline_presence.json
	public static final ResourceKey<Enchantment> FELINE_PRESENCE =
			ResourceKey.create(Registries.ENCHANTMENT,
					ResourceLocation.fromNamespaceAndPath("keurdeloup", "feline_presence"));

	/**
	 * Checks if the entity has the Feline Presence enchantment.
	 * Optimized for frequent calls in AI goals.
	 */
	public static boolean hasFelinePresence(LivingEntity entity) {
		return entity.level().registryAccess()
				.lookup(Registries.ENCHANTMENT)
				.flatMap(lookup -> lookup.get(FELINE_PRESENCE))
				.map(holder -> EnchantmentHelper.getEnchantmentLevel(holder, entity) > 0)
				.orElse(false);
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Feline Presence initialized!");

		// Feline Presence enchantment
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Check for Trail Ruins rare archaeology
			if (key.location().equals(ResourceLocation.withDefaultNamespace("archaeology/trail_ruins_rare"))) {

				// Get the enchantment holder from the registry
				var enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
				Holder.Reference<Enchantment> felinePresence = enchantmentLookup.getOrThrow(FELINE_PRESENCE);

				LootPool.Builder poolBuilder = LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1))
						.add(LootItem.lootTableItem(Items.ENCHANTED_BOOK) // .add() is often used for entries
								.apply(new SetEnchantmentsFunction.Builder()
										.withEnchantment(felinePresence, ConstantValue.exactly(1))
								));

				tableBuilder.pool(poolBuilder.build()); // Ensure this matches your mapping (usually .pool() or .withPool())
			}
		});
	}
}