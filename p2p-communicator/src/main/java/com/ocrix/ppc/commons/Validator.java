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

package com.ocrix.ppc.commons;

import java.net.URI;
import java.util.Map;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.PeerGroupAdvertisement;
import org.apache.commons.validator.routines.UrlValidator;

import com.ocrix.ppc.type.ThreadPoolType;

/**
 * Provides validators or asserts
 */
public final class Validator {

	/**
	 * Checks if a String is !null or !empty, otherwise throws PpcException
	 * Note: uses Java 1.6
	 * 
	 * @param desc
	 *            a String to be validated
	 * 
	 * @throws IllegalArgumentException
	 *             if a string is null or empty
	 */
	public static void validateString(String desc)
			throws IllegalArgumentException {
		if (desc == null || desc.isEmpty())
			throw new IllegalArgumentException(
					"Please check a provided paramenter [ " + desc + " ]");
	}

	/**
	 * Validates a list of Strings that they are neither null nor empty
	 * 
	 * @param params
	 *            String[]
	 * 
	 * @throws IllegalArgumentException
	 *             if String is null or empty
	 */
	public static void validateString(String... params)
			throws IllegalArgumentException {
		for (String param : params)
			if (param == null || param.isEmpty())
				throw new IllegalArgumentException(
						"Please check a provided paramenter [ " + param + " ]");
	}

	/**
	 * Validates an URI, if it is not valid then throws IllegalArgumentException
	 * 
	 * @param uri
	 *            to be validated
	 */
	public static void validateURI(URI uri) {
		UrlValidator urlValidator = new UrlValidator();
		if (!urlValidator.isValid(uri.toString()))
			throw new IllegalArgumentException("Please check a provided URI [ "
					+ uri + " ]");
	}

	/**
	 * Validates NetPeerGroup if it is not null, otherwise throws
	 * IllegalArgumentException
	 * 
	 * @param manager
	 */
	public static void validateNetPeerGroup(PeerGroup peerGroup) {
		if (peerGroup == null)
			throw new IllegalArgumentException(
					"NetPeerGroup is null, probably jxta is not started");
	}

	/**
	 * Validates NetworkManager if it's not null, otherwise throws
	 * IllegalArgumentException
	 * 
	 * @param manager
	 */
	public static void validateNetworkManager(NetworkManager manager) {
		if (manager == null)
			throw new IllegalArgumentException("NetworkManager is null");
	}

	/**
	 * Validates a {@link NetworkConfigurator}
	 * 
	 * @param config
	 *            a JXTA network configurator
	 */
	public static void validateNetworkConfigurator(NetworkConfigurator config) {
		if (config == null)
			throw new IllegalArgumentException("NetworkConfigurator is null");
	}

	/**
	 * Validates a {@link Map}
	 * 
	 * @param groups
	 *            - Map<String, PeerGroupAdvertisement>
	 */
	public static void validateGroupMap(
			Map<String, PeerGroupAdvertisement> groups) {
		if (groups == null || groups.size() == 0)
			throw new IllegalArgumentException("No group found");
	}

	/**
	 * Validates a {@link PeerGroup}
	 * 
	 * @param peerGroup
	 *            - a JXTA peer group
	 */
	public static void validatePeerGroup(PeerGroup peerGroup) {
		if (peerGroup == null)
			throw new IllegalArgumentException("Peer group is null");
	}

	/**
	 * Validates if an object not null
	 * 
	 * @param instance
	 *            {@link Object}
	 */
	public static void validateObjNotNull(Object instance) {
		if (instance == null)
			throw new IllegalArgumentException("The object is null");
	}

	/**
	 * Validates the {@link Integer} is positive
	 * 
	 * @param toBeValidated
	 */
	public static void validateInt(int toBeValidated) {
		if (toBeValidated < 0)
			throw new IllegalArgumentException("The number must be possitive");
	}

	/**
	 * Validates a {@link ThreadPoolType}
	 * 
	 * @param threadPoolType
	 */
	public static void validateThreadPoolType(String threadPoolType) {
		validateString(threadPoolType);
		try {
			ThreadPoolType.valueOf(threadPoolType);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
					"The thread pool type does not match");
		}
	}
}
