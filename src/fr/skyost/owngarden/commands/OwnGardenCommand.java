package fr.skyost.owngarden.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.Joiner;

import fr.skyost.owngarden.OwnGarden;

public class OwnGardenCommand implements CommandExecutor {

	@Override
	public final boolean onCommand(final CommandSender sender, final Command command, final String label, String[] args) {
		if(!sender.hasPermission("owngarden.command")) {
			OwnGarden.log(ChatColor.RED, "You do not have the permission to execute this command.", sender);
			return true;
		}
		
		final PluginDescriptionFile description = Bukkit.getPluginManager().getPlugin("OwnGarden").getDescription();
		sender.sendMessage(ChatColor.GREEN + description.getName() + " v" + description.getVersion() + ChatColor.GOLD + " by " + Joiner.on(' ').join(description.getAuthors()));
		
		final StringBuilder builder = new StringBuilder();
		for(int i = 0; i != ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 2; i++) {
			builder.append("=");
		}
		final String line = builder.toString();
		sender.sendMessage(ChatColor.RESET + line);
		
		sender.sendMessage(ChatColor.GOLD + "SCHEMATICS : ");
		
		sender.sendMessage(ChatColor.RESET + "" + ChatColor.BOLD + "- Oak : " + ChatColor.RESET + Joiner.on(' ').join(OwnGarden.config.saplingOakSchematics));
		sender.sendMessage(ChatColor.BOLD + "- Spruce : " + ChatColor.RESET + Joiner.on(' ').join(OwnGarden.config.saplingSpruceSchematics));
		sender.sendMessage(ChatColor.BOLD + "- Jungle : " + ChatColor.RESET + Joiner.on(' ').join(OwnGarden.config.saplingJungleSchematics));
		sender.sendMessage(ChatColor.BOLD + "- Acacia : " + ChatColor.RESET + Joiner.on(' ').join(OwnGarden.config.saplingAcaciaSchematics));
		sender.sendMessage(ChatColor.BOLD + "- Dark Oak : " + ChatColor.RESET + Joiner.on(' ').join(OwnGarden.config.saplingDarkOakSchematics));

		sender.sendMessage(line);
		sender.sendMessage(ChatColor.GOLD + "PERMISSIONS : ");
		
		for(final Permission permission : description.getPermissions()) {
			sender.sendMessage(sender.hasPermission(permission) ?
					(ChatColor.GREEN + "- You have the permission " + ChatColor.BOLD + permission.getName() + ChatColor.RESET + ChatColor.GREEN + ".") :
					(ChatColor.RED + "- You do not have the permission " + ChatColor.BOLD + permission.getName() + ChatColor.RESET + ChatColor.RED + ".")
			);
		}
		
		sender.sendMessage(ChatColor.RESET + line);
		sender.sendMessage(ChatColor.AQUA + "" + ChatColor.ITALIC + "The above list is scrollable.");
		return true;
	}
	
}