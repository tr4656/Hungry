package us.icebrg.hungry.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.icebrg.hungry.Hungry;
import us.icebrg.hungry.HungryConfiguration;
import us.icebrg.hungry.HungryMessages;

public class HungryToggleCommand implements CommandExecutor {

	protected Hungry plugin;
	protected HungryConfiguration config;

	public HungryToggleCommand(Hungry plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String cmdLabel, String[] args) {
		if (sender instanceof Player
				&& !Hungry.permissions.hasGuard((Player) sender,
						"hungry.admin.toggle")) {
			return true;
		}

		// Simply invert config.isEnabled
		this.plugin.getConfig().isEnabled = (!this.plugin.getConfig().isEnabled);

		if (this.plugin.getConfig().isEnabled) {
			sender.sendMessage(this.plugin.getConfig().getMessage(
					HungryMessages.MSG_TOGGLED_ENABLED));
		} else {
			sender.sendMessage(this.plugin.getConfig().getMessage(
					HungryMessages.MSG_TOGGLED_DISABLED));			
		}

		return true;
	}
}
