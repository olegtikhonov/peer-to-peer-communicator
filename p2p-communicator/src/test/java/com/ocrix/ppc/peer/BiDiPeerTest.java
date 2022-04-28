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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
//import com.att.tlv.infra.core.logger.api.ILogger;
import com.ocrix.ppc.VerificationConstants;
//import com.ocrix.ppc.commons.MockLogger;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.commons.Utils.JXTA_LOG_LEVEL;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.listener.MessageReceiver;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.MessageFactory;
import com.ocrix.ppc.peer.BiDiPeer;
import com.ocrix.ppc.peer.PeerFactory;
import com.ocrix.ppc.type.Tag;

import static org.junit.Assert.*;

public class BiDiPeerTest implements MessageReceiver {
	/* Class member declarations */
	private static BiDiPeer biDiPeer = null;
	private static PeerFactory peerFactory = null;
	private static String peerName = BiDiPeerTest.class.getSimpleName();
	private static BiDiPeer alice = null;
	private static BiDiPeer bob = null;
	private static MessageFactory messageFactory = null;
	private static final Logger logger = Logger.getLogger(BiDiPeerTest.class);
	private Object testObject = new Object();

	/* End of class member declarations */

	@BeforeClass
	public static void setUp() throws Exception {
		Utils.setJXTALogger(JXTA_LOG_LEVEL.OFF);
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".alice"));
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".bob"));
		peerFactory = new PeerFactory();
		messageFactory = new MessageFactory();
	}

	@Test
	public void shallCreateBiDiPeer() throws IOException, PPCException {
		PPCUtils.deleteDir(new File("." + peerName));
		biDiPeer = peerFactory.createBiDiPeer(peerName, VerificationConstants.TARGET + "/" + "." + peerName);
		assertNotNull(biDiPeer);
		assertEquals(peerName, biDiPeer.getPeerName());
		biDiPeer.destroy();
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + "." + peerName));
	}

	@Test
	public void shallSendMessageUsingILoggerConstructor() throws IOException,
			PPCException {
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".carol"));
		biDiPeer = peerFactory.createBiDiPeer(VerificationConstants.TARGET + "/" + "carol", logger, true, "JAVA", Tag.JNDI_NAME.getValue());
		assertNotNull(biDiPeer);
		assertEquals(VerificationConstants.TARGET + "/" + "carol", biDiPeer.getCachePath());
		biDiPeer.destroy();
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".carol"));
	}

	@Test
	public void shallSendMessage() throws IOException, PPCException {
		/* Clears the caches of Alice and Bobski */
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/"
				+ ".alice"));
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".bob"));

		/* Creates a new instance of bidi peer, called Alice */
		alice = peerFactory.createBiDiPeer("alice", VerificationConstants.TARGET + "/" + ".alice");
		/* Creates a new instance of bidi peer, called Bob */
		bob = peerFactory.createBiDiPeer("bob", VerificationConstants.TARGET + "/" + ".bob");

		/* Subscribes to the Alice */
		alice.subscribeRequestMessages(this);
		/* Subscribes to the Bobski */
		bob.subscribeRequestMessages(this);

		/* Sends X messages to the Bobski */
		for (int i = 0; i < 7; i++) {
			Message aliceGreetings = null;
			MyObjectClassData o = new MyObjectClassData("Hello", i);
			if ((i % 3) == 0) {
				aliceGreetings = messageFactory.createBinaryMessage("alice", "bob", ("hello bob, i'm alice " + String.valueOf(i)).getBytes());
			} else if ((i % 3) == 1) {
				aliceGreetings = messageFactory.createTextualMessage("alice", "bob", "hello bob, i'm alice " + String.valueOf(i));
			} else {
				aliceGreetings = messageFactory.createObjectMessage("alice", "bob", o);
			}
			
			alice.send(aliceGreetings);
		}

		/* A message that Bobski sends to the Alice */
		for (int i = 0; i < 3; i++) {
			Message bobGreatings = null;
			if ((i % 3) == 0) {
				bobGreatings = messageFactory.createTextualMessage("bob", "alice", "hello alice, i'm bob " + String.valueOf(i));
			} else if ((i % 3) == 1) {
				bobGreatings = messageFactory.createBinaryMessage("bob", "alice", ("hello alice, i'm bob " + String.valueOf(i)).getBytes());
			} else { 
				bobGreatings = messageFactory.createObjectMessage("bob", "alice", new MyObjectClassData("NYC- international love", 1));
			}
			bob.send(bobGreatings);
		}

		/* O Alice, wait for 30 sec */
		alice.getPeerManager().waitForRendezvousConnection(15000);
		/* Bobski, wait for 30 sec */
		bob.getPeerManager().waitForRendezvousConnection(15000);

		/* Stops Alice's pool and stops its JXTA */
		alice.destroy();
		/* Stops Bobski pool and stops its JXTA */
		bob.destroy();

		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".alice"));
		PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + ".bob"));
		Utils.listDirectories();
	}


	@Test
	public void test5PeersNetwork() {
		int numOfPeersInNetwork = 5;
        List<BiDiPeer> gangOfFive = new ArrayList<>(numOfPeersInNetwork);

		IntStream.range(0, numOfPeersInNetwork).forEach(aPeer -> {
			try {
				String peerName = UUID.randomUUID().toString();
				gangOfFive.add(peerFactory.createBiDiPeer(peerName, VerificationConstants.TARGET + "/" + "." + peerName));

			} catch (IOException | PPCException e) {
				e.printStackTrace();
				assertFalse(true);
			}
		});

		assertEquals(numOfPeersInNetwork, gangOfFive.size());

		gangOfFive.stream().forEach(aPeerGone -> {
			PPCUtils.deleteDir(new File(VerificationConstants.TARGET + "/" + "." + aPeerGone.getPeerName()));
		});
	}



	@AfterClass
	public static void tearDown() throws Exception {
		if (biDiPeer != null) {
			biDiPeer.destroy();
		}
		Utils.listDirectories();
	}

	protected void messageDebug(Message message) {
		try {
			testObject = message.getObject();
			if (testObject != null) {
				int countUp = 0;
				Class<?> myClass = (Class<?>) testObject;
				java.lang.reflect.Method[] methods = myClass.getMethods();
				for (java.lang.reflect.Method method : methods) {
					logger.info("MTD=" + method.getName());
					if (method.getName().contains("get"))
						++countUp;
				}
				assertTrue(countUp > 0);
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public void onMessage(Object message) {
		Message textualMessage = (Message) message;
		try {
			if (textualMessage != null && textualMessage.getMessageElement(Tag.OBJECT.getValue()) != null) {
				MyObjectClassData m = (MyObjectClassData) textualMessage.getObject();
				assertNotNull(m);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		assertTrue(textualMessage.getDestination() != null);
		assertTrue(textualMessage.getSource() != null);
		assertTrue(textualMessage.getTextualMesage() != null);

	}

	protected static byte[] marshall(Object myObj) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(myObj);
		byte[] yourBytes = bos.toByteArray();
		out.close();
		bos.close();
		return yourBytes;
	}

	protected static Object unmarshall(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = new ObjectInputStream(bis);
		Object o = in.readObject();
		bis.close();
		in.close();
		return o;
	}

	protected static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();

	}
}
