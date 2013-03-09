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

package com.ocrix.ppc.threadpool;

import java.util.concurrent.BlockingQueue;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgListener;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.state.BiDiMessageSender;
import com.ocrix.ppc.state.MessageSenderJavaThread;
import com.ocrix.ppc.state.MessageSenderWasThreadPool;
import com.ocrix.ppc.state.WasBiDiMessageSender;
//import com.ocrix.ppc.type.Tag;
import com.ocrix.ppc.type.ThreadPoolType;

/**
 * Shifts between Java fixed thread pool and WAS' thread pool
 */
public class ThreadPoolSwitcher {
	/* A WAS' jndi thread pool name */
	private String jndiName;

	/**
	 * Default citor
	 */
	public ThreadPoolSwitcher() {

	}

	/**
	 * Creates a new instance of the TaskManager TaskManager can return either
	 * Standard fixed java thread pool or WAS's thread pool.
	 * 
	 * @param type
	 *            {@link ThreadPoolType}
	 * 
	 * @return or WAS's thread pool or JAVA's fixed thread pool
	 */
	public TaskManager getTaskManager(ThreadPoolType type) {
		switch (type) {
		case JAVA:
			return new StandardFixedThreadPool();

			// case WAS:
			// if (jndiName == null)
			// return new WasThreadPool(Tag.JNDI_NAME.getValue());
			// else
			// return new WasThreadPool(getJndiName());

		default:
			return new StandardFixedThreadPool();
		}
	}

	/**
	 * Sets up a jndi name
	 * 
	 * @param jndiName
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	/**
	 * Returns either {@link BiDiMessageSender} or {@link WasBiDiMessageSender}
	 * 
	 * @param type
	 *            - a {@link ThreadPoolType}
	 * @param backlog
	 *            - a {@link BlockingQueue}
	 * @param pipeFactory
	 *            - a {@link PipeFactory}
	 * @param peerGroup
	 *            - a {@link PeerGroup}
	 * @param pipeDiscovery
	 *            - a {@link PipeDiscovery}
	 * @param biDiPipeMsgListener
	 *            - a {@link PipeMsgListener}
	 * 
	 * @return a {@link Runnable}
	 */
	public Runnable getWorker(ThreadPoolType type,
			BlockingQueue<Message> backlog, PipeFactory pipeFactory,
			PeerGroup peerGroup, PipeDiscovery pipeDiscovery,
			PipeMsgListener biDiPipeMsgListener) {

		switch (type) {
		case JAVA:
			return new BiDiMessageSender(backlog, pipeFactory, peerGroup,
					pipeDiscovery, biDiPipeMsgListener);

		case WAS:
			return new WasBiDiMessageSender(backlog, pipeFactory, peerGroup,
					pipeDiscovery, biDiPipeMsgListener);
		default:
			return null;
		}
	}

	public Runnable getWorker(ThreadPoolType type,
			BlockingQueue<Message> backlog, PipeFactory pipeFactory,
			PeerGroup peerGroup, PipeDiscovery pipeDiscovery,
			PipeMsgListener biDiPipeMsgListener, int retry) {

		switch (type) {
		case JAVA:
			return new BiDiMessageSender(backlog, pipeFactory, peerGroup,
					pipeDiscovery, biDiPipeMsgListener, retry);

		case WAS:
			return new WasBiDiMessageSender(backlog, pipeFactory, peerGroup,
					pipeDiscovery, biDiPipeMsgListener, retry);
		default:
			return null;
		}
	}

	/**
	 * Returns either JAVA fixed thread pool or WAS thread pool
	 * 
	 * @param type
	 *            a {@link ThreadPoolType}
	 * @param backlog
	 *            a {@link BlockingQueue}
	 * @param peerGroup
	 *            a {@link PeerGroup}
	 * @param pipeDiscovery
	 *            a {@link PipeDiscovery}
	 * 
	 * @return a {@link Runnable}
	 * 
	 * @throws PPCException
	 *             - if could not create a thread pool
	 */
	public Runnable getSenderWorker(ThreadPoolType type,
			BlockingQueue<Message> backlog, PeerGroup peerGroup,
			PipeDiscovery pipeDiscovery) throws PPCException {

		switch (type) {
		case JAVA:
			try {
				return new MessageSenderJavaThread(backlog, peerGroup,
						pipeDiscovery);
			} catch (PPCException e) {
				System.out.println(e.getMessage());
			}
		case WAS:
			return new MessageSenderWasThreadPool(backlog, peerGroup,
					pipeDiscovery);

		default:
			return new MessageSenderJavaThread(backlog, peerGroup,
					pipeDiscovery);
		}
	}

	public Runnable getSenderWorker(ThreadPoolType type,
			BlockingQueue<Message> backlog, PeerGroup peerGroup,
			PipeDiscovery pipeDiscovery, int retry) throws PPCException {

		switch (type) {
		case JAVA:
			try {
				return new MessageSenderJavaThread(backlog, peerGroup,
						pipeDiscovery, retry);
			} catch (PPCException e) {
				System.out.println(e.getMessage());
			}
		case WAS:
			return new MessageSenderWasThreadPool(backlog, peerGroup,
					pipeDiscovery, retry);

		default:
			return new MessageSenderJavaThread(backlog, peerGroup,
					pipeDiscovery, retry);
		}
	}

	/**
	 * Attains a jndi name of the WAS thread pool.
	 * 
	 * @return a jndi name
	 */
	protected String getJndiName() {
		return this.jndiName;
	}
}
