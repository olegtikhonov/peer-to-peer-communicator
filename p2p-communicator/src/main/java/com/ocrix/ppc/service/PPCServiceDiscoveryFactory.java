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

package com.ocrix.ppc.service;

import net.jxta.platform.NetworkManager;

import com.ocrix.ppc.discovery.GroupDiscovery;
import com.ocrix.ppc.discovery.PeerDiscovery;
import com.ocrix.ppc.discovery.PipeDiscovery;

/**
 * Defines a discovery
 * 
 */
public interface PPCServiceDiscoveryFactory {
	/**
	 * Creates a group discovery, that makes possible group discovery
	 * 
	 * @param manager
	 *            a network manager
	 * 
	 * @return {@link GroupDiscovery}
	 */
	GroupDiscovery createGroupDiscovery(NetworkManager manager);

	/**
	 * Creates a peer discovery, that makes possible peer discovery
	 * 
	 * @param manager
	 *            a network manager
	 * 
	 * @return {@link PeerDiscovery}
	 */
	PeerDiscovery createPeerDiscovery(NetworkManager manager);

	/**
	 * Creates a pipe discovery, that makes possible a pipe discovery
	 * 
	 * @param manager
	 *            a network manager
	 * 
	 * @return {@link PipeDiscovery}
	 */
	PipeDiscovery createPipeDiscovery(NetworkManager manager);
}
