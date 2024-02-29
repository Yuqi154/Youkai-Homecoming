package dev.xkmc.youkaishomecoming.init.data;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.youkaishomecoming.init.YoukaisHomecoming;
import dev.xkmc.youkaishomecoming.init.food.YHCrops;
import dev.xkmc.youkaishomecoming.init.loot.YHLootGen;
import dev.xkmc.youkaishomecoming.init.registrate.YHEntities;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.AppendLoot;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class YHDatapackRegistriesGen extends DatapackBuiltinEntriesProvider {

	private static final ResourceLocation NEST = new ResourceLocation(YoukaisHomecoming.MODID, "youkai_nest");

	private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.CONFIGURED_FEATURE, ctx -> {
				for (var e : YHCrops.values()) {
					e.registerConfigs(ctx);
				}
			})
			.add(Registries.PLACED_FEATURE, ctx -> {
				for (var e : YHCrops.values()) {
					e.registerPlacements(ctx);
				}
			})
			.add(ForgeRegistries.Keys.BIOME_MODIFIERS, YHDatapackRegistriesGen::registerBiomeModifiers)

			.add(Registries.PROCESSOR_LIST, ctx -> {
				var noRep = new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE);
				ctx.register(ResourceKey.create(Registries.PROCESSOR_LIST, NEST), new StructureProcessorList(List.of(
						noRep, new RuleProcessor(List.of(injectData(Blocks.CHEST, YHLootGen.NEST_CHEST),
								injectData(Blocks.BARREL, YHLootGen.NEST_BARREL)))
				)));
			})
			.add(Registries.TEMPLATE_POOL, ctx -> {
				var empty = ctx.lookup(Registries.TEMPLATE_POOL)
						.getOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation("empty")));
				var list = ctx.lookup(Registries.PROCESSOR_LIST)
						.getOrThrow(ResourceKey.create(Registries.PROCESSOR_LIST, NEST));
				ctx.register(ResourceKey.create(Registries.TEMPLATE_POOL, NEST), new StructureTemplatePool(empty, List.of(
						Pair.of(new SinglePiece(NEST, list, StructureTemplatePool.Projection.RIGID), 1)
				)));
			})
			.add(Registries.STRUCTURE, ctx -> {
				var biome = ctx.lookup(Registries.BIOME).getOrThrow(YHBiomeTagsProvider.HAS_NEST);
				var pool = ctx.lookup(Registries.TEMPLATE_POOL).getOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, NEST));
				ctx.register(ResourceKey.create(Registries.STRUCTURE, NEST), new JigsawStructure(
						new Structure.StructureSettings(biome, Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
						pool, 1, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Heightmap.Types.WORLD_SURFACE_WG)
				);
			})
			.add(Registries.STRUCTURE_SET, ctx -> {
				var str = ctx.lookup(Registries.STRUCTURE).getOrThrow(ResourceKey.create(Registries.STRUCTURE, NEST));
				ctx.register(ResourceKey.create(Registries.STRUCTURE_SET, NEST), new StructureSet(
						str, new RandomSpreadStructurePlacement(16, 8, RandomSpreadType.LINEAR, NEST.hashCode())));
			});

	private static void registerBiomeModifiers(BootstapContext<BiomeModifier> ctx) {
		var biomes = ctx.lookup(Registries.BIOME);
		var features = ctx.lookup(Registries.PLACED_FEATURE);
		HolderSet<Biome> set = biomes.getOrThrow(YHBiomeTagsProvider.LAMPREY);
		ctx.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(YoukaisHomecoming.MODID, "lamprey")),
				new ForgeBiomeModifiers.AddSpawnsBiomeModifier(set, List.of(new MobSpawnSettings.SpawnerData(
						YHEntities.LAMPREY.get(), 12, 3, 5)
				)));
		registerCropBiome(ctx, YHCrops.SOYBEAN, biomes.getOrThrow(YHBiomeTagsProvider.SOYBEAN), features);
		registerCropBiome(ctx, YHCrops.REDBEAN, biomes.getOrThrow(YHBiomeTagsProvider.REDBEAN), features);
		registerCropBiome(ctx, YHCrops.COFFEA, biomes.getOrThrow(YHBiomeTagsProvider.COFFEA), features);
		registerCropBiome(ctx, YHCrops.TEA, biomes.getOrThrow(YHBiomeTagsProvider.TEA), features);
		registerCropBiome(ctx, YHCrops.MANDRAKE, biomes.getOrThrow(YHBiomeTagsProvider.MANDRAKE), features);
		registerCropBiome(ctx, YHCrops.UDUMBARA, biomes.getOrThrow(YHBiomeTagsProvider.UDUMBARA), features);
	}

	private static void registerCropBiome(BootstapContext<BiomeModifier> ctx,
										  YHCrops tree,
										  HolderSet<Biome> set,
										  HolderGetter<PlacedFeature> features) {
		ctx.register(ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(YoukaisHomecoming.MODID,
				tree.getName())), new ForgeBiomeModifiers.AddFeaturesBiomeModifier(set,
				HolderSet.direct(features.getOrThrow(tree.getPlacementKey())),
				GenerationStep.Decoration.VEGETAL_DECORATION));
	}

	public YHDatapackRegistriesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries, BUILDER, Set.of("minecraft", YoukaisHomecoming.MODID));
	}

	@NotNull
	public String getName() {
		return "Youkai's Homecoming Data";
	}

	private static ProcessorRule injectData(Block block, ResourceLocation table) {
		return new ProcessorRule(new BlockMatchTest(block), AlwaysTrueTest.INSTANCE, PosAlwaysTrueTest.INSTANCE,
				block.defaultBlockState(), new AppendLoot(table));
	}

	private static class SinglePiece extends SinglePoolElement {

		protected SinglePiece(ResourceLocation template, Holder<StructureProcessorList> list, StructureTemplatePool.Projection proj) {
			super(Either.left(template), list, proj);
		}

	}

}
