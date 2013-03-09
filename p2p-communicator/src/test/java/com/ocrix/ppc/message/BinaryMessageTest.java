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

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.MessageFactory;

public class BinaryMessageTest {
	/* Message factory */
	private static MessageFactory messageFactory = null;

	@BeforeClass
	public static void setUp() throws Exception {
		messageFactory = new MessageFactory();
	}

	@Test
	public void shallCreateBinaryMessage() throws IOException {
		String message = VerificationConstants.MSG_BINARY;
		Message binaryMessage = messageFactory.createBinaryMessage("alice",
				"bob", message.getBytes());
		InputStream is = binaryMessage.getStream();
		String text = PPCUtils.toString(is);
		assertTrue(!text.isEmpty());
		if (is != null)
			is.close();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreatesBinaryMessageNullSource() {
		Message binaryMessage = messageFactory.createBinaryMessage(null,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_BINARY.getBytes());
		assertTrue(binaryMessage != null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreatesBinaryMessageNullDestination() {
		Message binaryMessage = messageFactory.createBinaryMessage(
				VerificationConstants.MSG_SOURCE, null,
				VerificationConstants.MSG_BINARY.getBytes());
		assertTrue(binaryMessage != null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreatesBinaryMessageNullPayload() {
		Message binaryMessage = messageFactory.createBinaryMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION, null);
		assertTrue(binaryMessage != null);
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}
}
