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

package com.ocrix.ppc.pipe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.PipeAdvertisement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Rule;
import org.junit.Test;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.pipe.UnicastPipe;
import com.ocrix.ppc.type.PipeType;

/**
 * Tests {@link UnicastPipeTest} functionality.
 */
public class UnicastPipeTest {
	// @Rule
	// public static TemporaryFolder tempStore = new TemporaryFolder();

	private static NetworkManager testManager;
	private static PeerGroup peerGroup;
	private static UnicastPipe unicastPipe;
	private static File file = null;

	@BeforeClass
	public static void setUp() throws Exception {
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());

		file = new File(VerificationConstants.TARGET + "/"
				+ UnicastPipeTest.class.getName());

		/* Creates a NetworkManager */
		testManager = new NetworkManager(ConfigMode.ADHOC,
				UnicastPipeTest.class.getSimpleName(), file.toURI());

		testManager.registerShutdownHook();
		peerGroup = testManager.startNetwork();
		/* Creates an unicast pipe */
		unicastPipe = new UnicastPipe(peerGroup,
				UnicastPipeTest.class.getSimpleName());
	}

	@Test
	public void shallTestConstructor() throws IOException {
		assertNotNull(unicastPipe);
	}

	@Test
	public void shallTestPipeAdvertisement() {
		PipeAdvertisement pipeAdvertisement = unicastPipe.getPipeAdv();
		assertNotNull(pipeAdvertisement);
	}

	@Test
	public void shallGetPipeType() {
		assertEquals(PipeType.UNICAST, unicastPipe.getPipeType());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();
		if (file != null && file.exists()) {
			file.delete();
		}
	}
}
