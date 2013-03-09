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

package com.ocrix.ppc.commons;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import com.ocrix.ppc.message.BinaryMessage;
import com.ocrix.ppc.message.MessageFactory;
import com.ocrix.ppc.message.ObjectMessage;
import com.ocrix.ppc.message.TextualMessage;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.Tag;

/**
 * Peer to peer communicator utilities class.
 */
public final class PPCUtils {
	private final static MimeMediaType GZIP_MEDIA_TYPE = new MimeMediaType(
			"application/gzip").intern();
	private final static Logger logger = Logger.getLogger(PPCUtils.class);

	// private final static PPCLogger logger =
	// PPCLogger.getPPCLogger(PPCUtils.class.getName());
	/* Do not try init this class */
	private PPCUtils() {
		/* Logger off */
		Logger.getLogger("net.jxta").setLevel(Level.OFF);
	}

	/**
	 * Deletes directory and its sub-folders recursively
	 * 
	 * @param dir
	 *            {@link File}
	 * 
	 * @return - true if succeeded to delete a folder
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		/* The directory is now empty so delete it */
		return dir.delete();
	}

	/**
	 * Creates a directory and sub directories if needed
	 * 
	 * @param dir
	 *            {@link File}
	 */
	public static void createDir(File dir) {
		if (dir != null)
			dir.mkdirs();
	}

	/**
	 * Converts an object to {@link TextualMessage}
	 * 
	 * @param messag
	 *            - Object
	 * 
	 * @return {@link TextualMessage}
	 */
	public static TextualMessage convertMessage(Object messag) {
		Message message = (Message) messag;
		MessageFactory ms = new MessageFactory();
		return ms.createTextualMessage(
				message.getMessageElement(Tag.SOURCE.getValue()).toString(),
				message.getMessageElement(Tag.DESTINATION.getValue())
						.toString(),
				message.getMessageElement(Tag.PAYLOAD.getValue()).toString());
	}

	/**
	 * Converts {@link Message} to
	 * {@link com.ocrix.ppc.message.Message}
	 * 
	 * @param msg
	 *            a JXTA message
	 * 
	 * @return {@link com.ocrix.ppc.message.Message}
	 * @throws IOException
	 */
	public synchronized static com.ocrix.ppc.message.Message convertJxtaMsgToPpcMsg(
			Message msg) throws IOException {

		ReentrantLock lock = new ReentrantLock();
		com.ocrix.ppc.message.Message myMsg = null;
		try {
			lock.lock();
			Message temp = msg;
			System.out.println(PPCUtils.class.getName() + " " + temp);

			String src = temp.getMessageElement(Tag.SOURCE.getValue())
					.toString();
			String dest = temp.getMessageElement(Tag.DESTINATION.getValue())
					.toString();

			// ========================================================
			// TODO: Prevent using new
			// ========================================================

			/* Textual message */
			if (temp.getMessageElement(Tag.PAYLOAD.getValue()) != null) {
				myMsg = new TextualMessage(src, dest, temp.getMessageElement(
						Tag.PAYLOAD.getValue()).toString());
			} /* Binary message */
			else if (temp.getMessageElement(Tag.STREAM.getValue()) != null) {
				InputStream result = getInputStreamFromMessage(temp,
						Tag.STREAM.getValue());
				byte[] ar = toByteArray(result);
				// System.out.println("BINARY ################## " + new
				// String(ar));
				logger.info("BINARY ################## " + String.valueOf(ar));
				myMsg = new BinaryMessage(src, dest, ar);
			} /* Object message */
			else if (temp.getMessageElement(Tag.OBJECT.getValue()) != null) {
				Object ob = getObjectFromMessage(temp, Tag.OBJECT.getValue());
				// System.out.println("OBJECT @@@@@@@@@@@@@ " + ob.toString());
				logger.info("OBJECT @@@@@@@@@@@@@ " + ob.toString());
				myMsg = new ObjectMessage(src, dest, ob);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(PPCUtils.class.getName() + " thrown an exception "
					+ e.getMessage());
		} finally {
			if (lock != null && lock.isLocked())
				lock.unlock();
		}
		return myMsg;
	}

	/**
	 * Extracts an Object from the JXTA message
	 * 
	 * @param message
	 *            - a jxta message
	 * @param elemName
	 *            - an jxta message
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object getObjectFromMessage(Message message, String elemName)
			throws IOException, ClassNotFoundException {
		InputStream is = getInputStreamFromMessage(message, elemName);

		if (null == is) {
			return null;
		}
		ObjectInputStream ois = new ObjectInputStream(is);

		return ois.readObject();
	}

	/**
	 * Extracts a {@link InputStream} from the jxta message
	 * 
	 * @param message
	 *            - a message
	 * @param elemName
	 *            - an element message
	 * 
	 * @return
	 * @throws IOException
	 */
	public static InputStream getInputStreamFromMessage(Message message,
			String elemName) throws IOException {
		InputStream result = null;
		MessageElement element = message.getMessageElement(elemName);

		if (null == element) {
			return null;
		}

		if (element.getMimeType().equals(GZIP_MEDIA_TYPE)) {
			result = new GZIPInputStream(element.getStream());
		} else if (element.getMimeType().equals(MimeMediaType.AOS)) {
			result = element.getStream();
		}
		return result;
	}

	/**
	 * Converts an {@link InputStream} to byte[].
	 * 
	 * @param is
	 *            - an {@link InputStream}
	 * 
	 * @return
	 * 
	 * @throws IOException
	 */
	public static byte[] toByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();

	}

	/**
	 * As simple as it is
	 * 
	 * @param timeToWait
	 */
	public static void sleep(long timeToWait) {
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			// System.out.println(e.getMessage());
		}
	}

	/**
	 * This method recursively visits all thread groups under group.
	 * 
	 * @param group
	 *            {@link ThreadGroup}
	 * @param level
	 *            a thread level
	 */
	public static void viewRunningThreads(ThreadGroup group, int level) {
		/* Get threads in group */
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);

		/* Enumerate each thread in group */
		for (int i = 0; i < numThreads; i++) {
			// Get thread
			Thread thread = threads[i];
			System.out.println(thread.getState() + ", " + thread.getName());
		}

		/* Get thread subgroups of group */
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);

		/* Recursively visit each subgroup */
		for (int i = 0; i < numGroups; i++) {
			viewRunningThreads(groups[i], level + 1);
		}
	}

	/**
	 * This method visits all threads under group.
	 * 
	 * @param group
	 *            group {@link ThreadGroup}
	 * @param level
	 *            a thread level
	 */
	public static void visit(ThreadGroup group, int level) {
		/* Gets threads in group */
		int numThreads = group.activeCount();
		Thread[] threads = new Thread[numThreads * 2];
		numThreads = group.enumerate(threads, false);

		/* Enumerate each thread in group */
		for (int i = 0; i < numThreads; i++) {
			/* Gets a thread */
			try {
				Thread thread = threads[i];
				if (thread.getName().contains("New I/O server boss")
						|| (thread.getName().contains(Tag.BIDI_THREAD_PREFIX
								.getValue()))) {//
					thread.interrupt();
				}
			} catch (Exception e) {
				// Skip it
			}
		}

		/* Gets thread subgroups of group */
		int numGroups = group.activeGroupCount();
		ThreadGroup[] groups = new ThreadGroup[numGroups * 2];
		numGroups = group.enumerate(groups, false);

		/* Recursively visit each subgroup */
		for (int i = 0; i < numGroups; i++) {
			visit(groups[i], level + 1);
		}
	}

	/**
	 * Loads property file, seeks the key & if found, returns the value.
	 * If nothing was found, an empty String will be returned.
	 * 
	 * @param propKey search key.
	 * 
	 * @return a value of the provided key.
	 */
	public static String getValue(String propKey){
		Properties prop = new Properties();
		String val = "";
		try {
			/* Loads a properties file from class path, inside static method */
    		prop.load(PPCUtils.class.getClassLoader().getResourceAsStream("config/config.properties"));
    		val = prop.getProperty(propKey);
    		
		} catch (Exception e) {
			logger.error(e);
		}
		
		return val;
	}
	
	/**
	 * Copies input stream to the outstream
	 * 
	 * @param input
	 *            {@link InputStream}
	 * @param output
	 *            {@link OutputStream}
	 * 
	 * @return a number indicating how many bytes were copied
	 * 
	 * @throws IOException
	 *             if an input stream is greater than {@link Integer.MAX_VALUE}
	 */
	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Copies input stream to output stream
	 * 
	 * @param input
	 *            {@link InputStream}
	 * @param output
	 *            {@link OutputStream}
	 * 
	 * @return a number indicating how many bytes were copied
	 * 
	 * @throws IOException
	 *             if could not read an {@link InputStream}
	 */
	public static long copyLarge(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DefaultParameter.DEFAULT_BUFFER_SIZE.getCode()];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Transforms {@link InputStream} to {@link String}
	 * 
	 * @param input
	 *            a {@link InputStream}
	 * 
	 * @return String representation in the {@link InputStream}
	 * 
	 * @throws IOException
	 *             if could not read an {@link InputStream}
	 */
	public static String toString(InputStream input) throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw);
		return sw.toString();
	}

	/**
	 * Copies {@link InputStream} to {@link Writer}
	 * 
	 * @param input
	 *            {@link InputStream}
	 * @param output
	 *            {@link Writer}
	 * 
	 * @throws IOException
	 *             if could not read an {@link InputStream}
	 */
	public static void copy(InputStream input, Writer output)
			throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(in, output);
	}

	/**
	 * Copies byte from {@link Reader} to {@link Writer}
	 * 
	 * @param input
	 *            {@link Reader}
	 * @param output
	 *            {@link Writer}
	 * 
	 * @return a number indicating how many bytes were copied, if none, returns
	 *         -1
	 * 
	 * @throws IOException
	 *             if could not read {@link Reader}
	 */
	public static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	/**
	 * Defines a {@link PPCLogger} levels
	 * <ul>
	 * <li>SEVERE</li>
	 * </ul>
	 * <ul>
	 * <li>OFF - means logger off</li>
	 * </ul>
	 * <ul>
	 * <li>WARNING</li>
	 * </ul>
	 * <ul>
	 * <li>INFO</li>
	 * </ul>
	 * <ul>
	 * <li>FINE</li>
	 * </ul>
	 * <ul>
	 * <li>FINER</li>
	 * </ul>
	 * <ul>
	 * <li>FINEST</li>
	 * </ul>
	 */
	public enum JXTA_LOG_LEVEL {
		FINEST, FINER, FINE, INFO, WARNING, SEVERE, OFF
	};

	/**
	 * 
	 * @param input
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public static long copyLarge(Reader input, Writer output)
			throws IOException {
		char[] buffer = new char[DefaultParameter.DEFAULT_BUFFER_SIZE.getCode()];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	/**
	 * Sets up a JXTA log level
	 * 
	 * @param logLevel
	 */
	public static void setJXTALogger(JXTA_LOG_LEVEL logLevel) {
		switch (logLevel) {
		case FINEST:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.FINEST.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.FINEST.name());
			Logger.getLogger("net.jxta").setLevel(Level.ALL);
			break;
		case FINER:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.FINER.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.FINER.name());
			Logger.getLogger("net.jxta").setLevel(Level.TRACE);
			break;
		case FINE:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.FINE.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.FINE.name());
			Logger.getLogger("net.jxta").setLevel(Level.INFO);
			break;
		case INFO:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.INFO.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.INFO.name());
			Logger.getLogger("net.jxta").setLevel(Level.INFO);
			break;
		case WARNING:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.WARNING.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.WARNING.name());
			Logger.getLogger("net.jxta").setLevel(Level.WARN);
			break;
		case SEVERE:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.SEVERE.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.SEVERE.name());
			Logger.getLogger("net.jxta").setLevel(Level.FATAL);
			break;
		case OFF:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.OFF.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.OFF.name());
			Logger.getLogger("net.jxta").setLevel(Level.OFF);
			break;
		default:
			break;
		}
	}

	private static void removeJavaStdLoggerHandlers() {
		ReentrantLock lock = new ReentrantLock();
		try {
			lock.lock();
			Logger globalLogger = Logger.getLogger("global");
			@SuppressWarnings("rawtypes")
			Enumeration handlers = globalLogger.getAllAppenders();
			while (handlers.hasMoreElements()) {
				globalLogger.removeAppender((Appender) handlers.nextElement());
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (!lock.isLocked())
				lock.unlock();
		}
	}
}
