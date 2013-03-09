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

import com.ocrix.ppc.message.Message;

/**
 * Enum defines the following:
 * 
 * <ul>
 * <li><b>NAME</b> - uses for discovery service</li>
 * <li><b>GROUP_ID</b> - defines a tag group id</li>
 * <li><b>DEFAULT_PEER_GROUP</b> - the default group name</li>
 * <li><b>SOURCE</b> - defines a field in {@link Message} indicating who is an
 * initializer</li>
 * <li><b>DESTINATION</b> - defines a field in {@link Message} indicating who is
 * a recipient</li>
 * <li><b>PAYLOAD</b> - defines a field in {@link Message} indicating a pay-load
 * </li>
 * <li><b>STREAM</b> - defines a field in {@link Message} indicating a binary
 * pay-load</li>
 * <li><b>JNDI_NAME</b> - defines a default jndi name of WAS thread pool</li>
 * <li><b>BIDI_THREAD_PREFIX</b> - a prefix will be given for the thread pool in
 * bi-di peer</li>
 * </ul>
 * 
 */
public enum Tag {
	NAME("Name"), GROUP_ID("GID"), DEFAULT_PEER_GROUP("NetPeerGroup"), SOURCE(
			"from"), DESTINATION("to"), PAYLOAD("payLoad"), STREAM("binary"), OBJECT(
			"object"), JNDI_NAME("wm/default"), BIDI_THREAD_PREFIX("PPC-BIDI");

	private String value;

	Tag(String tagValue) {
		this.value = tagValue;
	}

	public String getValue() {
		return value;
	}
}
