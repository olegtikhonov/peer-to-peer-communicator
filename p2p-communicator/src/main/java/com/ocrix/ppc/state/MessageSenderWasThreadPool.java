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
import org.apache.log4j.Logger;
import net.jxta.peergroup.PeerGroup;
//import com.ibm.websphere.asynchbeans.Work;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.message.Message;


/**
 * Represents a worker for sending message that will work on WAS' thread pool
 */
public class MessageSenderWasThreadPool extends MessageSenderJavaThread {// implements Work{
	private static final Logger logger = Logger.getLogger(MessageSenderWasThreadPool.class);

	/**
	 * Creates a {@link MessageSenderJavaThread}
	 * 
	 * @param backlog - a message backlog
	 * @param peerGroup - a a {@link PeerGroup}
	 * @param pipeDiscovery - a {@link PipeDiscovery}
	 * 
	 * @throws PPCException - if could not create a worker
	 */
	public MessageSenderWasThreadPool(BlockingQueue<Message> backlog, PeerGroup peerGroup, PipeDiscovery pipeDiscovery) throws PPCException {
		super(backlog, peerGroup, pipeDiscovery);
		logger.info("MessageSenderWasThreadPool constructor");
	}
	
	public MessageSenderWasThreadPool(BlockingQueue<Message> backlog, PeerGroup peerGroup, PipeDiscovery pipeDiscovery, int retry) throws PPCException {
		super(backlog, peerGroup, pipeDiscovery, retry);
		logger.info("MessageSenderWasThreadPool constructor");
	}


//	@Override
	public void release() {
		super.setIsStopped(true);
	}
}
