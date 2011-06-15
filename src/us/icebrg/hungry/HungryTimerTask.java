package us.icebrg.hungry;

import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HungryTimerTask extends TimerTask {

	Hungry plugin;
	HungryConfiguration config;

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

		// Get the equivalent set for playerHungers, then get that set's
		// iterator to iterate over
		Iterator<Map.Entry<String, Integer>> it = this.plugin.getConfig().playerHungers
				.entrySet().iterator();

		while (it.hasNext()) {
			// Increment the hungers of already registered players...
			Map.Entry<String, Integer> pairs = it.next();

			String playerName = pairs.getKey();
			Integer playerHunger = pairs.getValue();

			Player player = this.plugin.getServer().getPlayer(playerName);

			// If the player is offline...
			if (player == null || !player.isOnline()) {
				// ... don't increment their hunger or starve them!
				return;
			}

			// If the player doesn't have the hungry.hunger permission...
			if (!Hungry.permissions.has(player, "hungry.player.hunger")) {
				// ... quit
				continue;
			}

			// Only increment if the player is below the maximum hunger set in
			// maxHunger
			if (playerHunger < this.plugin.getConfig().maxHunger) {
				pairs.setValue(playerHunger
						+ this.plugin.getConfig().incrementAmount);
			}

			// If the player's hunger is somehow magically above the max, ceil
			// it
			if (playerHunger > this.plugin.getConfig().maxHunger) {
				pairs.setValue(this.plugin.getConfig().maxHunger);
			}

			// If the player's hunger is at the max...
			if (playerHunger == this.plugin.getConfig().maxHunger) {
				// ... the player is starving.

				// Inform them!
				player.sendMessage(ChatColor.RED + "You are starving!");

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
		}
	}
}
