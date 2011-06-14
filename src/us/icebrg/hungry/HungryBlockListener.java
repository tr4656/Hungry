package us.icebrg.hungry;

import org.bukkit.event.block.BlockListener;

class HungryBlockListener extends BlockListener {
	protected Hungry plugin;

	public HungryBlockListener(Hungry plugin) {
		this.plugin = plugin;
	}
}