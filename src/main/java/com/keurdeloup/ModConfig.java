package com.keurdeloup;

import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.util.Properties;

public class ModConfig {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("felinepresence.properties").toFile();
    // Default weight is 1 (~1.5% chance)
    public static int giftWeight = 1;

    public static void load() {
        Properties properties = new Properties();
        if (CONFIG_FILE.exists()) {
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                properties.load(input);
                int weight = Integer.parseInt(properties.getProperty("gift_weight", "1"));

                giftWeight = Math.max(0, Math.min(100, weight));
            } catch (IOException | NumberFormatException e) {
                FelinePresence.LOGGER.error("Failed to load config, using defaults", e);
            }
        } else {
            save();
        }
    }

    public static void save() {
        Properties properties = new Properties();
        properties.setProperty("gift_weight", String.valueOf(giftWeight));
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            String comments = """
                Feline Presence Configuration
                
                gift_weight: determines how likely cats are to give you the enchanted book.
                Values are limited between 0 and 100.
                
                Approximate probabilities:
                - 1  : ~1.5%  (Very Rare - Default)
                - 2  : ~3.1%  (Rare)
                - 5  : ~7.5%  (Uncommon)
                - 10 : ~14.0% (Common)
                - 50 : ~45.0% (Very Common)
                - 100: ~62.0% (Almost every morning)
                - 0  : Disables the gift entirely
                """;
            properties.store(output, comments);
        } catch (IOException e) {
            FelinePresence.LOGGER.error("Failed to save config", e);
        }
    }
}