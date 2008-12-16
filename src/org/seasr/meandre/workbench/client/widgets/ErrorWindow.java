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

package org.seasr.meandre.workbench.client.widgets;

import org.seasr.meandre.workbench.client.Application;
import org.seasr.meandre.workbench.client.exceptions.WBException;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * @author Boris Capitanu
 *
 */
public class ErrorWindow extends Window {

    public ErrorWindow(Throwable throwable) {
        this("Error", null, throwable);
    }

    public ErrorWindow(String title, Throwable throwable) {
        this(title, null, throwable);
    }

    public ErrorWindow(String title, String message, Throwable throwable) {
        setTitle(title);
        setModal(true);
        setWidth(500);
        setAutoHeight(true);

        Panel hPanel = new Panel();
        hPanel.setLayout(new HorizontalLayout(10));
        hPanel.add(new Image("images/error32.png"));

        String errMsg = (message != null) ? message : throwable.getMessage();
        Label lblMessage = new Label(errMsg, true);
        hPanel.add(lblMessage);

        String details = Application.formatException(throwable);

        if (throwable instanceof WBException) {
            String serverTrace = ((WBException)throwable).getServerTrace();
            if (serverTrace != null)
                details = serverTrace;
        }

        TextArea txtDetails = new TextArea();
        txtDetails.setAutoWidth(true);
        txtDetails.setHeight(400);
        txtDetails.setReadOnly(true);
        txtDetails.setValue(details);

        FieldSet fsDetails = new FieldSet("Details");
        fsDetails.setCollapsible(true);
        fsDetails.setAutoHeight(true);
        fsDetails.add(txtDetails);

        Panel vPanel = new Panel();
        vPanel.setLayout(new VerticalLayout(15));
        vPanel.add(hPanel);
        vPanel.add(fsDetails);

        add(vPanel);
    }
}
