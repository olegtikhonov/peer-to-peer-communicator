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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.jxta.exception.PeerGroupException;
//import net.jxta.peergroup.NetPeerGroupFactory;
//import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
//import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.PeerAdvertisement;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.discovery.DiscoveryFactory;
import com.ocrix.ppc.discovery.PeerDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.peer.Peer;
import com.ocrix.ppc.peer.PeerFactory;
import com.ocrix.ppc.peer.ReceiverPeer;

/**
 * {@link DiscoveryPeer}
 */
public class DiscoveryPeerTest {
	/* Test class members */
	private static DiscoveryFactory discoveryFactory;
	private static NetworkManager testManager;
	private static PeerDiscovery peerDiscovery = null;
	private static PeerGroup defaultPeerGroup = null;
	private static NewPeerThread newPeerThread = null;
	private static PeerFactory peerFactory = null;
	private static File file = null;
	private static final Logger LOG = Logger.getLogger(DiscoveryPeerTest.class);
	private static final String NAME = DiscoveryPeerTest.class.getName();

	@BeforeClass
	public static void setUp() {
		try {
			/* Logger off */
			System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
					java.util.logging.Level.OFF.toString());
			/* Creates and inits a NetworkManager */
			
			file = new File(VerificationConstants.TARGET + "/." + NAME);

			Utils.deleteDir(file);
			
			testManager = new NetworkManager(ConfigMode.ADHOC, NAME, file.toURI());
//			testManager.setPeerID(PeerID.create(file.toURI()));
			//NetworkManager.setPeerId
			
			
//			NetworkConfigurator nc = NetworkConfigurator.newAdHocConfiguration(file.toURI());
//			nc.setName(NAME);
//			NetPeerGroupFactory factory = new NetPeerGroupFactory(nc.getPlatformConfig(), file.toURI());
//
//	        PeerGroup netPeerGroup = factory.getNetPeerGroup();
//	        nc.save();
	        
	        
			/* Starts a network */
			defaultPeerGroup = testManager.startNetwork();

			/* Creates DescoveryFactory */
			discoveryFactory = new DiscoveryFactory();
			/* Creates a peerDiscovery */
			peerDiscovery = discoveryFactory.createPeerDiscovery(testManager);

			newPeerThread = new NewPeerThread(defaultPeerGroup);

			peerFactory = new PeerFactory();
		} catch (Exception e) {
			if(file.exists()){
				file = new File(VerificationConstants.TARGET + "/." + NAME);
			}
			
			LOG.error(e);
		}
	}

	@Test
	public void shallCreatePeerDiscoveryNotNull() {
		try {
			/* Checks a created object */
			assertNotNull(peerDiscovery);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookupRegexNull() throws IOException, PeerGroupException {
		try {
			ExecutorService es = runPeerThread(defaultPeerGroup);
			/* Performs lookup, tries to find a peer */
			PeerAdvertisement peerAdv = peerDiscovery.lookup(null);
			/* If a peer is found, peerAdv shouldn't be a null */
			assertNotNull(peerAdv);
			waitFor(es);

		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookup() throws IOException, PPCException {
		try {
			ReceiverPeer rp = peerFactory.createReceiverPeer(
					VerificationConstants.PEER_NAME,
					VerificationConstants.TARGET + "/" + "."
							+ VerificationConstants.PEER_NAME + "_1");
			
			
			PPCUtils.sleep(500);
			peerDiscovery.lookup();
			PPCUtils.sleep(500);
			PeerAdvertisement peerAdv = peerDiscovery
					.lookup(VerificationConstants.PEER_NAME);
			if (peerAdv != null) {
				assertEquals(VerificationConstants.PEER_NAME, peerAdv.getName());
			} else {
				LOG.info("ADV IS NULL");
			}

			destroy(rp);
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "."
					+ VerificationConstants.PEER_NAME + "_1"));
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookupRegEx() throws IOException, PPCException {
		try {
			String regEx = "*" + VerificationConstants.PEER_NAME.substring(3)
					+ "*";
			ReceiverPeer rp = peerFactory.createReceiverPeer(
					VerificationConstants.PEER_NAME,
					VerificationConstants.TARGET + "/" + "."
							+ VerificationConstants.PEER_NAME + "_2");
			peerDiscovery.lookup();
			PeerAdvertisement peerAdv = peerDiscovery.lookup(regEx);
			if (peerAdv != null)
				assertEquals(VerificationConstants.PEER_NAME, peerAdv.getName());
			else {
				LOG.info(DiscoveryPeerTest.class.getName()
						+ " could not find a peer by regex");
			}
			destroy(rp);
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "."
					+ VerificationConstants.PEER_NAME + "_2"));
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookupWrongPeerName() throws IOException {
		try {
			ExecutorService es = runPeerThread(defaultPeerGroup);
			PeerAdvertisement peerAdv = peerDiscovery.lookup("wrong_peer_name");
			assertNull(peerAdv);
			waitFor(es);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookupList() throws PPCException, IOException {
		try {
			List<PeerAdvertisement> peers = peerDiscovery.lookup();
			assertNotNull(peers);
			for (PeerAdvertisement pa : peers) {
				assertTrue(!pa.getName().isEmpty());
			}
		} catch (Exception e) {
			LOG.error(e);
		}

	}

	// ========================================================
	// private functions
	// ========================================================
	private ExecutorService runPeerThread(PeerGroup peerGroup) {
		/* Creates fixed thread pool */
		ExecutorService es = Executors.newFixedThreadPool(1);
		/* Runs a thread */
		es.execute(newPeerThread);
		return es;
	}

	private void waitFor(ExecutorService es) {
		if (es != null) {
			es.shutdown();
			/* Waits for thread interrupting */
			while (!es.isTerminated()) {
				testManager.waitForRendezvousConnection(2000);
			}
		}
	}

	private void destroy(Peer peer) {
		if (peer != null)
			peer.destroy();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();
		if (file != null && file.exists()) {
			file.delete();
		}
	}
}
