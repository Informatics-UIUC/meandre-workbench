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

package org.seasr.meandre.workbench.client.callbacks;

import org.seasr.meandre.workbench.client.Application;
import org.seasr.meandre.workbench.client.Workbench;
import org.seasr.meandre.workbench.client.exceptions.MeandreCommunicationException;
import org.seasr.meandre.workbench.client.exceptions.SessionExpiredException;
import org.seasr.meandre.workbench.client.exceptions.WBException;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author Boris Capitanu
 * @param <T>
 *
 */
public abstract class WBCallback<T> implements AsyncCallback<T> {

    public abstract void onSuccess(T result);

    public void onFailure(Throwable caught) {
        if (!(caught instanceof WBException))
            Application.showError("Callback failure", caught);

        if (caught instanceof MeandreCommunicationException)
            onMeandreCommunicationProblem(caught);

        else

        if (caught instanceof SessionExpiredException)
            onSessionExpired();
    }

    public void onMeandreCommunicationProblem(Throwable caught) {
        Application.showError("Meandre communication problem", caught);
    }

    public void onSessionExpired() {
        final Label lblSessionExpired = new Label("Your session has expired - please log in again.");
        lblSessionExpired.setWidth("100%");
        lblSessionExpired.setHorizontalAlignment(Label.ALIGN_CENTER);
        lblSessionExpired.addStyleName("msg-sessionExpired");

        Workbench.clear();
        RootPanel.get().clear();
        RootPanel.get().add(lblSessionExpired);
        Workbench.showLogin();
    }
}
