package com.keurdeloup;

import com.keurdeloup.mixin.EntityAccessor;
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
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FelinePresence implements ModInitializer {
	public static final String MOD_ID = "felinepresence";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceKey<Enchantment> FELINE_PRESENCE =
			ResourceKey.create(Registries.ENCHANTMENT,
					ResourceLocation.fromNamespaceAndPath("keurdeloup", "feline_presence"));

	public static boolean hasFelinePresence(LivingEntity entity) {
		return ((EntityAccessor) entity).felinePresence$getLevel().registryAccess()
				.lookup(Registries.ENCHANTMENT)
				.flatMap(lookup -> lookup.get(FELINE_PRESENCE))
				.map(holder -> EnchantmentHelper.getEnchantmentLevel(holder, entity) > 0)
				.orElse(false);
	}

	@Override
	public void onInitialize() {
		ModConfig.load();

		LOGGER.info("Feline Presence initialized with gift weight: {}" , ModConfig.giftWeight);

		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			if (key.location().equals(ResourceLocation.withDefaultNamespace("gameplay/cat_morning_gift"))) {
				var enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
				Holder.Reference<Enchantment> felinePresence = enchantmentLookup.getOrThrow(FELINE_PRESENCE);
				tableBuilder.modifyPools(poolBuilder -> poolBuilder.add(LootItem.lootTableItem(Items.ENCHANTED_BOOK)
                        .setWeight(ModConfig.giftWeight)
                        .apply(new SetEnchantmentsFunction.Builder()
                                .withEnchantment(felinePresence, ConstantValue.exactly(1))
                        )));
			}
		});
	}
}