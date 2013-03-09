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

import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.PeerGroupAdvertisement;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.TemporaryFolder;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.discovery.DiscoveryFactory;
import com.ocrix.ppc.discovery.GroupDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.service.impl.GroupService;

/**
 * Tests group discovery lookup, creates a new group also
 */
public class DiscoveryGroupTest {
	// @Rule
	// public static TemporaryFolder tempStore = new TemporaryFolder();

	/* Test class members */
	private static DiscoveryFactory discoveryFactory;
	private static NetworkManager testManager;
	private static GroupDiscovery groupDiscovery = null;
	private static PeerGroup pg1;
	private static File file = null;
	private static final Logger LOG = Logger.getLogger(DiscoveryPeerTest.class);

	@BeforeClass
	public static void setUp() throws Exception {
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());
		/* Creates a factory */
		discoveryFactory = new DiscoveryFactory();

		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ DiscoveryGroupTest.class.getName()));

		file = new File(VerificationConstants.TARGET + "/"
				+ DiscoveryGroupTest.class.getName());

		/* Creates a NetworkManager */
		testManager = new NetworkManager(ConfigMode.ADHOC,
				DiscoveryGroupTest.class.getSimpleName(), file.toURI());

		/* Starts a network */
		pg1 = testManager.startNetwork();
		/* Creates GroupDiscovery */
		groupDiscovery = discoveryFactory.createGroupDiscovery(testManager);
	}

	@Test
	public void shallDiscoveryGroup() {
		GroupDiscovery groupDiscovery = (GroupDiscovery) discoveryFactory
				.createGroupDiscovery(testManager);
		assertNotNull(groupDiscovery);
	}

	@Test
	public void shallDiscoveryGroupLookupEmpty() throws IOException,
			PPCException {
		/* At the beginning look at local cache */
		/* Case 1: parameter is an empty */
		PeerGroupAdvertisement peerGroup = groupDiscovery.lookup("");
		assertNotNull(peerGroup);
		assertEquals(VerificationConstants.NET_GROUP_NAME, peerGroup.getName());
	}

	@Test
	public void shallDiscoveryGroupLookupNull() throws IOException,
			PPCException {
		/* Case 2: parameter is an null */
		PeerGroupAdvertisement peerGroup = groupDiscovery.lookup(null);
		assertNotNull(peerGroup);
		assertEquals(VerificationConstants.NET_GROUP_NAME, peerGroup.getName());
	}

	@Test
	public void shallDiscoveryGroupLookupExactName() throws IOException,
			PPCException {
		/* Case 3: parameter is exact peer group name */
		PeerGroupAdvertisement peerGroup = groupDiscovery
				.lookup(VerificationConstants.NET_GROUP_NAME);
		assertNotNull(peerGroup);
		assertEquals(VerificationConstants.NET_GROUP_NAME, peerGroup.getName());
	}

	@Test
	public void shallDiscoveryGroupLookupRegEx() throws IOException,
			PPCException {
		/* Case 3: parameter is exact peer group name */
		String regEx = VerificationConstants.NET_GROUP_NAME.substring(5);
		PeerGroupAdvertisement peerGroup = groupDiscovery.lookup("*" + regEx
				+ "*");
		assertNotNull(peerGroup);
		assertEquals(VerificationConstants.NET_GROUP_NAME, peerGroup.getName());
	}

	@Test
	public void shallDiscoveryGroupLookupWrongName() throws IOException,
			PPCException {
		/* Case 4 : wrong group name */
		PeerGroupAdvertisement peerGroup = groupDiscovery
				.lookup("wrong_peer_group_name");
		assertNull(peerGroup);
	}

	@Test
	public void shallDiscoveryGroupLookup() throws IOException {
		List<PeerGroupAdvertisement> peerGroup = (List<PeerGroupAdvertisement>) groupDiscovery
				.lookup();
		assertNotNull(peerGroup);
		for (PeerGroupAdvertisement peerGroupAdv : peerGroup)
			assertTrue(!peerGroupAdv.getName().isEmpty());
	}

	@Test
	public void newGroupLookup() throws IOException, PPCException {
		String myGroupName = "MyGroupName";
		GroupService gs = new GroupService();

		new File(VerificationConstants.TARGET + "/"
				+ DiscoveryGroupTest.class.getName()).delete();

		if (pg1 != null) {
			PeerGroup pg = gs.create(myGroupName, pg1);
			assertNotNull(pg);
			PeerGroupAdvertisement peerGroup = groupDiscovery
					.lookup(myGroupName);

			assertNotNull(peerGroup);
			assertEquals(myGroupName, peerGroup.getName());
		} else {
			LOG.warn("Check why peer group is null");
		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();

		if (file != null && file.exists()) {
			file.delete();
		}

	}
}
