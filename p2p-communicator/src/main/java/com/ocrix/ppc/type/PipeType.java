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
 * Defines the type of pipes. <br>
 * <b>UNICAST</b> Means one directional pipe <br>
 * <b>UNICAST_SECURE</b> Means one directional secured pipe <br>
 * <b>PROPAGATE</b> Means multicasting <br>
 * <b>BIDI_PIPE</b> Means bi-directional pipe <br>
 * <b>SERVER_PIPE</b> Means a server pipe, usually is created on the server side
 * for incoming connections. By default accepts 50 connections.
 */
public enum PipeType {
	UNICAST, UNICAST_SECURE, PROPAGATE, BIDI_PIPE, SERVER_PIPE
}
