/**
 * University of Illinois/NCSA
 * Open Source License
 *
 * Copyright (c) 2008, Board of Trustees-University of Illinois.
 * All rights reserved.
 *
 * Developed by:
 *
 * Automated Learning Group
 * National Center for Supercomputing Applications
 * http://www.seasr.org
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal with the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimers.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimers in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the names of Automated Learning Group, The National Center for
 *    Supercomputing Applications, or University of Illinois, nor the names of
 *    its contributors may be used to endorse or promote products derived from
 *    this Software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * WITH THE SOFTWARE.
 */

package org.seasr.meandre.workbench.client;

import org.seasr.meandre.workbench.client.widgets.ErrorWindow;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.MessageBoxConfig;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 *
 * @author Boris Capitanu
 */
public abstract class Application implements EntryPoint {

    /* (non-Javadoc)
     * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
     */
    @Override
	public void onModuleLoad() {
        // Register an UncaughtExceptionHandler
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
			public void onUncaughtException(final Throwable throwable) {
                showError("Uncaught exception", throwable);
            }
        });

        // Use a deferred command so that the handler catches onLoad() exceptions
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				onLoad();
			}
		});
    }

    /**
     * The functional entry point of the application
     * (to be overridden by main application)
     */
    protected abstract void onLoad();

    public static void showError(String title, String message) {
        showError(title, message, null, null);
    }

    public static void showError(String title, Throwable throwable) {
        showError(title, null, throwable, null);
    }

    public static void showError(final String title, final String message, final Throwable throwable) {
        showError(title, message, throwable, null);
    }

    /**
     * Helper method that shows an error window describing an exception
     *
     * @param title The window title
     * @param message The message to be displayed
     * @param throwable The exception that caused the error
     * @param callback A callback that indicates when the window was dismissed
     */
    public static void showError(final String title, final String message, final Throwable throwable, final MessageBox.ConfirmCallback callback) {
        new ErrorWindow(title, message, throwable, callback).show();
    }

    public static void showMessage(String title, String message, String iconCls) {
        showMessage(title, message, iconCls, null, null);
    }

    public static void showMessage(String title, String message, String iconCls, MessageBox.Button buttons) {
        showMessage(title, message, iconCls, buttons, null);
    }

    /**
     * Helper method that displays a message in a window, and optionally prompts for an action
     *
     * @param title The title of the window
     * @param message The message to be displayed
     * @param iconCls An optional icon class
     * @param buttons Optional buttons to be displayed
     * @param callback A callback that indicates when the window was dismissed
     */
    public static void showMessage(final String title, final String message,
            final String iconCls, final MessageBox.Button buttons, final PromptCallback callback) {

        if (message == null) throw new IllegalArgumentException("message cannot be null");

        MessageBox.hide(); // just in case
        MessageBox.show(new MessageBoxConfig() {
            {
                if (title != null)    setTitle(title);
                if (iconCls != null)  setIconCls(iconCls);
                if (buttons != null)
                    setButtons(buttons);
                else
                    setButtons(MessageBox.OK);
                if (callback != null) setCallback(callback);
                setMsg(message);
            }
        });

    }
}
