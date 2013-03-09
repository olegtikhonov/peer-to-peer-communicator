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

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
//import org.junit.Rule;
//import org.junit.rules.TemporaryFolder;
import com.ocrix.ppc.VerificationConstants;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;

/**
 * Internal use only ! Intended for testing. Creates new peer.
 * 
 */
public class NewPeerThread implements Runnable {
	// @Rule
	// public static TemporaryFolder tempStore = new TemporaryFolder();
	private static File file = null;
	private PeerGroup peerGroup;
	private NetworkManager manager;
	private static final String NAME = NewPeerThread.class.getName();
	private static final Logger LOG = Logger.getLogger(NewPeerThread.class);

	public NewPeerThread(PeerGroup peerGroup) {
		file = new File(VerificationConstants.TARGET + "/" + NAME);
		this.peerGroup = peerGroup;
	}

	public void run() {
		try {
			manager = new NetworkManager(ConfigMode.ADHOC,
					NewPeerThread.class.getSimpleName(), file.toURI());
			manager.setPeerID(IDFactory.newPeerID(peerGroup.getPeerGroupID()));
			manager.getConfigurator().setName(VerificationConstants.PEER_NAME);
			startNetwork(manager);
		} catch (IOException e) {
			LOG.error(e);
		} catch (PeerGroupException e) {
			LOG.error(e);
		}
	}

	public void shutdownNetwork() {
		manager.stopNetwork();
	}

	private void startNetwork(NetworkManager manager)
			throws PeerGroupException, IOException {
		if (!manager.isStarted())
			manager.startNetwork();
		if (file != null && file.exists()) {
			file.delete();
		}
	}
}
