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

import com.ocrix.ppc.message.BinaryMessage;
import com.ocrix.ppc.message.MessageFactory;
import com.ocrix.ppc.message.ObjectMessage;
import com.ocrix.ppc.message.TextualMessage;
import com.ocrix.ppc.service.PPCServiceMessageFactory;

/**
 * Provides a service for creating massages
 * 
 */
public class PPCServiceMessageFactoryImpl implements PPCServiceMessageFactory {
	/* Message factory is responsible for creating messages */
	private MessageFactory messageFactory = new MessageFactory();

	// @Override
	public TextualMessage createTextualMessage(String source,
			String destination, String payLoad) {
		return messageFactory
				.createTextualMessage(source, destination, payLoad);
	}

	// @Override
	public BinaryMessage createBinaryMessage(String source, String destination,
			byte[] data) {
		return messageFactory.createBinaryMessage(source, destination, data);
	}

	// @Override
	public ObjectMessage createObjectMessage(String source, String destination,
			Object object) {
		return messageFactory.createObjectMessage(source, destination, object);
	}
}
