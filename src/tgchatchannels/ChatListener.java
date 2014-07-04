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

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class ChatListener implements Listener {

	private ChannelsStorage storage;

	public ChatListener(ChannelsStorage storage) {
		this.storage = storage;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		UUID playerUUID = event.getPlayer().getUniqueId();
		PlayerData data = storage.getPlayerData(playerUUID);
		if (data.getCurrentChannel() == null) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Вы не можете говорить в несуществующий канал");
			return;
		}
		String channelName = data.getCurrentChannel();
		if (!storage.channelExists(channelName)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Вы не можете говорить в несуществующий канал");
			return;
		}
		event.setFormat("["+channelName+"] "+event.getFormat());
		ChannelData currentChannel = storage.getChannelData(channelName);
		if (!currentChannel.isInChannel(playerUUID)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Вы не можете говорить в канал в котором вы не находитесь");
			return;		
		}
		Iterator<Player> recipientsIt = event.getRecipients().iterator();
		while (recipientsIt.hasNext()) {
			UUID uuid = recipientsIt.next().getUniqueId();
			if (!currentChannel.isInChannel(uuid) && !currentChannel.isOwner(playerUUID)) {
				recipientsIt.remove();
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!storage.isPlayerDataExist(event.getPlayer().getUniqueId())) {
			storage.addToDefaultChannels(event.getPlayer().getUniqueId());
		}
	}

}
