/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ocrix.ppc.discovery;

import net.jxta.platform.NetworkManager;

/**
 * Creates different type of discovery objects such as: <br>
 * <b>GroupDiscovery</b> - creates the {@link GroupDiscovery} <br>
 * <b>PeerDiscovery</b> - creates the {@link PeerDiscovery} <br>
 * <b>PipeDiscovery</b> - creates the {@link PipeDiscovery}
 */
public class DiscoveryFactory {

	public DiscoveryFactory() {
	}

	/**
	 * Creates a group discovery. Helps to find group(s) on the network.
	 * 
	 * @param manager
	 *            {@link NetworkManager}
	 * 
	 * @return GroupDiscovery
	 */
	public GroupDiscovery createGroupDiscovery(NetworkManager manager) {
		return new GroupDiscovery(manager);
	}

	/**
	 * Creates a new peer discovery. Helps to find peers on the network.
	 * 
	 * @param manager
	 *            {@link NetworkManager}
	 * 
	 * @return PeerDiscovery
	 */
	public PeerDiscovery createPeerDiscovery(NetworkManager manager) {
		return new PeerDiscovery(manager);
	}

	/**
	 * Creates pipe discovery instance
	 * 
	 * @param manager
	 *            {@link NetworkManager}
	 * 
	 * @return PipeDiscovery
	 */
	public PipeDiscovery createPipeDiscovery(NetworkManager manager) {
		return new PipeDiscovery(manager);
	}
}
