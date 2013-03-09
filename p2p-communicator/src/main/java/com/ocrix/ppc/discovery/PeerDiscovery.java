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
import net.jxta.impl.protocol.PeerAdv;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PeerAdvertisement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.Tag;

/**
 * Creates PeerDiscovery and provides lookup functionality. <br>
 * Options:</br>
 * <ul>
 * <li>lookup(String regex) - returns a peer advertisement, if null passed,
 * returns the first found peer adv. if nothing was found a PpcException will be
 * thrown</li>
 * <li>
 * lookup - returns a list of found peer adv, if nothing was found a
 * PpcException will be thrown</li>
 * </ul>
 * 
 */
public class PeerDiscovery extends Discovery {

	/**
	 * Creates PeerDiscovery
	 * 
	 * @param manager
	 *            {@link NetworkManager}
	 */
	public PeerDiscovery(NetworkManager manager) {
		super(manager);
	}

	/**
	 * Seeking for certain peer, if regex = null, will return first found peer
	 * 
	 * @param regex
	 *            a name of the peer, accepts wildcard, such as "*XXX*", for
	 *            instance, *Peer*, "*sub_string*
	 * 
	 * @return {@link PeerAdvertisement}
	 * 
	 * @throws IOException
	 */
	public PeerAdvertisement lookup(String regex) throws IOException {
		return lookupPeer(regex);
	}

	/**
	 * Looks up for peers found on the network
	 * 
	 * @return List<PeerAdvertisement>
	 * 
	 * @throws PPCException
	 *             when no peer is found
	 * 
	 * @throws IOException
	 */
	public List<PeerAdvertisement> lookup() throws PPCException, IOException {
		// Does not check first a local host
		Enumeration<Advertisement> enumerator = acquirePeerAdvertisements(null);

		if (enumerator != null) {
			return getAdv(enumerator);
		} else {
			throw new PPCException("could not find peers");
		}
	}

	// ===============================================
	// private functions
	// ===============================================
	/**
	 * Seeks for certain peer
	 * 
	 * @param regex
	 *            - peer to be looked up, accepts also wildcard expression
	 * 
	 * @return found peer
	 * 
	 * @throws IOException
	 */
	private PeerAdvertisement lookupPeer(String regex) throws IOException {
		PeerGroup parent = getDiscoveryManager().getNetPeerGroup();
		Enumeration<Advertisement> enumerator = parent.getDiscoveryService()
				.getLocalAdvertisements(DiscoveryService.PEER, null, null);

		if (enumerator != null && regex == null) {
			return getAdv(enumerator, regex);
		} else if (enumerator == null || !contains(enumerator, regex)) {
			enumerator = acquirePeerAdvertisements(regex);
		}

		return getAdv(enumerator, regex);
	}

	/**
	 * Looks up sending discovery query first, before looking at local cache
	 * 
	 * @param regex
	 *            peer to be sought
	 * 
	 * @return {@link Enumeration<Advertisement> }
	 * 
	 * @throws IOException
	 */
	private Enumeration<Advertisement> acquirePeerAdvertisements(String regex)
			throws IOException {
		// if enumerator is empty, send a discovery query
		NetworkConfigurator config = getDiscoveryManager().getConfigurator();
		Validator.validateNetworkConfigurator(config);

		if (regex == null || regex.isEmpty()) {// config.getPeerID().toString()
			
			//String peerid, int type, String attribute, String value, int threshold
			getDiscovery().getRemoteAdvertisements(config.getPeerID().toString(), DiscoveryService.PEER,
					null, null, DefaultParameter.THRESHOLD.getCode());
			// waits for a while
			getDiscoveryManager().waitForRendezvousConnection(
					DefaultParameter.WAITING_TIME.getCode());
			return getDiscovery().getLocalAdvertisements(DiscoveryService.PEER,
					null, null);
		} else {
			/* Sends a discovery query knowing peer name */
			getDiscovery().getRemoteAdvertisements(null, DiscoveryService.PEER,
					Tag.NAME.getValue(), regex,
					DefaultParameter.THRESHOLD.getCode());
			// waits for a while
			getDiscoveryManager().waitForRendezvousConnection(
					DefaultParameter.WAITING_TIME.getCode());
			/* Gest all found peer locally and returns Enumeration */
			return getDiscovery().getLocalAdvertisements(DiscoveryService.PEER,
					Tag.NAME.getValue(), regex);
		}
	}

	/**
	 * Checks if certain peer name is containing in Enumeration
	 * 
	 * @param enumerator
	 *            {@link Enumeration<Advertisement>}
	 * @param searchKey
	 *            String
	 * 
	 * @return <b>true</b> if searchKey found, <b>false</b> otherwise
	 */
	private boolean contains(Enumeration<Advertisement> enumerator,
			String searchKey) {
		if (enumerator != null) {
			while (enumerator.hasMoreElements()) {
				Advertisement advertisement = (Advertisement) enumerator
						.nextElement();
				if (advertisement instanceof PeerAdv) {
					if (((PeerAdv) advertisement).getName().contains(
							searchKey.replace("*", "")))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns a list of found PeerAdvertisement
	 * 
	 * @param enumerator
	 *            {@link Enumeration<Advertisement>}
	 * 
	 * @return {@link List<PeerAdvertisement>}
	 */
	private List<PeerAdvertisement> getAdv(Enumeration<Advertisement> enumerator) {
		List<PeerAdvertisement> groups = new ArrayList<PeerAdvertisement>();
		if (enumerator != null)
			while (enumerator.hasMoreElements()) {
				Object advertisement = (Advertisement) enumerator.nextElement();
				if (advertisement instanceof PeerAdv) {
					groups.add((PeerAdv) advertisement);
				}
			}
		return groups;
	}

	/**
	 * Returns a found peer padvertisement
	 * 
	 * @param enumerator
	 *            {@link Enumeration<Advertisement>}
	 * @param regex
	 *            peer name to be sought
	 * 
	 * @return peer advertisement
	 */
	private PeerAdv getAdv(Enumeration<Advertisement> enumerator, String regex) {
		if (enumerator != null)
			while (enumerator.hasMoreElements()) {
				Object advertisement = (Advertisement) enumerator.nextElement();
				if (advertisement instanceof PeerAdv) {
					PeerAdv adv = (PeerAdv) advertisement;
					if (regex != null
							&& adv.getName().contains(regex.replace("*", "")))
						return adv;
					else if (regex == null || regex.isEmpty())// regex == null
																// means that
																// user does not
																// know exactly
																// what to be
																// found
						return adv;
				}
			}
		return null;
	}
}
