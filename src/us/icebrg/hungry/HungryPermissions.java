package us.icebrg.hungry;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class HungryPermissions {

	protected Logger log = Logger.getLogger("Minecraft");
	protected Hungry plugin;
	private PermissionHandler _permissions;

	public HungryPermissions(Hungry plugin) {
		this.plugin = plugin;
	}

	public boolean has(Player player, String permissionsNode) {
		return this._permissions.has(player, permissionsNode);
	}

	public boolean hasGuard(Player player, String permissionsNode) {
		if (!this.has(player, permissionsNode)) {
			player.sendMessage(ChatColor.RED
					+ "[Hungry] You do not have permission to perform that action!");
			return false;
		} else {
			return true;
		}
	}

	public boolean init() {
		Plugin permissions = this.plugin.getServer().getPluginManager()
				.getPlugin("Permissions");

		if (permissions == null) {
			return false;
		}

		this._permissions = ((Permissions) permissions).getHandler();
		log.info("[Hungry] Loaded "
				+ permissions.getDescription().getFullName());

		return true;
	}
}
