package com.teamabnormals.neapolitan.core;

import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import com.teamabnormals.neapolitan.client.model.BananaPeelModel;
import com.teamabnormals.neapolitan.client.model.ChimpanzeeModel;
import com.teamabnormals.neapolitan.client.renderer.*;
import com.teamabnormals.neapolitan.core.data.client.NeapolitanBlockStateProvider;
import com.teamabnormals.neapolitan.core.data.server.NeapolitanLootModifiersProvider;
import com.teamabnormals.neapolitan.core.data.server.NeapolitanLootTableProvider;
import com.teamabnormals.neapolitan.core.data.server.tags.NeapolitanBlockTagsProvider;
import com.teamabnormals.neapolitan.core.data.server.tags.NeapolitanItemTagsProvider;
import com.teamabnormals.neapolitan.core.other.NeapolitanCompat;
import com.teamabnormals.neapolitan.core.registry.*;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(Neapolitan.MOD_ID)
@Mod.EventBusSubscriber(modid = Neapolitan.MOD_ID)
public class Neapolitan {
	public static final String MOD_ID = "neapolitan";
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

	public Neapolitan() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(this);
		ForgeMod.enableMilkFluid();

		REGISTRY_HELPER.register(bus);
		NeapolitanEffects.EFFECTS.register(bus);
		NeapolitanFeatures.FEATURES.register(bus);
		NeapolitanBanners.PAINTINGS.register(bus);
		NeapolitanParticles.PARTICLES.register(bus);

		bus.addListener(this::commonSetup);
		bus.addListener(this::clientSetup);
		bus.addListener(this::dataSetup);

		bus.addListener(this::registerLayerDefinitions);
		bus.addListener(this::registerRenderers);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NeapolitanConfig.COMMON_SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			NeapolitanCompat.transformCookies();
			NeapolitanCompat.registerCompat();
			NeapolitanEntityTypes.registerEntitySpawns();
		});
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			NeapolitanCompat.registerRenderLayers();
			NeapolitanItems.registerItemProperties();
		});
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			NeapolitanBlockTagsProvider blockTagsProvider = new NeapolitanBlockTagsProvider(generator, helper);
			generator.addProvider(blockTagsProvider);
			generator.addProvider(new NeapolitanItemTagsProvider(generator, blockTagsProvider, helper));
			generator.addProvider(new NeapolitanLootTableProvider(generator));
			generator.addProvider(NeapolitanLootModifiersProvider.createLootModifierDataProvider(generator));
		}

		if (event.includeClient()) {
			generator.addProvider(new NeapolitanBlockStateProvider(generator, helper));
			//dataGenerator.addProvider(new NeapolitanLanguageProvider(dataGenerator));
		}
	}

	private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ChimpanzeeModel.LOCATION, () -> ChimpanzeeModel.createLayerDefinition(0.0F, false, false));
		event.registerLayerDefinition(BananaPeelModel.LOCATION, BananaPeelModel::createLayerDefinition);
	}

	private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(NeapolitanEntityTypes.CHIMPANZEE.get(), ChimpanzeeRenderer::new);
		event.registerEntityRenderer(NeapolitanEntityTypes.PLANTAIN_SPIDER.get(), PlantainSpiderRenderer::new);
		event.registerEntityRenderer(NeapolitanEntityTypes.BANANA_PEEL.get(), BananaPeelRenderer::new);
		event.registerEntityRenderer(NeapolitanEntityTypes.BANANARROW.get(), BananarrowRenderer::new);
	}
}
