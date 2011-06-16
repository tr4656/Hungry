package us.icebrg.hungry;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class HungryConfiguration {

	/**
	 * Whether or not the Hungry hunger increment loop is enabled
	 */
	public Boolean isEnabled = true;

	/**
	 * The interval after which to check for and increment hunger, in seconds
	 */
	public Long checkInterval = 60L;

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
	
	/**
	 * The prefix to display in front of messages sent by Hungry.
	 */
	public String messagePrefix = ChatColor.YELLOW + "[Hungry] ";
	
	/**
	 * Custom language support, through fully configurable messages!
	 * Uses printf-style formatting.
	 */
	public HashMap<HungryMessages, String> messages;
	
	/**
	 * Hunger level notifications.
	 */
	public HashMap<Integer, String> hungerLevelNotifications;

	/**
	 * Only show hunger level notifications when the hunger is exactly at the
	 * specified level.
	 */
	public Boolean hungerLevelNotificationsOnlyOnExact = false;
	
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
		this.messages = new HashMap<HungryMessages, String>();
		this.hungerLevelNotifications = new HashMap<Integer, String>();
		
		this.foods.put("APPLE", 100);
		this.foods.put("MUSHROOM_SOUP", 25);
		this.foods.put("PORK", 20);
		this.foods.put("GRILLED_PORK", 35);
		this.foods.put("GOLDEN_APPLE", 100);
		this.foods.put("RAW_FISH", 25);
		this.foods.put("COOKED_FISH", 35);
		this.foods.put("COOKIE", 35);
		
		this.foodBlocks.put("CAKE_BLOCK", 15);
		
		/**
		 * Non-Variable Messages
		 */
			/**
			 * Food list messages
			 */
			this.messages.put(HungryMessages.MSG_FOOD_LIST_START,
					ChatColor.GREEN + "=== EDIBLE FOODS ===");	
			this.messages.put(HungryMessages.MSG_FOOD_LIST_END,
					ChatColor.GREEN + "=== END EDIBLE FOODS ===");

		this.messages.put(HungryMessages.MSG_TOGGLED_ENABLED,
				ChatColor.GREEN + "Hungry is now enabled!");
		this.messages.put(HungryMessages.MSG_TOGGLED_DISABLED,
				ChatColor.RED + "Hungry is now disabled!");
		
		this.messages.put(HungryMessages.MSG_SAVECONFIG_SUCCESS,
				ChatColor.GREEN + "Configuration succesfully saved!");
		this.messages.put(HungryMessages.ERR_SAVECONFIG_FAILURE,
				ChatColor.RED + "Configuration failed to save!");
		
		this.messages.put(HungryMessages.MSG_RELOAD_SUCCESS,
				ChatColor.GREEN + "Hungry succesfully reloaded!");
		this.messages.put(HungryMessages.ERR_RELOAD_FAILURE,
				ChatColor.RED + "Hungry failed to reloaded!");		
		/**
		 * Error Messages
		 */
		this.messages.put(HungryMessages.ERR_ONLY_USABLE_INGAME,
				ChatColor.RED + "This command can only be used by in-game players.");
		
		this.messages.put(HungryMessages.ERR_NO_SUCH_FOOD,
				ChatColor.RED + "No food with that name/id was found.");
		
		this.messages.put(HungryMessages.ERR_INVALID_HUNGER_FORMAT,
				ChatColor.RED + "Invalid format for player hunger!");
		
		/**
		 * Variable Messages
		 */
		this.messages.put(HungryMessages.VAR_CURRENT_HUNGER_IS,
				ChatColor.GREEN + "Your current hunger level is " + ChatColor.WHITE + "%s");
		
		this.messages.put(HungryMessages.VAR_FOOD_INFO,
				ChatColor.WHITE + "%s " + ChatColor.GREEN
				+ " restores " + ChatColor.WHITE + " %s " + ChatColor.GREEN + " hunger.");
		
		this.messages.put(HungryMessages.VAR_SETHUNGER,
				ChatColor.GREEN + "Succesfully set"
				+ ChatColor.WHITE + " %s's " + ChatColor.GREEN + "hunger to"
				+ ChatColor.WHITE + " %s.");
		
		this.messages.put(HungryMessages.VAR_FOOD_LIST_ITEM,
				ChatColor.GREEN + "%s" + ChatColor.YELLOW + " : " + ChatColor.GREEN + "%s");		
		
		this.hungerLevelNotifications.put(20, "You feel a bit peckish.");
		this.hungerLevelNotifications.put(40, "Your stomach rumbles.");
		this.hungerLevelNotifications.put(60, "You start to feel hungry.");
		this.hungerLevelNotifications.put(80, "Your stomach aches from hunger.");
		this.hungerLevelNotifications.put(100, ChatColor.RED + "You are starving!");
	}
	
	/**
	 * Convenience function for getting a formattable message and replacing variables...
	 */
	public String getMessage(HungryMessages name, String ... variables) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.getDefault()); // The Locale doesn't really
		// matter, because we aren't using any localizable fields (date, etc.)
		
		formatter.format(this.messages.get(name), (Object[])variables);
		// Do tricky stuff with casting the variables array to an Object for a varargs
		// invocation
		
		// Example: (red) [Hungry] (reset to white) SomeMessageHere
		return this.messagePrefix + ChatColor.WHITE + sb.toString();
	}
	
	/**
	 * Convenience function for getting the suitable hungerLevelNotification for the
	 * specified hungerLevel.
	 * @param hungerLevel the hungerLevel to get a notification for
	 * @return a String with the proper message if possible, null otherwise
	 */
	public String getHungerLevelNotification(Integer hungerLevel) {
		Iterator<Map.Entry<Integer, String>> hungerLevelIt =
			this.hungerLevelNotifications.entrySet().iterator();
		
		Map.Entry<Integer, String> bestNotification;		
		
		// If we're only supposed to show hungerLevelNotifactions at the exact amount
		if (this.hungerLevelNotificationsOnlyOnExact) {
			if (this.hungerLevelNotifications.containsKey(hungerLevel)) {
				return this.hungerLevelNotifications.get(hungerLevel);
			}
			
			// If no hungerLevelNotification was found with exactly that value,
			// just return null;
			return null;
		}
		
		bestNotification = null;
		
		// Now, get the highest possible hunger
		while (hungerLevelIt.hasNext()) {
			Map.Entry<Integer, String> notification = hungerLevelIt.next();
			
			// If the playerHunger satisfies this key and if this key is a better match
			// (higher up) than the existing bestNotification...
			if (hungerLevel >= notification.getKey())
			{
				if (bestNotification == null
						|| notification.getKey() > bestNotification.getKey()) {
					bestNotification = notification;
				}
			}
		}
		
		// Double-check in case we couldn't get a suitable notification at all, then
		// display it
		if (bestNotification != null && hungerLevel >= bestNotification.getKey()) {
			return this.messagePrefix + ChatColor.WHITE + bestNotification.getValue();
		}
		
		return null;
	}
	
	public static HungryConfiguration load(File location) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		String jsonConfig = FileUtils.readFileToString(location, "utf-8");

		if (jsonConfig.length() == 0) {
			throw new JsonSyntaxException("File is empty!");
		}
		
		return gson.fromJson(jsonConfig, HungryConfiguration.class);
	}
	
	/**
	 * Saves the configuration object to disk
	 * @param location the location to save the configuration
	 */
	public void save(File location) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		FileUtils.writeStringToFile(location, gson.toJson(this), "utf-8");		
	}
}
