package us.icebrg.hungry.commands;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.icebrg.hungry.Hungry;
import us.icebrg.hungry.HungryMessages;

public class HungrySetHungerCommand implements CommandExecutor {

	protected Hungry plugin;

	public HungrySetHungerCommand(Hungry plugin) {
		this.plugin = plugin;
	}

	/**
	 * Sets a player hunger to the specified value. Expects a command of the
	 * format: "/<command> <playername> <hunger>"
	 * 
	 * @return true on success, false on failure
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String cmdLabel, String[] args) {
		// Requires two arguments (playername and hunger)
		if (args.length < 2) {
			return false;
		}

		if (sender instanceof Player
				&& !Hungry.permissions.hasGuard((Player) sender,
						"hungry.admin.sethunger")) {
			return true;
		}

		HashMap<String, Integer> playerHungers = this.plugin.getConfig().playerHungers;
		Integer playerHunger;

		try {
			playerHunger = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(this.plugin.getConfig().getMessage(HungryMessages.ERR_INVALID_HUNGER_FORMAT));

			return true;
		}

		// Set the key args[0] (the player name) in playerHungers to the value
		// specified in args[1] and parsed
		// above (the new player hunger)
		playerHungers.put(args[0], playerHunger);

		sender.sendMessage(this.plugin.getConfig().getMessage(
				HungryMessages.VAR_SETHUNGER,
				args[0], args[1]));

		return true;
	}
}
