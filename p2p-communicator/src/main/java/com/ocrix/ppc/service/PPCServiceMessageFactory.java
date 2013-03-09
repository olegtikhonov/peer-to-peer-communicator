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

package com.ocrix.ppc.service;

import com.ocrix.ppc.message.BinaryMessage;
import com.ocrix.ppc.message.ObjectMessage;
import com.ocrix.ppc.message.TextualMessage;

/**
 * Defines message factories
 */
public interface PPCServiceMessageFactory {
	/**
	 * Defines a creating of the textual message
	 * 
	 * @param source
	 *            - who is a message creator
	 * @param destination
	 *            - where to send, who is a receiver
	 * @param payLoad
	 *            - a message body
	 * 
	 * @return {@link TextualMessage}
	 */
	TextualMessage createTextualMessage(String source, String destination,
			String payLoad);

	/**
	 * Creates a binary message
	 * 
	 * @param source
	 *            a sender
	 * @param destination
	 *            a recipient
	 * @param data
	 *            a byte[]
	 * 
	 * @return {@link BinaryMessage}
	 */
	BinaryMessage createBinaryMessage(String source, String destination,
			byte[] data);

	/**
	 * Creates an {@link ObjectMessage}
	 * 
	 * @param source
	 *            - an entity that sends the message
	 * @param destination
	 *            - an entity that receives the message
	 * @param object
	 *            - an entity's pay-load
	 * 
	 * @return an {@link ObjectMessage}
	 */
	ObjectMessage createObjectMessage(String source, String destination,
			Object object);
}
