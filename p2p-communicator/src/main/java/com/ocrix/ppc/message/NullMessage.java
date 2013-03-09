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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.MessageElement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.Tag;

/**
 * Null message that contains either text and binary stream. If a pay-load is
 * not contained, N/A returned as {@link InputStream} or {@link String}
 */
public class NullMessage extends Message {

	private static final long serialVersionUID = 904057164005202320L;
	/* GZIP type */
	private final static MimeMediaType GZIP_MEDIA_TYPE = new MimeMediaType(
			"application/gzip").intern();
	/* A JXTA message */
	private volatile net.jxta.endpoint.Message message = null;
	/* Textual pay-load */
	private volatile String text;
	private static final Logger LOG = Logger.getLogger(NullMessage.class);

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            - who is sending
	 * @param destination
	 *            - who is receiving
	 * @param text
	 *            - text as pay-load
	 * @param data
	 *            - a data as payload
	 */
	public NullMessage(String source, String destination, String text,
			byte[] data) {
		super(source, destination);
	}

	/**
	 * Constructs a {@link NullMessage} using jxta Message.
	 * 
	 * @param message
	 */
	public NullMessage(net.jxta.endpoint.Message message) {
		super(message.getMessageElement(Tag.SOURCE.getValue()).toString(),
				message.getMessageElement(Tag.DESTINATION.getValue())
						.toString());
		this.message = message;
	}

	@Override
	public String getTextualMesage() {
		if (this.message.getMessageElement(Tag.PAYLOAD.getValue()) != null)
			setText(this.message.getMessageElement(Tag.PAYLOAD.getValue())
					.toString());
		else
			setText("N/A");

		return getText();

	}

	@Override
	public InputStream getStream() {
		InputStream result = null;
		MessageElement element = this.message.getMessageElement(Tag.STREAM
				.getValue());

		if (element != null) {
			result = getStreamPrvt(element);
		} else {
			setByteArray("N/A".getBytes());
			element = this.message.getMessageElement(Tag.STREAM.getValue());
			result = getStreamPrvt(element);
		}
		return result;
	}

	private InputStream getStreamPrvt(MessageElement element) {
		InputStream result = null;
		try {
			if (element != null && element.getStream() != null)
				result = new GZIPInputStream(element.getStream());
		} catch (IOException e) {
			LOG.error(BinaryMessage.class.getName() + " "
					+ e.getMessage());
		}

		return result;
	}

	/**
	 * Sets byte[].
	 * 
	 * @param data
	 */
	private void setByteArray(byte[] data) {
		/* Validates data */
		Validator.validateObjNotNull(data);
		byte[] buffer = data;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(outStream);
			gos.write(data, 0, data.length);
			gos.finish();
			gos.close();
			buffer = outStream.toByteArray();
			this.message.addMessageElement(new ByteArrayMessageElement(
					Tag.STREAM.getValue(), GZIP_MEDIA_TYPE, buffer, null));
		} catch (IOException e) {
			LOG.error(BinaryMessage.class.getName() + " "
					+ e.getMessage());
		} finally {// Closing streams
			try {
				if (outStream != null)
					outStream.close();
				if (gos != null)
					gos.close();
			} catch (IOException e) {
				LOG.error(e);
			}
		}
	}

	/**
	 * Sets a text.
	 * 
	 * @param text
	 */
	private void setText(String text) {
		this.text = text;
	}

	private String getText() {
		return text;
	}

	private InputStream getInputStreamFromMessage() throws IOException {
		MessageElement element = this.message.getMessageElement(Tag.OBJECT
				.getValue());

		if (null == element)
			return null;
		return element.getStream();
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
			LOG.error(e);
		} catch (ClassNotFoundException e) {
			LOG.error(e);
		}
		return null;
	}
}
