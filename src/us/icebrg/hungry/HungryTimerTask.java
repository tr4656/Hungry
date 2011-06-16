package us.icebrg.hungry;

import java.util.HashMap;
import java.util.TimerTask;

import org.bukkit.entity.Player;

public class HungryTimerTask extends TimerTask {

	Hungry plugin;

	public HungryTimerTask(Hungry plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		// Check if the increment loop is enabled...
		if (!this.plugin.getConfig().isEnabled) {
			// ... if it isn't, we're done here (just pass)
			return;
		}

		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			HashMap<String, Integer> playerHungers = this.plugin.getConfig().playerHungers;
			String playerName = player.getName();
			Integer playerHunger =
				playerHungers.containsKey(playerName) ? playerHungers.get(playerName) : 0;
			
			/**	
			 * Incrementing Hunger
			 */
				
			// If the player doesn't have the hungry.hunger permission...
			if (!Hungry.permissions.has(player, "hungry.player.hunger")) {
				// ... quit
				continue;
			}
			
			playerHunger++;
			
			// If the player's hunger is somehow magically above the max, ceil
			// it
			if (playerHunger > this.plugin.getConfig().maxHunger) {
				playerHunger = this.plugin.getConfig().maxHunger;
			}	
			
			/**
			 * Hunger level Notifications
			 */
			
			// Inform the player about their current hunger level
			String hungerLevelNotification = this.plugin.getConfig().getHungerLevelNotification(playerHunger);
			
			if (hungerLevelNotification != null) {
				player.sendMessage(hungerLevelNotification);
			}
			
			/**
			 * Starving
			 */
			
			// If the player's hunger is at the max...
			if (playerHunger == this.plugin.getConfig().maxHunger) {
				// ... the player is starving.
				
				// Check if starvation is set to do actual "physical" damage
				// (damages armor)
				if (this.plugin.getConfig().starvationDoesActualDamage) {
					// do "physical" damage
					player.damage(this.plugin.getConfig().starvationDamage);
					// check if it is > 0 because the player might be already dead					
				} else if (player.getHealth() > 0) {
					// do non-"physical" damage
					player.setHealth(player.getHealth()
							- this.plugin.getConfig().starvationDamage);
				}
			}
			
			/**
			 * Put the playerHunger back into storage
			 */
			
			// Note that if a key doesn't exist, this will create it. This means
			// we don't need the PLAYER_RESPAWN hook anymore.
			playerHungers.put(playerName, playerHunger);
		}
	}
}
