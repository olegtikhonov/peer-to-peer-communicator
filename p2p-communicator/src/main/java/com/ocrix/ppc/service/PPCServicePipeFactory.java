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

import java.io.IOException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.util.QueuingServerPipeAcceptor;

import com.ocrix.ppc.pipe.BiDiPipe;
import com.ocrix.ppc.pipe.PropagatePipe;
import com.ocrix.ppc.pipe.ServerPipe;
import com.ocrix.ppc.pipe.UnicastPipe;
import com.ocrix.ppc.pipe.UnicastSecurePipe;

/**
 * Defines an interface for pipes that will be created
 */
public interface PPCServicePipeFactory {
	/**
	 * Creates unicast pipe
	 * 
	 * @param peerGroup
	 *            - PeerGroup
	 * @param pipeName
	 *            - name to be given for the created pipe
	 * 
	 * @return Unicast pipe
	 */
	UnicastPipe createUnicastPipe(PeerGroup peerGroup, String pipeName)
			throws IOException;

	/**
	 * Creates an unicast secure pipe
	 * 
	 * @param peerGroup
	 *            - a peer group
	 * @param pipeName
	 *            - a name to be given for the created pipe
	 * 
	 * @return Unicast secure pipe
	 */
	UnicastSecurePipe createUnicastSecurePipe(PeerGroup peerGroup,
			String pipeName) throws IOException;

	/**
	 * Creates a propagate pipe
	 * 
	 * @param peerGroup
	 *            - a peer group
	 * @param pipeName
	 *            - a name to be given for the created pipe
	 * 
	 * @return Propagate pipe
	 */
	PropagatePipe createPropagatePipe(PeerGroup peerGroup, String pipeName)
			throws IOException;

	/**
	 * Creates a server pipe for bidi communication
	 * 
	 * @param peerGroup
	 *            a peer group
	 * @param serverAcceptorListener
	 *            - a listener that listens for the requests
	 * @param pipeName
	 *            a name to be given for the created pipe
	 * 
	 * @return a server pipe
	 */
	ServerPipe createServerPipe(PeerGroup peerGroup,
			QueuingServerPipeAcceptor serverAcceptorListener, String pipeName)
			throws IOException;

	/**
	 * Creates a bidi-pipe
	 * 
	 * @param peerGroup
	 *            a peer group
	 * @param serverPipeAdv
	 *            a pipe advertisement
	 * @param pipeName
	 *            a name to be given for the created pipe
	 * @param listener
	 *            a pipe message listener
	 * @return BiDiPipe
	 */
	BiDiPipe createBiDiPipe(PeerGroup peerGroup,
			PipeAdvertisement serverPipeAdv, String pipeName,
			PipeMsgListener listener) throws IOException;

	/**
	 * Creates a bidi pipe
	 * 
	 * @param peerGroup
	 *            a peer group
	 * @param serverPipeAdv
	 *            a pipe advertisement
	 * @param pipeName
	 *            a name to be given for the created pipe
	 * @param listener
	 *            a pipe message listener
	 * @param timeout
	 *            a timeout that take for trying to create a pipe
	 * 
	 * @return BiDiPipe
	 */
	BiDiPipe createBiDiPipe(PeerGroup peerGroup,
			PipeAdvertisement serverPipeAdv, String pipeName,
			PipeMsgListener listener, int timeout) throws IOException;
}
