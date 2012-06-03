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

import org.seasr.meandre.workbench.client.beans.session.WBSession;
import org.seasr.meandre.workbench.client.listeners.MainPanelActionListener;

import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

/**
 * @author Boris Capitanu
 *
 */
public class MainPanel extends Panel {
    private final TopMenuPanel _topMenuPanel;
    private final DetailsPanel _detailsPanel;
    private final RepositoryPanel _repositoryPanel;
    private final WorkspacePanel _workspacePanel;
    private MainPanelActionListener _actionListener;

    public MainPanel(WBSession session) {
        _topMenuPanel = new TopMenuPanel(session, new MainPanelActionListener() {
            public void onLogout() {
                if (_actionListener != null)
                    _actionListener.onLogout();
            }

            public void onCredits() {
                if (_actionListener != null)
                    _actionListener.onCredits();
            }

            public void onSettings() {
                if (_actionListener != null)
                    _actionListener.onSettings();
            }
        });

        _detailsPanel = new DetailsPanel();
        _repositoryPanel = new RepositoryPanel();
        _workspacePanel = new WorkspacePanel();

        BorderLayoutData northLayoutData = new BorderLayoutData(RegionPosition.NORTH);
        northLayoutData.setMinSize(40);

        BorderLayoutData eastLayoutData = new BorderLayoutData(RegionPosition.EAST);
        eastLayoutData.setSplit(true);
        eastLayoutData.setMinSize(175);
        eastLayoutData.setMaxSize(600);
        eastLayoutData.setMargins(new Margins(0, 0, 5, 0));

        BorderLayoutData westLayoutData = new BorderLayoutData(RegionPosition.WEST);
        westLayoutData.setSplit(true);
        westLayoutData.setMinSize(175);
        westLayoutData.setMaxSize(400);
        westLayoutData.setMargins(new Margins(0, 5, 0, 0));

        BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);

        setLayout(new BorderLayout());
        add(_topMenuPanel, northLayoutData);
        add(_detailsPanel, eastLayoutData);
        add(_repositoryPanel, westLayoutData);
        add(_workspacePanel, centerLayoutData);
    }

    public TopMenuPanel getTopMenuPanel() {
        return _topMenuPanel;
    }

    public DetailsPanel getDetailsPanel() {
        return _detailsPanel;
    }

    public RepositoryPanel getRepositoryPanel() {
        return _repositoryPanel;
    }

    public WorkspacePanel getWorkspacePanel() {
        return _workspacePanel;
    }

    public void setActionListener(MainPanelActionListener listener) {
        _actionListener = listener;
    }
}
