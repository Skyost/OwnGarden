package fr.skyost.owngarden.command

import com.google.common.base.Joiner
import fr.skyost.owngarden.OwnGarden
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.util.ChatPaginator

/**
 * The /owngarden command.
 */
class OwnGardenCommand(private val plugin: OwnGarden) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("owngarden.command")) {
            plugin.log(ChatColor.RED, "You do not have the permission to execute this command.", sender)
            return true
        }
        val description = Bukkit.getPluginManager().getPlugin("OwnGarden")!!.description
        sender.sendMessage(ChatColor.GREEN.toString() + description.name + " v" + description.version + ChatColor.GOLD + " by " + Joiner.on(' ').join(description.authors))
        val builder = StringBuilder()
        for (i in 0 until ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH - 2) {
            builder.append("=")
        }
        val line = builder.toString()
        sender.sendMessage(ChatColor.RESET.toString() + line)
        sender.sendMessage(ChatColor.GOLD.toString() + "SCHEMATICS : ")
        sender.sendMessage(ChatColor.RESET.toString() + "" + ChatColor.BOLD + "- Oak : " + ChatColor.RESET + Joiner.on(' ').join(plugin.pluginConfig!!.saplingOakSchematics))
        sender.sendMessage(ChatColor.BOLD.toString() + "- Spruce : " + ChatColor.RESET + Joiner.on(' ').join(plugin.pluginConfig!!.saplingSpruceSchematics))
        sender.sendMessage(ChatColor.BOLD.toString() + "- Jungle : " + ChatColor.RESET + Joiner.on(' ').join(plugin.pluginConfig!!.saplingJungleSchematics))
        sender.sendMessage(ChatColor.BOLD.toString() + "- Acacia : " + ChatColor.RESET + Joiner.on(' ').join(plugin.pluginConfig!!.saplingAcaciaSchematics))
        sender.sendMessage(ChatColor.BOLD.toString() + "- Dark Oak : " + ChatColor.RESET + Joiner.on(' ').join(plugin.pluginConfig!!.saplingDarkOakSchematics))
        sender.sendMessage(ChatColor.BOLD.toString() + "- Brown Mushroom : " + ChatColor.RESET + Joiner.on(' ').join(plugin.pluginConfig!!.mushroomBrownSchematics))
        sender.sendMessage(ChatColor.BOLD.toString() + "- Red Mushroom : " + ChatColor.RESET + Joiner.on(' ').join(plugin.pluginConfig!!.mushroomRedSchematics))
        sender.sendMessage(line)
        sender.sendMessage(ChatColor.GOLD.toString() + "PERMISSIONS : ")
        for (permission in description.permissions) {
            sender.sendMessage(if (sender.hasPermission(permission)) ChatColor.GREEN.toString() + "- You have the permission " + ChatColor.BOLD + permission.name + ChatColor.RESET + ChatColor.GREEN + "." else ChatColor.RED.toString() + "- You do not have the permission " + ChatColor.BOLD + permission.name + ChatColor.RESET + ChatColor.RED + "."
            )
        }
        sender.sendMessage(ChatColor.RESET.toString() + line)
        sender.sendMessage(ChatColor.AQUA.toString() + "" + ChatColor.ITALIC + "The above list is scrollable.")
        return true
    }
}