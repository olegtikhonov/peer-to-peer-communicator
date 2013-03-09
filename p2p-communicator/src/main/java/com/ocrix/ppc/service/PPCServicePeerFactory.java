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

package com.ocrix.ppc.service;

import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.peer.BiDiPeer;
import com.ocrix.ppc.peer.ReceiverPeer;
import com.ocrix.ppc.peer.SenderPeer;


/**
 * Defines a peer to peer service factory.
 * This service is exported in OSGi environment
 */
public interface PPCServicePeerFactory {

	/**
	 * Creates an instance of the Receiver peer which responsible for receiving incoming messages
	 * 
	 * @param peerName - a name to be given
	 * 
	 * @return {@link ReceiverPeer}
	 * 
	 * @throws PPCException - if could not start a JXTA network
	 */
	public ReceiverPeer createReceiverPeer(String peerName) throws PPCException;
	
	/**
	 * Creates an instance of the Sender peer which responsible for sending messages
	 * 
	 * @param peerName - a name to be given
	 * 
	 * @return {@link SenderPeer}
	 * 
	 * @throws PPCException - if could not start a JXTA network
	 */
	public SenderPeer createSenderPeer(String peerName) throws PPCException;

	
	/**
	 * Creates a bi-directional peer
	 * 
	 * @param peerName a name to be given 
	 * 
	 * @return {@link BiDiPeer}
	 * 
	 * @throws PPCException - if could not start a JXTA network
	 */
	public BiDiPeer createBiDiPeer(String peerName) throws PPCException;
	
}
