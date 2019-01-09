<div align="center">
    <img src="https://media.forgecdn.net/attachments/125/227/logo.png" title="OwnGarden" alt="OwnGarden"/>
</div>

## How does it work ?
Well, it is simple : first you have to plant a sapling (any type you want). Then let it grows or use some bonemeal. When the sapling is ready to become a tree, the plugin replaces the horrible default Minecraft tree by a nice custom schematic. 
The custom schematic is randomly picked from a list (where you can add, remove or edit available schematics per sapling type).

**NOTE :** This plugin needs to have WorldEdit installed on your server (it is used to load and past legacy, sponge schematics and NBT structure files).

## Configuration
Your server must be stopped before editing **config.yml**.

| KEY        |                           | POSSIBLE VALUES   | DESCRIPTION                                                                                            |
|------------|---------------------------|-------------------|--------------------------------------------------------------------------------------------------------|
| enable     |                           |                   |                                                                                                        |
|            | updater                   | `true` or `false` | Enables [Skyupdater](https://www.skyost.eu/skyupdater.txt).                                            |
|            | metrics                   | `true` or `false` | Enables [bStats](https://bstats.org/).                                                                 |
| schematics |                           |                   |                                                                                                        |
|            | directory                 | directory         | The root directory of where to pick schematics.                                                        |
|            | random-rotation           | `true` or `false` | Whether schematics should be randomly rotated.                                                         |
|            | check-height              | `true` or `false` | _(Experimental)_ Whether the plugin should check for a floor before pasting a schematic.               |
|            | remove-worldedit-metadata | `true` or `false` | Whether the plugin should try to edit the schematics and remove all WorldEdit metadata (at startup).   |
| sapling    |                           |                   |                                                                                                        |
|            | oak                       | list of files     | [Oak trees](https://minecraft.gamepedia.com/Tree#Oak_tree) schematics.                                 |
|            | spruce                    | list of files     | [Spruce trees](https://minecraft.gamepedia.com/Tree#Spruce_tree) schematics.                           |
|            | birch                     | list of files     | [Birch trees](https://minecraft.gamepedia.com/Tree#Birch_tree) schematics.                             |
|            | jungle                    | list of files     | [Jungle trees](https://minecraft.gamepedia.com/Tree#Jungle_tree) schematics.                           |
|            | acacia                    | list of files     | [Acacia trees](https://minecraft.gamepedia.com/Tree#Acacia_tree) schematics.                           |
|            | dark-oak                  | list of files     | [Dark oak trees](https://minecraft.gamepedia.com/Tree#Dark_oak_tree) schematics.                       |
| mushroom   |                           |                   |                                                                                                        |
|            | brown                     | list of files     | [Huge brown mushrooms](https://minecraft.gamepedia.com/Huge_mushroom#Huge_brown_mushrooms) schematics. |
|            | red                       | list of files     | [Huge red mushrooms](https://minecraft.gamepedia.com/Huge_mushroom#Huge_red_mushrooms) schematics.     |

If you want to disable the plugin for a tree type, just put the blank list `[]` instead of a list of files.

## Commands and permissions
Just one command : **/owngarden** (or **/own-garden**) which displays some information about the plugin, the loaded schematics, etc... This command requires the permission **owngarden.command**.

## Bugs, improvements and feature requests
Got a bug ? Have a feature request ? Then post a ticket [here](https://github.com/Skyost/OwnGarden/issues) !

## Credits
OwnGarden (concept and logo) comes from an idea of [Noxon](https://www.bukkit.fr/topic/14863-owngarden-faites-pousser-vos-propres-arbres/). Big thanks to him.

A lot of thanks to [Eremilion](https://www.planetminecraft.com/project/realistictreesandmanyotherthingsworkinprogress/) and [Murokmakto](https://www.planetminecraft.com/project/custom-tree-pack-vol2-tropical-forest/) for their trees schematics.

## Donations
Development costs time and time is the money of life. Please feel free to donate.

[![PayPal Button](https://www.paypal.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XLEBVBMQNTXMY)