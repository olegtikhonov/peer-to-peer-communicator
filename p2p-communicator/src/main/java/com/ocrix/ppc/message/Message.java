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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import net.jxta.endpoint.StringMessageElement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.Encoding;
import com.ocrix.ppc.type.Tag;

/**
 * Defines a base class of the Message
 */
public abstract class Message extends net.jxta.endpoint.Message { //
	private static final long serialVersionUID = 7637897916636312316L;

	abstract public String getTextualMesage();

	abstract public InputStream getStream();

	abstract public Object getObject();
	private static final Logger LOG = Logger.getLogger(Message.class);

	/**
	 * Constructs a message
	 * 
	 * @param source
	 *            who is an initializer
	 * @param destination
	 *            where to send
	 */
	public Message(String source, String destination) {
		super();
		setSource(source);
		setDestination(destination);

	}

	/**
	 * Sets a source to the {@link net.jxta.endpoint.Message}
	 * 
	 * @param source
	 *            an initializer
	 */
	private void setSource(String source) {
		Validator.validateString(source);
		try {
			addMessageElement(new StringMessageElement(Tag.SOURCE.getValue(),
					source, Encoding.UTF_8.toString(), null));
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
		}
	}

	/**
	 * Returns a source of the message
	 * 
	 * @return a source
	 */
	public String getSource() {
		return getMessageElement(Tag.SOURCE.getValue()).toString();
	}

	/**
	 * Sets a destination, where a message to be sent
	 * 
	 * @param destination
	 *            an endpoint where to send a message
	 */
	private void setDestination(String destination) {
		Validator.validateString(destination);
		try {
			addMessageElement(new StringMessageElement(
					Tag.DESTINATION.getValue(), destination,
					Encoding.UTF_8.toString(), null));
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
		}
	}

	/**
	 * Returns a destination
	 * 
	 * @return a destination
	 */
	public String getDestination() {
		return getMessageElement(Tag.DESTINATION.getValue()).toString();

	}
}
