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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.seasr.meandre.workbench.client.Application;
import org.seasr.meandre.workbench.client.Clipboard;
import org.seasr.meandre.workbench.client.RepositoryState;
import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;
import org.seasr.meandre.workbench.client.beans.repository.WBConnectorDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBPropertiesDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBPropertiesDescriptionDefinition;
import org.seasr.meandre.workbench.client.listeners.ClipboardListener;
import org.seasr.meandre.workbench.client.listeners.ComponentActionListenerAdapter;
import org.seasr.meandre.workbench.client.listeners.SaveFlowListener;
import org.seasr.meandre.workbench.client.listeners.WorkspaceActionListener;
import org.seasr.meandre.workbench.client.widgets.Component.ComponentPort;
import org.seasr.meandre.workbench.client.widgets.Component.PortType;

import pl.balon.gwt.diagrams.client.connection.AbstractConnection;
import pl.balon.gwt.diagrams.client.connection.ConnectionActionListener;
import pl.balon.gwt.diagrams.client.connection.RectilinearTwoEndedConnection;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.Direction;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.data.Record;
import com.gwtext.client.dd.DragData;
import com.gwtext.client.dd.DragSource;
import com.gwtext.client.dd.DropTarget;
import com.gwtext.client.dd.DropTargetConfig;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.GridDragData;
import com.gwtext.client.widgets.layout.AbsoluteLayout;
import com.gwtext.client.widgets.layout.AbsoluteLayoutData;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.MenuItem;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.menu.event.MenuListenerAdapter;

/**
 * @author Boris Capitanu
 *
 */
@SuppressWarnings("unchecked")
public class WorkspaceTab extends Panel implements ClipboardListener {
    private final static String COMP_TOP_KEY = "wb_top_pix_pos";
    private final static String COMP_LEFT_KEY = "wb_left_pix_pos";

    private static int TAB_COUNTER = 1;

    private WBFlowDescription _wbFlow;
    private final Map<WBExecutableComponentInstanceDescription, Component> _componentMap;
    private final Map<AbstractConnection, WBConnectorDescription> _connectionMap;
    private final Set<WorkspaceActionListener> _actionListeners = new HashSet<WorkspaceActionListener>();
    private final FlowOutputPanel _outputPanel = new FlowOutputPanel();
    private ComponentPort _selectedPort = null;
    private WorkspacePanel _parent = null;
    private boolean _dirty = false;
    private boolean _isClosing = false;
    private final boolean _isRunning = false;
    private int _componentCount = 0;
    private int _connectorCount = 0;

    private final ToolbarButton _btnSave = new ToolbarButton("Save");
    private final ToolbarButton _btnSaveAs = new ToolbarButton("Save As");
    private final ToolbarButton _btnRemoveComponent = new ToolbarButton("Remove");
    private final ToolbarButton _btnCopy = new ToolbarButton("Copy");
    private final ToolbarButton _btnPaste = new ToolbarButton("Paste");

    private final RepositoryState _repositoryState = RepositoryState.getInstance();
    private final Clipboard _clipboard = Clipboard.getInstance();

    public WorkspaceTab(final WBFlowDescription flow) {
        if (flow == null) {
            String flowName = getUntitledName();
            _wbFlow = createNewFlowDescription(flowName);
            setTitle(flowName);
        } else {
            _wbFlow = flow.clone();
            setTitle(_wbFlow.getName());
        }

        _componentMap = new HashMap<WBExecutableComponentInstanceDescription, Component>();
        _connectionMap = new HashMap<AbstractConnection, WBConnectorDescription>();

        setIconCls("icon-tab");
        setAutoScroll(true);
        setClosable(true);
        setLayout(new AbsoluteLayout());
        setBodyStyle("position: relative;"); // needed so that GWT-Diagrams works properly

        Toolbar toolbar = new Toolbar();

        _btnSave.setIconCls("icon-flow-save");
        _btnSaveAs.setIconCls("icon-flow-saveas");

        ButtonListenerAdapter btnSaveListener = new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                saveFlow(button.getId().equals(_btnSaveAs.getId()), null);
            }
        };

        _btnSave.addListener(btnSaveListener);
        _btnSaveAs.addListener(btnSaveListener);


        Menu mnuExport = new Menu();
        mnuExport.setShadow(true);
        mnuExport.setMinWidth(10);

        Item exportZZ = new Item("ZigZag", new BaseItemListenerAdapter() {
            @Override
            public void onClick(BaseItem item, EventObject e) {
                if (_componentMap.size() == 0)
                    return;

                for (WorkspaceActionListener listener : _actionListeners) {
                    listener.onFlowExport(_wbFlow, "zz");
                }
            }
        });
        exportZZ.setIconCls("icon-flow-export-zigzag");
        mnuExport.addItem(exportZZ);

        Item exportMAU = new Item("MAU", new BaseItemListenerAdapter() {
            @Override
            public void onClick(BaseItem item, EventObject e) {
                if (_componentMap.size() == 0)
                    return;

                for (WorkspaceActionListener listener : _actionListeners) {
                    listener.onFlowExport(_wbFlow, "mau");
                }
            }
        });
        exportMAU.setIconCls("icon-flow-export-mau");
        mnuExport.addItem(exportMAU);

        _btnExport = new ToolbarButton("Export");
        _btnExport.setIconCls("icon-flow-export");
        _btnExport.setMenu(mnuExport);

        _btnRemoveComponent.setIconCls("icon-component-delete");
        _btnRemoveComponent.disable();
        _btnRemoveComponent.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                for (Component selectedComponent : getSelectedComponents())
                    removeComponent(selectedComponent);
            }
        });

        _btnCopy.setIconCls("icon-copy");
        _btnCopy.disable();
        _btnCopy.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                List<WBExecutableComponentInstanceDescription> components = new ArrayList<WBExecutableComponentInstanceDescription>();
                Set<String> executableComponentInstances = new HashSet<String>();

                for (Component selectedComponent : getSelectedComponents()) {
                    WBExecutableComponentInstanceDescription instanceDescription = selectedComponent.getInstanceDescription();
                    components.add(instanceDescription.clone());
                    executableComponentInstances.add(instanceDescription.getExecutableComponentInstance());
                }

                List<WBConnectorDescription> connectors = new ArrayList<WBConnectorDescription>();
                for (WBConnectorDescription connector : _wbFlow.getConnectorDescriptions())
                    if (executableComponentInstances.contains(connector.getSourceInstance()) &&
                            executableComponentInstances.contains(connector.getTargetInstance()))
                        connectors.add(connector.clone());

                _clipboard.put(components, connectors);
            }
        });

        _btnPaste.setIconCls("icon-paste");
        if (_clipboard.isEmpty())
            _btnPaste.disable();
        else
            _btnPaste.enable();

        _btnPaste.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                List<WBExecutableComponentInstanceDescription> components = _clipboard.getComponents();
                List<WBConnectorDescription> connectors = _clipboard.getConnectors();

                Map<String, String> rewriteMap = new HashMap<String, String>();

                clearSelection();

                for (WBExecutableComponentInstanceDescription compInstance : components) {
                    WBExecutableComponentDescription compDesc =
                            _repositoryState.getComponent(compInstance.getExecutableComponent());
                    if (compDesc == null) {
                        Log.error("Could not find the component: " + compInstance.getExecutableComponent());
                        Window.alert("Component: " + compInstance.getExecutableComponent() + " was not found in the repository!");
                        continue;
                    }

                    WBExecutableComponentInstanceDescription newCompInstance = createComponentInstance(compDesc);
                    newCompInstance.setName(compInstance.getName());
                    newCompInstance.setDescription(compInstance.getDescription());
                    newCompInstance.setProperties(compInstance.getProperties().clone());

                    rewriteMap.put(compInstance.getExecutableComponentInstance(), newCompInstance.getExecutableComponentInstance());

                    Component newComponent = new Component(newCompInstance, compDesc);
                    addComponent(newComponent, true);

                    newComponent.select();
                }

                String baseURI = _wbFlow.getNormalizedFlowURI() + "connector/";

                final List<AbstractConnection> connections = new ArrayList<AbstractConnection>();
                for (WBConnectorDescription connector : connectors) {
                    // Create a unique URI
                    String resURI;
                    do {
                        resURI = baseURI + _connectorCount++;
                    }
                    while (connectorIsNotUnique(resURI));

                    connector = connector.clone();
                    connector.setConnector(resURI);
                    connector.setSourceInstance(rewriteMap.get(connector.getSourceInstance()));
                    connector.setTargetInstance(rewriteMap.get(connector.getTargetInstance()));

                    _wbFlow.getConnectorDescriptions().add(connector);
                    connections.add(createConnection(connector));
                }

                int[] scroll = getBody().getScroll();

                doLayout();

                getBody().scroll(Direction.LEFT, scroll[0], false);
                getBody().scroll(Direction.DOWN, scroll[1], false);

                // Perform lazy add of connections since they don't get rendered otherwise
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						for (AbstractConnection connection : connections)
                            connection.addTo(WorkspaceTab.this);
					}
				});
            }
        });

        _clipboard.addListener(this);

        _btnRunFlow.setIconCls("icon-flow-run");
        _btnRunFlow.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                if (_componentMap.size() == 0)
                    return;

                runFlow();
            }
        });

        _btnStopFlow.setIconCls("icon-flow-stop");
        _btnStopFlow.disable();
        _btnStopFlow.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onFlowStop(_wbFlow);
            }
        });

        _btnKillFlow.setIconCls("icon-flow-kill");
        _btnKillFlow.disable();
        _btnKillFlow.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(Button button, EventObject e) {
        		for (WorkspaceActionListener listener : _actionListeners)
                    listener.onFlowKill(_wbFlow);
        	}
        });

        toolbar.addButton(_btnSave);
        toolbar.addButton(_btnSaveAs);
        toolbar.addButton(_btnExport);
        toolbar.addSeparator();
        toolbar.addButton(_btnCopy);
        toolbar.addButton(_btnPaste);
        toolbar.addButton(_btnRemoveComponent);
        toolbar.addFill();
        toolbar.addButton(_btnRunFlow);
        toolbar.addButton(_btnStopFlow);
        toolbar.addButton(_btnKillFlow);

        setTopToolbar(toolbar);

        if (flow != null)
            loadFlow(_wbFlow);

        DropTargetConfig dtCfg = new DropTargetConfig();
        dtCfg.setTarget(true);
        dtCfg.setdDdGroup("ddComponents");

        new DropTarget(this, dtCfg) {
            @Override
            public String notifyEnter(DragSource source, EventObject e, DragData data) {
                return "x-tree-drop-ok-append";
            }

            @Override
            public String notifyOver(DragSource source, EventObject e, DragData data) {
                return "x-tree-drop-ok-append";
            }

            @Override
            public boolean notifyDrop(DragSource source, EventObject e, DragData data) {
                int[] xy = e.getXY();
                int[] pos = getBody().getXY();
                int[] scroll = getBody().getScroll();

                xy[0] -= pos[0];
                xy[1] -= pos[1];
                xy[0] += scroll[0];
                xy[1] += scroll[1];

                if (data instanceof GridDragData) {
                    GridDragData gridDragData = (GridDragData) data;
                    Record[] records = gridDragData.getSelections();

                    for (Record record : records) {
                        WBExecutableComponentDescription compDesc =
                            (WBExecutableComponentDescription) record.getAsObject("wbComponent");

                        WBExecutableComponentInstanceDescription compInstance = createComponentInstance(compDesc);
                        final Component component = new Component(compInstance, compDesc);
                        component.doOnRender(new Function() {
                            @Override
							public void execute() {
                                component.select();
                            }
                        });

                        WBPropertiesDescription compProps = compInstance.getProperties();
                        compProps.add(COMP_LEFT_KEY, Integer.toString(xy[0]));
                        compProps.add(COMP_TOP_KEY, Integer.toString(xy[1]));

                        addComponent(component, true);
                    }

                    doLayout();

                    getBody().scroll(Direction.LEFT, scroll[0], false);
                    getBody().scroll(Direction.DOWN, scroll[1], false);
                }

                return true;
            }
        };
    }

    public void enableRunFlow() {
        _btnRunFlow.enable();
    }

    public void disableRunFlow() {
        _btnRunFlow.disable();
    }

    public void enableKillFlow() {
    	_btnKillFlow.enable();
    }

    public void disableKillFlow() {
    	_btnKillFlow.disable();
    }

    public void setKillFlowVisible(boolean visible) {
    	_btnKillFlow.setVisible(visible);
    }

    public void loadFlow(final WBFlowDescription flow) {
        int nInstances = flow.getExecutableComponentInstances().size();
        WBExecutableComponentInstanceDescription[] instances = new WBExecutableComponentInstanceDescription[nInstances];
        instances = flow.getExecutableComponentInstances().toArray(instances);
        for (int i = 0; i < nInstances; i++) {
            final WBExecutableComponentInstanceDescription compInstance = instances[i];

            WBExecutableComponentDescription compDesc =
                _repositoryState.getComponent(compInstance.getExecutableComponent());

            if (compDesc == null) {
            	Scheduler.get().scheduleDeferred(new ScheduledCommand() {
    				@Override
					public void execute() {
                        _outputPanel.print("Missing component: The component '" + compInstance.getName() + "' needed by this flow " +
                                            "has not been found in the repository! Removing it...\n");
                    }
                });

                Log.error("Could not find the component: " + compInstance.getExecutableComponent() + " - removing from flow");
                flow.removeExecutableComponentInstance(compInstance);
                setDirty();
                continue;
            }

            // augment the properties of the component instance with the missing ones
            // from the component description definition
            Map<String, String> compDescPropsMap = compDesc.getProperties().getValueMap();
            Map<String, String> compInstancePropsMap = compInstance.getProperties().getValueMap();

            for (Entry<String, String> prop : compDescPropsMap.entrySet())
                if (!compInstancePropsMap.containsKey(prop.getKey()))
                    compInstancePropsMap.put(prop.getKey(), prop.getValue());

            // create the component and add it to the workspace
            Component component = new Component(compInstance, compDesc);
            addComponentToCanvas(component, false);
        }

        int nConnectors = flow.getConnectorDescriptions().size();
        WBConnectorDescription[] connectors = new WBConnectorDescription[nConnectors];
        connectors = flow.getConnectorDescriptions().toArray(connectors);
        for (int i = 0; i < nConnectors; i++) {
            WBConnectorDescription connector = connectors[i];
            if (createConnection(connector) == null) {
                flow.getConnectorDescriptions().remove(connector);
                setDirty();
            }
        }

        // Perform lazy add of connections since they don't get rendered otherwise
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
                for (AbstractConnection connection : _connectionMap.keySet())
                    connection.addTo(WorkspaceTab.this);
            }
        });
    }

    private void saveFlow(boolean saveAs, final AsyncCallback<WBFlowDescription> callback) {
        if ((_wbFlow.getName().length() == 0 || saveAs)) {
            SaveFlowDialog saveDialog = new SaveFlowDialog(new SaveFlowListener() {
                @Override
				public void onSave(final String name, final String description, final String rights, final String baseURI, final String tags) {
                    // Check whether the flow exists on the server
                    String flowURI = baseURI + name.toLowerCase().replaceAll(" |\t|/|'", "-") + "/";
                    if (_repositoryState.getFlow(flowURI) != null)
                        Application.showMessage(
                                "Overwrite",
                                "A flow with the name '" + name + "' already exists on the server.<br/><b>Do you want to overwrite it?</b>",
                                MessageBox.WARNING, MessageBox.YESNO, new PromptCallback() {
                                    @Override
									public void execute(String btnID, String text) {
                                        if (btnID.equalsIgnoreCase("yes"))
                                            saveFlow(name, description, rights, baseURI, tags, callback);
                                    }
                                });
                    else
                        saveFlow(name, description, rights, baseURI, tags, callback);
                }

                private void saveFlow(String name, String description, String rights, String baseURI, String tags, final AsyncCallback<WBFlowDescription> callback) {
                    _wbFlow.setName(name);
                    _wbFlow.setDescription(description);
                    _wbFlow.setRights(rights);
                    _wbFlow.setBaseURI(baseURI);
                    _wbFlow.getTags().getTags().clear();
                    for (String tag : tags.split(",")) {
                        tag = tag.trim();
                        if (tag.length() > 0)
                            _wbFlow.getTags().getTags().add(tag);
                    }

                    doSaveFlow(callback);
                }
            });

            if (_wbFlow.getName().length() > 0)
                saveDialog.setFormValues(_wbFlow.getName(), _wbFlow.getDescription(), _wbFlow.getRights(),
                        _wbFlow.getBaseURI(), _wbFlow.getTags().toString());

            saveDialog.show(/** button.getElement() **/);

            return;
        }

        doSaveFlow(callback);
    }

    private void doSaveFlow(AsyncCallback<WBFlowDescription> callback) {
        for (WorkspaceActionListener listener : _actionListeners)
            listener.onFlowSave(_wbFlow, callback);
    }

    private void runFlow() {
        if (isDirty()) {
            String message;
            String iconCls;
            MessageBox.Button buttons;
            PromptCallback callback;

            if (_wbFlow.getName().length() == 0) {
                message = "You <u>must</u> save your flow before you can execute it. Would you like to do that now?";
                iconCls = MessageBox.INFO;
                buttons = MessageBox.YESNO;
                callback = new PromptCallback() {
                    @Override
					public void execute(String btnID, String text) {
                        if (btnID.equalsIgnoreCase("yes")) {
                            saveFlow(false, new AsyncCallback<WBFlowDescription>() {
                                @Override
								public void onSuccess(WBFlowDescription result) {
                                    doRunFlow();
                                }

                                @Override
								public void onFailure(Throwable caught) {
                                    Log.warn("The flow could not be saved - aborting run request!", caught);
                                }
                            });
                        }
                    }
                };
            } else {
                message = "You have unsaved changes. Would you like to save them before proceeding?<br/>" +
                "<i>Note: Choosing 'No' will execute the last saved version of the flow.</i>";
                iconCls = MessageBox.QUESTION;
                buttons = MessageBox.YESNOCANCEL;
                callback = new PromptCallback() {
                    @Override
					public void execute(String btnID, String text) {
                        if (btnID.equalsIgnoreCase("yes")) {
                            saveFlow(false, new AsyncCallback<WBFlowDescription>() {
                                @Override
								public void onSuccess(WBFlowDescription result) {
                                    doRunFlow();
                                }

                                @Override
								public void onFailure(Throwable caught) {
                                    Log.warn("The flow could not be saved - aborting run request!", caught);
                                }
                            });
                        }
                        else
                            if (btnID.equalsIgnoreCase("no"))
                                doRunFlow();
                    }
                };
            }

            promptSaveFlow(getTitle(), message, iconCls, buttons, callback);

            return;
        }
        else
            doRunFlow();
    }

    private void doRunFlow() {
        for (WorkspaceActionListener listener : _actionListeners)
            listener.onFlowRun(WorkspaceTab.this);
    }

    private WBFlowDescription createNewFlowDescription(String name) {
        return new WBFlowDescription(name, WBFlowDescription.BASE_URL);
    }

    @Override
    protected void beforeDestroy() {
        Log.debug("Destroying tab '" + getTitle() + "'");
        _actionListeners.clear();
        _clipboard.removeListener(this);

        super.beforeDestroy();
    }

    public WBFlowDescription getFlowDescription() {
        return _wbFlow;
    }

    void setParent(WorkspacePanel parent) {
        _parent = parent;
    }

    private String getUntitledName() {
        String name = "Untitled";
        if (TAB_COUNTER > 1)
            name += " " + TAB_COUNTER;

        TAB_COUNTER++;

        return name;
    }

    @Override
	public void onCopyToClipboard() {
        Log.debug("onCopyToClipboard: " + this.getTitle() + ":  components=" +
                _clipboard.getComponents().size() + "  connectors=" + _clipboard.getConnectors().size());
        _btnPaste.enable();
    }

    @Override
	public void onClipboardReset() {
        Log.debug("onCopyToClipboard: " + this.getTitle() + ": Clipboard empty");
        _btnPaste.disable();
    }

    public boolean isDirty() {
        return _dirty;
    }

    public void setDirty() {
        if (isDirty()) return;

        _btnExport.disable();
        _dirty = true;

        setTitle("*" + getTitle());

        for (WorkspaceActionListener listener : _actionListeners)
            listener.onFlowModified();
    }

    public void clearDirty() {
        if (!isDirty()) return;

        _btnExport.enable();
        _dirty = false;

        setTitle(getTitle().substring(1));
    }

    public boolean isClosing() {
        return _isClosing;
    }

    public void close() {
        if (!_isClosing) {
            _isClosing = true;
            _parent.removeTab(this);
        }
    }

    boolean shouldClose() {
        if (_isClosing || !isDirty()) {
            // unselect any selected components
            // (causes the details panel to clear if any components were previously selected)
            clearSelection();

            return true;
        }

        final MessageBox.PromptCallback callback = new MessageBox.PromptCallback() {
            @Override
			public void execute(String btnID, String text) {
                if (btnID.equalsIgnoreCase("yes") || btnID.equalsIgnoreCase("no")) {
                    if (btnID.equalsIgnoreCase("yes"))
                        _btnSave.fireEvent("click");

                    _isClosing = true;
                    _parent.removeTab(WorkspaceTab.this);
                }
            }
        };

        promptSaveFlow(getTitle(), "You have unsaved changes. Would you like to save your flow?",
                MessageBox.WARNING, MessageBox.YESNOCANCEL, callback);

        return false;
    }

    private void promptSaveFlow(final String title, final String message, final String iconCls,
            final MessageBox.Button buttons, final MessageBox.PromptCallback callback) {

        MessageBox.show(new MessageBoxConfig() {
            {
                if (_parent.getActiveTab() != WorkspaceTab.this)
                    _parent.setActiveTab(WorkspaceTab.this);

                setTitle(title);
                setMsg(message);
                setIconCls(iconCls);
                setButtons(buttons);
                setCallback(callback);
            }
        });
    }

    public boolean isRunning() {
        return _isRunning;
    }

    public FlowOutputPanel getFlowOutputPanel() {
        return _outputPanel;
    }

    public void addListener(WorkspaceActionListener listener) {
        _actionListeners.add(listener);
    }

    public void addComponent(Component component, boolean setDirty) {
        WBExecutableComponentInstanceDescription compInstance = component.getInstanceDescription();

        Log.debug("Adding " + compInstance.getName() + " to flow " + _wbFlow.getFlowURI());
        _wbFlow.addExecutableComponentInstance(compInstance);

        addComponentToCanvas(component, setDirty);
    }

    private void addComponentToCanvas(final Component component, boolean setDirty) {
        int[] posXY = getComponentXY(component);

        final int x = posXY[0];
        final int y = posXY[1];

        _componentMap.put(component.getInstanceDescription(), component);

        component.addListener(new ComponentActionListenerAdapter() {
            private int[] _startDragPosition = null;
            private int[] _dragPosition = null;
            private boolean _dragOccurred = false;

            @Override
            public void onClicked(Component component, EventObject e) {
                if (_dragOccurred) {
                    // disregard the first click event occurring after a drag event
                    _dragOccurred = false;
                    return;
                }

//                Log.debug("onClicked: " + component.getName() + "  startDrag=" + _startDragPosition);

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentClicked(component);

                if (e.isCtrlKey()) {
                    if (component.isSelected())
                        component.unselect();
                    else
                        component.select();
                } else {
                    clearSelection();
                    component.select();
                }
            }

            @Override
            public void onSelected(Component component) {
                component.getEl().scrollIntoView(WorkspaceTab.this.getBody().getDOM(), true);

                _selectedComponents.add(component);
                _btnRemoveComponent.enable();
                _btnCopy.enable();

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentSelected(component);
            }

            @Override
            public void onUnselected(Component component) {
                _selectedComponents.remove(component);

                if (_selectedComponents.isEmpty()) {
                    _btnRemoveComponent.disable();
                    _btnCopy.disable();
                }

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentUnselected(component);
            }

            @Override
            public void onStartDrag(Component component, int x, int y) {
                _dragOccurred = true;

                if (!component.isSelected()) {
                    clearSelection();
                    component.select();
                }

                for (Component selectedComponent : getSelectedComponents())
                    selectedComponent.setDrag(true);

                _startDragPosition = component.getPosition(true);
                _dragPosition = component.getPosition(true);

//                int[] xy = _startDragPosition;
//                Log.debug("onStartDrag: (" + xy[0] + "," + xy[1] + ")");
            }

            @Override
            public void onDragging(Component component, int x, int y) {
                int xy[] = component.getPosition(true);
                int[] delta = new int[] { xy[0] - _dragPosition[0], xy[1] - _dragPosition[1] };
                _dragPosition = xy;

//                Log.debug("onDragging: delta=(" + delta[0] + "," + delta[1] + ")");

                for (Component selectedComponent : getSelectedComponents()) {
                    if (selectedComponent != component) {
                        xy = selectedComponent.getPosition(true);
                        selectedComponent.setPosition(xy[0] + delta[0], xy[1] + delta[1]);
                    }
                    selectedComponent.updateConnections();
                }

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentDragging(component);
            }

            @Override
            public void onDragged(Component component, int x, int y) {
                int xy[] = component.getPosition(true);
//                Log.debug("onDragged: (" + xy[0] + "," + xy[1] + ")");

                for (Component selectedComponent : getSelectedComponents())
                    selectedComponent.setDrag(false);

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentDragged(component);

                if (_startDragPosition != null && (_startDragPosition[0] != xy[0] || _startDragPosition[1] != xy[1])) {
                    for (Component selectedComponent : getSelectedComponents()) {
                        xy = selectedComponent.getPosition(true);
                        WBExecutableComponentInstanceDescription compInstance = selectedComponent.getInstanceDescription();
                        compInstance.getProperties().add(COMP_LEFT_KEY, Integer.toString(xy[0]));
                        compInstance.getProperties().add(COMP_TOP_KEY, Integer.toString(xy[1]));
                    }

                    setDirty();
                }

                _startDragPosition = null;
                _dragPosition = null;
            }

            @Override
            public void onPortSelected(ComponentPort port) {
                if (_selectedPort == null) {
                    _selectedPort = port;

                    //TODO: show message to select target port
                    //TODO: when performing flow consistency check, also check whether inputs and outputs
                    // are properly connected (no input-to-input or output-to-output)...
                } else {
                    if (_selectedPort.getPortType() == port.getPortType()) {
                        String portType = _selectedPort.getPortType().toString().toLowerCase();
                        MessageBox.show(new ErrorMsgBoxConfig("Cannot connect two " +
                                portType + " ports to each other!", port));
                        port.unselect();
                        return;
                    }

                    final ComponentPort srcPort =
                        _selectedPort.getPortType() == PortType.OUTPUT ? _selectedPort : port;
                    final ComponentPort dstPort =
                        port.getPortType() == PortType.INPUT ? port : _selectedPort;

                    srcPort.disconnect();  // cannot have two connections leaving an output port,
                                           // but it's ok to have two connections coming into an input port

                    String baseURI = _wbFlow.getNormalizedFlowURI() + "connector/";

                    // Create a unique URI
                    String resURI;
                    do {
                        resURI = baseURI + _connectorCount++;
                    }
                    while (connectorIsNotUnique(resURI));

                    WBConnectorDescription connectorDesc = new WBConnectorDescription();
                    connectorDesc.setConnector(resURI);
                    connectorDesc.setSourceInstance(srcPort.getComponent().getInstanceDescription().getExecutableComponentInstance());
                    connectorDesc.setSourceInstanceDataPort(srcPort.getDataPortDescription().getResourceURI());
                    connectorDesc.setTargetInstance(dstPort.getComponent().getInstanceDescription().getExecutableComponentInstance());
                    connectorDesc.setTargetInstanceDataPort(dstPort.getDataPortDescription().getResourceURI());

                    Log.debug("Adding connector " + resURI + " between " + srcPort.getComponent().getName() +
                            " and " + dstPort.getComponent().getName());

                    _wbFlow.getConnectorDescriptions().add(connectorDesc);

                    AbstractConnection connection = createConnection(srcPort, dstPort);
                    connection.addTo(WorkspaceTab.this);
                    _connectionMap.put(connection, connectorDesc);

                    _selectedPort.unselect();
                    port.unselect();

                    for (WorkspaceActionListener listener : _actionListeners)
                        listener.onConnectionAdded(srcPort, dstPort);

                    setDirty();
                }
            }

            @Override
            public void onPortUnselected(ComponentPort port) {
                if (_selectedPort == port)
                    _selectedPort = null;
            }

            @Override
            public void onRenamed(Component component, String oldName, String newName) {
                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentRenamed(component, oldName, newName);

                setDirty();
            }
        });

        Menu contextMenu = new Menu();

        Menu mnuDisconnect = new Menu();

        final Item btnDisconnectAll = new Item("All");
        btnDisconnectAll.addListener(new BaseItemListenerAdapter() {
            @Override
            public void onClick(BaseItem item, EventObject e) {
                component.disconnect();
            }
        });

        final Item btnDisconnectInputs = new Item("Inputs");
        btnDisconnectInputs.addListener(new BaseItemListenerAdapter() {
            @Override
            public void onClick(BaseItem item, EventObject e) {
                component.disconnectInputs();
            }
        });

        final Item btnDisconnectOutputs = new Item("Outputs");
        btnDisconnectOutputs.addListener(new BaseItemListenerAdapter() {
            @Override
            public void onClick(BaseItem item, EventObject e) {
                component.disconnectOutputs();
            }
        });

        mnuDisconnect.addItem(btnDisconnectInputs);
        mnuDisconnect.addItem(btnDisconnectOutputs);
        mnuDisconnect.addSeparator();
        mnuDisconnect.addItem(btnDisconnectAll);

        mnuDisconnect.addListener(new MenuListenerAdapter() {
            @Override
            public void doBeforeShow(Menu menu) {
                if (component.hasInputsConnected())
                    btnDisconnectInputs.enable();
                else
                    btnDisconnectInputs.disable();

                if (component.hasOutputsConnected())
                    btnDisconnectOutputs.enable();
                else
                    btnDisconnectOutputs.disable();
            }
        });

        final MenuItem btnDisconnect = new MenuItem("Disconnect", mnuDisconnect);

        final Item btnRemove = new Item("Remove");
        btnRemove.addListener(new BaseItemListenerAdapter() {
            @Override
            public void onClick(BaseItem item, EventObject e) {
                removeComponent(component);
            }
        });

        contextMenu.addItem(btnDisconnect);
        contextMenu.addSeparator();
        contextMenu.addItem(btnRemove);

        contextMenu.addListener(new MenuListenerAdapter() {
            @Override
            public void doBeforeShow(Menu menu) {
                component.select();

                if (component.isConnected())
                    btnDisconnect.enable();
                else
                    btnDisconnect.disable();
            }
        });

        component.setContextMenu(contextMenu);

        add(component, new AbsoluteLayoutData(x, y));

        for (WorkspaceActionListener listener : _actionListeners)
            listener.onComponentAdded(component, x, y);

        if (setDirty)
            setDirty();
    }

    private boolean connectorIsNotUnique(String resURI) {
        for (WBConnectorDescription connector : _connectionMap.values())
            if (connector.getConnector().equalsIgnoreCase(resURI))
                return true;

        return false;
    }

    private int[] getRandomCompPosition() {
        return new int[] { Random.nextInt(500), Random.nextInt(500) };
    }

    private int[] getComponentXY(Component component) {
        WBExecutableComponentInstanceDescription compInstance = component.getInstanceDescription();
        WBPropertiesDescription compProps = compInstance.getProperties();
        if (compProps.getKeys().contains(COMP_LEFT_KEY) && compProps.getKeys().contains(COMP_TOP_KEY)) {
            return new int[] {
                    (int) Double.parseDouble(compProps.getValue(COMP_LEFT_KEY)),
                    (int) Double.parseDouble(compProps.getValue(COMP_TOP_KEY))
            };
        } else {
            int[] xy = getRandomCompPosition();
            compProps.add(COMP_LEFT_KEY, Integer.toString(xy[0]));
            compProps.add(COMP_TOP_KEY, Integer.toString(xy[1]));

            return xy;
        }
    }

    private WBExecutableComponentInstanceDescription createComponentInstance(WBExecutableComponentDescription compDesc) {
        WBExecutableComponentInstanceDescription instance = new WBExecutableComponentInstanceDescription();
        instance.setExecutableComponent(compDesc.getResourceURI());
        String baseURI = _wbFlow.getNormalizedFlowURI() + "instance/" + compDesc.getName().toLowerCase().replaceAll(" |\t", "-") + "/";

        // Create a unique URI
        String resURI;
        do {
            resURI = baseURI + _componentCount++;
        }
        while (componentInstanceIsNotUnique(resURI));

        instance.setExecutableComponentInstance(resURI);
        instance.setDescription(compDesc.getDescription());
        instance.setName(compDesc.getName());

        Log.debug("Creating " + instance.getName() + " with URI: " + resURI);

        // copy all properties and default values from the component descriptor
        WBPropertiesDescriptionDefinition propsDef = compDesc.getProperties();
        WBPropertiesDescription props = new WBPropertiesDescription();
        for (Entry<String, String> entry : propsDef.getValueMap().entrySet())
            props.add(entry.getKey(), entry.getValue());  // TODO debug this to make sure it does what it's supposed to

        instance.setProperties(props);

        return instance;
    }

    private boolean componentInstanceIsNotUnique(String resURI) {
        for (WBExecutableComponentInstanceDescription ecid : _componentMap.keySet())
            if (ecid.getExecutableComponentInstance().equalsIgnoreCase(resURI))
                return true;

        return false;
    }

    private AbstractConnection createConnection(final ComponentPort srcPort, final ComponentPort dstPort) {
        AbstractConnection connection =
            new RectilinearTwoEndedConnection(srcPort.getConnector(), dstPort.getConnector());

        connection.addListener(new ConnectionActionListener() {
            @Override
			public void connectionRemoved(AbstractConnection connection) {

                WBConnectorDescription connectorDesc = _connectionMap.get(connection);
                Log.debug("Removing connection " + connectorDesc.getConnector());
                boolean success = _wbFlow.getConnectorDescriptions().remove(connectorDesc);
                if (!success)
                    Log.error("The connector was not found in the flow description!");

                Object result = _connectionMap.remove(connection);
                // for DEBUG purposes
                if (result == null)
                    Log.error("Could not find the specified connection in the connection map.");

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onConnectionRemoved(srcPort, dstPort);

                setDirty();
            }
        });

        return connection;
    }

    private AbstractConnection createConnection(final WBConnectorDescription connector) {
        final String srcCompInstanceURI = connector.getSourceInstance();
        final String dstCompInstanceURI = connector.getTargetInstance();

        Component srcComponent = getComponentForURI(srcCompInstanceURI);
        Component dstComponent = getComponentForURI(dstCompInstanceURI);

        if (srcComponent == null || dstComponent == null) {
            String msg = "";
            if (srcComponent == null) msg = "source";
            if (dstComponent == null) msg += (msg.length() > 0) ? " and target" : "target";
            Log.error("Could not retrieve the " + msg + " component(s) for connector: " +
                    connector.getConnector() + " - ignoring");
            Log.error("Source: " + srcCompInstanceURI + "  Target: " + dstCompInstanceURI);

            final String msgConsole = msg;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					_outputPanel.print("Could not retrieve the " + msgConsole + " component(s) for connector: " +
                            connector.getConnector() + " - ignoring; Source: " + srcCompInstanceURI + "  Target: " + dstCompInstanceURI + "\n");
				}
			});

            return null;
        }

        final String srcDataPort = connector.getSourceInstanceDataPort();
        final String dstDataPort = connector.getTargetInstanceDataPort();

        ComponentPort srcPort = srcComponent.getOutputPort(srcDataPort);
        ComponentPort dstPort = dstComponent.getInputPort(dstDataPort);

        if (srcPort == null || dstPort == null) {
            String msg = "";
            if (srcPort == null) msg = "source";
            if (dstPort == null) msg += (msg.length() > 0) ? " and target" : "target";
            Log.error("Could not retrive the " + msg + " port(s) for the connector: " +
                    connector.getConnector() + " - ignoring");
            Log.error("Source: " + srcDataPort + "  Target: " + dstDataPort);

            final String msgConsole = msg;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
                    _outputPanel.print("Could not retrive the " + msgConsole + " port(s) for the connector: " +
                            connector.getConnector() + " - ignoring; Source: " + srcDataPort + "  Target: " + dstDataPort + "\n");
                }
            });

            return null;
        }

        AbstractConnection connection = createConnection(srcPort, dstPort);
        _connectionMap.put(connection, connector);

        return connection;
    }

    private Component getComponentForURI(String compInstanceURI) {
        for (Component component : _componentMap.values())
            if (component.getInstanceDescription().getExecutableComponentInstance().equalsIgnoreCase(compInstanceURI))
                return component;

        return null;
    }

    public void removeComponent(final Component component) {
        if (getSelectedComponents().contains(component))
            component.unselect();

        component.disconnect();
        remove(component);

        if (_selectedPort != null && _selectedPort.getComponent() == component)
            _selectedPort = null;

        WBExecutableComponentInstanceDescription compInstance = component.getInstanceDescription();
        String instanceURI = compInstance.getExecutableComponentInstance();
        Log.debug("Removing component " + instanceURI);
        Object result = _componentMap.remove(compInstance);
        // for DEBUG purposes
        if (result == null)
            Log.error("Could not find " + instanceURI + " in the component map. Probably a bug!");

        boolean success = _wbFlow.removeExecutableComponentInstance(compInstance);
        if (!success)
            Log.error("Could not find the component " + component.getName() + " in the flow description");

        for (WorkspaceActionListener listener : _actionListeners)
            listener.onComponentRemoved(component);

        setDirty();
    }

    private final Set<Component> _selectedComponents = new HashSet<Component>();

    public Set<Component> getSelectedComponents() {
        return _selectedComponents;
    }

    public void clearSelection() {
        for (Component selectedComponent : _selectedComponents)
            selectedComponent.unselect();
    }

    public void checkValid() {
        for (final Component component : _componentMap.values()) {
            String compURI = component.getInstanceDescription().getExecutableComponent();
            if (_repositoryState.getComponent(compURI) == null) {
                // missing component
                _outputPanel.print("Missing component: The component '" + component.getName() + "' needed by this flow " +
                        "has been removed from the repository! It will also be removed from this flow...\n");
                removeComponent(component);
            }
        }
        // check whether components have been removed
        // check whether connections make sense
        // check whether ports have been added/removed - update UI?
    }

    private WBWebUIInfo _webUIInfo = null;
    private final ToolbarButton _btnRunFlow = new ToolbarButton("Run flow");
    private final ToolbarButton _btnStopFlow = new ToolbarButton("Stop flow");
    private final ToolbarButton _btnKillFlow = new ToolbarButton("Kill flow");
    private final ToolbarButton _btnExport;

    public void openWebUI() {
        if (_webUIInfo == null) {
            Application.showMessage("Show WebUI", "This flow is not currently executing!", MessageBox.WARNING);
            return;
        }

        Window.open(_webUIInfo.getWebUIUrl(), String.valueOf(_wbFlow.getFlowURI().hashCode()),
                "width=800,height=600,resizable=yes,scrollbars=yes,status=yes,centerscreen=yes");
    }

    public WBWebUIInfo getWebUIInfo() {
        return _webUIInfo;
    }

    public void setWebUIInfo(WBWebUIInfo webUI) {
        if (webUI != null && webUI.getPort() > 0)
            _btnStopFlow.enable();
        else
            _btnStopFlow.disable();

        _webUIInfo = webUI;
    }

    public void setFlowDescription(WBFlowDescription uploadedFlow) {
        _wbFlow = uploadedFlow;
    }
}
