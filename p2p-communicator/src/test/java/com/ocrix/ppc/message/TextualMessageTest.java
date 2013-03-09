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
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ocrix.ppc.VerificationConstants;
import com.ocrix.ppc.commons.Utils;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.TextualMessage;

public class TextualMessageTest {

	@BeforeClass
	public static void setUp() throws Exception {
	}

	@Test
	public void shallCreateTextualMessage() throws UnsupportedEncodingException {
		Message myMessage = new TextualMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_PAYLOAD);
		assertNotNull(myMessage);
		assertEquals(VerificationConstants.MSG_PAYLOAD,
				myMessage.getTextualMesage());
	}

	@Test
	public void shallCreateLongTextualMessage()
			throws UnsupportedEncodingException {
		Message myMessage = new TextualMessage(
				VerificationConstants.MSG_SOURCE,
				VerificationConstants.MSG_DESTINATION,
				VerificationConstants.MSG_TXT_LONG);
		assertNotNull(myMessage);
		assertEquals(VerificationConstants.MSG_TXT_LONG,
				myMessage.getTextualMesage());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		Utils.listDirectories();
	}
}
