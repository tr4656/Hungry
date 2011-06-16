package us.icebrg.hungry.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.icebrg.hungry.Hungry;
import us.icebrg.hungry.HungryMessages;

public class HungryHungerCommand implements CommandExecutor {

	protected Hungry plugin;

	public HungryHungerCommand(Hungry plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String cmdLabel, String[] args) {
		// Check to make sure the sender is an in-game player, not the
		// Console...
		if (!(sender instanceof Player)) {
			// If they are not an in-game player (i.e. they are the console)
			// inform them
			sender.sendMessage(this.plugin.getConfig().
					getMessage(HungryMessages.ERR_ONLY_USABLE_INGAME));

			return true;
		}

		if (!Hungry.permissions.hasGuard((Player) sender,
				"hungry.player.hunger")) {
			return true;
		}

		// Cast the sender to the player if they have been proved to be so by
		// the above check
		Player player = (Player) sender;
		HashMap<String, Integer> playerHungers = this.plugin.getConfig().playerHungers;

		// Check if the player is in the playerHungers list
		if (!playerHungers.containsKey(player.getName())) {
			// if the player wasn't already in the registry, add them
			playerHungers.put(player.getName(), 0);
		}

		player.sendMessage(this.plugin.getConfig().
				getMessage(HungryMessages.VAR_CURRENT_HUNGER_IS,
						playerHungers.get(player.getName()).toString()));

		return true;
	}
}