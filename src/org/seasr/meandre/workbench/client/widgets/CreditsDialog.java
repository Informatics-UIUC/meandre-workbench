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

import com.google.gwt.user.client.ui.Frame;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * @author Boris Capitanu
 *
 */
public class CreditsDialog extends Window {

    public CreditsDialog() {
        setLayout(new FitLayout());
        setCloseAction(Window.HIDE);
        setWidth(680);
        setHeight(600);
        setMinWidth(300);
        setMinHeight(300);
        setModal(true);
        setPlain(true);
        setPaddings(5);
        setTitle("Credits");
        setIconCls("icon-credits");

        Panel licMeandre = new Panel();
        licMeandre.setTitle("Meandre");
        licMeandre.setIconCls("icon-meandre-small");
        licMeandre.setClosable(false);
        licMeandre.setLayout(new FitLayout());
        licMeandre.add(new Frame("licenses/meandre.txt"));

        Panel licGWT = new Panel();
        licGWT.setTitle("GWT");
        licGWT.setClosable(false);
        licGWT.setLayout(new FitLayout());
        licGWT.add(new Frame("licenses/Apache2.txt"));

        Panel licExt = new Panel();
        licExt.setTitle("Ext");
        licExt.setClosable(false);
        licExt.setLayout(new FitLayout());
        licExt.add(new Frame("licenses/Ext.txt"));

        Panel licGWTExt = new Panel();
        licGWTExt.setTitle("GWT-Ext");
        licGWTExt.setClosable(false);
        licGWTExt.setLayout(new FitLayout());
        licGWTExt.add(new Frame("licenses/LGPLv3.txt"));

        Panel licGWTDiagrams = new Panel();
        licGWTDiagrams.setTitle("GWT-Diagrams");
        licGWTDiagrams.setClosable(false);
        licGWTDiagrams.setLayout(new FitLayout());
        licGWTDiagrams.add(new Frame("licenses/Apache2.txt"));

        Panel licJENA = new Panel();
        licJENA.setTitle("JENA");
        licJENA.setClosable(false);
        licJENA.setLayout(new FitLayout());
        licJENA.add(new Frame("licenses/JENA.txt"));

        Panel licJetty = new Panel();
        licJetty.setTitle("Jetty");
        licJetty.setClosable(false);
        licJetty.setLayout(new FitLayout());
        licJetty.add(new Frame("licenses/Apache2.txt"));

        Panel licDerby = new Panel();
        licDerby.setTitle("Apache Derby");
        licDerby.setClosable(false);
        licDerby.setLayout(new FitLayout());
        licDerby.add(new Frame("licenses/Apache2.txt"));

        Panel licCommons = new Panel();
        licCommons.setTitle("Apache Commons");
        licCommons.setClosable(false);
        licCommons.setLayout(new FitLayout());
        licCommons.add(new Frame("licenses/Apache2.txt"));

        TabPanel tabPanel = new TabPanel();
        tabPanel.setEnableTabScroll(true);
        tabPanel.setActiveTab(0);
        tabPanel.add(licMeandre);
        tabPanel.add(licGWT);
        tabPanel.add(licExt);
        tabPanel.add(licGWTExt);
        tabPanel.add(licGWTDiagrams);
        tabPanel.add(licJENA);
        tabPanel.add(licJetty);
        tabPanel.add(licDerby);
        tabPanel.add(licCommons);

        add(tabPanel);

    }

}
