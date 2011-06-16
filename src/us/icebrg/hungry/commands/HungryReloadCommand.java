package us.icebrg.hungry.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.icebrg.hungry.Hungry;
import us.icebrg.hungry.HungryMessages;

public class HungryReloadCommand implements CommandExecutor {

	protected Hungry plugin;

	public HungryReloadCommand(Hungry plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String cmdLabel, String[] args) {
		if (sender instanceof Player
				&& !Hungry.permissions.hasGuard((Player) sender,
						"hungry.admin.reload")) {
			return true;
		}

		// Simply setup the configuration again...
		if (!this.plugin.setupConfiguration()) {
			// If setupConfiguration() returned false, Hungry could not setup or
			// load the configuration
			// for some reason - inform the user
			sender.sendMessage(this.plugin.getConfig()
					.getMessage(HungryMessages.ERR_RELOAD_FAILURE));
		} else {
			sender.sendMessage(this.plugin.getConfig()
					.getMessage(HungryMessages.MSG_RELOAD_SUCCESS));
		}

		return true;
	}
}
