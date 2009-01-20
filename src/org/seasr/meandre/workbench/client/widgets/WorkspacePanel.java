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

import org.seasr.meandre.workbench.client.listeners.WorkspacePanelActionListener;

import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.ExtElement;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;


/**
 * @author Boris Capitanu
 *
 */
public class WorkspacePanel extends Panel {
    private final TabPanel _tabPanel = new TabPanel();
    private WorkspacePanelActionListener _actionListener = null;
    private final OutputPanel _outputPanel = new OutputPanel();


    public WorkspacePanel() {
        BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);

        BorderLayoutData southLayoutData = new BorderLayoutData(RegionPosition.SOUTH);
        southLayoutData.setMinSize(100);
        //southLayoutData.setMaxSize(600); // TODO set based on window size
        southLayoutData.setMargins(new Margins(0, 0, 0, 0));
        southLayoutData.setSplit(true);

        _tabPanel.setEnableTabScroll(true);
        _tabPanel.setDeferredRender(false);
        _tabPanel.setActiveTab(1);
        _tabPanel.addListener(new TabPanelListenerAdapter() {
            @Override
            public void onContextMenu(TabPanel tabPanel, Panel tab, EventObject e) {
                if (_actionListener == null || tab instanceof NewTabButton) return;

                e.stopEvent();
                WorkspaceTab wsTab = (WorkspaceTab) tab;
                _actionListener.getTabContextMenu(WorkspacePanel.this, wsTab).showAt(e.getXY());
            }

            @Override
            public boolean doBeforeRemove(Container tabPanel, Component component) {
                WorkspaceTab wsTab = (WorkspaceTab) component;

                return wsTab.shouldClose();
            }

            @Override
            public void onRemove(Container self, Component component) {
                if (_actionListener == null) return;

                WorkspaceTab wsTab = (WorkspaceTab) component;
                _actionListener.onTabClosed(WorkspacePanel.this, wsTab);
            }

            @Override
            public boolean doBeforeTabChange(TabPanel source, Panel newPanel, Panel oldPanel) {
                if (newPanel instanceof NewTabButton) return false;

                return _actionListener.doBeforeTabChange((WorkspaceTab) oldPanel, (WorkspaceTab)newPanel);
            }

            @Override
            public void onTabChange(TabPanel source, Panel tab) {
                _actionListener.onTabChanged((WorkspaceTab) tab);
            }
        });

        final NewTabButton newTabButton = new NewTabButton();
        _tabPanel.add(newTabButton);
        _tabPanel.doOnRender(new Function() {
            public void execute() {
                ExtElement tabEl = Ext.get(_tabPanel.getTabEl(newTabButton));
                tabEl.addListener("click", new EventCallback() {
                    public void execute(EventObject e) {
                        _actionListener.onNewTab(WorkspacePanel.this);
                    }
                });
            }
        });

        setLayout(new BorderLayout());
        add(_outputPanel, southLayoutData);
        add(_tabPanel, centerLayoutData);
    }

    public OutputPanel getOutputPanel() {
        return _outputPanel;
    }

    public void addTab(WorkspaceTab tab) {
        tab.setParent(this);
        _tabPanel.add(tab);
    }

    public void removeTab(WorkspaceTab tab) {
        _tabPanel.remove(tab);
    }

    public WorkspaceTab getActiveTab() {
        return (WorkspaceTab) _tabPanel.getActiveTab();
    }

    public void setActiveTab(WorkspaceTab tab) {
        _tabPanel.setActiveTab(tab.getId());
    }

    public WorkspaceTab[] getTabs() {
        Component[] tabItems = _tabPanel.getItems();
        WorkspaceTab[] wsTabs = new WorkspaceTab[tabItems.length-1];  // -1 to account for the NewTab fake tab

        for (int i = 1; i < tabItems.length; i++)
            wsTabs[i-1] = (WorkspaceTab) tabItems[i];

        return wsTabs;
    }

    public void setActionListener(WorkspacePanelActionListener listener) {
        _actionListener = listener;
    }


    private class NewTabButton extends Panel {
        public NewTabButton() {
            setTitle("&nbsp;");
            setIconCls("icon-tab-new");
            setClosable(false);
            addClass("x-unselectable");
        }
    }
}
