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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeID;
import net.jxta.pipe.PipeService;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.TemporaryFolder;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.discovery.DiscoveryFactory;
import com.ocrix.ppc.discovery.PipeDiscovery;


public class PipeDiscoveryTest {
	/* Test members */
	private static DiscoveryFactory discoveryFactory;
	private static NetworkManager testManager;
	private static PipeDiscovery pipeDiscovery;
	private static PeerGroup defaultPeerGroup;
	private static PipeID pipeID;
	private static File file = null;
	private static final Logger LOG = Logger.getLogger(PipeDiscoveryTest.class);

	@BeforeClass
	public static void setUp() {
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());

		
		
		
		file = new File(VerificationConstants.TARGET + "/"
				+ PipeDiscoveryTest.class.getName());
		
		Utils.deleteDir(file);
		

		try {
			/* Creates and inits a NetworkManager */
			testManager = new NetworkManager(ConfigMode.ADHOC,
					PipeDiscoveryTest.class.getSimpleName(), file.toURI());

			/* Registers shutdown hook in order to properly stop JXTA network */
			testManager.registerShutdownHook();

			/* Starts a network */
			defaultPeerGroup = startNetwork(testManager);

		} catch (Exception e) {
			LOG.error(e);
		}

		discoveryFactory = new DiscoveryFactory();
		/* Creates a pipe discovery */
		pipeDiscovery = discoveryFactory.createPipeDiscovery(testManager);

		Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".alice"));
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ ".BiDiPeerTest"));
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ ".BiDiPeerTest"));
		testManager.waitForRendezvousConnection(10000);

	}

	@Test
	public void shallPipeDiscoveryInstance() {
		assertNotNull(pipeDiscovery);
	}

	@Test
	public void shallPipeLookup() {
		try {
			/* Creates UnicastPipe */
			createPipe(PipeService.UnicastType);
			/* Creates UnicastSecurePipe */
			createPipe(PipeService.UnicastSecureType);
			/* Creates PropagatePipe */
			createPipe(PipeService.PropagateType);
			/* lookups for created and published pipes */
			List<PipeAdvertisement> pipes = pipeDiscovery.lookup();
			/*
			 * Verifies that a number of found pipe is eq to number of created
			 * pipes
			 */
			assertTrue((pipes.size() >= VerificationConstants.CREATED_PIPE_NUMBER));
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookupNullRegEx() {
		try {
			createPipe(PipeService.UnicastType);
			/* Looks up for any found pipe */
			PipeAdvertisement pipeAdv = pipeDiscovery.lookup(null);
			assertNotNull(pipeAdv);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookupRegEx() {
		try {
			String pipeRegex = "*" + PipeService.PropagateType.substring(5, 9)
					+ "*";
			createPipe(PipeService.PropagateType);
			PipeAdvertisement pipeAdv = pipeDiscovery.lookup(pipeRegex);
			assertNotNull(pipeAdv);
			assertTrue(pipeAdv.getName().contains(PipeService.PropagateType));
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	@Test
	public void shallLookupWrongRegEx() {
		try {
			String pipeRegex = "*wrong_regex_pipe*";
			createPipe(PipeService.UnicastSecureType);
			PipeAdvertisement pipeAdv = pipeDiscovery.lookup(pipeRegex);
			assertNull(pipeAdv);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	private void createPipe(String pipeType) {
		try {
			/* Creates a pipeID */
			pipeID = PipeID.create(URI.create(IDFactory.newPipeID(
					defaultPeerGroup.getPeerGroupID()).toString()));
			/* Creates unicast pipe advertisement */
			PipeAdvertisement pipeAdv = (PipeAdvertisement) AdvertisementFactory
					.newAdvertisement(PipeAdvertisement.getAdvertisementType());
			/* Sets a pipe name */
			pipeAdv.setName("Test" + pipeType);
			/* Sets a pipe ID */
			pipeAdv.setPipeID(pipeID);
			/* Sets a pipe type */
			pipeAdv.setType(PipeService.UnicastSecureType);
			defaultPeerGroup.getDiscoveryService().publish(pipeAdv);
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	private static PeerGroup startNetwork(NetworkManager manager)
			throws PeerGroupException, IOException {
		if (!manager.isStarted())
			defaultPeerGroup = manager.startNetwork();
		else
			defaultPeerGroup = manager.getNetPeerGroup();
		return defaultPeerGroup;
	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();
	}
}
