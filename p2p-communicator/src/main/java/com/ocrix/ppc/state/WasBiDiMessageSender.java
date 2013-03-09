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

package com.ocrix.ppc.state;

import java.util.concurrent.BlockingQueue;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgListener;
//import com.ibm.websphere.asynchbeans.Work;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.pipe.PipeFactory;

/**
 * Represents a {@link WasBiDiMessageSender}
 */
public class WasBiDiMessageSender extends BiDiMessageSender {// implements Work{

	/**
	 * Creates a WAS' bi - directional message sender
	 * 
	 * @param backLog
	 *            - a reference to the message backlog, i.e. a queue of messages
	 *            to send
	 * @param pipeFactory
	 *            - a {@link PipeFactory}
	 * @param peerGroup
	 *            - a {@link PeerGroup}
	 * @param pipeDiscovery
	 *            - a {@link PipeDiscovery}
	 * @param listener
	 *            - a {@link PipeMsgListener}
	 */
	public WasBiDiMessageSender(BlockingQueue<Message> backLog,
			PipeFactory pipeFactory, PeerGroup peerGroup,
			PipeDiscovery pipeDiscovery, PipeMsgListener listener) {
		super(backLog, pipeFactory, peerGroup, pipeDiscovery, listener);
	}

	/**
	 * Creates an instance of the bi-directional message sender using WAS thread
	 * pool
	 * 
	 * @param backLog
	 *            a message buffer
	 * @param pipeFactory
	 *            a {@link PipeFactory}
	 * @param peerGroup
	 *            a {@link PeerGroup}
	 * @param pipeDiscovery
	 *            a {@link PipeDiscovery}
	 * @param listener
	 *            a {@link PipeMsgListener}
	 * @param retry
	 *            a number of tries to send message
	 */
	public WasBiDiMessageSender(BlockingQueue<Message> backLog,
			PipeFactory pipeFactory, PeerGroup peerGroup,
			PipeDiscovery pipeDiscovery, PipeMsgListener listener, int retry) {
		super(backLog, pipeFactory, peerGroup, pipeDiscovery, listener);
	}

	// @Override
	public void release() {
		super.setStopped(true);
	}
}
