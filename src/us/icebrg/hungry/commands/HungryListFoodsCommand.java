package us.icebrg.hungry.commands;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.icebrg.hungry.Hungry;

public class HungryListFoodsCommand implements CommandExecutor {

	protected Hungry plugin;

	public HungryListFoodsCommand(Hungry plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String cmdLabel, String[] args) {
		if (sender instanceof Player
				&& !Hungry.permissions.hasGuard((Player) sender,
						"hungry.player.food.list")) {
			return true;
		}

		sender.sendMessage(ChatColor.GREEN + "[Hungry] EDIBLE FOODS:");

		sender.sendMessage(ChatColor.YELLOW + "[Hungry] EDIBLE FOOD ITEMS:");

		Iterator<Map.Entry<String, Integer>> itFoods = this.plugin.getConfig().foods
				.entrySet().iterator();

		while (itFoods.hasNext()) {
			Map.Entry<String, Integer> food = itFoods.next();
			sender.sendMessage(ChatColor.YELLOW + food.getKey() + " : "
					+ food.getValue());
		}

		sender.sendMessage(ChatColor.GREEN + "[Hungry] EDIBLE FOOD BLOCKS:");

		Iterator<Map.Entry<String, Integer>> itBlocks = this.plugin.getConfig().foodBlocks
				.entrySet().iterator();

		while (itBlocks.hasNext()) {
			Map.Entry<String, Integer> block = itBlocks.next();
			sender.sendMessage(ChatColor.YELLOW + block.getKey() + " : "
					+ block.getValue());
		}

		sender.sendMessage(ChatColor.GREEN + "[Hungry] END OF EDIBLE FOODS");

		return true;
	}
}
