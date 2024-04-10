package dev.xkmc.youkaishomecoming.init.loot;

import com.tterrag.registrate.providers.loot.RegistrateLootTableProvider;
import dev.xkmc.l2library.util.data.LootTableTemplate;
import dev.xkmc.youkaishomecoming.init.YoukaisHomecoming;
import dev.xkmc.youkaishomecoming.init.food.YHCrops;
import dev.xkmc.youkaishomecoming.init.food.YHFood;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class YHLootGen {

	public static final ResourceLocation NEST_CHEST = YoukaisHomecoming.loc("chests/youkai_nest/chest");
	public static final ResourceLocation NEST_BARREL = YoukaisHomecoming.loc("chests/youkai_nest/barrel");

	public static final ResourceLocation UDUMBARA_LOOT = YoukaisHomecoming.loc("udumbara_chest_loot");

	public static void genLoot(RegistrateLootTableProvider pvd) {
		var bone = LootTableTemplate.getPool(1, 0)
				.add(LootTableTemplate.getItem(Items.BONE, 16));
		var misc = LootTableTemplate.getPool(5, 1)
				.add(LootTableTemplate.getItem(Items.BOWL, 1))
				.add(LootTableTemplate.getItem(Items.ROTTEN_FLESH, 8))
				.add(LootTableTemplate.getItem(Items.BAMBOO, 8))
				.add(LootTableTemplate.getItem(Items.POTATO, 8))
				.add(LootTableTemplate.getItem(Items.CARROT, 8))
				.add(LootTableTemplate.getItem(Items.COAL, 8));
		var dango = LootTableTemplate.getPool(3, 1)
				.add(LootTableTemplate.getItem(YHFood.MOCHI.item.get(), 8).setWeight(100))
				.add(LootTableTemplate.getItem(YHFood.SAKURA_MOCHI.item.get(), 4).setWeight(100))
				.add(LootTableTemplate.getItem(YHFood.MATCHA_MOCHI.item.get(), 4).setWeight(100))
				.add(LootTableTemplate.getItem(YHFood.KINAKO_DANGO.item.get(), 1).setWeight(50))
				.add(LootTableTemplate.getItem(YHFood.TSUKIMI_DANGO.item.get(), 1).setWeight(50))
				.add(LootTableTemplate.getItem(YHFood.ASSORTED_DANGO.item.get(), 1).setWeight(50))
				.add(LootTableTemplate.getItem(YHFood.MITARASHI_DANGO.item.get(), 1).setWeight(50))
				.add(LootTableTemplate.getItem(YHFood.YASHOUMA_DANGO.item.get(), 1).setWeight(50));
		var rice = LootTableTemplate.getPool(2, 1)
				.add(LootTableTemplate.getItem(YHFood.ONIGILI.item.get(), 2))
				.add(LootTableTemplate.getItem(YHFood.SENBEI.item.get(), 2))
				.add(LootTableTemplate.getItem(YHFood.SEKIBANKIYAKI.item.get(), 2))
				.add(LootTableTemplate.getItem(YHFood.YAKUMO_INARI.item.get(), 2))
				.add(LootTableTemplate.getItem(YHFood.BUN.item.get(), 1))
				.add(LootTableTemplate.getItem(YHFood.OYAKI.item.get(), 2))
				.add(LootTableTemplate.getItem(YHFood.PORK_RICE_BALL.item.get(), 1));
		var flesh = LootTableTemplate.getPool(1, 1)
				.add(LootTableTemplate.getItem(YHFood.FLESH.item.get(), 4).setWeight(100))
				.add(LootTableTemplate.getItem(YHFood.COOKED_FLESH.item.get(), 2).setWeight(50))
				.add(LootTableTemplate.getItem(YHFood.FLESH_ROLL.item.get(), 2).setWeight(20))
				.add(LootTableTemplate.getItem(YHFood.FLESH_STEW.item.get(), 1).setWeight(20));

		pvd.addLootAction(LootContextParamSets.CHEST, cons -> cons.accept(NEST_CHEST, LootTable.lootTable()
				.withPool(bone).withPool(misc).withPool(rice).withPool(flesh)
		));

		pvd.addLootAction(LootContextParamSets.CHEST, cons -> cons.accept(NEST_BARREL, LootTable.lootTable()
				.withPool(bone).withPool(misc).withPool(dango).withPool(flesh)
		));

		pvd.addLootAction(LootContextParamSets.EMPTY, cons -> cons.accept(UDUMBARA_LOOT, LootTable.lootTable()
				.withPool(LootPool.lootPool().add(LootTableTemplate.getItem(YHCrops.UDUMBARA.getSeed(), 2, 4)
						.when(LootTableTemplate.chance(0.3f))))));


	}

}
