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

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Utils {
	/* Do not create the instance of this class because all functions are static */
	private Utils() {
	}

	public enum JXTA_LOG_LEVEL {
		FINEST, FINER, FINE, INFO, WARNING, SEVERE, OFF
	};

	/**
	 * Deletes a folder and its context recursively
	 * 
	 * @param dir
	 *            - to be deleted
	 * 
	 * @return true if succeeded
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
		// The directory is now empty so delete it
		return dir.delete();
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
			break;
		case FINER:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.FINER.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.FINER.name());
			break;
		case FINE:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.FINE.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.FINE.name());
			break;
		case INFO:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.INFO.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.INFO.name());
			break;
		case WARNING:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.WARNING.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.WARNING.name());
			break;
		case SEVERE:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.SEVERE.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.SEVERE.name());
			break;
		case OFF:
			removeJavaStdLoggerHandlers();
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.OFF.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.OFF.name());
			break;
		default:
			break;
		}
	}

	private static void removeJavaStdLoggerHandlers() {
		ReentrantLock lock = new ReentrantLock();
		try {
			lock.lock();
			Logger globalLogger = Logger.getLogger("net.jxta");
			Handler[] handlers = globalLogger.getHandlers();
			for (Handler handler : handlers) {
				System.out.println("HANDLER=" + handler);
				// globalLogger.removeHandler(handler);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (!lock.isLocked())
				lock.unlock();
		}
	}

	public static void listDirectories() {
		File baseDir = new File(System.getProperty("user.dir") + "/" + "target");

		File[] files = baseDir.listFiles();
		for (File f : files) {
			if (f.isDirectory() && f.getName().contains("Test")
					|| f.getName().contains("freak")
					|| f.getName().contains("alice")
					|| f.getName().contains("testPeer")
					|| f.getName().contains("first")
					|| f.getName().contains("second")
					|| f.getName().contains("geek")
					|| f.getName().contains("jxta")
					|| f.getName().contains("other")
					|| f.getName().contains("dork")
					|| f.getName().contains("carol")
					|| f.getName().contains("bob")) {
				deleteDir(f);
			}
		}
	}
}
