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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.MessageElement;

import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.type.Tag;

/**
 * Presents a binary message that returns {@link InputStream}
 */
public class BinaryMessage extends Message {

	private static final long serialVersionUID = -6292637179092706213L;

	/* Media type - zip */
	private final static MimeMediaType GZIP_MEDIA_TYPE = new MimeMediaType(
			"application/gzip").intern();
	private static final Logger LOG = Logger.getLogger(BinaryMessage.class);

	/**
	 * Constructs a binary message
	 * 
	 * @param source
	 *            a sender
	 * @param destination
	 *            a recipient
	 * @param data
	 *            a byte[]
	 */
	public BinaryMessage(String source, String destination, byte[] data) {
		super(source, destination);
		setByteArray(data);
	}

	@Override
	public String getTextualMesage() {
		return "Binary message cannot return the text, use TextualMessage instead";
	}

	@Override
	public InputStream getStream() {
		InputStream result = null;
		MessageElement element = getMessageElement(Tag.STREAM.getValue());

		try {
			result = new GZIPInputStream(element.getStream());

		} catch (IOException e) {
			LOG.error(BinaryMessage.class.getName() + " "
					+ e.getMessage());
		}

		return result;
	}

	/**
	 * Adds byte[] into {@link Message} A byte[] will be compressed before
	 * transmission
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
			addMessageElement(new ByteArrayMessageElement(
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

	@Override
	public Object getObject() {
		return null;
	}
}
