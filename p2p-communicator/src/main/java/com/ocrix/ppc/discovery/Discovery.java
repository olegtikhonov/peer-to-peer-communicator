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

import com.ocrix.ppc.commons.Validator;

import net.jxta.discovery.DiscoveryService;
import net.jxta.platform.NetworkManager;

/**
 * Defines a skeleton for sub-classes
 */
public abstract class Discovery {
	/*
	 * Discovery service gives a possibility to find published resources over
	 * JXTA network
	 */
	private DiscoveryService discovery = null;
	/* Network manager is responsible for managing JXTA network */
	private NetworkManager discoveryManager = null;

	/**
	 * Creates Discovery service as a base class
	 * 
	 * @param manager
	 *            @see NetworkManager
	 */
	public Discovery(NetworkManager manager) {
		/* Sets network manager */
		setDiscoveryManager(manager);

		/* Creates a new instance of the DiscoveryService */
		setDiscovery(manager.getNetPeerGroup().getDiscoveryService());
	}

	/**
	 * Attains {@link DiscoveryService}
	 * 
	 * @return a discovery service
	 */
	public DiscoveryService getDiscovery() {
		return discovery;
	}

	/**
	 * Attains a JXTA network manager
	 * 
	 * @return {@link NetworkManager}
	 */
	public NetworkManager getDiscoveryManager() {
		return discoveryManager;
	}

	/**
	 * Sets up a {@link DiscoveryService}
	 * 
	 * @param discovery
	 *            a service that makes possible to discover resources over JXTA
	 *            network
	 */
	protected void setDiscovery(DiscoveryService discovery) {
		Validator.validateNetPeerGroup(getDiscoveryManager().getNetPeerGroup());
		this.discovery = discovery;
	}

	/**
	 * Sets up {@link NetworkManager}
	 * 
	 * @param discoveryManager
	 */
	protected void setDiscoveryManager(NetworkManager discoveryManager) {
		Validator.validateNetworkManager(discoveryManager);
		this.discoveryManager = discoveryManager;
	}
}
