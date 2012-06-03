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

import org.seasr.meandre.workbench.client.exceptions.WBException;
import org.seasr.meandre.workbench.shared.Utils;

import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.Label;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.HorizontalLayout;
import com.gwtext.client.widgets.layout.VerticalLayout;

/**
 * @author Boris Capitanu
 *
 */
public class ErrorWindow extends Window {

    public ErrorWindow(Throwable throwable) {
        this("Error", throwable);
    }

    public ErrorWindow(String title, Throwable throwable) {
        this(title, null, throwable);
    }

    public ErrorWindow(String title, String message, Throwable throwable) {
        this(title, message, throwable, null);
    }

    public ErrorWindow(String title, String message, Throwable throwable, final MessageBox.ConfirmCallback callback) {
        setTitle(title);
        setModal(true);
        setShadow(false);
        setLayout(new FitLayout());
        setButtonAlign(Position.CENTER);
        setPaddings(5);
        setWidth(550);
        setMinWidth(300);
        setAutoHeight(true);

        Panel hPanel = new Panel();
        hPanel.setBaseCls("x-plain");
        hPanel.setAutoWidth(true);
        hPanel.setAutoHeight(true);
        hPanel.setLayout(new HorizontalLayout(10));
        hPanel.add(new Image("images/error32.png"));

        String errMsg = (message != null) ? message : throwable.getMessage();
        Label lblMessage = new Label(errMsg);
        lblMessage.setCls("msg-error");
        hPanel.add(lblMessage);

        String details = Utils.formatException(throwable);

        if (throwable instanceof WBException) {
            String serverTrace = ((WBException)throwable).getServerTrace();
            if (serverTrace != null)
                details = serverTrace;
        }

        TextArea txtDetails = new TextArea();
        txtDetails.setWidth("100%");
        txtDetails.setHeight(200);
        txtDetails.setHideLabel(true);
        txtDetails.setReadOnly(true);
        txtDetails.setValue(details);

        FieldSet fsDetails = new FieldSet("Details");
        fsDetails.setAutoWidth(true);
        fsDetails.setAutoHeight(true);
        fsDetails.setCollapsible(true);
        fsDetails.setCollapsed(true);
        fsDetails.add(txtDetails);

        Panel vPanel = new Panel();
        vPanel.setBaseCls("x-plain");
        vPanel.setAutoWidth(true);
        vPanel.setAutoHeight(true);
        vPanel.setLayout(new VerticalLayout(15));
        vPanel.add(hPanel);
        vPanel.add(fsDetails);

        FormPanel formPanel = new FormPanel();
        formPanel.setBaseCls("x-plain");
        formPanel.setHideLabels(true);
        formPanel.setAutoWidth(true);
        formPanel.setAutoHeight(true);
        formPanel.add(vPanel);

        add(formPanel);

        this.addListener(new WindowListenerAdapter() {
            @Override
            public void onClose(Panel panel) {
                if (callback != null)
                    callback.execute("OK");
            }
        });

        addButton(new Button("OK", new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                close();
            }
        }));
    }
}
