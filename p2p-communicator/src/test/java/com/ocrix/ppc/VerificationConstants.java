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

package com.ocrix.ppc;

/**
 * Holds test constants such as verification points.
 */
public final class VerificationConstants {

	private VerificationConstants() throws NoSuchMethodException {
		throw new NoSuchMethodException("you cannot initialize final class");
	}

	public static final int NET_TOPOLOGY_TYPE_SIZE = 4;
	public static String MANAGER_NAME = "alice";
	public static String NET_GROUP_NAME = "NetPeerGroup";
	public static String CUST_GROUP_NAME = "infra_ppc";
	public static String MIA = "jxta:MIA";
	public static String PEER_NAME = "testPeer";
	public static final int CREATED_PIPE_NUMBER = 3;
	public static String MSG_PAYLOAD = "alice wants to call Bob";
	public static String MSG_SOURCE = "Alice";
	public static String MSG_DESTINATION = "Bob";
	public static String VP_PIPE_UNICAST_OUTPUT_PIPE = "JxtaUnicast";
	public static String TARGET = "target";

	public static String MSG_TXT_LONG = "When the JxtaServerPipe is created, it creates an input pipe from the pipe service and behaves as a listener"
			+ "for it. When a new pipe connect message arrives, it used to create a JxtaServerPipe which is registered in a"
			+ "connection queue. During this process, a Messenger is retrieved from the endpoint service and used as a"
			+ "When the accept() method is called, the JxtaServerPipe polls the connection queue to retrieve an"
			+ "When a JxtaBidiPipe is created with one of its public constructor, it calls it connect() method which"
			+ "creates an open message (i.e., connection request) and an output pipe. Then, it waits for its resolution and notify"
			+ "Bidirectional pipes can be made reliable and use a \"sliding window\" mechanism (similar to TCP/IP) where"
			+ "received messages are acknowledged and re-ordered for proper delivery. These can be resent if necessary."
			+ "In order to establish a JxtaSocket communication, a 'server' peer will create a JxtaSocketServer and"
			+ "'accept' a socket from it. Another 'socket' peer will create a JxtaSocket, using the same pipe advertisement as"
			+ "The JxtaMulticastSocket class replicates the typical \"groups\" (i.e., reserved IP addresses)";

	public static String MSG_BINARY = "Read the directions and directly you will be directed in the right direction";

}
