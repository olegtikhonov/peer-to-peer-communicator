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

package com.ocrix.ppc.service.impl;

import net.jxta.platform.NetworkManager;

import com.ocrix.ppc.discovery.DiscoveryFactory;
import com.ocrix.ppc.discovery.GroupDiscovery;
import com.ocrix.ppc.discovery.PeerDiscovery;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.service.PPCServiceDiscoveryFactory;

/**
 * {@link PPCServiceDiscoveryFactory} implementation
 */
public class PPCServiceDiscoveryFactoryImpl implements
		PPCServiceDiscoveryFactory {

	// @Override
	public GroupDiscovery createGroupDiscovery(NetworkManager manager) {
		DiscoveryFactory discoveryFactory = new DiscoveryFactory();
		return discoveryFactory.createGroupDiscovery(manager);
	}

	// @Override
	public PeerDiscovery createPeerDiscovery(NetworkManager manager) {
		DiscoveryFactory discoveryFactory = new DiscoveryFactory();
		return discoveryFactory.createPeerDiscovery(manager);
	}

	// @Override
	public PipeDiscovery createPipeDiscovery(NetworkManager manager) {
		DiscoveryFactory discoveryFactory = new DiscoveryFactory();
		return discoveryFactory.createPipeDiscovery(manager);
	}
}
