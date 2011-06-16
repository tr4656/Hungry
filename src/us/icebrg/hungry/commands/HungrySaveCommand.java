package us.icebrg.hungry.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.icebrg.hungry.*;

public class HungrySaveCommand implements CommandExecutor {

	protected Hungry plugin;

	public HungrySaveCommand(Hungry plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String cmdLabel, String[] args) {
		if (sender instanceof Player
				&& !Hungry.permissions.hasGuard((Player) sender,
						"hungry.admin.save")) {
			return true;
		}

		if (!this.plugin.saveConfiguration()) {
			// Failed to save configuration - inform the user
			sender.sendMessage(this.plugin.getConfig()
					.getMessage(HungryMessages.ERR_SAVECONFIG_FAILURE));
		} else {
			sender.sendMessage(this.plugin.getConfig()
					.getMessage(HungryMessages.MSG_SAVECONFIG_SUCCESS));
		}

		return true;
	}
}
