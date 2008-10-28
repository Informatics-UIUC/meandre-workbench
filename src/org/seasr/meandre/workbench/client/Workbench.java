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

import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;
import org.seasr.meandre.workbench.client.beans.repository.WBConnectorDescription;
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
 * Main controller class for the Workbench
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

    /**
     * Called at application startup (from Application.java)
     */
    @Override
    protected void onLoad() {
        _workbench = this;

        // hook the close window event to check whether there are unsaved flows
        Window.addWindowCloseListener(new WindowCloseListener() {
            public String onWindowClosing() {
                String closingMsg = null;

                // _loggingOut is set when user clicks the "logout" button - an intentional action
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

        // retrieve the user session, if already exists
        Repository.getSession(new WBCallback<WBSession>() {
            @Override
            public void onSuccess(WBSession session) {
                Log.info("Session " + session.getSid() + " from date " + session.getDate() +
                        " for user " + session.getUserName() + " has been found");

                // session found, continue loading the app
                onLoginSuccess(session);
            }

            @Override
            public void onSessionExpired() {
                doLogin();
            }
        });
    }

    /**
     * Force the Workbench to jump to the login screen
     */
    public static void showLogin() {
        _workbench.doLogin();
    }

    /**
     * Removes all visual elements
     */
    public static void clear() {
        _workbench.doClear();
    }

    /**
     * Removes all visual elements from the main panel
     */
    private void doClear() {
        _mainPanel.getEl().remove();
    }

    /**
     * Presents the login dialog and invokes the authentication routine when the login button is pressed
     */
    private void doLogin() {
        LoginListener loginListener = new LoginListener() {
            public void onLogin(String userName, String password, String hostName, int port) {
                login(userName, password, hostName, port);
            }
        };

        _loginDialog = new LoginDialog(loginListener);
        RootPanel.get().add(_loginDialog);
    }

    /**
     * Constructs the user interface after the user session has been validated
     *
     * @param session The user session
     */
    protected void onLoginSuccess(WBSession session) {
        _session = session;

        // constructs the base url used for invoking the execute flow service on the meandre server
        _meandreBaseURL = "http://" + _session.getUserName() + ":" + _session.getPassword() +
            "@" + _session.getHostName() + ":" + _session.getPort();

        // remove all visual elements
        RootPanel.get().clear();

        // construct the main panel
        _mainPanel = new MainPanel(session);

        // set up the repository panel
        final RepositoryPanel repositoryPanel = _mainPanel.getRepositoryPanel();
        repositoryPanel.getComponentsPanel().setStore(_repositoryState.getComponentsStore());
        repositoryPanel.getFlowsPanel().setStore(_repositoryState.getFlowsStore());
        repositoryPanel.getLocationsPanel().setStore(_repositoryState.getLocationsStore());
        repositoryPanel.addListener(new RefreshListener() {
            public void onRefresh() {
                refreshRepository(null);
            }
        });

        // set up the workspace panel
        final WorkspacePanel workspacePanel = _mainPanel.getWorkspacePanel();
        workspacePanel.setActionListener(new WorkspacePanelActionListenerAdapter() {
            /**
             * Constructs the context menu for tabs
             *
             * @param wsPanel The panel to which the tab belongs
             * @param wsTab The tab where the context menu was invoked
             * @return The context menu
             */
            public Menu getTabContextMenu(final WorkspacePanel wsPanel, final WorkspaceTab wsTab) {
                wsPanel.setActiveTab(wsTab);

                // create the "New Tab" menu option
                final Item btnNewTab = new Item("New Tab");
                btnNewTab.setIconCls("icon-tab-new");
                btnNewTab.addListener(new BaseItemListenerAdapter() {
                    @Override
                    public void onClick(BaseItem item, EventObject e) {
                        addNewTab(null);
                    }
                });

                // create the "Close Tab" menu option
                final Item btnCloseTab = new Item("Close Tab");
                btnCloseTab.addListener(new BaseItemListenerAdapter() {
                    @Override
                    public void onClick(BaseItem item, EventObject e) {
                        wsTab.close();
                    }
                });

                // create the tab context menu
                Menu tabContextMenu = new Menu();
                tabContextMenu.addItem(btnNewTab);
                tabContextMenu.addItem(btnCloseTab);

                return tabContextMenu;
            }

            /**
             * Removes the output panel associated with the closed tab
             *
             * @param wsPanel The panel to which the tab belongs
             * @param wsTab The tab that was closed
             */
            public void onTabClosed(WorkspacePanel wsPanel, WorkspaceTab wsTab) {
                Log.info("Tab " + wsTab.getTitle() + " closed");

                // remove the output panel
                workspacePanel.getOutputPanel().remove(wsTab.getFlowOutputPanel());

                if (wsPanel.getTabs().length == 0)
                    addNewTab(null);
            }

            /**
             * Adds a new tab
             *
             * @param wsPanel The panel where the new tab will be created
             */
            public void onNewTab(WorkspacePanel wsPanel) {
                addNewTab(null);
            }

            /**
             * Resets the selection state of the current tab before switching to a different one
             *
             * @param oldTab The current active tab
             * @param newTab The new tab that will be activated
             * @return
             */
            @Override
            public boolean doBeforeTabChange(WorkspaceTab oldTab, WorkspaceTab newTab) {
                // if there's something selected in the old tab, reset state to default
                if (oldTab != null && oldTab.getSelectedComponent() != null)
                    resetDetails();

                return super.doBeforeTabChange(oldTab, newTab);
            }

            /**
             * Updates the output panel and selection state for the newly switched to tab
             *
             * @param tab The newly activated tab
             */
            @Override
            public void onTabChanged(WorkspaceTab tab) {
                // bring the output panel associated with this tab into the foreground
                workspacePanel.getOutputPanel().setActiveItemID(tab.getFlowOutputPanel().getId());
                // restore any visual selections
                if (tab.getSelectedComponent() != null)
                    showDetails(tab, tab.getSelectedComponent());
            }
        });

        // set up the components panel
        final ComponentsGrid componentsPanel = repositoryPanel.getComponentsPanel();
        componentsPanel.addListener(new ComponentsGridActionListenerAdapter() {
            /**
             * Updates the selection state and details section
             *
             * @param comp The component that was selected
             */
            @Override
            public void onSelected(WBExecutableComponentDescription comp) {
                // clear any tab selection (only one component can be selected at any time,
                // either in the workspace area or in the components grid - so that the details panel
                // shows the details of the currently selected component, if any)
                workspacePanel.getActiveTab().clearSelection();
                clearFlowsGridSelection();
                showDetails(comp);
            }

            /**
             * Resets the selection state
             *
             * @param comp The component that was unselected
             */
            @Override
            public void onUnselected(WBExecutableComponentDescription comp) {
                // clear all info from the details panel
                resetDetails();
            }
        });
        componentsPanel.addListener(new PanelListenerAdapter() {
            /**
             * Resets the details section state upon components panel collapse
             *
             * @param panel The components panel
             */
            @Override
            public void onCollapse(Panel panel) {
                if (componentsPanel.getSelectedComponent() != null)
                    resetDetails();
            }

            /**
             * Restores the details section state upon components panel expand
             *
             * @param panel The components panel
             */
            @Override
            public void onExpand(Panel panel) {
                WBExecutableComponentDescription selectedComponent = componentsPanel.getSelectedComponent();
                if (selectedComponent != null)
                    showDetails(selectedComponent);
            }
        });

        // set up the flows panel
        final FlowsGrid flowsPanel = repositoryPanel.getFlowsPanel();
        flowsPanel.addListener(new FlowsGridActionListenerAdapter() {
            /**
             * Updates the details section state with information about the selected flow
             *
             * @param flow The selected flow
             */
            public void onSelected(WBFlowDescription flow) {
                workspacePanel.getActiveTab().clearSelection();
                clearComponentsGridSelection();
                showDetails(flow);
            }

            /**
             * Opens a new tab when a flow is "opened" (double-clicked on), if one doesn't already exist
             *
             * @param flow The opened flow
             */
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
            /**
             * Resets the details section state when the flows panel is collapsed
             *
             * @param panel The flows panel
             */
            @Override
            public void onCollapse(Panel panel) {
                if (flowsPanel.getSelectedFlow() != null)
                    resetDetails();
            }

            /**
             * Updates the details section state with information about the currently selected flow, if any
             *
             * @param panel The flows panel
             */
            @Override
            public void onExpand(Panel panel) {
                WBFlowDescription selectedFlow = flowsPanel.getSelectedFlow();
                if (selectedFlow != null)
                    showDetails(selectedFlow);
            }
        });

        // set up the locations panel
        final LocationsGrid locationsPanel = repositoryPanel.getLocationsPanel();
        locationsPanel.setActionListener(new LocationsGridActionListener() {
            /**
             * Adds a new Meandre components location via the wizard
             */
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

            /**
             * Removes an existing Meandre components location
             *
             * @param location The location to be removed
             */
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

            /**
             * Warns user about regenerating repository, and invokes regenerate if user approved
             */
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

        // set up the main panel
        _mainPanel.setActionListener(new MainPanelActionListener() {
            /**
             * Warns user about logging out while having unsaved changes, and performs logout if user approves
             */
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

            /**
             * Presents application credits information
             */
            public void onCredits() {
                if (_creditsDialog == null)
                    _creditsDialog = new CreditsDialog();

                _creditsDialog.show();
            }
        });

        // create an empty flow for people to use
        workspacePanel.addTab(createTab(null));

        new Viewport(_mainPanel);

        refreshRepository(null);
    }

    /**
     * Adds a new tab based on a specified flow
     *
     * @param flow The flow
     */
    private void addNewTab(WBFlowDescription flow) {
        WorkspaceTab newTab = createTab(flow);

        WorkspacePanel wsPanel = _mainPanel.getWorkspacePanel();
        wsPanel.addTab(newTab);
        wsPanel.setActiveTab(newTab);
    }

    /**
     * Creates (but does not add) a new tab for the specified flow
     *
     * @param flow The flow
     * @return The new tab object
     */
    private WorkspaceTab createTab(WBFlowDescription flow) {
        final WorkspaceTab workspaceTab = new WorkspaceTab(flow);
        _mainPanel.getWorkspacePanel().getOutputPanel().add(workspaceTab.getFlowOutputPanel());
        workspaceTab.addListener(new WorkspaceActionListenerAdapter() {
            /**
             * Shows the details for the selected component
             *
             * @param component The component
             */
            @Override
            public void onComponentSelected(Component component) {
                showDetails(workspaceTab, component);
            }

            /**
             * Resets the details section when a component is unselected
             *
             * @param component The component
             */
            @Override
            public void onComponentUnselected(Component component) {
                resetDetails();
            }

            /**
             * Saves/uploads a flow
             *
             * @param flow The flow
             * @param callback Called upon the completion of the save operation
             */
            @Override
            public void onFlowSave(final WBFlowDescription flow, final AsyncCallback<WBFlowDescription> callback) {
                // TODO check if the flow already exists and present overwrite message

                // update the flow details
                flow.setCreator(_session.getUserName());
                flow.setCreationDate(new Date());

                // show informative message to user
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

                // perform the upload to the server (via RPC)
                Repository.uploadFlow(flow, true, new WBCallback<WBFlowDescription>() {
                    public void onSuccess(WBFlowDescription uploadedFlow) {
                        _repositoryState.addFlow(uploadedFlow);
                        WBFlowDescription uploadClone = uploadedFlow.clone();
                        workspaceTab.refresh(uploadClone);   //FIXME: for now this is fine - a RepositoryState solution
                                                                      //that provides update events is better


                        workspaceTab.clearDirty();
                        workspaceTab.setTitle(flow.getName());

                        MessageBox.hide();

                        if (callback != null)
                            callback.onSuccess(uploadClone);
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

            /**
             * Runs a flow
             *
             * @param flowTab The tab for the flow to be ran
             */
            @Override
            public void onFlowRun(final WorkspaceTab flowTab) {
                WBFlowDescription flow = flowTab.getFlowDescription();

                // construct the URL used to execute the flow on the server
                final String token = _session.getUserName() + "_" + new Date().getTime();
                String meandreExecuteURL = _meandreBaseURL + "/services/execute/flow.txt";
                String runFlowURL = meandreExecuteURL + "?uri=" + flow.getFlowURI() + "&statistics=true&token=" + token;

                Log.info("Running flow " + runFlowURL);
                // start the flow
                flowTab.getFlowOutputPanel().setUrl(runFlowURL);

                boolean showWebUI = false;

                // check if the flow contains any components that have a WebUI
                for (WBExecutableComponentInstanceDescription compInstance : flow.getExecutableComponentInstances()) {
                    WBExecutableComponentDescription compDesc = compInstance.getExecutableComponentDescription();
                    if (compDesc.getMode().equals(WBExecutableComponentDescription.WEBUI_COMPONENT)) {
                        boolean isConnected = false;
                        for (WBConnectorDescription connector : flow.getConnectorDescriptions())
                            if (connector.getTargetInstance().equals(compInstance.getExecutableComponentInstance())) {
                                isConnected = true;
                                break;
                            }
                        if (compDesc.getInputs().isEmpty() || isConnected) {
                            showWebUI = true;
                            break;
                        }
                    }
                }

                if (showWebUI) {
                    Log.debug("Flow should have a WebUI - looking for one (timeout: " + WEBUI_TIMEOUT/1000 + " seconds)...");

                    // wait to receive the WebUI info for the component
                    Timer timer = new Timer() {
                        private Long startTime = null;

                        @Override
                        public void run() {
                            if (startTime == null)
                                startTime = new Date().getTime();
                            else {
                                Long timeNow = new Date().getTime();
                                // give up if we waited long enough
                                if (timeNow - startTime > WEBUI_TIMEOUT) {
                                    Log.debug("WebUI retrieve timeout");
                                    cancel();
                                    return;
                                }
                            }

                            Log.debug("Checking for WebUI");

                            // check whether a WebUI is available (via RPC)
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

                                    flowTab.setWebUIInfo(webUIInfo);
                                    flowTab.openWebUI();
                                }
                            });
                        }
                    };

                    timer.schedule(2000);
                }
            }

            /**
             * Attempts to abort an executing flow
             *
             * @param flow The flow to abort
             */
            @Override
            public void onFlowStop(final WBFlowDescription flow) {
                WBWebUIInfo webUI = workspaceTab.getWebUIInfo();
                if (webUI == null) {
                    Application.showMessage("Stop Flow", "This flow is not currently running", MessageBox.INFO);
                    return;
                }

                // attempts to abort the flow (via RPC)
                Repository.abortFlow(webUI.getPort(), new WBCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        workspaceTab.setWebUIInfo(null);
                        Application.showMessage("Abort Flow", "Request successfully dispatched", MessageBox.INFO);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        super.onFailure(caught);
                        workspaceTab.setWebUIInfo(null);
                    }
                });
            }

        });

        return workspaceTab;
    }

    /**
     * Refreshes the components/flows/locations repositories (pulls fresh data from the server)
     *
     * @param cmd An optional follow-on executable command
     */
    private void refreshRepository(final ICommand<?> cmd) {
        final RepositoryPanel repositoryPanel = _mainPanel.getRepositoryPanel();

        repositoryPanel.setMask("Loading");
        _repositoryState.refresh(new ICommand<Object>() {
            public void execute(Object args) {
                repositoryPanel.clearMask();

                for (WorkspaceTab tab : _mainPanel.getWorkspacePanel().getTabs())  //FIXME: temporary, remove when the event-driven
                    tab.checkValid();                                                 //RepositoryState is implemented

                if (cmd != null)
                    cmd.execute(null);
            }
        });
    }

    /**
     * Authenticates user credentials
     *
     * @param userName The user name
     * @param password The user password
     * @param hostName The host name
     * @param port The port for the running Meandre server
     */
    private void login(final String userName, final String password, final String hostName, final int port) {
        _loginDialog.disableLogin();

        // perform login (via RPC)
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

    /**
     * Logs out user and clears the user's session
     */
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

    /**
     * Forces a browser refresh
     */
    public static native void reload() /*-{
        $wnd.location.reload();
    }-*/;

    /**
     * Resets the selection state for the components and flows panels
     */
    private void clearRepositoryGridSelections() {
        clearComponentsGridSelection();
        clearFlowsGridSelection();
    }

    /**
     * Resets the selection state for the components panel
     */
    private void clearComponentsGridSelection() {
        _mainPanel.getRepositoryPanel().getComponentsPanel().clearSelection();
    }

    /**
     * Resets the selection state for the flows panel
     */
    private void clearFlowsGridSelection() {
        _mainPanel.getRepositoryPanel().getFlowsPanel().clearSelection();
    }

    /**
     * Shows the details of a component
     *
     * @param comp The component
     */
    private void showDetails(WBExecutableComponentDescription comp) {
        _mainPanel.getDetailsPanel().view(comp);
    }

    /**
     * Shows the details for a flow
     *
     * @param flow The flow
     */
    private void showDetails(WBFlowDescription flow) {
        _mainPanel.getDetailsPanel().view(flow);
    }

    /**
     * Shows the details for an open flow
     *
     * @param tab The tab corresponding to the open flow
     * @param component The selected component
     */
    private void showDetails(WorkspaceTab tab, Component component) {
        clearRepositoryGridSelections();
        _mainPanel.getDetailsPanel().view(tab, component);
    }

    /**
     * Resets the details panel
     */
    private void resetDetails() {
        _mainPanel.getDetailsPanel().reset();
    }
}
