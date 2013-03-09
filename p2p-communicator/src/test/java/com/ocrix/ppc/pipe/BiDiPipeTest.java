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
import net.jxta.util.QueuingServerPipeAcceptor;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
//import com.ocrix.ppc.discovery.DiscoveryPeerTest;
import com.ocrix.ppc.pipe.BiDiPipe;
import com.ocrix.ppc.pipe.ServerPipe;
import com.ocrix.ppc.type.PipeType;


public class BiDiPipeTest {
//	@Rule
//	public static TemporaryFolder tempStore = new TemporaryFolder();

	private static NetworkManager testManager;
	private static PeerGroup peerGroup;
	private static BiDiPipe biDiPipe;
	private static ServerPipe serverPipe;
	private static QueuingServerPipeAcceptor serverAcceptorListener;
	private static File file = null;
	private static final Logger LOG = Logger.getLogger(BiDiPipeTest.class);

	@BeforeClass
	public static void setUp() throws Exception {

		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());

		/* Creates a server pipe acceptor */
		serverAcceptorListener = new QueuingServerPipeAcceptor(2, 60000);

		/* Creates a NetworkManager */
		file = new File(VerificationConstants.TARGET + "/" + BiDiPipeTest.class.getName());
		
		testManager = new NetworkManager(ConfigMode.ADHOC, BiDiPipeTest.class.getSimpleName(), file.toURI());

		testManager.registerShutdownHook();
		peerGroup = testManager.startNetwork();

		serverPipe = new ServerPipe(peerGroup, serverAcceptorListener,
				ServerPipeTest.class.getSimpleName());
		biDiPipe = new BiDiPipe(peerGroup, serverPipe.getServerPipeAdv(),
				BiDiPipeTest.class.getSimpleName(), new MyMsgListener(), 60000);
	}

	@Test
	public void shallTestConstructor() {
		assertNotNull(biDiPipe);
	}

	@Test
	public void shallGetPipeType() {
		assertEquals(PipeType.BIDI_PIPE, biDiPipe.getPipeType());
	}

	@Test
	public void shallGetBidiPipe() {
		assertNotNull(biDiPipe.getBidiPipe());
	}

	@Test
	public void shallgetBidiPipeAdv() {
		assertNotNull(biDiPipe.getBidiPipeAdv());
	}

	// ------------------------------------
	// Illegal argument exception section
	// ------------------------------------

	@Test(expected = IllegalArgumentException.class)
	public void shallTryPassIllegalFirstParamNull() throws IOException {
		biDiPipe = new BiDiPipe(null, serverPipe.getServerPipeAdv(), "test",
				new MyMsgListener());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallTryPassIllegalSecondParamNull() throws IOException {
		biDiPipe = new BiDiPipe(peerGroup, null, "test", new MyMsgListener());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallTryPassIllegalThirdParamNull() throws IOException {
		biDiPipe = new BiDiPipe(peerGroup, serverPipe.getServerPipeAdv(), null,
				new MyMsgListener());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallTryPassIllegalFourthParamNull() throws IOException {
		biDiPipe = new BiDiPipe(peerGroup, serverPipe.getServerPipeAdv(),
				BiDiPipeTest.class.getSimpleName(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallTryPassIllegalFifthParamNull() throws IOException {
		biDiPipe = new BiDiPipe(peerGroup, serverPipe.getServerPipeAdv(),
				BiDiPipeTest.class.getSimpleName(), new MyMsgListener(), -5);
	}

	@AfterClass
	public static void tearDown() {
		try {
			testManager.stopNetwork();
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "."
					+ BiDiPipeTest.class.getName()));
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "."
					+ BiDiPipeTest.class.getSimpleName()));
			
			if(file != null && file.exists()){
				file.delete();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}
}
