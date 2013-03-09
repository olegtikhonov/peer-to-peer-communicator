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
import java.io.File;
import java.io.IOException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.QueuingServerPipeAcceptor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.discovery.DiscoveryGroupTest;
import com.ocrix.ppc.pipe.BiDiPipe;
import com.ocrix.ppc.pipe.MyMsgListener;
import com.ocrix.ppc.pipe.PropagatePipe;
import com.ocrix.ppc.pipe.ServerPipe;
import com.ocrix.ppc.pipe.UnicastPipe;
import com.ocrix.ppc.pipe.UnicastSecurePipe;
import com.ocrix.ppc.service.impl.PPCServicePipeFactoryImpl;
import com.ocrix.ppc.type.PipeType;

/**
 * Tests {@link PipeService} functionality.
 */
public class PipeFactoryServiceTest {
	// @Rule
	// public static TemporaryFolder tempStore = new TemporaryFolder();

	/* Test members */
	private static NetworkManager testManager;
	private static PeerGroup peerGroup;
	private static PPCServicePipeFactoryImpl pipeFactoryService;
	private static File file = null;

	@BeforeClass
	public static void setUp() throws Exception {
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());

		Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + PipeFactoryServiceTest.class.getName()));
		
		
		file = new File(VerificationConstants.TARGET + "/" 				+ PipeFactoryServiceTest.class.getName());

		/* Creates a NetworkManager */
		testManager = new NetworkManager(ConfigMode.ADHOC,
				DiscoveryGroupTest.class.getSimpleName(), file.toURI());

		testManager.registerShutdownHook();
		peerGroup = testManager.startNetwork();
		/* Creates pipe factory */
		pipeFactoryService = new PPCServicePipeFactoryImpl();
	}

	@Test
	public void shallCreateUnicastPipe() throws IOException {
		Utils.deleteDir(new File(PipeFactoryServiceTest.class.getName()));
		UnicastPipe unicastPipe = pipeFactoryService.createUnicastPipe(
				peerGroup, PipeFactoryServiceTest.class.getSimpleName());
		assertNotNull(unicastPipe);
		assertNotNull(unicastPipe.getPipeAdv());
		assertEquals(unicastPipe.getPipeAdv().getName(),
				PipeFactoryServiceTest.class.getSimpleName());
		Utils.deleteDir(new File(PipeFactoryServiceTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateUnicastSecurePipe() throws IOException {
		UnicastSecurePipe unicastSecurePipe = pipeFactoryService
				.createUnicastSecurePipe(peerGroup,
						PipeFactoryServiceTest.class.getSimpleName());
		assertNotNull(unicastSecurePipe);
		assertNotNull(unicastSecurePipe.getPipeAdv());
		assertEquals(unicastSecurePipe.getPipeAdv().getName(),
				PipeFactoryServiceTest.class.getSimpleName());
		Utils.deleteDir(new File(PipeFactoryServiceTest.class.getSimpleName()));
	}

	@Test
	public void shallCreatePropagatePipe() throws IOException {
		PropagatePipe propagatePipe = pipeFactoryService.createPropagatePipe(
				peerGroup, PipeFactoryServiceTest.class.getSimpleName());
		assertNotNull(propagatePipe);
		assertNotNull(propagatePipe.getPipeAdv());
		assertEquals(propagatePipe.getPipeAdv().getName(),
				PipeFactoryServiceTest.class.getSimpleName());
		Utils.deleteDir(new File(PipeFactoryServiceTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateServerPipe() throws IOException {
		QueuingServerPipeAcceptor serverAcceptorListener = new QueuingServerPipeAcceptor(
				2, 60000);
		ServerPipe serverPipe = pipeFactoryService.createServerPipe(peerGroup,
				serverAcceptorListener,
				PipeFactoryServiceTest.class.getSimpleName());
		assertNotNull(serverPipe);
		assertNotNull(serverPipe.getServerPipe().getPipeAdv());
		assertEquals(serverPipe.getServerPipe().getPipeAdv().getName(),
				PipeFactoryServiceTest.class.getSimpleName());
		Utils.deleteDir(new File(PipeFactoryServiceTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateBiDiPipe() throws Exception {
		/* Creates a server acceptor listener */
		QueuingServerPipeAcceptor serverAcceptorListener = new QueuingServerPipeAcceptor(
				2, 60000);
		/* Creates server pipe advertisement */
		String pipeName = PipeFactoryServiceTest.class.getSimpleName() + "_1";

//		tearDown();
//		setUp();

		PipeAdvertisement serverPipeAdv = pipeFactoryService.createServerPipe(
				peerGroup, serverAcceptorListener, pipeName).getServerPipeAdv();

		BiDiPipe biDiPipe = pipeFactoryService.createBiDiPipe(peerGroup,
				serverPipeAdv, PipeFactoryServiceTest.class.getSimpleName(),
				new MyMsgListener());
		assertNotNull(biDiPipe);
		assertNotNull(biDiPipe.getBidiPipe());
		assertNotNull(biDiPipe.getBidiPipeAdv());
		assertEquals(PipeFactoryServiceTest.class.getSimpleName(),
				biDiPipe.getPipeName());
		assertEquals(PipeType.BIDI_PIPE, biDiPipe.getPipeType());
		Utils.deleteDir(new File(PipeFactoryServiceTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateBiDiPipeOneParamMore() throws Exception {
		/* Creates a server acceptor listener */
		QueuingServerPipeAcceptor serverAcceptorListener = new QueuingServerPipeAcceptor(
				2, 60000);
		/* Creates server pipe advertisement */
		String pipeName = PipeFactoryServiceTest.class.getSimpleName() + "_2";
//
//		tearDown();
//		setUp();

		PipeAdvertisement serverPipeAdv = pipeFactoryService.createServerPipe(
				peerGroup, serverAcceptorListener, pipeName).getServerPipeAdv();

		BiDiPipe biDiPipe = pipeFactoryService.createBiDiPipe(peerGroup,
				serverPipeAdv, PipeFactoryServiceTest.class.getSimpleName(),
				new MyMsgListener(), 60000);
		assertNotNull(biDiPipe);
		assertNotNull(biDiPipe.getBidiPipe());
		assertNotNull(biDiPipe.getBidiPipeAdv());
		assertEquals(PipeFactoryServiceTest.class.getSimpleName(),
				biDiPipe.getPipeName());
		assertEquals(PipeType.BIDI_PIPE, biDiPipe.getPipeType());
		assertEquals(biDiPipe.getBidiPipe().getRetryTimeout(), 60000);
		Utils.deleteDir(new File(PipeFactoryServiceTest.class.getSimpleName()));
	}

	@AfterClass
	public static void tearDown() throws Exception {
		testManager.stopNetwork();
		if (file != null && file.exists()) {
			file.delete();
		}

		// tempStore.delete();
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ PipeFactoryServiceTest.class.getSimpleName()));
		Utils.listDirectories();
	}
}
