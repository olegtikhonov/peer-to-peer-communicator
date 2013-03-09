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

package com.ocrix.ppc.type;

/**
 * Defines peer network topology
 * <p>
 * <b>ADHOC</b> - Is a very simple topology. It's not connected to RDV or RELAY
 * peers. Use a multi-casting on the LAN, in order to communicate with other
 * peers.
 * <p>
 * <b>EDGE</b> - Automatically try to connect and to remain connected to one and
 * only one rendezvous peer If a connection with their rendezvous peer is lost,
 * it will try establish a connection with the next rendezvous it knows about,
 * until success
 * <p>
 * <b>RDV</b> - Is an EDGE peer. Can communicate using multi-casting, TCP and
 * HTTP. Should operate with a public address, which makes them reachable from
 * anywhere on the WAN. With a private address is redundant when multi-casting
 * is enabled by all peers on the LAN. Can demote themselves into edge peers
 * when configured so and when few edge peers are connected to them. They can
 * also become rendezvous again if there are not enough rendezvous peers
 * available. Accept lease requests from edge peers and use these to propagate
 * infrastructure related messages and messages propagated through the peergroup
 * to edge peers
 * <p>
 * <b>RELAY</b> - Is an EDGE peer providing means for peers having a private
 * address on a LAN to be reachable from the WAN. Are typically enabled with a
 * public IP addresses. Edge peers with a private address have to be able to
 * connect to them. A relay with a private address is useless. Edge peers
 * connect to relay peers. When a remote peers wants to connect to NAT-ed peers,
 * the process of finding a route will indicate that they should go through the
 * relay peer.
 * 
 */
public enum NetTopologyType {
	ADHOC, EDGE, RDV, RELAY
}
