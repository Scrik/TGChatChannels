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

import java.util.HashSet;
import java.util.UUID;

public class ChannelData {

	private UUID owner;
	private boolean privateChannel = true;
	private HashSet<UUID> players = new HashSet<UUID>();

	private HashSet<UUID> invites = new HashSet<UUID>();

	protected ChannelData() {
		privateChannel = false;
	}

	public ChannelData(UUID owner) {
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

	public boolean isOwner(UUID uuid) {
		return owner != null ? owner.equals(uuid) : false;
	}

	public boolean isPrivate() {
		return privateChannel;
	}

	public void setPrivate() {
		privateChannel = true;
	}

	public void setPublic() {
		privateChannel = false;
	}

	public boolean isInChannel(UUID player) {
		return players.contains(player);
	}

	public void addPlayer(UUID player) {
		players.add(player);
	}

	public void removePlayer(UUID player) {
		players.remove(player);
	}

	protected HashSet<UUID> getPlayers() {
		return players;
	}

	public void invite(UUID uuid) {
		invites.add(uuid);
	}

	public boolean isInvited(UUID uuid) {
		return invites.contains(uuid);
	}

}
