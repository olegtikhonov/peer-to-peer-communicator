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

package com.ocrix.ppc.peer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.listener.MessageReceiver;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.MessageFactory;
import com.ocrix.ppc.peer.PeerFactory;
import com.ocrix.ppc.peer.ReceiverPeer;
import com.ocrix.ppc.service.impl.PPCServiceDiscoveryFactoryImpl;

public class ReceiverPeerTest implements MessageReceiver {
	private static final Logger logger = Logger
			.getLogger(ReceiverPeerTest.class);
	private static PeerFactory peerFactory = null;
	private static ReceiverPeer receiverPeer = null;

	@BeforeClass
	public static void setUp() throws Exception {
		System.setProperty("net.jxta.logging.Logging", "OFF");
		System.setProperty("net.jxta.level", "OFF");
		peerFactory = new PeerFactory();
	}

	@Test
	public void shallCreateReceiverPeer() throws IOException, PPCException {
		receiverPeer = peerFactory.createReceiverPeer(
				ReceiverPeerTest.class.getSimpleName(),
				VerificationConstants.TARGET + "/" + "."
						+ ReceiverPeerTest.class.getSimpleName());
		assertNotNull(receiverPeer);
		assertEquals(ReceiverPeerTest.class.getSimpleName(),
				receiverPeer.getPeerName());
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "."
				+ ReceiverPeerTest.class.getSimpleName()));
	}

	@Test
	public void shallCreateReceiverPeerWithLogger() throws IOException,
			PPCException {
		String peerName = "freak";
		receiverPeer = peerFactory.createReceiverPeer(
				VerificationConstants.TARGET + "/" + peerName, logger, true);
		assertNotNull(receiverPeer);
		assertEquals(VerificationConstants.TARGET + "/" + peerName,
				receiverPeer.getPeerName());
	}

	@Test
	public void shallCreateNumberOfReceiversWithSameName() throws IOException,
			PPCException {
		String peerName = "freak";
		ReceiverPeer[] peers = new ReceiverPeer[3];
		for (int i = 0; i < 3; i++) {
			peers[i] = peerFactory.createReceiverPeer(peerName,
					VerificationConstants.TARGET + "/" + "." + peerName + "_"
							+ i);
		}

		for (int i = 0; i < 3; i++) {
			peers[i].destroy();
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "."
					+ peerName + "_" + i));
		}

		// receiverPeer = peerFactory.createReceiverPeer(peerName, mockLogger,
		// true);
		// assertNotNull(receiverPeer);
		// assertEquals(peerName, receiverPeer.getPeerName());
	}

	@Test
	public void shallSendMessageAndReceiveIt() throws IOException, PPCException {
		/* Deletes a cache */
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".other/"
				+ ReceiverPeerTest.class.getSimpleName()));
		receiverPeer = peerFactory.createReceiverPeer(
				ReceiverPeerTest.class.getSimpleName(),
				VerificationConstants.TARGET + "/" + ".other/"
						+ ReceiverPeerTest.class.getSimpleName());
		receiverPeer.subscribeReceiveMessages(this);

		PPCServiceDiscoveryFactoryImpl dfs = new PPCServiceDiscoveryFactoryImpl();
		PipeDiscovery pipeDiscovery = dfs.createPipeDiscovery(receiverPeer
				.getPeerManager());
		PipeAdvertisement pipeAdvertisement = pipeDiscovery
				.lookup(ReceiverPeerTest.class.getSimpleName());
		PipeService ps = receiverPeer.getPeerManager().getNetPeerGroup()
				.getPipeService();

		OutputPipe outputPipe = ps.createOutputPipe(pipeAdvertisement, 2000);
		MessageFactory mf = new MessageFactory();
		boolean isReceived = outputPipe.send(mf.createTextualMessage("PBX_1",
				"PBX_2", "Warring superstitions, joy and inhibitions"));
		receiverPeer.getPeerManager().waitForRendezvousConnection(200);
		assertTrue(isReceived);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		if (receiverPeer != null)
			receiverPeer.destroy();
		/* Deletes a cache */
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".other/"
				+ ReceiverPeerTest.class.getSimpleName()));
		/* Deletes a cache */
		Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "."
				+ ReceiverPeerTest.class.getSimpleName()));
	}

	public void onMessage(Object msg) {
		Message message = (Message) msg;
		logger.info("GOT A MESSAGE form " + message.getSource() + ", to "
				+ message.getDestination() + ", " + message.getTextualMesage());
		assertNotNull(message);

	}
}
