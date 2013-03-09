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
import java.lang.ref.WeakReference;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;

import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.util.JxtaBiDiPipe;
import net.jxta.util.ServerPipeAcceptListener;
import com.ocrix.ppc.commons.PPCUtils;
import com.ocrix.ppc.commons.Validator;
import com.ocrix.ppc.discovery.DiscoveryFactory;
import com.ocrix.ppc.discovery.PipeDiscovery;
import com.ocrix.ppc.exception.PPCException;
import com.ocrix.ppc.listener.MessageReceiver;
import com.ocrix.ppc.listener.ReceiverObservable;
import com.ocrix.ppc.message.Message;
import com.ocrix.ppc.message.TextualMessage;
import com.ocrix.ppc.pipe.PipeFactory;
import com.ocrix.ppc.pipe.ServerPipe;
import com.ocrix.ppc.threadpool.TaskManager;
import com.ocrix.ppc.threadpool.ThreadPoolSwitcher;
import com.ocrix.ppc.type.DefaultParameter;
import com.ocrix.ppc.type.ThreadPoolType;

/**
 * Represents a bi-directional peer, that can send and receive messages
 * asynchronicaly
 */
public class BiDiPeer extends Peer implements PipeMsgListener {
	/* Class members */
	private PeerGroup peerGroup = null;
	/* server accept listener */
	private ServerPipeAcceptListener serverAcceptListener = null;
	/* atomic reference for Jxta bi-directional pipe */
	private final AtomicReference<JxtaBiDiPipe> acceptedPipe = new AtomicReference<JxtaBiDiPipe>();
	/* Server pipe */
	private ServerPipe serverPipe = null;
	/* Pipe factory */
	private PipeFactory pipeFactory = null;
	/* Bi directional pipe */
	private PipeMsgListener biDiPipeMsgListener = null;
	/* Thread pool */
	private TaskManager taskManager = null;
	/* Thread pool type */
	private ThreadPoolType type = null;
	/* Backlog of the messages */
	private BlockingQueue<Message> backLog = null;
	/* A switcher that switches between thread pools - JAVA or WAS */
	private ThreadPoolSwitcher switcher = null;
	/*
	 * A publisher responsible for notifying the clients that messages is
	 * obtained
	 */
	private AtomicReference<ReceiverObservable> publisher = null;
	/* A pipe discovery */
	private PipeDiscovery pipeDiscovery = null;
	/* How many time trying to retransmit the message */
	private int retry;

	// ----------------- END CLASS MEMBERS -----------------

	/**
	 * Creates a bi-directional peer
	 * 
	 * @param peerName
	 *            - a name to be given, for instance <code>PBX_5</code>
	 * @param cacheFolder
	 *            - a place where a cache will be saved
	 * 
	 * @throws IOException
	 *             - if a peer creation failed
	 * @throws PPCException
	 *             - if a JXTA network is not started
	 */
	public BiDiPeer(String peerName, String cacheFolder) throws IOException,
			PPCException {
		/* Initializes the super Peer */
		super(peerName, cacheFolder);
		/* Logger off */
		PPCUtils.setJXTALogger(PPCUtils.JXTA_LOG_LEVEL.OFF);
		/* Sets Java's fixed thread pool */
		setDefaultThreadPoolType();
		/* Initializes the class members */
		initialize();
	}

	/**
	 * Creates an instance of bi-directional pipe.
	 * 
	 * @param peerName
	 *            a name to be given
	 * @param log
	 *            - {@link ILogger}
	 * @param isJXTADebug
	 *            - if there is a need for activating JXTA logger in debug mode
	 * @param threadPoolType
	 *            {@link ThreadPoolType}
	 * @param jndiName
	 *            - if a chosen thread pool type is WAS, then have to provide
	 *            WAS' thread pool jndi name
	 * 
	 * @throws IOException
	 *             - if a creation of bidi peer is failed
	 * @throws PPCException
	 *             - if could not start JXTA network
	 */
	public BiDiPeer(String peerName, Logger log, boolean isJXTADebug,
			String threadPoolType, String jndiName) throws IOException,
			PPCException {
		/* Initializes the base class */
		super(peerName, log, isJXTADebug);
		/* Prevents user's slip ups */
		setThreadPoolType(threadPoolType.toUpperCase().trim());

		if (isJXTADebug) {/* JXTA Logger in Debug mode is ON */
			PPCUtils.setJXTALogger(PPCUtils.JXTA_LOG_LEVEL.SEVERE);
		} else {
			PPCUtils.setJXTALogger(PPCUtils.JXTA_LOG_LEVEL.OFF);
		}

		initialize();

		if (threadPoolType.equals(ThreadPoolType.WAS.toString())) {
			Validator.validateObjNotNull(jndiName);
			getSwitcher().setJndiName(jndiName);
		}
	}

	/**
	 * Sends a response, useful in case when requires immediately response to
	 * the incoming message As JXTA suggests its old approach. Try avoid using
	 * this function, instead use send.
	 * 
	 * @param messageToBeSent
	 *            {@link TextualMessage}
	 * 
	 * @throws IOException
	 *             if could not send message
	 */
	public void sendResponse(Message messageToBeSent) throws IOException {
		/* Sets a message listener */
		Validator.validateObjNotNull(messageToBeSent);

		@SuppressWarnings("unused")
		boolean status = acceptedPipe.get().sendMessage(messageToBeSent);
		// TODO: add error/status callback
		publisher.get().alter();
	}

	/**
	 * Sends a message to the recipient. <br>
	 * Firstly puts message to the blocking queue <br>
	 * Secondly, take a thread from the thread pool and send message
	 * 
	 * @param messageToBeSent
	 *            - a message to be sent
	 * 
	 * @throws IOException
	 * @throws PPCException
	 */
	public void send(Message messageToBeSent) throws IOException, PPCException {
		/* 1. puts a message into blocking queue, default is 50 */
		if (getBackLog() != null) {
			getBackLog().offer(messageToBeSent);
		}
		/*
		 * 2. Runs a worker. For each message will be created its own worker.
		 * The reason for that is to prevent locking issues for only single
		 * instance of the worker. It may be changed in near future.
		 */
		getTaskManager().execute(
				new WeakReference<Runnable>(getSwitcher().getWorker(type,
						getBackLog(), getPipeFactory(), getPeerGroup(),
						getPipeDiscovery(), getBiDiPipeMsgListener(),
						getRetry())).get());
	}

	@Override
	public void destroy() {
		try {
			getTaskManager().shutdown();
			PPCUtils.visit(Thread.currentThread().getThreadGroup().getParent(),
					0);
			getPeerManager().stopNetwork();
			/* Clears all messages from the queue */
			getBackLog().clear();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	// @Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		AtomicReference<PipeMsgEvent> handler = new AtomicReference<PipeMsgEvent>(
				event);
		if (handler.get() != null) {
			if (handler.get().getMessage() != null) {
				Message myMessage = null;
				try {
					getLog().info("EVENT " + event.getMessage());
					myMessage = PPCUtils.convertJxtaMsgToPpcMsg(handler.get()
							.getMessage());
				} catch (IOException e) {
					getLog().info(
							this.getClass().getName() + " " + e.getMessage());
				} finally {
					publisher.get().alter();
					publisher.get().notifySubscribers(myMessage);
				}
			} else {
				getLog().error("MESSAGE IS NULL");
			}
		} else {
			getLog().error("EVENT IS NULL");
		}
	}

	/**
	 * Sets up a message backlog size
	 * 
	 * @param bufferSize
	 */
	public void setBufferSize(int bufferSize) {
		if (bufferSize > 0)
			setBackLog(new ArrayBlockingQueue<Message>(bufferSize));
	}

	/**
	 * If a bidi client should get incoming messages, it have to subscribe
	 * getting messages. In addition, a client have to implement
	 * {@link MessageReceiver} update function.
	 * <ol>
	 * <li>
	 * <code>public class BiDiMessageSender implements Runnable</code></li>
	 * <li>
	 * <code>
	 * 
	 * @Override public void update(Observable o, Object arg) {<br> if(arg
	 *           instanceof TextualMessage){<br> TextualMessage textualMessage =
	 *           (TextualMessage) arg;<br> }<br> }<br> </code> </li>
	 *           </ol>
	 * @param subscriber
	 */
	public void subscribeRequestMessages(MessageReceiver subscriber) {
		publisher.get().subscribeReceiveMessages(subscriber);
	}

	// ------------------------------------------------------
	// PRIVATE FUNCTIONS AREA - ONLY MEMBERS ARE ALLOWED
	// ------------------------------------------------------

	/**
	 * Initializes the following:
	 * <ul>
	 * <li>ReceiverObservable</li>
	 * <li>{@link PeerGroup}</li>
	 * <li>BiDi pipe msg listener</li>
	 * <li>server pipe accept listener</li>
	 * <li>{@link PipeFactory}</li>
	 * <li>server pipe</li>
	 * <li>{@link PipeDiscovery}</li>
	 * <li>{@link ThreadPoolSwitcher}</li>
	 * <li>{@link TaskManager}</li>
	 * <li>backlog - message buffering</li>
	 * </ul>
	 */
	private void initialize() throws IOException, PPCException {
		/* Initializes the publisher */
		publisher = new AtomicReference<ReceiverObservable>(
				new ReceiverObservable());
		/* Starts a network */
		setPeerGroup(startNetwork());
		/* Sets pipe message listener */
		setBiDiPipeMsgListener(this);
		/* Creates a server accept listener */
		createServerPipeAcceptListener();
		/* Creates pipe factory */
		setPipeFactory(new PipeFactory());
		/* Creates a server pipe for listening to incoming messages */
		setServerPipe(getPipeFactory().createServerPipe(getPeerGroup(),
				getServerAcceptListener(), getPeerName()));
		/* Creates a pipe discovery */
		setPipeDiscovery(new DiscoveryFactory()
				.createPipeDiscovery(getPeerManager()));
		/* Creates a switcher */
		setSwitcher(new ThreadPoolSwitcher());
		/* Sets a type of thread pool */
		setTaskManager(getSwitcher().getTaskManager(type));
		/* Initializes blocking queue */
		setBackLog(new ArrayBlockingQueue<Message>(
				DefaultParameter.QUEUE_CAPASITY.getCode()));
	}

	/**
	 * Sets up a default thread pool
	 */
	private void setDefaultThreadPoolType() {
		type = ThreadPoolType.JAVA;
	}

	/**
	 * Creates a server pipe accept listener
	 */
	private void createServerPipeAcceptListener() {
		setServerAcceptListener(new ServerPipeAcceptListener() {

			// @Override
			public void serverPipeClosed() {
			}

			// @Override
			public void pipeAccepted(JxtaBiDiPipe pipe) {
				acceptedPipe.set(pipe);
				acceptedPipe.get().setMessageListener(getBiDiPipeMsgListener());
			}
		});
	}

	/**
	 * Sets a peer group
	 * 
	 * @param peerGroup
	 *            {@link PeerGroup}
	 */
	private void setPeerGroup(PeerGroup peerGroup) {
		this.peerGroup = peerGroup;
	}

	/**
	 * Attains a peer group
	 * 
	 * @return {@link PeerGroup}
	 */
	private PeerGroup getPeerGroup() {
		return peerGroup;
	}

	/**
	 * Arranges a server accept listener
	 * 
	 * @param serverAcceptListener
	 */
	private void setServerAcceptListener(
			ServerPipeAcceptListener serverAcceptListener) {
		this.serverAcceptListener = serverAcceptListener;
	}

	/**
	 * Attains a server pipe accept listener
	 * 
	 * @return {@link ServerPipeAcceptListener}
	 */
	private ServerPipeAcceptListener getServerAcceptListener() {
		return serverAcceptListener;
	}

	/**
	 * Arranges a server pipe
	 * 
	 * @param serverPipe
	 *            {@link ServerPipe}
	 */
	private void setServerPipe(ServerPipe serverPipe) {
		this.serverPipe = serverPipe;
	}

	/**
	 * Attains a server pipe
	 * 
	 * @return {@link ServerPipe }
	 */
	protected ServerPipe getServerPipe() {
		return serverPipe;
	}

	/**
	 * Arranges a pipe factory
	 * 
	 * @param pipeFactory
	 *            {@link PipeFactory}
	 */
	private void setPipeFactory(PipeFactory pipeFactory) {
		this.pipeFactory = pipeFactory;
	}

	/**
	 * Attains a {@link PipeFactory}
	 * 
	 * @return {@link PipeFactory}
	 */
	private PipeFactory getPipeFactory() {
		return pipeFactory;
	}

	/**
	 * Arranges a bi-directional pipe
	 * 
	 * @param biDiPipeMsgListener
	 *            - bi-directional listener
	 */
	private void setBiDiPipeMsgListener(PipeMsgListener biDiPipeMsgListener) {
		this.biDiPipeMsgListener = biDiPipeMsgListener;
	}

	/**
	 * Attains a bi-directional pipe message listener
	 * 
	 * @return {@link PipeMsgListener}
	 */
	private PipeMsgListener getBiDiPipeMsgListener() {
		return biDiPipeMsgListener;
	}

	/**
	 * Arranges a backlog of the incoming messages
	 * 
	 * @param backLog
	 *            {@link BlockingQueue}
	 */
	private void setBackLog(BlockingQueue<Message> backLog) {
		this.backLog = backLog;
	}

	/**
	 * Attains a backlog of the incoming massages
	 * 
	 * @return {@link BlockingQueue}
	 */
	private BlockingQueue<Message> getBackLog() {
		return backLog;
	}

	/**
	 * Sets up the task manager
	 * 
	 * @param taskManager
	 *            {@link TaskManager}
	 */
	private void setTaskManager(TaskManager taskManager) {
		Validator.validateObjNotNull(taskManager);
		this.taskManager = taskManager;
	}

	/**
	 * Attains a task manager
	 * 
	 * @return {@link TaskManager}
	 */
	private TaskManager getTaskManager() {
		return taskManager;
	}

	/**
	 * Sets up a thread pool switcher
	 * 
	 * @param switcher
	 *            {@link ThreadPoolSwitcher}
	 */
	private void setSwitcher(ThreadPoolSwitcher switcher) {
		Validator.validateObjNotNull(switcher);
		this.switcher = switcher;
	}

	/**
	 * Sets up a thread pool.
	 * 
	 * @see {@link ThreadPoolType}
	 * 
	 * @param threadPoolType
	 *            a type of thread pool to use
	 */
	private void setThreadPoolType(String threadPoolType) {
		Validator.validateThreadPoolType(threadPoolType);
		this.type = ThreadPoolType.valueOf(threadPoolType);
	}

	/**
	 * Shifts between thread pools
	 * 
	 * @return {@link ThreadPoolSwitcher}
	 */
	private ThreadPoolSwitcher getSwitcher() {
		return switcher;
	}

	/**
	 * Sets up a pipe discovery for seeking published pipes within JXTA network
	 * 
	 * @param pipeDiscovery
	 *            {@link PipeDiscovery}
	 */
	private void setPipeDiscovery(PipeDiscovery pipeDiscovery) {
		this.pipeDiscovery = pipeDiscovery;
	}

	/**
	 * Attains a pipe discovery
	 * 
	 * @return {@link PipeDiscovery}
	 */
	private PipeDiscovery getPipeDiscovery() {
		return pipeDiscovery;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	private int getRetry() {
		return retry;
	}
}
