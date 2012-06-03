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

import java.util.Date;

import org.seasr.meandre.workbench.client.beans.session.WBSession;
import org.seasr.meandre.workbench.client.listeners.MainPanelActionListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.layout.FitLayout;


/**
 * @author Boris Capitanu
 *
 */
public class TopMenuPanel extends ContainerPanel {

    public TopMenuPanel(final WBSession session, final MainPanelActionListener actionListener) {
        setHeight(40);
        setLayout(new FitLayout());

        HorizontalPanel topContainer = new HorizontalPanel() {
            {
                setStyleName("top-header");
                setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);

                HorizontalPanel hpMenu = new HorizontalPanel();
                hpMenu.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
                hpMenu.setStyleName("top-header-menu");

                Label lblSettings = new Label("settings");
                lblSettings.setStyleName("top-header-menu-items");
                lblSettings.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
                        actionListener.onSettings();
					}
				});

                Label lblCredits = new Label("credits");
                lblCredits.setStyleName("top-header-menu-items");
                lblCredits.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
                        actionListener.onCredits();
					}
				});

                Label lblSeparator1 = new Label("|");
                lblSeparator1.setStyleName("top-header-menu-separator");
                Label lblSeparator2 = new Label("|");
                lblSeparator2.setStyleName("top-header-menu-separator");
                Label lblSeparator3 = new Label("|");
                lblSeparator3.setStyleName("top-header-menu-separator");

                Label lblLogout = new Label("logout");
                lblLogout.setStyleName("top-header-menu-items");
                lblLogout.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
                        actionListener.onLogout();
					}
				});

                Label lblLoginInfo = new Label("logged in as " + session.getUserName());
                lblLoginInfo.setStyleName("top-header-menu-items");
                lblLoginInfo.addStyleName("top-header-menu-loginInfo");

                ToolTip tipServer = new ToolTip();
                tipServer.setHtml("Meandre server " + session.getServerVersion() + " running on " + session.getHostName() + " port " + session.getPort());
                tipServer.applyTo(lblLoginInfo.getElement());

                hpMenu.add(lblCredits);
                hpMenu.add(lblSeparator1);
                hpMenu.add(lblSettings);
                hpMenu.add(lblSeparator2);
                hpMenu.add(lblLogout);
                hpMenu.add(lblSeparator3);
                hpMenu.add(lblLoginInfo);

                ContainerPanel imgContainer = new ContainerPanel();
                Image imgLogo = new Image("images/meandre_logo.png");
                //imgLogo.setStyleName("top-header-image-logo");
                imgContainer.add(imgLogo);

                String wbVersion = session.getWBVersion().getFullVersion();
                Date wbBuildDate = session.getWBVersion().getBuildDate();

                if (wbVersion != null) {
                    ToolTip tipVersion = new ToolTip();
                    tipVersion.setHtml("Meandre Workbench version " + wbVersion);
                    if (wbBuildDate != null) {
                        String buildDate = DateTimeFormat.getFormat("MMM dd, yyyy h:mm:ssa z").format(wbBuildDate);
                        tipVersion.setHtml(tipVersion.getHtml() + "<br/>Built on " + buildDate);
                    }
                    tipVersion.applyTo(imgLogo.getElement());
                }

                this.add(imgLogo);
                this.add(hpMenu);

                setCellHorizontalAlignment(hpMenu, HorizontalPanel.ALIGN_RIGHT);
            }
        };

        add(topContainer);
    }
}
