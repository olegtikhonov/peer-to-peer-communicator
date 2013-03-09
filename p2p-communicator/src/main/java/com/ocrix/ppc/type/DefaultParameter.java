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

public enum DefaultParameter {
	WAITING_TIME(500), THRESHOLD(10), TWO_SEC(2000), TRIAL_NUMBER(3), THREAD_POOL_SIZE(
			2), QUEUE_CAPASITY(50), WAIT_TIME_PIPE_CREATION(30000), DEFAULT_BUFFER_SIZE(
			1024 * 4);

	private int code;

	private DefaultParameter(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
}
