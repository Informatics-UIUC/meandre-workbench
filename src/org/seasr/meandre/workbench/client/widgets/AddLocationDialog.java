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

import org.seasr.meandre.workbench.client.listeners.AddLocationListener;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * @author Boris Capitanu
 *
 */
public class AddLocationDialog extends Window {
    private final TextArea _fDescription;
    private final TextField _fURL;

    public AddLocationDialog(final AddLocationListener listener) {
        _fURL = new TextField("Location URL");
        _fURL.setAllowBlank(false);
        _fURL.setBlankText("The URL field cannot be blank");
        _fURL.setRegexText("The specified URL is invalid");
        _fURL.setRegex("^http(s?)://\\w+([_-]\\w+)*(\\.\\w+([_-]\\w+)*)*(:[\\d]{1,5})?((/\\w*\\.?\\w+([_-]\\w+)*)*)*$");

        _fDescription = new TextArea("Description");
        _fDescription.setAllowBlank(false);
        _fDescription.setBlankText("The description cannot be blank");

        final Button btnAdd = new Button("Add Location");
        btnAdd.setIconCls("icon-location-add");
        btnAdd.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                String description = _fDescription.getText();
                String url = _fURL.getText();

                AddLocationDialog.this.close();

                listener.onAdd(description, url);
            }
        });

        final Button btnCancel = new Button("Cancel");
        btnCancel.setIconCls("icon-cancel");
        btnCancel.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                AddLocationDialog.this.close();
            }
        });

        FormPanel addForm = new FormPanel();
        addForm.setBaseCls("x-plain");
        addForm.setLabelWidth(90);
        addForm.setWidth(500);
        addForm.setHeight(200);
        addForm.setLabelAlign(Position.RIGHT);
        addForm.setMonitorValid(true);
        addForm.addListener(new FormPanelListenerAdapter() {
            @Override
            public void onClientValidation(FormPanel formPanel, boolean valid) {
                btnAdd.setDisabled(!valid);
            }
        });
        addForm.doOnRender(new Function() {
            public void execute() {
                _fURL.focus();
            }
        }, 1000);

        addForm.add(_fURL, new AnchorLayoutData("-20"));
        addForm.add(_fDescription, new AnchorLayoutData("-20 -26"));

        setTitle("Add Location");
        setIconCls("icon-locations");
        setWidth(500);
        setHeight(200);
        setMinWidth(400);
        setMinHeight(120);
        setLayout(new FitLayout());
        setPaddings(5);
        setPlain(true);
        setButtonAlign(Position.CENTER);
        setButtons(new Button[] { btnAdd, btnCancel });
        setModal(true);

        add(addForm);
    }

}
