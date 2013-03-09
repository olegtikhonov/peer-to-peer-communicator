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
import net.jxta.util.QueuingServerPipeAcceptor;

//import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.TemporaryFolder;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
//import com.ocrix.ppc.discovery.DiscoveryGroupTest;
import com.ocrix.ppc.pipe.BiDiPipe;
import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.pipe.PropagatePipe;
import com.ocrix.ppc.pipe.ServerPipe;
import com.ocrix.ppc.pipe.UnicastPipe;
import com.ocrix.ppc.pipe.UnicastSecurePipe;
import com.ocrix.ppc.type.PipeType;

public class PipeFactoryTest {
	// @Rule
	// public static TemporaryFolder tempStore = new TemporaryFolder();
	private static File file = null;
//	private static final Logger LOG = Logger.getLogger(PipeFactoryTest.class);
	/* Test members */
	private static NetworkManager testManager;
	private static PeerGroup peerGroup;
	private static PipeFactory pipeFactory;

	@BeforeClass
	public static void setUp() throws Exception {

		Utils.listDirectories();

		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());

		file = new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryTest.class.getSimpleName());

		/* Creates a NetworkManager */
		testManager = new NetworkManager(ConfigMode.ADHOC,
				PipeFactoryTest.class.getSimpleName(), file.toURI());

//		testManager.registerShutdownHook();
		peerGroup = testManager.startNetwork();
		/* Creates pipe factory */
		pipeFactory = new PipeFactory();
	}

	@Test
	public void shallCreateUnicastPipe() throws IOException {
		UnicastPipe unicastPipe = pipeFactory.createUnicastPipe(peerGroup,
				PipeFactoryTest.class.getSimpleName());
		assertNotNull(unicastPipe);
		assertNotNull(unicastPipe.getPipeAdv());
		assertEquals(unicastPipe.getPipeAdv().getName(),
				PipeFactoryTest.class.getSimpleName());
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateUnicastSecurePipe() throws IOException {
		UnicastSecurePipe unicastSecurePipe = pipeFactory
				.createUnicastSecurePipe(peerGroup,
						PipeFactoryTest.class.getSimpleName());
		assertNotNull(unicastSecurePipe);
		assertNotNull(unicastSecurePipe.getPipeAdv());
		assertEquals(unicastSecurePipe.getPipeAdv().getName(),
				PipeFactoryTest.class.getSimpleName());
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryTest.class.getSimpleName()));
	}

	@Test
	public void shallCreatePropagatePipe() throws IOException {
		PropagatePipe propagatePipe = pipeFactory.createPropagatePipe(
				peerGroup, PipeFactoryTest.class.getSimpleName());
		assertNotNull(propagatePipe);
		assertNotNull(propagatePipe.getPipeAdv());
		assertEquals(propagatePipe.getPipeAdv().getName(),
				PipeFactoryTest.class.getSimpleName());
		
		
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateServerPipe() throws IOException {
		QueuingServerPipeAcceptor serverAcceptorListener = new QueuingServerPipeAcceptor(
				2, 60000);
		
		
		
		
		ServerPipe serverPipe = pipeFactory.createServerPipe(peerGroup,
				serverAcceptorListener, PipeFactoryTest.class.getSimpleName());
		
		serverPipe.getServerPipe().close();
				
		assertNotNull(serverPipe);
		assertNotNull(serverPipe.getServerPipe().getPipeAdv());
		assertNotNull(serverPipe.getServerPipeAdv());
		assertEquals(serverPipe.getServerPipe().getPipeAdv().getName(),
				PipeFactoryTest.class.getSimpleName());
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateBiDiPipe() throws Exception {
		/* Creates a server acceptor listener */
		QueuingServerPipeAcceptor serverAcceptorListener = new QueuingServerPipeAcceptor(
				2, 60000);
		/* Creates server pipe advertisement */

//		tearDown();
//		setUp();

		PipeAdvertisement serverPipeAdv = pipeFactory.createServerPipe(
				peerGroup, serverAcceptorListener,
				PipeFactoryTest.class.getSimpleName()).getServerPipeAdv();
		/* Creates a bi-directional pipe */
		BiDiPipe biDiPipe = pipeFactory.createBiDiPipe(peerGroup,
				serverPipeAdv, PipeFactoryTest.class.getSimpleName(),
				new MyMsgListener());
		assertNotNull(biDiPipe);
		assertNotNull(biDiPipe.getBidiPipe());
		assertNotNull(biDiPipe.getBidiPipeAdv());
		assertEquals(PipeFactoryTest.class.getSimpleName(),
				biDiPipe.getPipeName());
		assertEquals(PipeType.BIDI_PIPE, biDiPipe.getPipeType());

		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryTest.class.getSimpleName()));

	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();

		if (file != null && file.exists()) {
			file.delete();
		}

		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryTest.class.getSimpleName()));
	}
}
