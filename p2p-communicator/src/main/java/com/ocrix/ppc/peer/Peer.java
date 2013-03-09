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

package com.ocrix.ppc.peer;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import net.jxta.platform.NetworkManager.ConfigMode;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.commons.PPCUtils.JXTA_LOG_LEVEL;
import com.ocrix.ppc.exception.PPCException;

/**
 * Defines a skeleton of the sub-peers. The cache location is default and named
 * ".jxta". Created where application is running. <br>
 * <b>Note</b>: in the future release will be configurable. <br>
 * <b>Note</b>: do not forget clean the cache.
 */
public abstract class Peer {// extends Observable
	/* A network manager */
	private NetworkManager peerManager;
	/* A peer name */
	private String peerName;
	/* A path to the folder where a cache should be saved */
	private String cachePath;
	/* A logger */
	private Logger log;
	/* A flag indicates if use debug or not */
	private boolean isDebug = false;

	/**
	 * Closes the Input pipe and stops the JXTA network
	 */
	public abstract void destroy();

	/**
	 * Creates an instance of Peer
	 * 
	 * @param peerManager
	 *            - JXTA network manager
	 * @param peerName
	 *            - a name to be given for the peer
	 * @throws IOException
	 */
	public Peer(String peerName, String cacheFolder) throws IOException {
		/* Initializes a peer name */
		setPeerName(peerName);
		/* Initializes a peer cache location */
		setCachePath(cacheFolder);
		/* Initializes a network manager */
		setPeerManager(createNetworkManager(peerName));
		/* Sets a multicast pool size */
		getPeerManager().getConfigurator().setMulticastPoolSize(1);

//		getPeerManager().getNetPeerGroup().getEndpointService().
		
		/* Logger off */
		System.setProperty(net.jxta.logging.Logging.JXTA_LOGGING_PROPERTY,
				java.util.logging.Level.OFF.toString());
		/* Will create PPCLogger */
		setLog(null);

	}

	/**
	 * Creates an instance of Peer. The path to the peer's cache will match: "."
	 * + peer name
	 * 
	 * @param peerName
	 *            a name to be given
	 * @param log
	 *            a logger
	 * @param isDebug
	 *            - indicates if a logger will be in debug mode or not, so
	 *            <b>true</b> - if we wants to ON JXTA logger
	 * 
	 * @throws IOException
	 *             - if a peer creation failed
	 */
	public Peer(String peerName, Logger log, boolean isDebug)
			throws IOException {
		/* Initilaizes a peer name */
		setPeerName(peerName);
		/* Initializes a peer's cache folder */
		setCachePath(getPeerName());
		/* Initializes a ILogger */
		setLog(log);
		/* Activate JXTA logger in debug mode */
		setDebug(isDebug);
		/* Initializes a network manager */
		setPeerManager(createNetworkManager(peerName));
		/* Sets up a multicast pool size */
		getPeerManager().getConfigurator().setMulticastPoolSize(1);

		if (!isDebug()) {
			/* Logger off */
			System.setProperty("net.jxta.logging.Logging",
					JXTA_LOG_LEVEL.OFF.name());
			System.setProperty("net.jxta.level", JXTA_LOG_LEVEL.OFF.name());
		}
	}

	/**
	 * Returns a peer's cache path, i.e. cache folder location
	 * 
	 * @return a cache location
	 */
	public String getCachePath() {
		return cachePath;
	}

	/**
	 * Returns a given peer name
	 * 
	 * @return a peer name, for example <code>PBX_1</code>
	 */
	public String getPeerName() {
		return peerName;
	}

	/**
	 * Attains a logger
	 * 
	 * @return ILogger
	 */
	protected synchronized Logger getLog() {
		return log;
	}

	/**
	 * Attains a {@link NetworkManager}
	 * 
	 * @return a network manager
	 */
	protected NetworkManager getPeerManager() {
		return peerManager;
	}

	/**
	 * Starts the JXTA network
	 * 
	 * @throws PeerGroupException
	 *             - if starting is failed
	 * @throws IOException
	 *             - if any other problem occured
	 * @throws PPCException
	 */
	protected synchronized PeerGroup startNetwork() throws IOException,
			PPCException {
		try {
			return getPeerManager().startNetwork();
		} catch (PeerGroupException e) {
			throw new PPCException("Could not start JXTA network "
					+ e.getMessage());
		}
	}

	/**
	 * Sets up a logger
	 * 
	 * @param log
	 *            a logger
	 */
	private void setLog(Logger log) {
		if (log == null) {
			this.log = log;
		}
	}

	/**
	 * Attains a flag that indicates if we wants to switch ON a JXTA logger
	 * 
	 * @return boolean - true if switch ON JXTA logger
	 */
	private boolean isDebug() {
		return isDebug;
	}

	/**
	 * Sets up a flag indicating JXTA activation logger in debug mode
	 * 
	 * @param isDebug
	 *            - {@link Boolean}
	 */
	private void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * Sets up a peer's manager
	 * 
	 * @param peerManager
	 *            {@link NetworkManager}
	 */
	private void setPeerManager(NetworkManager peerManager) {
		Validator.validateNetworkManager(peerManager);
		this.peerManager = peerManager;
	}

	/**
	 * Sets up a peer name
	 * 
	 * @param peerName
	 */
	private void setPeerName(String peerName) {
		Validator.validateString(peerName);
		this.peerName = peerName;
	}

	/**
	 * Creates a {@link NetworkManager}
	 * 
	 * @param peerName
	 *            a peer name to be given
	 * 
	 * @return a network manager
	 * 
	 * @throws IOException
	 *             - if could not create a network manager
	 */
	private NetworkManager createNetworkManager(String peerName)
			throws IOException {
		PPCUtils.deleteDir(new File(getCachePath()));
		PPCUtils.createDir(new File(getCachePath()));
		return new NetworkManager(ConfigMode.ADHOC, peerName, new File(
				getCachePath()).toURI());
	}

	/**
	 * Sets up a cache location
	 * 
	 * @param cachePath
	 */
	private void setCachePath(String cachePath) {
		Validator.validateString(cachePath);
		this.cachePath = cachePath;
	}
}
