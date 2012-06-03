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

import org.seasr.meandre.workbench.client.listeners.LoginListener;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.ValidationException;
import com.gwtext.client.widgets.form.Validator;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;

/**
 * @author Boris Capitanu
 *
 */
public class LoginDialog extends Composite {

    private final VerticalPanel _vpContainer = new VerticalPanel();
    private final Image _imgWait = new Image("images/wait.gif");
    private final FormPanel _loginPanel = new FormPanel();

    public LoginDialog(final LoginListener loginListener) {
        _vpContainer.setWidth("100%");
        _vpContainer.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
        _vpContainer.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
        _vpContainer.setSpacing(8);

        final TextField txtUsername = new TextField("Username", "username");
        //txtUsername.setValue("admin");
        txtUsername.setAllowBlank(false);

        final TextField txtPassword = new TextField("Password", "password");
        //txtPassword.setValue("admin");
        txtPassword.setPassword(true);

        final TextField txtHostName = new TextField("Server", "server");
        txtHostName.setAllowBlank(false);

        final TextField txtPort = new TextField("Port", "port");
        txtPort.setValue("1714");
        txtPort.setAllowBlank(false);
        txtPort.setInvalidText("Invalid port number");
        txtPort.setValidator(new Validator() {
            public boolean validate(String value) throws ValidationException {
                try {
                    int port = Integer.parseInt(value);
                    return (port > 0 && port < 65535);
                }
                catch (NumberFormatException e) {
                    return false;
                }
            }
        });

        final Button btnLogin = new Button("Login", new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                String userName = txtUsername.getText();
                String password = txtPassword.getText();
                String hostName = txtHostName.getText();
                int port = Integer.parseInt(txtPort.getText());

                loginListener.onLogin(userName, password, hostName, port);
            }
        });

        EventCallback enterKeyListener = new EventCallback() {
            public void execute(EventObject e) {
                if (e.getKey() == EventObject.ENTER) {
                    if (!btnLogin.isDisabled())
                        btnLogin.fireEvent("click");
                    else {
                        txtHostName.validate();
                        txtPassword.validate();
                        txtHostName.validate();
                        txtPort.validate();
                    }
                }
            }
        };

        txtUsername.addKeyPressListener(enterKeyListener);
        txtPassword.addKeyPressListener(enterKeyListener);
        txtHostName.addKeyPressListener(enterKeyListener);
        txtPort.addKeyPressListener(enterKeyListener);

        _loginPanel.setTitle("Meandre Workbench Login");
        _loginPanel.setIconCls("icon-meandre-small");
        _loginPanel.setFrame(true);
        _loginPanel.setWidth(300);
        _loginPanel.setLabelAlign(Position.RIGHT);
        _loginPanel.setMaskDisabled(false);
        _loginPanel.setMonitorValid(true);
        _loginPanel.addListener(new FormPanelListenerAdapter() {
            @Override
            public void onClientValidation(FormPanel formPanel, boolean valid) {
                btnLogin.setDisabled(!valid);
            }
        });
        _loginPanel.add(txtUsername);
        _loginPanel.add(txtPassword);
        _loginPanel.add(txtHostName);
        _loginPanel.add(txtPort);
        _loginPanel.setButtons(new Button[] { btnLogin });

        _loginPanel.doOnRender(new Function() {
            public void execute() {
                txtUsername.focus();
            }
        });

        _vpContainer.add(_loginPanel);
        initWidget(_vpContainer);
    }

    public void disableLogin() {
        _loginPanel.disable();
        _vpContainer.add(_imgWait);
    }

    public void enableLogin() {
        _loginPanel.enable();
        _vpContainer.remove(_imgWait);
    }
}
