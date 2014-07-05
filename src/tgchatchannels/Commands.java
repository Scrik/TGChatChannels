/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package tgchatchannels;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	private ChannelsStorage storage;

	public Commands(ChannelsStorage storage) {
		this.storage = storage;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
			String channelName = args[1];
			if (!storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Этот канал не существует");
				return true;
			}
			ChannelData data = storage.getChannelData(channelName);
			if (data.isOwner(uuid) || data.isInChannel(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы уже в этом канале");
				return true;
			}
			if (data.isPrivate() && !data.isInvited(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы не приглашены в данный канал");
				return true;
			}
			data.addPlayer(uuid);
			player.sendMessage(ChatColor.BLUE + "Вы присоединились к каналу "+channelName);
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("leave")) {
			String channelName = args[1];
			if (!storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Этот канал не существует");
				return true;
			}
			ChannelData data = storage.getChannelData(channelName);
			if (!data.isInChannel(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы не можете покинуть данный канал, вы в нём не находитесь");
				return true;
			}
			if (data.isOwner(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы не можете покинуть данный канал, вы его владелец");
				return true;
			}
			data.removePlayer(uuid);
			player.sendMessage(ChatColor.BLUE + "Вы покинули канал "+channelName);
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("switch")) {
			String channelName = args[1];
			if (!storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Этот канал не существует");
				return true;
			}
			storage.getPlayerData(uuid).setCurrentChannel(channelName);
			player.sendMessage(ChatColor.BLUE + "Теперь вы говорите в канал "+channelName);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
			String channelName = args[1];
			if (!player.hasPermission("tgchat.create")) {
				player.sendMessage(ChatColor.RED + "У вас нет прав на создание каналов");
				return true;
			}
			if (storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Такой канал уже существует");
				return true;
			}
			storage.addChannel(uuid, channelName);
			player.sendMessage(ChatColor.BLUE + "Вы создали канал "+channelName);
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			String channelName = args[1];
			if (!storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Этот канал не существует");
				return true;
			}
			ChannelData data = storage.getChannelData(channelName);
			if (!data.isOwner(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы не владелец данного канала");
				return true;
			}
			storage.removeChannel(channelName);
			player.sendMessage(ChatColor.BLUE + "Вы удалили канал "+channelName);
			return true;
		} else if (args.length == 3 && args[0].equalsIgnoreCase("invite")) {
			String channelName = args[1];
			if (!storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Этот канал не существует");
				return true;
			}
			ChannelData data = storage.getChannelData(channelName);
			if (!data.isOwner(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы не владелец данного канала");
				return true;
			}
			Player inviting = Bukkit.getPlayerExact(args[1]);
			if (inviting == null) {
				player.sendMessage(ChatColor.RED + "Данный игрок не найден");
			}
			data.invite(inviting.getUniqueId());
			player.sendMessage(ChatColor.BLUE + "Вы пригласили игрока "+args[1]+" в канал "+channelName);
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("public")) {
			String channelName = args[1];
			if (!player.hasPermission("tgchat.public")) {
				player.sendMessage(ChatColor.RED + "У вас нет прав на переключение каналов в публичный режим");
				return true;
			}
			if (!storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Этот канал не существует");
				return true;
			}
			ChannelData data = storage.getChannelData(channelName);
			if (!data.isOwner(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы не владелец данного канала");
				return true;
			}
			data.setPublic();
			player.sendMessage(ChatColor.BLUE + "Вы сделали публичным канал "+channelName);
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("private")) {
			String channelName = args[1];
			if (!storage.channelExists(channelName)) {
				player.sendMessage(ChatColor.RED + "Этот канал не существует");
				return true;
			}
			ChannelData data = storage.getChannelData(channelName);
			if (!data.isOwner(uuid)) {
				player.sendMessage(ChatColor.RED + "Вы не владелец данного канала");
				return true;
			}
			data.setPrivate();
			player.sendMessage(ChatColor.BLUE + "Вы сделали приватным канал "+channelName);
			return true;
		}
		return false;
	}

}
