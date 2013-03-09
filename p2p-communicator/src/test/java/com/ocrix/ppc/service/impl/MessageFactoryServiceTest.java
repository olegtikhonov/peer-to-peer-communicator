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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.TextualMessage;
import com.ocrix.ppc.service.impl.PPCServiceMessageFactoryImpl;

public class MessageFactoryServiceTest {
	/* Test members */
	private static PPCServiceMessageFactoryImpl messageFactoryService = null;
	private static String source = "PBX_1";
	private static String destination = "PBX_2";
	private static String msgPayload = "The quick brown fox jumps over the lazy dog";

	@BeforeClass
	public static void setUp() throws Exception {
		messageFactoryService = new PPCServiceMessageFactoryImpl();
	}

	@Test
	public void shallCreateTextualMessage() {
		TextualMessage textualMessage = messageFactoryService
				.createTextualMessage(source, destination, msgPayload);
		assertNotNull(textualMessage);
		assertEquals(source, textualMessage.getSource());
		assertEquals(destination, textualMessage.getDestination());
		assertEquals(msgPayload, textualMessage.getTextualMesage());
	}

	@Test
	public void shallCreateObjectMessage() {
		int originalHashCode = this.getClass().hashCode();
		Message objectMessage = messageFactoryService.createObjectMessage(
				source, destination, this.getClass());
		Object obj = objectMessage.getObject();
		assertEquals(originalHashCode, obj.hashCode());
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}
}
