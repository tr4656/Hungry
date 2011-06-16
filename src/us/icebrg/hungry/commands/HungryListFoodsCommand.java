package us.icebrg.hungry.commands;

import java.util.Iterator;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import us.icebrg.hungry.*;

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

		sender.sendMessage(this.plugin.getConfig().getMessage(HungryMessages.MSG_FOOD_LIST_START));

		Iterator<Map.Entry<String, Integer>> itFoods = this.plugin.getConfig().foods
				.entrySet().iterator();

		while (itFoods.hasNext()) {
			Map.Entry<String, Integer> food = itFoods.next();
			
			sender.sendMessage(this.plugin.getConfig()
					.getMessage(HungryMessages.VAR_FOOD_LIST_ITEM,
							food.getKey(), food.getValue().toString()));
		}

		Iterator<Map.Entry<String, Integer>> itBlocks = this.plugin.getConfig().foodBlocks
				.entrySet().iterator();

		while (itBlocks.hasNext()) {
			Map.Entry<String, Integer> block = itBlocks.next();
			
			sender.sendMessage(this.plugin.getConfig()
					.getMessage(HungryMessages.VAR_FOOD_LIST_ITEM,
							block.getKey(), block.getValue().toString()));
		}

		sender.sendMessage(this.plugin.getConfig().getMessage(HungryMessages.MSG_FOOD_LIST_END));

		return true;
	}
}
