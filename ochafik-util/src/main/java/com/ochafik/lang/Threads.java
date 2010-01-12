/*
	Copyright (c) 2009 Olivier Chafik, All Rights Reserved
	
	This file is part of JNAerator (http://jnaerator.googlecode.com/).
	
	JNAerator is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	JNAerator is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with JNAerator.  If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * 
 */
package com.ochafik.lang;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

/**
 * <p>
 * Group of coordinated Runnable instances that can be started, interrupted and waited for together.
 * </p><p>
 * Once you added as many runnable tasks as needed through the add(Runnable) method,  
 * there are two ways of waiting for the tasks to finish :
 * <ul>
 * <li>call join() in some thread. This will implicitely start the threads if start() was not called yet, and the join() method will not return until all the thread finished their execution
 * </li><li>call start() and register some ActionListener instances. Whenever all threads finished their execution, the actionPerformed method of all the listeners will be called.
 * </li>
 * @author Olivier Chafik
 */
public final class Threads {
	private final List<Runner> runners = new ArrayList<Runner>();
	private final Semaphore semaphore = new Semaphore(0);
	private boolean fired = false, started = false;

	private List<ActionListener> actionListeners; 

	private class Runner extends Thread {
		private final Runnable runnable;
		public Runner(Runnable runnable) {
			this.runnable = runnable;
		}
		public void run() {
			try {
				runnable.run();
			} finally {
				int nThreads = runners.size();
				if (semaphore.tryAcquire(nThreads - 1)) {
					semaphore.release(nThreads);
					synchronized (this) {
						if (!fired) {
							fired = true;
							fireActionPerformed();
						}
					}
				} else {
					semaphore.release();
				}
			}
		}
	}
	
	/**
	 * Add a task that is to be executed in its own thread.
	 * @param runnable task to be executed in its own thread
	 * @return the runnable argument unchanged
	 */
	public synchronized <T extends Runnable> T add(T runnable) {
		if (started)
			throw new IllegalThreadStateException("Cannot add another runnable to " + getClass().getSimpleName() + " after it started !");
		
		runners.add(new Runner(runnable));
		return runnable;
	}
	
	/**
	 * Starts all the threads.
	 * @throws IllegalThreadStateException if the threads were already started.
	 * @throws NoSuchElementException if no runnable were added to this Threads instance.
	 */
	public synchronized void start() {
		if (started)
			throw new IllegalThreadStateException(getClass().getSimpleName() + " already started !");
		
		if (runners.isEmpty())
			throw new NoSuchElementException("No runnable were added to this " + getClass().getSimpleName());
		
		for (Runner t : runners) {
			t.start();
		}
		started = true;
	}
	
	/**
	 * Calls interrupt() on each of the running threads.
	 * @throws IllegalThreadStateException if threads were not started 
	 */
	public synchronized void interrupt() {
		if (!started)
			throw new IllegalThreadStateException(getClass().getSimpleName() + " not started !");
		
		for (Runner t : runners) {
			try {
				t.interrupt();
			} catch (IllegalThreadStateException ex) {
				// t might have finished its execution
				ex.printStackTrace();
			}
		} 
	}
	
	/**
	 * Waits for all runnable to have finished their execution.
	 * Can be called multiple times : after the first time, this method always returns immediately.
	 * If the Threads is not started yet, this method will start it implicitely.
	 * @throws InterruptedException if method interrupt() was called on the thread that is calling this method.
	 */
	public synchronized void join() throws InterruptedException {
		int nThreads = runners.size();
		if (nThreads == 0)
			return;
		
		if (!started)
			start();
		
		semaphore.acquire(nThreads);
		semaphore.release(nThreads);
	}
	
	public enum State {
		NotStarted, Running, Finished, NoRunnables
	}
	
	public synchronized State getState() {
		int nThreads = runners.size();
		if (nThreads == 0)
			return State.NoRunnables;
		
		if (!started)
			return State.NotStarted;
		
		if (semaphore.tryAcquire(nThreads)) {
			semaphore.release(nThreads);
			return State.Finished;
		}
		return State.Running;
	}
	
	/**
	 * Adds a listener that will be notified upon completion of all of the running threads.
	 * Its actionPerformed method will be called immediately if the threads already finished.
	 * @param actionListener
	 */
	public synchronized void addActionListener(ActionListener actionListener) {
		if (actionListeners == null)
			actionListeners = new ArrayList<ActionListener>();
		
		actionListeners.add(actionListener);
		
		if (fired) {
			actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
		}
	}
	
	private synchronized void fireActionPerformed() {
		if (actionListeners == null) 
			return;
		
		ActionEvent a = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "");
		for (ActionListener l : actionListeners)
			l.actionPerformed(a);
	}
	
}