package us.icebrg.hungry;

import java.util.HashMap;

public class HungryConfiguration {

	/**
	 * Whether or not the Hungry hunger increment loop is enabled
	 */
	public Boolean isEnabled = true;

	/**
	 * The interval after which to check for and increment hunger, in seconds
	 */
	public Integer checkInterval = 60;

	/**
	 * The amount by which to increment hunger after every check
	 */
	public Integer incrementAmount = 1;

	/**
	 * The default amount of hunger (what a new player starts out with)
	 */
	public Integer defaultHunger = 0;

	/**
	 * The maximum amount of hunger a player can have before starving
	 */
	public Integer maxHunger = 100;

	/**
	 * The minimum amount of hunger a player can have - ignored if canStockUp is
	 * set to true
	 */
	public Integer minHunger = 0;

	/**
	 * Whether the player can stock up infinitely on food, overrides minHunger
	 */
	public Boolean canStockUpInfinitely = false;

	/**
	 * The amount of damage that should be done to the player every time a check
	 * finds them as starving - a value of 2 equals one heart
	 */
	public Integer starvationDamage = 2;

	/**
	 * Whether or not starvation should do actual damage (affects armor first)
	 * or simply subtract health (will not affect armor)
	 */
	public Boolean starvationDoesActualDamage = false;

	/**
	 * Whether or not to reset the hunger level when the player respawns
	 */
	public Boolean resetHungerAtRespawn = true;

	/**
	 * The hunger levels of all players
	 */
	public volatile HashMap<String, Integer> playerHungers;

	/**
	 * The different foods and the amount of hunger each restores, specify item
	 * names using the org.bukkit.Material enum
	 */
	public HashMap<String, Integer> foods;

	/**
	 * Blocks which right-clicking on should restore hunger, specify block names
	 * using the org.bukkit.Material enum NOTE: the only supported block food is
	 * cake. No others will work.
	 */
	public HashMap<String, Integer> foodBlocks;

	public String[] ignoreClickBlocks = { "DISPENSER", "NOTE_BLOCK",
			"BED_BLOCK", "CHEST", "WORKBENCH", "FURNACE", "BURNING_FURNACE",
			"WOODEN_DOOR", "LEVER", "IRON_DOOR_BLOCK", "STONE_BUTTON",
			"JUKEBOX", "LOCKED_CHEST", "TRAP_DOOR" };

	public HungryConfiguration() {
	}
	
	/**
	 * Sets default sub-values for complex values.
	 * We can't have this in the constructor, because then we'd mess up the Gson
	 * object deserialization process. Instead, we specifically call this when
	 * we are setting up an initial configuration file.
	 */
	public void setDefaults() {
		this.foods = new HashMap<String, Integer>();
		this.foodBlocks = new HashMap<String, Integer>();
		this.playerHungers = new HashMap<String, Integer>();
		
		this.foods.put("APPLE", 100);
		this.foods.put("MUSHROOM_SOUP", 25);
		this.foods.put("PORK", 20);
		this.foods.put("GRILLED_PORK", 35);
		this.foods.put("GOLDEN_APPLE", 100);
		this.foods.put("RAW_FISH", 25);
		this.foods.put("COOKED_FISH", 35);
		this.foods.put("COOKIE", 35);
		

		/**
		 * @todo Fix cake. Disable because currently, it is infinite.
		 */
		this.foodBlocks.put("CAKE_BLOCK", 15);
	}
}
