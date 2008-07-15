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

package org.seasr.meandre.workbench.client;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBLocation;
import org.seasr.meandre.workbench.client.beans.session.WBSession;
import org.seasr.meandre.workbench.client.callbacks.WBCallback;
import org.seasr.meandre.workbench.client.exceptions.CorruptedFlowException;
import org.seasr.meandre.workbench.client.exceptions.LoginFailedException;
import org.seasr.meandre.workbench.client.listeners.AddLocationListener;
import org.seasr.meandre.workbench.client.listeners.ComponentsGridActionListenerAdapter;
import org.seasr.meandre.workbench.client.listeners.FlowsGridActionListenerAdapter;
import org.seasr.meandre.workbench.client.listeners.LocationsGridActionListener;
import org.seasr.meandre.workbench.client.listeners.LoginListener;
import org.seasr.meandre.workbench.client.listeners.MainPanelActionListener;
import org.seasr.meandre.workbench.client.listeners.RefreshListener;
import org.seasr.meandre.workbench.client.listeners.WorkspaceActionListenerAdapter;
import org.seasr.meandre.workbench.client.listeners.WorkspacePanelActionListenerAdapter;
import org.seasr.meandre.workbench.client.rpc.IRepository;
import org.seasr.meandre.workbench.client.rpc.IRepositoryAsync;
import org.seasr.meandre.workbench.client.widgets.AddLocationDialog;
import org.seasr.meandre.workbench.client.widgets.Component;
import org.seasr.meandre.workbench.client.widgets.CreditsDialog;
import org.seasr.meandre.workbench.client.widgets.LoginDialog;
import org.seasr.meandre.workbench.client.widgets.MainPanel;
import org.seasr.meandre.workbench.client.widgets.RepositoryPanel;
import org.seasr.meandre.workbench.client.widgets.WorkspacePanel;
import org.seasr.meandre.workbench.client.widgets.WorkspaceTab;
import org.seasr.meandre.workbench.client.widgets.RepositoryPanel.ComponentsGrid;
import org.seasr.meandre.workbench.client.widgets.RepositoryPanel.FlowsGrid;
import org.seasr.meandre.workbench.client.widgets.RepositoryPanel.LocationsGrid;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowCloseListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.MessageBox.ConfirmCallback;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

/**
 *
 * @author Boris Capitanu
 *
 */
public class Workbench extends Application {

    private final static int WEBUI_TIMEOUT = 30000;  // 30 second timeout waiting to retrieve WebUI info

    private static Workbench _workbench;
    private static final IRepositoryAsync Repository = IRepository.Util.getInstance();

    private final RepositoryState _repositoryState = RepositoryState.getInstance();
    private MainPanel _mainPanel;

    private CreditsDialog _creditsDialog;
    private LoginDialog _loginDialog;
    private WBSession _session;
    private String _meandreBaseURL;

    private static boolean _loggingOut = false;

    @Override
    protected void onLoad() {
        _workbench = this;

        Window.addWindowCloseListener(new WindowCloseListener() {
            public String onWindowClosing() {
                String closingMsg = null;

                if (_mainPanel != null && !_loggingOut) {
                    WorkspacePanel workspacePanel = _mainPanel.getWorkspacePanel();
                    boolean dirty = false;
                    for (WorkspaceTab tab : workspacePanel.getTabs())
                        if (tab.isDirty()) {
                            dirty = true;
                            break;
                        }
                    if (dirty)
                        closingMsg = "You have unsaved changes which will be lost if you proceed!!!";
                }

                return closingMsg;
            }

            public void onWindowClosed() {
            }
        });

        Repository.getSession(new WBCallback<WBSession>() {
            @Override
            public void onSuccess(WBSession session) {
                Log.info("Session " + session.getSid() + " from date " + session.getDate() +
                        " for user " + session.getUserName() + " has been found");

                onLoginSuccess(session);
            }

            @Override
            public void onSessionExpired() {
                doLogin();
            }
        });
    }

    public static void showLogin() {
        _workbench.doLogin();
    }

    public static void clear() {
        _workbench.doClear();
    }

    private void doClear() {
        _mainPanel.getEl().remove();
    }

    private void doLogin() {
        LoginListener loginListener = new LoginListener() {
            public void onLogin(String userName, String password, String hostName, int port) {
                login(userName, password, hostName, port);
            }
        };

        _loginDialog = new LoginDialog(loginListener);
        RootPanel.get().add(_loginDialog);
    }

    protected void onLoginSuccess(WBSession session) {
        _session = session;
        _meandreBaseURL = "http://" + _session.getUserName() + ":" + _session.getPassword() +
            "@" + _session.getHostName() + ":" + _session.getPort();

        RootPanel.get().clear();

        _mainPanel = new MainPanel(session);
        final RepositoryPanel repositoryPanel = _mainPanel.getRepositoryPanel();
        repositoryPanel.getComponentsPanel().setStore(_repositoryState.getComponentsStore());
        repositoryPanel.getFlowsPanel().setStore(_repositoryState.getFlowsStore());
        repositoryPanel.getLocationsPanel().setStore(_repositoryState.getLocationsStore());
        repositoryPanel.addListener(new RefreshListener() {
            public void onRefresh() {
                refreshRepository(null);
            }
        });

        final WorkspacePanel workspacePanel = _mainPanel.getWorkspacePanel();
        workspacePanel.setActionListener(new WorkspacePanelActionListenerAdapter() {
            public Menu getTabContextMenu(final WorkspacePanel wsPanel, final WorkspaceTab wsTab) {
                wsPanel.setActiveTab(wsTab);

                final Item btnNewTab = new Item("New Tab");
                btnNewTab.setIconCls("icon-tab-new");
                btnNewTab.addListener(new BaseItemListenerAdapter() {
                    @Override
                    public void onClick(BaseItem item, EventObject e) {
                        addNewTab(null);
                    }
                });

                final Item btnCloseTab = new Item("Close Tab");
                btnCloseTab.addListener(new BaseItemListenerAdapter() {
                    @Override
                    public void onClick(BaseItem item, EventObject e) {
                        wsTab.close();
                    }
                });

                Menu tabContextMenu = new Menu();
                tabContextMenu.addItem(btnNewTab);
                tabContextMenu.addItem(btnCloseTab);

                return tabContextMenu;
            }

            public void onTabClosed(WorkspacePanel wsPanel, WorkspaceTab wsTab) {
                Log.info("Tab " + wsTab.getTitle() + " cosed");
                workspacePanel.getOutputPanel().remove(wsTab.getFlowOutputPanel());

                if (wsPanel.getTabs().length == 0)
                    addNewTab(null);
            }

            public void onNewTab(WorkspacePanel wsPanel) {
                addNewTab(null);
            }

            @Override
            public boolean doBeforeTabChange(WorkspaceTab oldTab, WorkspaceTab newTab) {
                if (oldTab != null && oldTab.getSelectedComponent() != null)
                    resetDetails();

                return super.doBeforeTabChange(oldTab, newTab);
            }

            @Override
            public void onTabChanged(WorkspaceTab tab) {
                workspacePanel.getOutputPanel().setActiveItemID(tab.getFlowOutputPanel().getId());
                if (tab.getSelectedComponent() != null)
                    showDetails(tab, tab.getSelectedComponent());
            }
        });

        final ComponentsGrid componentsPanel = repositoryPanel.getComponentsPanel();
        componentsPanel.addListener(new ComponentsGridActionListenerAdapter() {
            @Override
            public void onSelected(WBExecutableComponentDescription comp) {
                workspacePanel.getActiveTab().clearSelection();
                clearFlowsGridSelection();
                showDetails(comp);
            }

            @Override
            public void onUnselected(WBExecutableComponentDescription comp) {
                resetDetails();
            }
        });
        componentsPanel.addListener(new PanelListenerAdapter() {
            @Override
            public void onCollapse(Panel panel) {
                if (componentsPanel.getSelectedComponent() != null)
                    resetDetails();
            }

            @Override
            public void onExpand(Panel panel) {
                WBExecutableComponentDescription selectedComponent = componentsPanel.getSelectedComponent();
                if (selectedComponent != null)
                    showDetails(selectedComponent);
            }
        });

        final FlowsGrid flowsPanel = repositoryPanel.getFlowsPanel();
        flowsPanel.addListener(new FlowsGridActionListenerAdapter() {
            public void onSelected(WBFlowDescription flow) {
                workspacePanel.getActiveTab().clearSelection();
                clearComponentsGridSelection();
                showDetails(flow);
            }

            @Override
            public void onOpen(WBFlowDescription flow) {
                boolean alreadyOpened = false;
                for (WorkspaceTab wsTab : workspacePanel.getTabs()) {
                    WBFlowDescription wsFlow = wsTab.getFlowDescription();
                    String baseURI = wsFlow.getDesiredBaseURI();
                    if (wsFlow.getName().equals(flow.getName()) &&
                            baseURI != null && baseURI.equals(flow.getDesiredBaseURI())) {
                        alreadyOpened = true;
                        workspacePanel.setActiveTab(wsTab);
                        break;
                    }
                }

                if (!alreadyOpened)
                    addNewTab(flow);
            }
        });
        flowsPanel.addListener(new PanelListenerAdapter() {
            @Override
            public void onCollapse(Panel panel) {
                if (flowsPanel.getSelectedFlow() != null)
                    resetDetails();
            }

            @Override
            public void onExpand(Panel panel) {
                WBFlowDescription selectedFlow = flowsPanel.getSelectedFlow();
                if (selectedFlow != null)
                    showDetails(selectedFlow);
            }
        });

        final LocationsGrid locationsPanel = repositoryPanel.getLocationsPanel();
        locationsPanel.setActionListener(new LocationsGridActionListener() {
            public void onAdd() {
                AddLocationDialog addLocationDialog = new AddLocationDialog(new AddLocationListener() {
                    public void onAdd(final String description, final String url) {
                        Repository.addLocation(url, description, new WBCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean success) {
                                if (success)
                                    refreshRepository(null);
                                else
                                    Application.showError("Add Location", "Could not add location!");
                            }
                        });
                    }
                });

                addLocationDialog.show();
            }

            public void onRemove(final WBLocation location) {
                MessageBox.confirm("Remove Location", "Are you sure you want to remove this location?",
                        new ConfirmCallback() {
                            public void execute(String btnID) {
                                if (btnID.equalsIgnoreCase("yes"))
                                    Repository.removeLocation(location.getLocation(), new WBCallback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean success) {
                                            if (success)
                                                refreshRepository(null);
                                            else
                                                Application.showError("Remove Location", "Could not remove location!");
                                        }
                                    });
                            }
                        });
            }

            public void onRegenerate() {
                Application.showMessage("Regenerate",
                        "<b>Regenerating the repository will delete all components and " +
                        "flows not contained in your location sources!</b> " +
                        "Are you sure you want to continue? ",
                        MessageBox.WARNING,
                        MessageBox.YESNO,
                        new PromptCallback() {
                            public void execute(String btnID, String text) {
                                if (btnID.equalsIgnoreCase("yes"))
                                    Repository.regenerate(new WBCallback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean success) {
                                            if (success)
                                                refreshRepository(null);
                                            else
                                                Application.showError("Regenerate", "Could not regenerate the repository!");
                                        }
                                    });
                            }
                        });
            }
        });

        _mainPanel.setActionListener(new MainPanelActionListener() {
            public void onLogout() {
                boolean dirty = false;
                for (WorkspaceTab tab : workspacePanel.getTabs())
                    if (tab.isDirty()) {
                        dirty = true;
                        break;
                    }

                String logoutMsg = "Are you sure you want to log out?";
                String icon = MessageBox.QUESTION;

                if (dirty) {
                        logoutMsg = "You have unsaved changes which will be lost if you log out! " +
                            "<b>Are you sure you want to log out and lose the changes?</b>";
                        icon = MessageBox.WARNING;
                }

                Application.showMessage("Logout", logoutMsg, icon, MessageBox.YESNO, new PromptCallback() {
                    public void execute(String btnID, String text) {
                        if (btnID.equalsIgnoreCase("yes"))
                            logout();
                    }
                });
            }

            public void onCredits() {
                if (_creditsDialog == null)
                    _creditsDialog = new CreditsDialog();

                _creditsDialog.show();
            }
        });

        workspacePanel.addTab(createTab(null));

        new Viewport(_mainPanel);

        refreshRepository(null);
    }

    private void addNewTab(WBFlowDescription flow) {
        WorkspaceTab newTab = createTab(flow);

        WorkspacePanel wsPanel = _mainPanel.getWorkspacePanel();
        wsPanel.addTab(newTab);
        wsPanel.setActiveTab(newTab);
    }

    private WorkspaceTab createTab(WBFlowDescription flow) {
        final WorkspaceTab workspaceTab = new WorkspaceTab(flow);
        _mainPanel.getWorkspacePanel().getOutputPanel().add(workspaceTab.getFlowOutputPanel());
        workspaceTab.addListener(new WorkspaceActionListenerAdapter() {
            @Override
            public void onComponentSelected(Component component) {
                showDetails(workspaceTab, component);
            }

            @Override
            public void onComponentUnselected(Component component) {
                resetDetails();
            }

            @Override
            public void onFlowSave(final WBFlowDescription flow, final AsyncCallback<WBFlowDescription> callback) {
                // TODO check if the flow already exists and present overwrite message

                flow.setCreator(_session.getUserName());
                flow.setCreationDate(new Date());

                MessageBox.show(new MessageBoxConfig() {
                    {
                        setMsg("Saving your flow, please wait...");
                        setProgressText("Saving...");
                        setWidth(300);
                        setWait(true);
                        setWaitConfig(new WaitConfig() {
                            {
                                setInterval(100);
                            }
                        });
                    }
                });

                Repository.uploadFlow(flow, true, new WBCallback<WBFlowDescription>() {
                    public void onSuccess(WBFlowDescription uploadedFlow) {
                        _repositoryState.addFlow(uploadedFlow);
                        workspaceTab.setFlowDescription(uploadedFlow);

                        workspaceTab.clearDirty();
                        workspaceTab.setTitle(flow.getName());

                        MessageBox.hide();

                        if (callback != null)
                            callback.onSuccess(uploadedFlow);
                    }

                    @Override
                    public void onFailure(final Throwable caught) {
                        MessageBox.hide();

                        super.onFailure(caught);

                        if (caught instanceof CorruptedFlowException)
                            Application.showError("Save Flow", null, caught,
                                    new PromptCallback() {
                                        public void execute(String btnID, String text) {
                                            if (callback != null)
                                                callback.onFailure(caught);
                                        }
                            });
                    }
                });
            }

            @Override
            public void onFlowRun(final WorkspaceTab flowTab) {
                WBFlowDescription flow = flowTab.getFlowDescription();
                final String token = _session.getUserName() + "_" + new Date().getTime();
                String meandreExecuteURL = _meandreBaseURL + "/services/execute/flow.txt";
                String runFlowURL = meandreExecuteURL + "?uri=" + flow.getFlowURI() + "&statistics=true&token=" + token;

                Log.info("Running flow " + runFlowURL);
                flowTab.getFlowOutputPanel().setUrl(runFlowURL);

                boolean hasWebUI = false;

                for (WBExecutableComponentInstanceDescription compInstance : flow.getExecutableComponentInstances()) {
                    WBExecutableComponentDescription compDesc = compInstance.getExecutableComponentDescription();
                    if (compDesc.getMode().equals(WBExecutableComponentDescription.WEBUI_COMPONENT)) {
                        hasWebUI = true;
                        break;
                    }
                }

                if (hasWebUI) {
                    Log.debug("Flow should have a WebUI - looking for one (timeout: " + WEBUI_TIMEOUT/1000 + " seconds)...");

                    Timer timer = new Timer() {
                        private Long startTime = null;

                        @Override
                        public void run() {
                            if (startTime == null)
                                startTime = new Date().getTime();
                            else {
                                Long timeNow = new Date().getTime();
                                if (timeNow - startTime > WEBUI_TIMEOUT) {
                                    Log.debug("WebUI retrieve timeout");
                                    cancel();
                                    return;
                                }
                            }

                            Log.debug("Checking for WebUI");

                            Repository.retrieveWebUIInfo(token, new WBCallback<WBWebUIInfo>() {
                                @Override
                                public void onSuccess(WBWebUIInfo webUIInfo) {
                                    if (webUIInfo == null) {
                                        schedule(1000);
                                        return;
                                    }

                                    cancel();

                                    Log.info("Found WebUI: " + webUIInfo.getWebUIUrl() + " (" +
                                            webUIInfo.getURI() + ") token: " + webUIInfo.getToken());

                                    flowTab.openWebUI(webUIInfo);
                                }
                            });
                        }
                    };

                    timer.schedule(2000);
                }
            }

            @Override
            public void onFlowStop(final WBFlowDescription flow) {
                Repository.retrieveRunningFlows(new WBCallback<Map<String,String>>() {
                    @Override
                    public void onSuccess(Map<String, String> result) {
                        Log.debug("Got result: size=" + result.size());
                        for (Entry<String, String> entry : result.entrySet())
                            Log.debug("flow: " + entry.getKey() + " webui: " + entry.getValue());

                        final String webUIURL = result.get(flow.getFlowURI());
                        if (webUIURL == null)
                            Application.showMessage("Stop Flow", "This flow is not currently running", MessageBox.INFO);
                        else {
                            Log.info("Found running flow at " + webUIURL);
                        }
                    }
                });
            }

        });

        return workspaceTab;
    }

    private void refreshRepository(final ICommand<?> cmd) {
        final RepositoryPanel repositoryPanel = _mainPanel.getRepositoryPanel();

        repositoryPanel.setMask("Loading");
        _repositoryState.refresh(new ICommand<Object>() {
            public void execute(Object args) {
                repositoryPanel.clearMask();

                if (cmd != null)
                    cmd.execute(null);
            }
        });
    }

    private void login(final String userName, final String password, final String hostName, final int port) {
        _loginDialog.disableLogin();

        Repository.login(userName, password, hostName, port, new WBCallback<WBSession>() {
            @Override
            public void onSuccess(WBSession session) {
                onLoginSuccess(session);
            }

            @Override
            public void onFailure(final Throwable caught) {
                _loginDialog.enableLogin();

                super.onFailure(caught);

                final String errMsg = (caught.getCause() != null) ?
                        caught.getCause().getMessage() : caught.getMessage();

                if (caught instanceof LoginFailedException) {
                    MessageBox.show(new MessageBoxConfig() {
                        {
                            setTitle("Login failed");
                            setMsg(errMsg);
                            setButtons(MessageBox.OK);
                            setIconCls(MessageBox.ERROR);
                            setWidth(700);
                        }
                    });
                }

            }
        });
    }

    public static void logout() {
        Repository.logout(new WBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                _loggingOut = true;
                reload();
            }

            @Override
            public void onSessionExpired() {
                _loggingOut = true;
                reload();
            }
        });
    }

    public static native void reload() /*-{
        $wnd.location.reload();
    }-*/;


    private void clearRepositoryGridSelections() {
        clearComponentsGridSelection();
        clearFlowsGridSelection();
    }

    private void clearComponentsGridSelection() {
        _mainPanel.getRepositoryPanel().getComponentsPanel().clearSelection();
    }

    private void clearFlowsGridSelection() {
        _mainPanel.getRepositoryPanel().getFlowsPanel().clearSelection();
    }

    private void showDetails(WBExecutableComponentDescription comp) {
        _mainPanel.getDetailsPanel().view(comp);
    }

    private void showDetails(WBFlowDescription flow) {
        _mainPanel.getDetailsPanel().view(flow);
    }

    private void showDetails(WorkspaceTab tab, Component component) {
        clearRepositoryGridSelections();
        _mainPanel.getDetailsPanel().view(tab, component);
    }

    private void resetDetails() {
        _mainPanel.getDetailsPanel().reset();
    }
}
