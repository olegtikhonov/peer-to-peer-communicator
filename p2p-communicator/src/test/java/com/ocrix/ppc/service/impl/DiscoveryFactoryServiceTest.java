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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.discovery.GroupDiscovery;
import com.ocrix.ppc.discovery.PeerDiscovery;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.service.impl.GroupService;
import com.ocrix.ppc.service.impl.PPCServiceDiscoveryFactoryImpl;
import com.ocrix.ppc.service.impl.PPCServicePipeFactoryImpl;

/**
 * Tests {@link PPCServiceDiscoveryFactoryImpl}.
 */
public class DiscoveryFactoryServiceTest {
	private static File file = null;
	private static NetworkManager testManager;
	private static PeerGroup pg1;
	private static PPCServiceDiscoveryFactoryImpl discoveryFactoryService = null;

	@BeforeClass
	public static void setUp() throws Exception {
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());

		file = new File(VerificationConstants.TARGET + "/"
				+ DiscoveryFactoryServiceTest.class.getName());

		testManager = new NetworkManager(ConfigMode.ADHOC,
				DiscoveryFactoryServiceTest.class.getSimpleName(), file.toURI());

		testManager.registerShutdownHook();
		/* Starts a network */
		pg1 = testManager.startNetwork();

		discoveryFactoryService = new PPCServiceDiscoveryFactoryImpl();
	}

	@Test
	public void shallCreateGroupDiscovery() throws IOException {
		GroupDiscovery groupDiscovery = discoveryFactoryService
				.createGroupDiscovery(testManager);
		assertNotNull(groupDiscovery);
		List<PeerGroupAdvertisement> foundGroups = groupDiscovery.lookup();
		for (PeerGroupAdvertisement pga : foundGroups) {
			assertTrue(!pga.toString().isEmpty());
		}
	}

	@Test
	public void shallCreateGroupDiscoveryNewPeerGroup() throws IOException,
			PPCException {
		String myGroupName = "PubTest";
		GroupService gs = new GroupService();
		PeerGroup pg = gs.create(myGroupName, pg1);
		assertNotNull(pg);
		GroupDiscovery groupDiscovery = discoveryFactoryService
				.createGroupDiscovery(testManager);
		PeerGroupAdvertisement peerGroup = groupDiscovery.lookup(myGroupName);
		assertNotNull(peerGroup);
		assertEquals(myGroupName, peerGroup.getName());
	}

	@Test
	public void shallCreatePeerDiscovery() throws PPCException, IOException {
		PeerDiscovery peerDiscovery = discoveryFactoryService
				.createPeerDiscovery(testManager);
		List<PeerAdvertisement> foundPeers = peerDiscovery.lookup();
		for (PeerAdvertisement pa : foundPeers) {
			assertTrue(!pa.getName().isEmpty());
		}
	}

	@Test
	public void shallCreatePipeDiscovery() throws PPCException, IOException {
		PPCServicePipeFactoryImpl pipeFactoryService = new PPCServicePipeFactoryImpl();
		pipeFactoryService.createUnicastPipe(testManager.getNetPeerGroup(),
				DiscoveryFactoryServiceTest.class.getSimpleName());

		PipeDiscovery pipeDiscovery = discoveryFactoryService
				.createPipeDiscovery(testManager);
		/* Seeks all pipes */
		List<PipeAdvertisement> pipes = pipeDiscovery.lookup();
		for (PipeAdvertisement pa : pipes) {
			assertTrue(!pa.getName().isEmpty());
		}

		/* Seeks for particular pipe */
		PipeAdvertisement pa = pipeDiscovery
				.lookup(DiscoveryFactoryServiceTest.class.getSimpleName());
		assertNotNull(pa);
		assertEquals(DiscoveryFactoryServiceTest.class.getSimpleName(),
				pa.getName());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();

		if (file != null && file.exists()) {
			file.delete();
		}

		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ DiscoveryFactoryServiceTest.class.getName()));
	}
}
