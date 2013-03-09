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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.MessageFactory;

public class ObjectMessageTest {
	/* Message factory */
	private static MessageFactory messageFactory = null;
	private String source = "alice";
	private String destination = "bob";

	@BeforeClass
	public static void setUp() throws Exception {
		messageFactory = new MessageFactory();
	}

	@Test
	public void shallCreateObjectMessage() {
		Message objectMessage = messageFactory.createObjectMessage(source,
				destination, this.getClass());
		assertEquals(source, objectMessage.getSource());
		assertEquals(destination, objectMessage.getDestination());
		assertEquals(this.getClass().hashCode(), objectMessage.getObject()
				.hashCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreateObjectMessageWithNullSource() {
		Message objectMessage = messageFactory.createObjectMessage(null,
				destination, this.getClass());
		assertNull(objectMessage.getSource());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreateObjectMessageWithNullDestination() {
		Message objectMessage = messageFactory.createObjectMessage(source,
				null, this.getClass());
		assertNull(objectMessage.getDestination());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shallCreateObjectMessageWithNullPayload() {
		Message objectMessage = messageFactory.createObjectMessage(source,
				destination, null);
		assertNull(objectMessage.getObject());
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}
}
