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

import org.seasr.meandre.workbench.client.listeners.SaveFlowListener;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.TextArea;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * @author Boris Capitanu
 *
 */
public class SaveFlowDialog extends Window {
    private final TextField _fName;
    private final HtmlEditor _fDescription;
    private final TextArea _fRights;
    private final TextField _fBaseURL;
    private final TextField _fTags;

    public SaveFlowDialog(final SaveFlowListener listener) {
        _fName = new TextField("Name");
        _fName.setAllowBlank(false);
        _fName.setBlankText("The flow name cannot be blank");

        _fDescription = new HtmlEditor("Description");
        _fDescription.setEnableSourceEdit(false);
        _fDescription.setEnableLinks(false);

        _fRights = new TextArea("Rights");

        _fBaseURL = new TextField("Base URL");
        _fBaseURL.setAllowBlank(false);
        _fBaseURL.setBlankText("The URL field cannot be blank");
        _fBaseURL.setRegexText("The specified URL is invalid");
        _fBaseURL.setRegex("^\\w+([_-]\\w+)*://(\\w+([_-]\\w+)*(\\.\\w+([_-]\\w+)*)*/?)*$");

        _fTags = new TextField("Tags");

        final Button btnSave = new Button("Save");
        btnSave.setIconCls("icon-flow-save");
        btnSave.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                String name = _fName.getText();
                String description = _fDescription.getValueAsString();
                String rights = _fRights.getValueAsString();
                String baseURL = _fBaseURL.getText();
                if (!baseURL.endsWith("/"))
                    baseURL += "/";
                String tags = _fTags.getText();

                SaveFlowDialog.this.close();

                listener.onSave(name, description, rights, baseURL, tags);
            }
        });

        final Button btnCancel = new Button("Cancel");
        btnCancel.setIconCls("icon-cancel");
        btnCancel.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                SaveFlowDialog.this.close();
            }
        });

        FormPanel saveForm = new FormPanel();
        saveForm.setBaseCls("x-plain");
        saveForm.setLabelWidth(80);
        saveForm.setWidth(580);
        saveForm.setHeight(560);
        saveForm.setLabelAlign(Position.RIGHT);
        saveForm.setMonitorValid(true);
        saveForm.addListener(new FormPanelListenerAdapter() {
            @Override
            public void onClientValidation(FormPanel formPanel, boolean valid) {
                btnSave.setDisabled(!valid);
            }
        });
        saveForm.doOnRender(new Function() {
            public void execute() {
                _fName.focus();
            }
        }, 1000);

        saveForm.add(_fName, new AnchorLayoutData("-20"));
        saveForm.add(_fDescription, new AnchorLayoutData("-20"));
        saveForm.add(_fTags, new AnchorLayoutData("-20"));
        saveForm.add(_fRights, new AnchorLayoutData("-20 -386"));
        saveForm.add(_fBaseURL, new AnchorLayoutData("-20"));

        setTitle("Save Flow");
        setIconCls("icon-flows");
        setWidth(580);
        setHeight(560);
        setMinWidth(620);
        setMinHeight(560);
        setLayout(new FitLayout());
        setPaddings(5);
        setPlain(true);
        setButtonAlign(Position.CENTER);
        setButtons(new Button[] { btnSave, btnCancel });
        setModal(true);

        add(saveForm);
    }

    public void setFormValues(String name, String description, String rights, String url, String tags) {
        if (name != null)         _fName.setValue(name);
        if (description != null)  _fDescription.setValue(description);
        if (rights != null)       _fRights.setValue(rights);
        if (url != null)          _fBaseURL.setValue(url);
        if (tags != null)         _fTags.setValue(tags);
    }

}
