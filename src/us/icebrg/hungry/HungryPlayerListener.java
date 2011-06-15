package us.icebrg.hungry;

import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class HungryPlayerListener extends PlayerListener {
	protected Hungry plugin;
	protected HungryConfiguration config;
	protected Logger log;

	public HungryPlayerListener(Hungry plugin) {
		this.plugin = plugin;

		this.log = plugin.log;
	}

	@Override
	/**
	 * For our purposes, if the player is eating.
	 */
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		this.plugin.handlePlayerEat(event.getPlayer(), event.getPlayer()
				.getItemInHand());
	}

	@Override
	/**
	 * For our purposes, if the player is right-clicking and doing anything but eating (for
	 * default non-edible blocks)
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
			// The player right-clicked on the air with an item.
			this.plugin.handlePlayerEat(event.getPlayer(), event.getPlayer()
					.getItemInHand());
			break;

		case RIGHT_CLICK_BLOCK:
			// The player right-clicked on a block.

			try {
				// Check if the item they are holding is edible, first...
				if (event.hasItem()
						&& this.plugin.getConfig().foods.containsKey(event
								.getPlayer().getItemInHand().getType().name())) {
					// ... if it is, act as if the player ate the item
					this.plugin.handlePlayerEat(event.getPlayer(), event
							.getPlayer().getItemInHand());
					
					return;
				}
			} catch (NullPointerException e) {
				// If the player placed a block, so an item was involved but is
				// no longer in the player's
				// hand (NOTE: probably should refactor this)...
				return;
			}

			Block block = event.getClickedBlock();

			this.plugin.handlePlayerEat(event.getPlayer(), block);
			
			break;
		}
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event) {
		// Check if the player doesn't exist in the config.playerHungers value
		if (!this.plugin.getConfig().playerHungers.containsKey(event
				.getPlayer().getName())) {
			// if the player didn't exist, add them with the default hunger
			// level (config.defaultHunger) to the playerHungers array.
			this.plugin.getConfig().playerHungers.put(event.getPlayer()
					.getName(), this.plugin.getConfig().defaultHunger);

			this.log.info("[Hungry] Added player "
					+ event.getPlayer().getName() + " to players list.");
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		// Check if resetHungerAtRespawn is set to true
		if (this.plugin.getConfig().resetHungerAtRespawn) {
			// ... if it is, set the player's hunger to config.defaultHunger
			this.plugin.getConfig().playerHungers.put(event.getPlayer()
					.getName(), this.plugin.getConfig().defaultHunger);
		}
	}
}
