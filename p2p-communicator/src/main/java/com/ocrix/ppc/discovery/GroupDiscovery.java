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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.impl.protocol.PeerGroupAdv;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PeerGroupAdvertisement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.Tag;

/**
 * Creates GroupDiscovery. This class intended provide lookup functionality for
 * finding groups on the network
 */
public class GroupDiscovery extends Discovery {

	public GroupDiscovery(NetworkManager manager) {
		super(manager);
	}

	/**
	 * Looks up a particular group. If null or an empty String is sent, then a
	 * function will return default net group
	 * 
	 * @param regex
	 *            - a name of the PeerGroup, also accepted wildcards, like
	 *            "*Group*" - will seek any group where its name contains
	 *            "Group", i.e. "MyGroup" or "MyCoolGroupIn"
	 * 
	 * @return {@link PeerGroupAdvertisement}
	 * 
	 * @throws IOException
	 * @throws PPCException
	 *             throws if no group was found
	 */
	public PeerGroupAdvertisement lookup(String regex) throws IOException,
			PPCException {
		PeerGroupAdvertisement peerGroupToBeFound = null;
		Map<String, PeerGroupAdvertisement> groups = findAll();
		
		/* Found something */
		if (groups != null) {
			/* user does not care about peerGroup, will return NetPeerGroup */
			if (regex == null || regex.isEmpty()) {
				peerGroupToBeFound = groups.get(Tag.DEFAULT_PEER_GROUP
						.getValue());
			} else { /* regex means something */
				peerGroupToBeFound = search(groups, regex);
			}
		} else {/* groups == null, nothing found */
			throw new PPCException(
					"Cannot find that responses following critarion " + regex);
		}
		return peerGroupToBeFound;
	}

	/**
	 * Looks up all group on the network
	 * 
	 * @return List<PeerGroupAdvertisement>
	 * 
	 * @throws IOException
	 */
	public List<PeerGroupAdvertisement> lookup() throws IOException {
		Enumeration<Advertisement> enumerator = null;
		PeerGroup parent = getDiscoveryManager().getNetPeerGroup();// .getParentGroup()

		enumerator = parent.getDiscoveryService().getLocalAdvertisements(
				DiscoveryService.GROUP, null, null);
		if (enumerator == null || !enumerator.hasMoreElements())//
			acquireGroupAdvertisements(null);

		return getAdv(enumerator);
	}

	/**
	 * Internal use. Looks up a NetGroup only
	 * 
	 * @return PeerGroupAdvertisement
	 * 
	 * @throws IOException
	 */
	private PeerGroupAdvertisement lookupNetGroup() throws IOException {
		PeerGroup parent = getDiscoveryManager().getNetPeerGroup()
				.getParentGroup();
		Enumeration<Advertisement> enumerator = parent.getDiscoveryService()
				.getLocalAdvertisements(DiscoveryService.GROUP, null, null);
		return (PeerGroupAdvertisement) getAdv(enumerator,
				Tag.DEFAULT_PEER_GROUP.getValue());
	}

	/**
	 * Searches for specific group, even it given by RegEx, i.e. "*MyGroup*"
	 * 
	 * @param groups
	 *            Map<String, PeerGroupAdvertisement>
	 * @param regEx
	 *            key for searching
	 * 
	 * @return PeerGroupAdvertisement
	 */
	private PeerGroupAdvertisement search(
			Map<String, PeerGroupAdvertisement> groups, String regEx) {
		Validator.validateGroupMap(groups);
		Validator.validateString(regEx);
		Set<String> keys = groups.keySet();
		String reg = regEx.replace("*", "");
		for (String key : keys) {
			if (key.contains(reg)) {
				return groups.get(key);
			}
		}
		return null;
	}

	/**
	 * Sometimes local cache does not contain advertisements, so there is a need
	 * for search once more
	 * 
	 * @param regex
	 *            key for searching
	 * 
	 * @return Enumeration<Advertisement> a list of found advertisements
	 * 
	 * @throws IOException
	 */
	private Enumeration<Advertisement> acquireGroupAdvertisements(String regex)
			throws IOException {
		// if enumerator is empty, send a discovery query
		NetworkConfigurator config = getDiscoveryManager().getConfigurator();
		Validator.validateNetworkConfigurator(config);

		if (regex == null || regex.isEmpty()) {
			getDiscovery().getRemoteAdvertisements(
					config.getPeerID().toString(), DiscoveryService.GROUP,
					null, null, DefaultParameter.THRESHOLD.getCode());
			// waits for a while
			getDiscoveryManager().waitForRendezvousConnection(
					DefaultParameter.WAITING_TIME.getCode());
			return getDiscovery().getLocalAdvertisements(
					DiscoveryService.GROUP, null, null);
		} else {
			getDiscovery().getRemoteAdvertisements(
					config.getPeerID().toString(), DiscoveryService.GROUP,
					Tag.NAME.getValue(), regex,
					DefaultParameter.THRESHOLD.getCode());
			// waits for a while
			getDiscoveryManager().waitForRendezvousConnection(
					DefaultParameter.WAITING_TIME.getCode());
			return getDiscovery().getLocalAdvertisements(
					DiscoveryService.GROUP, null, null);
		}
	}

	/**
	 * Performs a search for sub groups of the NetGroup
	 * 
	 * @return Map<String, PeerGroupAdvertisement>
	 * @throws IOException
	 */
	private Map<String, PeerGroupAdvertisement> findAll() throws IOException {
		Map<String, PeerGroupAdvertisement> groups = new HashMap<String, PeerGroupAdvertisement>();
		/* First - lookup default NetGroup */
		PeerGroupAdvertisement pga = lookupNetGroup();
		groups.put(pga.getName(), pga);

		/* Second - lookup the rest */
		List<PeerGroupAdvertisement> listGrp = new ArrayList<PeerGroupAdvertisement>();
		listGrp = lookup();
		for (PeerGroupAdvertisement pgas : listGrp)
			groups.put(pgas.getName(), pgas);

		return groups;
	}

	/**
	 * Returns a group advertisement
	 * 
	 * @param enumerator
	 *            a list of found group advertisements
	 * @param regex
	 *            a key for searching
	 * 
	 * @return {@link PeerGroupAdv}
	 */
	private PeerGroupAdv getAdv(Enumeration<Advertisement> enumerator,
			String regex) {
		if (enumerator != null)
			while (enumerator.hasMoreElements()) {
				Object advertisement = (Advertisement) enumerator.nextElement();
				if (advertisement instanceof PeerGroupAdv) {
					PeerGroupAdv adv = (PeerGroupAdv) advertisement;
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

	/**
	 * Returns a list of group advertisements
	 * 
	 * @param enumerator
	 *            a list of found group advertisements
	 * @return List<PeerGroupAdvertisement>
	 */
	private List<PeerGroupAdvertisement> getAdv(
			Enumeration<Advertisement> enumerator) {
		List<PeerGroupAdvertisement> groups = new ArrayList<PeerGroupAdvertisement>();
		if (enumerator != null)
			while (enumerator.hasMoreElements()) {
				Object advertisement = (Advertisement) enumerator.nextElement();
				if (advertisement instanceof PeerGroupAdv) {
					groups.add((PeerGroupAdv) advertisement);
				}
			}

		/* Means that no custom group is found, if so, return */
		if (groups.size() == 0)
			groups.add(getDiscoveryManager().getNetPeerGroup()
					.getPeerGroupAdvertisement());
		return groups;
	}
}
