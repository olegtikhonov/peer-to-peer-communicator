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

package com.ocrix.ppc.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.message.BinaryMessage;
import com.ocrix.ppc.message.MessageFactory;
import com.ocrix.ppc.message.TextualMessage;

public class MessageFactoryTest {
	private static MessageFactory msgFactory = null;

	@BeforeClass
	public static void setUp() throws Exception {
		msgFactory = new MessageFactory();
	}

	@Test
	public void shallCreateTextualMsg() {
		TextualMessage testMessage = msgFactory.createTextualMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_PAYLOAD);
		assertEquals(VerificationConstants.MSG_PAYLOAD,
				testMessage.getTextualMesage());
	}

	@Test
	public void shallGetSource() {
		TextualMessage testMessage = msgFactory.createTextualMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_PAYLOAD);
		assertEquals(VerificationConstants.MSG_SOURCE, testMessage.getSource());
	}

	@Test
	public void shallGetDestination() {
		TextualMessage testMessage = msgFactory.createTextualMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_PAYLOAD);
		assertEquals(VerificationConstants.MSG_DESTINATION,
				testMessage.getDestination());
	}

	// ------------------------------------
	// Stress tests
	// ------------------------------------
	@Test(expected = IllegalArgumentException.class)
	public void shallCreateTextMsgSourceIsNull() {
		TextualMessage testMessage = msgFactory.createTextualMessage(null,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_PAYLOAD);
		assertNull(testMessage);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreateTextMsgDestinationIsNull() {
		TextualMessage testMessage = msgFactory.createTextualMessage(
				VerificationConstants.MSG_SOURCE, null,
				VerificationConstants.MSG_PAYLOAD);
		assertNull(testMessage);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreateTextMsgPayloadIsNull() {
		TextualMessage testMessage = msgFactory.createTextualMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION, null);
		assertNull(testMessage);
	}

	@Test
	public void shallCreateBinaryMessage() throws IOException {
		BinaryMessage binaryMessage = msgFactory.createBinaryMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_PAYLOAD.getBytes());
		assertEquals(VerificationConstants.MSG_SOURCE,
				binaryMessage.getSource());
		assertEquals(VerificationConstants.MSG_DESTINATION,
				binaryMessage.getDestination());
		assertEquals(VerificationConstants.MSG_PAYLOAD,
				PPCUtils.toString(binaryMessage.getStream()));
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}
}
