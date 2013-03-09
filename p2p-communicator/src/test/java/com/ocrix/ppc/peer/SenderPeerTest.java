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
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.commons.Utils.JXTA_LOG_LEVEL;
//import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.listener.MessageReceiver;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.MessageFactory;
import com.ocrix.ppc.peer.PeerFactory;
import com.ocrix.ppc.peer.ReceiverPeer;
import com.ocrix.ppc.peer.SenderPeer;
import com.ocrix.ppc.type.Tag;
import com.ocrix.ppc.type.ThreadPoolType;

/**
 * 
 * @author olegt
 * 
 */
public class SenderPeerTest implements MessageReceiver {
	private static PeerFactory peerFactory = null;
	private static SenderPeer sender = null;
	private static String peerName = SenderPeerTest.class.getSimpleName();
	private static MessageFactory messageFactory = null;
	private static final Logger logger = Logger.getLogger(SenderPeerTest.class);
	private static final long SECOND = 1000;
	private static final long TWO_SECOND = 2000;

	@BeforeClass
	public static void setUp() throws Exception {
		Utils.setJXTALogger(JXTA_LOG_LEVEL.OFF);
		peerFactory = new PeerFactory();
		messageFactory = new MessageFactory();
	}

	@Test
	public void shallCreateSenderPeer(){
		try {
			sender = peerFactory.createSenderPeer(peerName, VerificationConstants.TARGET + "/" + "." + peerName);
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "." + peerName));
			assertNotNull(sender);
			assertEquals(peerName, sender.getPeerName());
			assertEquals(VerificationConstants.TARGET + "/" + "." + peerName, sender.getCachePath());
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "." + peerName));
			Utils.listDirectories();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Test
	public void shallSendMessage() {
		try {
			/* Deletes a cache */
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".first/" + "PBX_2"));
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".second/" + "PBX_1"));

			// Creates the receiver
			ReceiverPeer receiverPeer = peerFactory.createReceiverPeer("PBX_2", VerificationConstants.TARGET + "/" + ".first/" + "PBX_2");
			/*
			 * Adds a listener, i.e. when a receiver gets a message, it will notify
			 * about that message
			 */
			receiverPeer.subscribeReceiveMessages(this);

			/* Creates the sender */
			SenderPeer sender = peerFactory.createSenderPeer("PBX_1", VerificationConstants.TARGET + "/" + ".second/" + "PBX_1");
			/* Subscribes to get notification about sent messages */
			sender.subscribeSent(this);

			/* Sends a binary message */
			for (int i = 0; i < 10; i++) {
				try {
					Message message = null;
					if ((i % 2) == 0)/* creates binary message */
						message = messageFactory.createBinaryMessage("PBX_1", "PBX_2", ("Sentence first -- verdict afterwards." + i).getBytes());
					else
						/* creates textual message */
						message = messageFactory.createTextualMessage("PBX_1", "PBX_2", "Twinkle, twinkle, little bat! How I wonder what you're at .." + i);

					Validator.validateObjNotNull(message);
					sender.send(message);
				} catch (Exception e) {
					logger.error(SenderPeerTest.class.getName() + " " + e.getMessage());
				}
			}
			PPCUtils.sleep(SECOND);

			/* Case when source and destination are wrong */
			for (int i = 0; i < 10; i++) {
				try {
					Message message = messageFactory.createTextualMessage("PBX_8", "PBX_9", "Sentence first -- verdict afterwards." + i);
					sender.send(message);
				} catch (Exception e) {
					logger.error(SenderPeerTest.class.getName() + " " + e.getMessage());
				}
			}
			PPCUtils.sleep(TWO_SECOND);

			/* Shuts down the peers */
			receiverPeer.destroy();
			sender.destroy();
			/* Deletes a cache */
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".first/" + "PBX_2"));
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".second/" + "PBX_1"));
			Utils.listDirectories();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Test
	public void shallCreateSenderPeerWithLogger() {
		String peerName = "geek";

		try {
			SenderPeer senderPeer = peerFactory.createSenderPeer(VerificationConstants.TARGET + "/" + peerName, logger, true, ThreadPoolType.JAVA.name(), Tag.JNDI_NAME.getValue());
			assertNotNull(senderPeer);
			assertEquals(VerificationConstants.TARGET + "/" + peerName, senderPeer.getPeerName());

			if (senderPeer != null)
				senderPeer.destroy();
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "." + peerName));
			Utils.listDirectories();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Test
	public void shallCreateWasSenderPeerWithLogger() {
		try {
			String peerName = "dork";
			SenderPeer senderPeer = peerFactory.createSenderPeer(
					VerificationConstants.TARGET + "/" + peerName, logger, true, ThreadPoolType.WAS.name(), Tag.JNDI_NAME.getValue());

			assertNotNull(senderPeer);
			assertEquals(VerificationConstants.TARGET + "/" + peerName, senderPeer.getPeerName());

			if (senderPeer != null)
				senderPeer.destroy();
			Utils.deleteDir(new File(VerificationConstants.TARGET + "/" + "." + peerName));
			Utils.listDirectories();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void takeCareOf(Message arg) {
		assertTrue(arg.getSource() != null);
		try {
			assertTrue(arg.getTextualMesage() != null);
			assertTrue(PPCUtils.toString(arg.getStream()) != null);
			// System.out.println("GOT " + arg.getTextualMesage() + " " +
			// PPCUtils.toString(arg.getStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertTrue(arg.getTextualMesage().contains("sent"));
	}

	@AfterClass
	public static void tearDown() {
		try {
			/* Deletes a cache */
			if (sender != null){
				sender.destroy();
			}
			
			Utils.listDirectories();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	public void onMessage(Object arg) {
		Validator.validateObjNotNull(arg);
		try {
			Message message = (Message) arg;
			takeCareOf(message);
		} catch (Exception e) {
			logger.error(SenderPeerTest.class.getName() + " " + e.getMessage());
		}

	}
}
