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

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.peer.BiDiPeer;
import com.ocrix.ppc.peer.SenderPeer;
import com.ocrix.ppc.service.impl.PPCServicePeerFactoryImpl;

/**
 * Tests {@link PPCServiceDiscoveryFactoryImpl} functionality.
 */
public class PPCServicePeerFactoryImplTest {

	private static PPCServicePeerFactoryImpl peerServiceFactory = null;

	@BeforeClass
	public static void setUp() throws Exception {
		peerServiceFactory = new PPCServicePeerFactoryImpl();
	}


	@Test
	public void shallCreateBiDiPeer() throws IOException, PPCException {
		BiDiPeer peer = peerServiceFactory.createBiDiPeer(VerificationConstants.TARGET + "/" + PPCServicePeerFactoryImplTest.class.getName());
		assertTrue(peer.getCachePath().contains(PPCServicePeerFactoryImplTest.class.getName()));
		peer.destroy();
		Utils.listDirectories();
	}

	@Test
	public void shallCreateSenderPeer() throws IOException, PPCException,
			Exception {
		SenderPeer senderPeer = peerServiceFactory.createSenderPeer(VerificationConstants.TARGET + "/" + PPCServicePeerFactoryImplTest.class.getName() + "_1");
		assertTrue(senderPeer.getCachePath().contains(PPCServicePeerFactoryImplTest.class.getSimpleName()));
//		senderPeer.destroy();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Utils.listDirectories();
	}
}
