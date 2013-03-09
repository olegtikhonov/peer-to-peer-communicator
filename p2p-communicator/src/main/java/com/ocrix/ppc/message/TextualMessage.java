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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import net.jxta.endpoint.StringMessageElement;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.Encoding;
import com.ocrix.ppc.type.Tag;

/**
 * Defines a textual message. In order to get a payload, use
 * <code>getTextualMesage()</code>
 * 
 */
public class TextualMessage extends Message {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance of the TextualMessage
	 * 
	 * @param source
	 *            - who is sending
	 * @param destination
	 *            - who is receiving
	 * @param payLoad
	 *            - a message itself
	 */
	public TextualMessage(String source, String destination, String payLoad) {
		/* First two params are validated by super class */
		super(source, destination);
		setPayLoad(payLoad);
	}

	/**
	 * Returns a payload of the message
	 * 
	 * @return a payload
	 */
	public String getTextualMesage() {
		return getPayLoad();
	}

	/**
	 * Sets a payload
	 * 
	 * @param payLoad
	 */
	private void setPayLoad(String payLoad) {
		Validator.validateString(payLoad);
		try {
			super.addMessageElement(new StringMessageElement(Tag.PAYLOAD
					.getValue(), payLoad, Encoding.UTF_8.toString(), null));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a message's payload
	 * 
	 * @return a payload
	 */
	private String getPayLoad() {
		return getMessageElement(Tag.PAYLOAD.getValue()).toString();
	}

	@Override
	public InputStream getStream() {
		return new ByteArrayInputStream(
				"Textual message cannot return the InputStream, use BinaryMessage instead"
						.getBytes());
	}

	@Override
	public Object getObject() {
		return null;
	}
}
