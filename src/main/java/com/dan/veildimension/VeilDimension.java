package com.dan.veildimension;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VeilDimension implements ModInitializer
{
	public static final String MOD_ID = "veildimension";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		LOGGER.info("Initializing Veil Dimension mod...");

		ModBlocks.initialize();
		ModItems.initialize();

		LOGGER.info("Veil Dimension mod initialized successfully!");
	}
}