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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Logger;
import net.jxta.peergroup.PeerGroup;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.listener.MessageReceiver;
import com.ocrix.ppc.listener.SenderObservable;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.MessageFactory;
import com.ocrix.ppc.service.impl.PPCServiceDiscoveryFactoryImpl;
import com.ocrix.ppc.threadpool.TaskManager;
import com.ocrix.ppc.threadpool.ThreadPoolSwitcher;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.ThreadPoolType;

/**
 * Presents an instance of Sender peer, which is responsible for sending
 * messages.
 */
public class SenderPeer extends Peer {
	/* A peer group */
	private PeerGroup peerGroup = null;
	/* A thread pool, uses for taking care of sending messages */
	private ExecutorService pool = null;
	/* A pipe discovery */
	private PipeDiscovery pipeDiscovery = null;
	/* Backlog of the messages */
	private BlockingQueue<Message> backLog = null;
	/* A message factory */
	private MessageFactory messageFactory = null;
	/* Sender observer */
	private AtomicReference<SenderObservable> publisher = null;
	/* A switcher that switches between thread pools - JAVA or WAS */
	private ThreadPoolSwitcher switcher = null;
	/* A thread pool type */
	private ThreadPoolType type = null;
	/* A task manager */
	private TaskManager taskManager = null;
	/* A time of retries */
	private int retry;

	// ------ END OF CLASS MEMBERS ------\\

	/**
	 * Note: please use another constructor with ILogger
	 */
	public SenderPeer(String peerName, String cacheFolder) throws IOException,
			PPCException {
		/* Initializes the super class - Peer */
		super(peerName, cacheFolder);
		/* Logger off */
		PPCUtils.setJXTALogger(PPCUtils.JXTA_LOG_LEVEL.OFF);

		initializes();

	}

	/**
	 * Constructor
	 * 
	 * @param peerName
	 *            a name to be given for the peer
	 * @param log
	 *            - ILogger
	 * @param isJXTADebug
	 *            - a flag indicating the JXTA logger activation. <b>true</b> -
	 *            means to activate logger
	 * 
	 * @throws IOException
	 *             - if could not create a sender peer
	 * @throws PPCException
	 *             - if could not start JXTA network
	 */
	public SenderPeer(String peerName, Logger log, boolean isJXTADebug,
			String threadPoolType, String jndiName) throws IOException,
			PPCException {
		/* Initializes the base class */
		super(peerName, log, isJXTADebug);

		if (isJXTADebug) {// JXTA logger ON
			PPCUtils.setJXTALogger(PPCUtils.JXTA_LOG_LEVEL.SEVERE);
		} else {// JXTA logger OFF
			PPCUtils.setJXTALogger(PPCUtils.JXTA_LOG_LEVEL.OFF);
		}

		/* Prevents user's slip ups */
		setThreadPoolType(threadPoolType.toUpperCase().trim());

		initializes();

		if (threadPoolType.equals(ThreadPoolType.WAS.toString())) {
			Validator.validateObjNotNull(jndiName);
			getSwitcher().setJndiName(jndiName);
		}
	}

	private void initializes() throws IOException, PPCException {
		/* Creates a new thread pool and save it */
		setPool(Executors.newFixedThreadPool(DefaultParameter.THREAD_POOL_SIZE
				.getCode()));
		/* Initializes blocking queue */
		setBackLog(new ArrayBlockingQueue<Message>(
				DefaultParameter.QUEUE_CAPASITY.getCode()));
		/* Starts the JXTA network */
		init();
		/* Creates a discovery service factory */
		PPCServiceDiscoveryFactoryImpl dfs = new PPCServiceDiscoveryFactoryImpl();
		/* Creates a pipe discovery */
		pipeDiscovery = dfs.createPipeDiscovery(getPeerManager());
		/* Create an instance of the Sender observable */
		publisher = new AtomicReference<SenderObservable>(
				new SenderObservable());
		/* Initializes the messages factory */
		messageFactory = new MessageFactory();
		/* Initializes a switcher */
		setSwitcher(new ThreadPoolSwitcher());
		/* Sets up a task manager */
		if (type == null) {
			type = ThreadPoolType.JAVA;
			setTaskManager(getSwitcher().getTaskManager(ThreadPoolType.JAVA));
		} else
			setTaskManager(getSwitcher().getTaskManager(type));
	}

	/**
	 * Sends a given message.
	 * 
	 * @param messageToBeSent
	 * 
	 * @throws IOException
	 *             - if could not create a sender peer
	 * @throws PPCException
	 *             - if could not start JXTA network
	 * @throws ExecutionException
	 *             - if the execution was terminated violently
	 */
	public void send(Message messageToBeSent) throws IOException, PPCException,
			ExecutionException {
		try {
			/* Puts a message to the backlog */
			getBackLog().offer(messageToBeSent);
			/* Sends the massage */
			getTaskManager().execute(
					getSwitcher().getSenderWorker(type, getBackLog(),
							getPeerGroup(), pipeDiscovery, getRetry()));

			// ==========================================================================
			// TEMPORARLY SOLUTION, UNTIL UNDERSTANDING WHAT EXACTLY WE SHOULD
			// NOTIFY
			// ==========================================================================
			/* Notification to the subscribers */
			getTaskManager().execute(
					new Notifier(getPeerName(), messageToBeSent));

		} catch (Exception e) {
			throw new PPCException(e.getMessage());
		}
	}

	/**
	 * Will notify about sent messages
	 * 
	 * @param subscriber
	 *            {@link MessageReceiver}
	 */
	public void subscribeSent(MessageReceiver subscriber) {
		publisher.get().subscribeSendMessage(subscriber);
	}

	// ----------------------------------------------------
	// Pay attention: shutdown is done in two stages:
	// 1. Disables new tasks from being submitted
	// 2. Cancels currently executing tasks
	// ----------------------------------------------------
	@Override
	public void destroy() {
		try {
			/* Disables new tasks from being submitted */
			getPool().shutdown();
			/* Defines the boolean indicating the status of the awaitTermination */
			boolean isTimeout = false;

			try {
				isTimeout = getPool().awaitTermination(
						DefaultParameter.WAITING_TIME.getCode(),
						TimeUnit.MILLISECONDS);
				/* Cancels currently executing tasks */
				if (getPool() != null && !isTimeout) {
					getPool().shutdownNow();
				}

			} catch (InterruptedException e) {
				/* (Re-)Cancels if current thread also interrupted */
				pool.shutdownNow();
				/* Preserves interrupt status */
				Thread.currentThread().interrupt();
				getLog().error(
						SenderPeer.class.getName() + " " + e.getMessage());
			}

			if (getPool() != null && getPool().isTerminated())
				getLog().info(" Is pool terminated ? " + isTimeout);

			if (!isTimeout) {
				setPool(null);
			}

			net.jxta.impl.util.threads.TaskManager tm = getPeerManager()
					.getNetPeerGroup().getTaskManager();
			getPeerManager().stopNetwork();
			if (tm != null)
				tm.shutdown();

			PPCUtils.visit(Thread.currentThread().getThreadGroup(), 0);

		} catch (Exception e) {
			getLog().error(SenderPeer.class.getName() + " " + e.getMessage());
		}
	}

	/**
	 * Sets a message backlog size
	 * 
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize) {
		if (bufferSize > 0)
			setBackLog(new ArrayBlockingQueue<Message>(bufferSize));
	}

	/**
	 * Returns a peer group
	 * 
	 * @return {@link PeerGroup}
	 */
	protected PeerGroup getPeerGroup() {
		return peerGroup;
	}

	private void init() throws IOException, PPCException {
		/* Starts a JXTA network */
		setPeerGroup(startNetwork());
	}

	private void setThreadPoolType(String threadPoolType) {
		Validator.validateThreadPoolType(threadPoolType);
		this.type = ThreadPoolType.valueOf(threadPoolType);
	}

	/**
	 * Assigns a peer group
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 */
	private void setPeerGroup(PeerGroup peerGroup) {
		Validator.validateNetPeerGroup(peerGroup);
		this.peerGroup = peerGroup;
	}

	/**
	 * Assigns a JAVA's thread pool
	 * 
	 * @param pool
	 *            {@link ExecutorService}
	 */
	private void setPool(ExecutorService pool) {
		Validator.validateObjNotNull(pool);
		this.pool = pool;
	}

	/**
	 * Returns a JAVA's thread pool
	 * 
	 * @return {@link ExecutorService}
	 */
	private ExecutorService getPool() {
		return pool;
	}

	/**
	 * Assigns a backlog, i.e. message buffer
	 * 
	 * @param backLog
	 *            {@link BlockingQueue}
	 */
	private void setBackLog(BlockingQueue<Message> backLog) {
		Validator.validateObjNotNull(backLog);
		this.backLog = backLog;
	}

	/**
	 * Returns a backlog
	 * 
	 * @return {@link BlockingQueue}
	 */
	private BlockingQueue<Message> getBackLog() {
		return backLog;
	}

	private void setSwitcher(ThreadPoolSwitcher switcher) {
		this.switcher = switcher;
	}

	private ThreadPoolSwitcher getSwitcher() {
		return switcher;
	}

	private void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

	private TaskManager getTaskManager() {
		return taskManager;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	private int getRetry() {
		if (retry <= 0)
			setRetry(DefaultParameter.TRIAL_NUMBER.getCode());
		return retry;
	}

	class Notifier implements Runnable {
		String peerName;
		Message msg;

		public Notifier(String peerName, Message message) {
			this.peerName = peerName;
			this.msg = message;
		}

		// @Override
		public void run() {
			publisher.get().alter();
			Message message = messageFactory.createTextualMessage(
					this.peerName, this.msg.getSource(), "sent");
			publisher.get().notifySubscribers(message);
		}
	}
}
