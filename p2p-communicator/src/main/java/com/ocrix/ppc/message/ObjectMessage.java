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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.MessageElement;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.Tag;

/**
 * Defines a {@link Message} which contains {@link Object} as its pay-load
 */
public class ObjectMessage extends Message {
	private static final long serialVersionUID = 8818845859744946166L;

	/**
	 * Constructor
	 * 
	 * @param source
	 *            - an entity that sends the message
	 * @param destination
	 *            - an entity that receives the message
	 * @param object
	 *            - an entity's pay-load
	 */
	public ObjectMessage(String source, String destination, Object object) {
		super(source, destination);
		setObject(object);
	}

	@Override
	public String getTextualMesage() {
		return null;
	}

	@Override
	public InputStream getStream() {
		return null;
	}

	@Override
	public Object getObject() {
		InputStream is = null;
		ObjectInputStream ois = null;
		try {
			is = getInputStreamFromMessage();

			ois = new ObjectInputStream(is);
			return ois.readObject();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Puts an object into {@link Message}
	 * 
	 * @param object
	 *            a {@link Object}
	 */
	private void setObject(Object object) {
		Validator.validateObjNotNull(object);
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;

		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.close();
			bos.close();
			super.addMessageElement(new ByteArrayMessageElement(Tag.OBJECT
					.getValue(), MimeMediaType.AOS, bos.toByteArray(), null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Extracts an Object from the {@link Message} as {@link InputStream}
	 * 
	 * @return an {@link InputStream}
	 * 
	 * @throws IOException
	 */
	private InputStream getInputStreamFromMessage() throws IOException {
		MessageElement element = super.getMessageElement(Tag.OBJECT.getValue());
		if (null == element) {
			return null;
		}
		return element.getStream();
	}
}
