package us.icebrg.hungry;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import us.icebrg.hungry.commands.HungryFoodCommand;
import us.icebrg.hungry.commands.HungryHungerCommand;
import us.icebrg.hungry.commands.HungryListFoodsCommand;
import us.icebrg.hungry.commands.HungryReloadCommand;
import us.icebrg.hungry.commands.HungrySaveCommand;
import us.icebrg.hungry.commands.HungrySetHungerCommand;
import us.icebrg.hungry.commands.HungryToggleCommand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Hungry extends JavaPlugin {

	public static String pluginDir = "plugins/Hungry/";
	public static HungryPermissions permissions;

	protected Logger log = Logger.getLogger("Minecraft");
	protected volatile HungryConfiguration config;
	protected HungryBlockListener blockListener;
	protected HungryPlayerListener playerListener;
	
	protected Timer timer = new Timer();
	
	protected Gson gson;

	public HungryConfiguration getConfig() {
		return this.config;
	}

	/**
	 * To be called if a player has clicked on a block (a food block, i.e. cake,
	 * or a default-non-edible block) to check for and take appropriate action
	 * if the block is registered as edible with Hungry.
	 * 
	 * @todo fix cake
	 * @param player
	 *            the player in question
	 * @param block
	 *            the block that was clicked on
	 * @return true if the block was registered as eaten by Hungry, false
	 *         otherwise
	 */
	public boolean handlePlayerEat(Player player, Block block) {
		if (!Hungry.permissions.has(player, "hungry.player.hunger")) {
			return true;
		}

		String blockMatName = block.getType().name();

		// Check to see if the material name of the block was specified in the
		// HungryConfiguration
		if (!this.config.foodBlocks.containsKey(blockMatName)) {
			// If not, pass the event and just return.
			return false;
		}

		/* Apparently, this mess is not needed, but leave it in for future study
		if (block.getType().name() == "CAKE_BLOCK") {
			// Handle the Special Case of Cake (could be a book)
			Material cakeMat = block.getType();
			Cake cake = new Cake(cakeMat);
			
			cake.setSlicesEaten(cake.getSlicesEaten() + 1);
			
			block.setData(cake.getData());
		}*/
		
		// Otherwise, if it's another block, just destroy it... (NOTE: To be
		// implemented in the future.
		// For now, the only supported block food is cake.)
		this.restorePlayerHunger(player,
				this.config.foodBlocks.get(blockMatName));

		return true;
	}

	/**
	 * To be called if a player has used an item (a food item or
	 * default-non-edible item) to check for and take appropriate action if the
	 * item is registered as edible with Hungry.
	 * 
	 * @todo reduce duplication in here!
	 * @param player
	 *            the player in question
	 * @param item
	 *            the item the player is posibbly eating
	 * @return true if the item was registered as eaten by Hungry, false
	 *         otherwise
	 */
	public boolean handlePlayerEat(Player player, ItemStack item) {
		if (!Hungry.permissions.has(player, "hungry.player.hunger")) {
			return true;
		}
		// Get the block the player was looking at, for comparison with the
		// HungryConfiguration.ignoreClickBlocks (all blocks except air, within
		// 100 blocks)
		Block block = player.getTargetBlock(null, 100);
		String blockMatName = block.getType().name();
		List<String> ignoreClickBlocks = Arrays
				.asList(this.config.ignoreClickBlocks);

		// If the player selected a block...
		if (block != null) {
			// ... and if the Material enum name is on the ignoreClickBlocks
			// list...
			if (ignoreClickBlocks.contains(blockMatName)) {
				// Return false, as the player clicked on an ignoreClickBlock (a
				// door, etc.)
				return false;
			}
		}

		String itemMatName = item.getType().name();

		// First off, get the name of the Material enum key (used in
		// HungryConfiguration) that
		// identifies this item. This would, for example, be BREAD or
		// MUSHROOM_SOUP.
		// Then, check if HungryConfiguration.foods contains this key. If it
		// doesn't, this isn't
		// an edible item, so just return.
		if (!this.config.foods.containsKey(itemMatName)) {
			return false;
		}

		this.restorePlayerHunger(player, this.config.foods.get(itemMatName));

		return true;
	}

	@Override
	public void onDisable() {

		// Cancel all HungryTimerTasks
		this.timer.cancel();
		
		this.saveConfiguration();

		this.log.info("[Hungry] Hungry disabled!");
	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		PluginManager pm = this.getServer().getPluginManager();
		this.blockListener = new HungryBlockListener(this);
		this.playerListener = new HungryPlayerListener(this);
		this.gson = new GsonBuilder().setPrettyPrinting().create();

		// Setup configuration, initializing if it isn't already there...
		if (!this.setupConfiguration()) {
			// If setupConfiguration() returned false, an error was occurred and
			// configuration
			// could not be setup. Disable the plugin.
			log.severe("[Hungry] Failed to setup and or load configuration files - aborting!");

			this.getServer().getPluginManager().disablePlugin(this);

			return;
		}

		// Register events
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener,
				Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener,
				Priority.Monitor, this);

		// Register commands

		try {
			// Administrative commands...
			this.getCommand("hungryreload").setExecutor(
					new HungryReloadCommand(this));
			this.getCommand("hungrysave").setExecutor(
					new HungrySaveCommand(this));
			this.getCommand("hungrytoggle").setExecutor(
					new HungryToggleCommand(this));
			this.getCommand("sethunger").setExecutor(
					new HungrySetHungerCommand(this));

			// Player commands...
			this.getCommand("hunger")
					.setExecutor(new HungryHungerCommand(this));
			this.getCommand("listfoods").setExecutor(
					new HungryListFoodsCommand(this));
			this.getCommand("food").setExecutor(new HungryFoodCommand(this));
		} catch (NullPointerException e) {
			this.log.severe("[Hungry] Failed to register one or more commands!"
					+ "Please disable conflicting plugins - aborting!");

			this.getServer().getPluginManager().disablePlugin(this);

			return;
		}

		Hungry.permissions = new HungryPermissions(this);

		if (!Hungry.permissions.init()) {
			this.log.severe("[Hungry] Failed to load Permissions plugin - aborting!");

			this.getServer().getPluginManager().disablePlugin(this);

			return;
		}

		this.log.info("[Hungry] Initializing hunger loop...");

		// Schedule the hunger check & incrementing event to occur every
		// this.config.checkInterval seconds
		// (multiply by 20 to get the number of ticks), starting immediately
		// (0L)
		/*this.getServer()
				.getScheduler()
				.scheduleAsyncRepeatingTask(this,
						new HungryRepeatingTask(this), 0L,
						this.config.checkInterval * 20);*/
		
		this.timer.scheduleAtFixedRate(
				new HungryTimerTask(this),
				this.config.checkInterval * 1000, this.config.checkInterval * 1000);
		// Multiply by 1000 because Timer accepts its arguments in milliseconds...
		
		
		this.log.info("[Hungry] Hungry version " + pdfFile.getVersion()
				+ " loaded!");
	}

	/**
	 * Restores the given player's hunger by the given amount, respecting
	 * configuration options such as minHunger and canStockUpInfinitely...
	 * 
	 * @param player
	 *            the player whose hunger to restore
	 * @param amount
	 *            the amount by which to restore the player's hunger
	 */
	public void restorePlayerHunger(Player player, Integer amount) {
		HashMap<String, Integer> playerHungers = this.config.playerHungers;
		Integer currentPlayerHunger = playerHungers.get(player.getName());
		Integer newPlayerHunger = currentPlayerHunger - amount;

		// Check if the canStockUpInfinitely option is true and, if so,
		// if the new player hunger is below the minimum...
		if (!this.config.canStockUpInfinitely
				&& newPlayerHunger < this.config.minHunger) {
			// ... floor it at the minimum
			newPlayerHunger = this.config.minHunger;
		}

		playerHungers.put(player.getName(), newPlayerHunger);
	}

	/**
	 * Attempts to save the configuration to disk.
	 * 
	 * @return true if success, false if failure
	 */
	public boolean saveConfiguration() {
		try {
			// Try to write the configuration from this.config...
			this.log.info("[Hungry] Attempting to write configuration file to disk...");

			FileUtils.writeStringToFile(new File(Hungry.pluginDir
					+ "config.json"), this.gson.toJson(this.config), "utf-8");
		} catch (IOException e) {
			// If we failed to save, display a message but
			// a) don't disable the plugin (this is called in onDisable)
			// b) don't delete/reset the file (let the user check it out)
			this.log.severe("[Hungry] Failed to write configuration file to disk!");

			return false;
		}

		this.log.info("[Hungry] Succesfully saved configuration to disk.");

		return true;
	}

	/**
	 * Attempts to load configuration. If configuration is not present, sets up
	 * default configuration.
	 * 
	 * @return true if success, false if failure in any operation
	 */
	public boolean setupConfiguration() {
		File pluginDirH = new File(Hungry.pluginDir);
		File configFileH;

		this.log.info("[Hungry] Loading configuration...");

		if (!pluginDirH.exists()) {
			this.log.info("[Hungry] Configuration directory doesn't exist! Creating...");

			if (!pluginDirH.mkdir()) {
				this.log.severe("[Hungry] Failed to create configuration directory !");

				return false;
			}
		}

		configFileH = new File(Hungry.pluginDir + "config.json");

		if (!configFileH.exists()) {
			this.log.info("[Hungry] Configuration file doesn't exist! Creating...");

			try {
				this.config = new HungryConfiguration();
				
				this.config.setDefaults();
				
				try {
					FileUtils.writeStringToFile(new File(Hungry.pluginDir
							+ "config.json"), this.gson.toJson(this.config));
				} catch (JsonIOException e) {
					this.log.severe("[Hungry] Failed to serialize configuration file!");

					return false;
				}

				this.log.info("[Hungry] Configuration file succesfully serialized...");

			} catch (IOException e) {
				this.log.severe("[Hungry] Failed to create configuration file!");

				return false;
			}
			
			return true;
		}

		// If the configuration file didn't have to be created, just load it!

		this.log.info("[Hungry] Loading configuration file...");

		try {
			String jsonConfig = FileUtils.readFileToString(new File(
					Hungry.pluginDir + "config.json"), "utf-8");

			if (jsonConfig.length() == 0) {
				throw new JsonSyntaxException("File is empty!");
			}

			this.config = this.gson.fromJson(jsonConfig, HungryConfiguration.class);
		} catch (JsonSyntaxException e) {
			this.log.severe("[Hungry] Configuration file corrupt or invalid!");

			this.log.severe("[Hungry] Moving old configuration file to config.json.bak...");

			// "Rename" the config.json file to config.json.bak
			(new File(Hungry.pluginDir + "config.json")).renameTo(new File(
					Hungry.pluginDir + "config.json.bak"));

			this.log.severe("[Hungry] Generating a new, default configuration file...");

			this.config = new HungryConfiguration();
			
			this.config.setDefaults();

			return false;
		} catch (IOException e) {
			this.log.severe("[Hungry] Failed to load configuration file!");

			return false;
		}
		

		this.log.info("[Hungry] Configuration file succesfully created!");
		
		this.log.info("[Hungry] Succesfully loaded configuration file!");

		
		
		return true;
	}
}