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

import java.io.IOException;
import org.apache.log4j.Logger;

import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.peer.BiDiPeer;

/**
 * Creates Peers i. BIDIRECTIONAL Peer - has a two unidirectional pipes ii.
 * UNIDIRECTIONAL Peer - has only one pipe for sending
 * 
 */
public class PeerFactory {

	/**
	 * Creates a receiver peer, that peer is responsible for getting messages.
	 * 
	 * @param peerName
	 *            a name to be given for the peer
	 * 
	 * @return {@link ReceiverPeer}
	 * 
	 * @throws IOException
	 *             - if a creation of the peer failed
	 * @throws PPCException
	 */
	public ReceiverPeer createReceiverPeer(String peerName, String cachePath)
			throws IOException, PPCException {
		return new ReceiverPeer(peerName, cachePath);
	}

	/**
	 * Creates a receiver peer, that responsible for obtaining incoming messages
	 * 
	 * @param peerName
	 *            - a name to be given for the peer
	 * @param log
	 *            - ILogger
	 * @param isDebug
	 *            - a flag indicating if activate JXTA logger or not.
	 *            <b>true</b> activates JXTA logger
	 * 
	 * @return {@link ReceiverPeer}
	 * 
	 * @throws IOException
	 *             - if could not create Receiver peer
	 * @throws PPCException
	 *             - if could not start JXTA's network
	 */
	public ReceiverPeer createReceiverPeer(String peerName, Logger log,
			boolean isDebug) throws IOException, PPCException {
		return new ReceiverPeer(peerName, log, isDebug);
	}

	/**
	 * Creates an instance of the SenderPeer, that's responsible for sending
	 * messages.
	 * 
	 * @param peerName
	 *            a name to be given for the peer
	 * @param cachePath
	 *            a path where a cache will be saved
	 * 
	 * @return {@link SenderPeer}
	 * 
	 * @throws IOException
	 *             - if a creation failed
	 * @throws PPCException
	 *             - if could not start JXTA network
	 */
	public SenderPeer createSenderPeer(String peerName, String cachePath)
			throws IOException, PPCException {
		return new SenderPeer(peerName, cachePath);
	}

	/**
	 * Creates a sender peer which responsible for sending messages
	 * 
	 * @param peerName
	 *            - a name to be given for the peer
	 * @param log
	 *            - ILogger
	 * @param isJXTADebug
	 *            - a flag indicating the JXTA logger activation. <b>true</b>
	 *            means to activate a logger
	 * @param threadPoolType
	 *            - either JAVA or WAS
	 * @param jndiName
	 *            - if working with WAS and is desired to use WAS' thread pool,
	 *            provide jndi WAS' thread pool
	 * 
	 * @return {@link SenderPeer}
	 * 
	 * @throws IOException
	 *             - if could not create a peer
	 * @throws PPCException
	 *             - if could not start JXTA's network
	 */
	public SenderPeer createSenderPeer(String peerName, Logger log,
			boolean isJXTADebug, String threadPoolType, String jndiName)
			throws IOException, PPCException {
		return new SenderPeer(peerName, log, isJXTADebug, threadPoolType,
				jndiName);
	}

	/**
	 * Creates a new instance of the bi-directional peer
	 * 
	 * @param peerName
	 *            a name to be given for the peer
	 * @param cachePath
	 *            a path to the cache where a peer will be save its cache
	 * 
	 * @return {@link BiDiPeer}
	 * 
	 * @throws IOException
	 *             if a peer creation failed or could not start JXTA network
	 */
	public BiDiPeer createBiDiPeer(String peerName, String cachePath)
			throws IOException, PPCException {
		return new BiDiPeer(peerName, cachePath);
	}

	/**
	 * Creates BiDiPeer with default cache location
	 * 
	 * @param peerName
	 *            a name to be given
	 * @param log
	 *            a log to be used
	 * @param isJXTADebug
	 *            if do activate JXTA logger
	 * @param threadPoolType
	 *            - either <b>WAS</b> or <b>JAVA</b>
	 * 
	 * @return {@link BiDiPeer}
	 * @throws IOException
	 *             if a peer creation failed
	 * @throws PPCException
	 *             if could not start JXTA network
	 */
	public BiDiPeer createBiDiPeer(String peerName, Logger log,
			boolean isJXTADebug, String threadPoolType, String jndiName)
			throws IOException, PPCException {

		return new BiDiPeer(peerName, log, isJXTADebug, threadPoolType,
				jndiName);
	}
}
