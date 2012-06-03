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

import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;

import com.google.gwt.user.client.ui.Frame;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * @author Boris Capitanu
 *
 */
public class WebUI extends Window {
    private final Frame _frame = new Frame();
    private WBWebUIInfo _webUIInfo;

    public WebUI(String title) {
        this(title, null);
    }

    public WebUI(String title, WBWebUIInfo webUIInfo) {
        _webUIInfo = webUIInfo;

        setTitle(title);
        setIconCls("icon-webui");
        setLayout(new FitLayout());
        setWidth(600);
        setHeight(600);
        setPlain(true);

        add(_frame);
    }

    public void setWebUIInfo(WBWebUIInfo webUIInfo) {
        _webUIInfo = webUIInfo;
        if (webUIInfo != null)
            _frame.setUrl(getWebUIUrl());
    }

    public WBWebUIInfo getWebUIInfo() {
        return _webUIInfo;
    }

    private String getWebUIUrl() {
        return "http://" + _webUIInfo.getHostName() + ":" + _webUIInfo.getPort();
    }
}
