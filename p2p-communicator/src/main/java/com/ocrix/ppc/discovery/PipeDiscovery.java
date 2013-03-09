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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.impl.protocol.PipeAdv;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PipeAdvertisement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.Tag;

/**
 * Provides functionality for the pipe look up
 */
public class PipeDiscovery extends Discovery {

	public PipeDiscovery(NetworkManager manager) {
		super(manager);
	}

	/**
	 * Looks up a pipe advertisement. If null is passed, will returned first
	 * found pipeAdv. Accepts wildcards such as: "*" +
	 * substring_of_the_pipe_name + "*"
	 * 
	 * @param regex
	 *            - fully qualified pipeAdv name or wildcard
	 * 
	 * @return a certain pipeAdv
	 * 
	 * @throws IOException
	 */
	public PipeAdvertisement lookup(String regex) throws IOException {
		PeerGroup parent = getDiscoveryManager().getNetPeerGroup();
		Enumeration<Advertisement> enumerator = parent.getDiscoveryService()
				.getLocalAdvertisements(DiscoveryService.ADV, null, null);
		return getPipeAdvertisement(enumerator, regex);
	}

	public PipeAdvertisement lookup(String regex, DiscoveryService service)
			throws IOException {
		synchronized (service) {
			Enumeration<Advertisement> enumerator = service
					.getLocalAdvertisements(DiscoveryService.ADV, null, null);
			return getPipeAdvertisement(enumerator, regex);
		}
	}

	/**
	 * Looks up for all pipeAdv in the PeerGroup
	 * 
	 * @return a list of found peers
	 * 
	 * @throws PPCException
	 *             will be thrown if no peer found
	 * @throws IOException
	 */
	public List<PipeAdvertisement> lookup() throws PPCException, IOException {
		// Does not check first a local host
		Enumeration<Advertisement> enumerator = acquirePipeAdvertisements(null);

		if (enumerator != null) {

			return getAdv(enumerator);
		} else {
			System.err.println("could not find peers");
			// throw new PPCException("could not find peers");
		}
		return null;
	}

	// ================================================
	// PRIVATE FUNCTIONS
	// ================================================
	private PipeAdv getPipeAdv(Enumeration<Advertisement> enumerator,
			String regex) {
		if (enumerator != null)
			while (enumerator.hasMoreElements()) {
				Object advertisement = (Advertisement) enumerator.nextElement();
				if (advertisement instanceof PipeAdv) {
					PipeAdv adv = (PipeAdv) advertisement;

					if (regex != null
							&& adv.getName().contains(regex.replace("*", "")))
						return adv;
					else if (regex == null || regex.isEmpty())// regex == null
																// means that
																// user do not
																// know exactly
																// what to be
																// found
						return adv;
				}
			}
		return null;
	}

	private Enumeration<Advertisement> acquirePipeAdvertisements(String regex)
			throws IOException {
		// if enumerator is empty, send a discovery query
		NetworkConfigurator config = getDiscoveryManager().getConfigurator();
		Validator.validateNetworkConfigurator(config);
		Validator.validateObjNotNull(getDiscovery());

		if (regex == null || regex.isEmpty()) {// config.getPeerID().toString()
			getDiscovery().getRemoteAdvertisements(null, DiscoveryService.ADV,
					null, null, DefaultParameter.THRESHOLD.getCode());

			// waits for a while
			getDiscoveryManager().waitForRendezvousConnection(
					DefaultParameter.WAITING_TIME.getCode());
			return getDiscovery().getLocalAdvertisements(DiscoveryService.ADV,
					null, null);
		} else {
			/* Sends a discovery query knowing peer name */
			getDiscovery().getRemoteAdvertisements(null, DiscoveryService.ADV,
					Tag.NAME.getValue(), regex,
					DefaultParameter.THRESHOLD.getCode());
			// waits for a while
			getDiscoveryManager().waitForRendezvousConnection(
					DefaultParameter.WAITING_TIME.getCode());
			/* Gest all found peer locally and returns Enumeration */
			return getDiscovery().getLocalAdvertisements(DiscoveryService.ADV,
					Tag.NAME.getValue(), regex);
		}
	}

	private List<PipeAdvertisement> getAdv(Enumeration<Advertisement> enumerator) {
		List<PipeAdvertisement> groups = null;
		if (enumerator != null) {
			groups = new ArrayList<PipeAdvertisement>(1);
			while (enumerator.hasMoreElements()) {
				Object advertisement = (Advertisement) enumerator.nextElement();
				if (advertisement instanceof PipeAdv) {
					groups.add((PipeAdv) advertisement);
				}
			}
		}
		return groups;
	}

	private PipeAdvertisement getPipeAdvertisement(
			Enumeration<Advertisement> enumerator, String searchKey)
			throws IOException {
		if ((enumerator != null && searchKey == null)
				|| (enumerator != null && searchKey != null)) {
			return getPipeAdv(enumerator, searchKey);
		} else {
			enumerator = acquirePipeAdvertisements(searchKey);
			return getPipeAdv(enumerator, searchKey);
		}
	}
}
