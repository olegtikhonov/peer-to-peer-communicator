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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ocrix.ppc.type.DefaultParameter;

/**
 * Represents an implementation of {@link TaskManager}
 */
public class StandardFixedThreadPool implements TaskManager {
	/* Class member */
	private ExecutorService pool = null;
	private static final Logger logger = Logger
			.getLogger(StandardFixedThreadPool.class);

	/* ----- end of class member declaration */

	/**
	 * Creates an instance of {@link StandardFixedThreadPool}
	 */
	public StandardFixedThreadPool() {
		setPool(Executors.newFixedThreadPool(DefaultParameter.THREAD_POOL_SIZE
				.getCode()));
	}

	// @Override
	public void execute(Runnable jobToDo) {
		getPool().execute(jobToDo);
	}

	// @Override
	public void shutdown() {

		boolean isTimeout = false;

		getPool().shutdown();

		try {
			isTimeout = getPool().awaitTermination(
					DefaultParameter.WAITING_TIME.getCode(),
					TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.error(StandardFixedThreadPool.class.getName() + " "
					+ e.getMessage());
		}

		if (getPool().isTerminated())
			logger.debug("Pool is terminated, " + isTimeout);

		if (!isTimeout) {
			int countDown = DefaultParameter.THREAD_POOL_SIZE.getCode();
			while (getPool().isTerminated() && countDown-- > 0) {
				sleep(DefaultParameter.TWO_SEC.getCode());
				logger.debug(">");
			}
		}
	}

	// @Override
	public void init() {
		logger.debug("Is not implemented");
	}

	/**
	 * Sets up a thread pool
	 * 
	 * @param pool
	 */
	private void setPool(ExecutorService pool) {
		this.pool = pool;
	}

	/**
	 * Attains a thread pool
	 * 
	 * @return a {@link ExecutorService}
	 */
	private ExecutorService getPool() {
		return pool;
	}

	/**
	 * Thread.sleep
	 * 
	 * @param timeToWait
	 *            - a time to wait
	 */
	private void sleep(long timeToWait) {
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			// ignore
		}
	}
}
