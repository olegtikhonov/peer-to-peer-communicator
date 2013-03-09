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

import org.apache.log4j.Logger;
//import net.jxta.document.AdvertisementFactory;
import net.jxta.document.AdvertisementFactory;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
//import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
//import net.jxta.peergroup.PeerGroupFactory;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.service.PPCService;

/**
 * Creates a PeerGroup.
 */
public class GroupService implements PPCService<PeerGroup> {
	/* Local logger */
	private static final Logger LOG = Logger.getLogger(GroupService.class);

	/**
	 * Creates a {@link PeerGroup}.
	 * 
	 * @param nameOfInstance
	 * @param netGroup
	 */
	public PeerGroup create(String nameOfInstance, PeerGroup netGroup) {
		PeerGroup newPg = null;

		try {
			Validator.validatePeerGroup(netGroup);
			ModuleImplAdvertisement newGroupImpl = netGroup
					.getAllPurposePeerGroupImplAdvertisement();
			netGroup.getDiscoveryService().publish(newGroupImpl,
					PeerGroup.DEFAULT_LIFETIME, PeerGroup.DEFAULT_EXPIRATION);
			netGroup.getDiscoveryService().remotePublish(newGroupImpl,
					PeerGroup.DEFAULT_LIFETIME);

			PeerGroupAdvertisement newPGAdv = (PeerGroupAdvertisement) AdvertisementFactory
					.newAdvertisement(PeerGroupAdvertisement
							.getAdvertisementType());
			newPGAdv.setName(nameOfInstance);
			newPGAdv.setPeerGroupID(IDFactory.newPeerGroupID());
			newPGAdv.setModuleSpecID(newGroupImpl.getModuleSpecID());
			newPg = netGroup.newGroup(newPGAdv);

			/*
			 * publish this advertisement, (send out to other peers and
			 * rendezvous peer)
			 */
			netGroup.getDiscoveryService().remotePublish(newPGAdv);

		} catch (Exception see) {
			LOG.error("Group creation failed with " + see.toString());
		}

		return newPg;
	}
}
