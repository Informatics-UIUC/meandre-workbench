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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasr.meandre.workbench.client.beans.execution.WBWebUIInfo;
import org.seasr.meandre.workbench.client.beans.repository.WBConnectorDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBPropertiesDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBPropertiesDescriptionDefinition;
import org.seasr.meandre.workbench.client.listeners.ComponentActionListenerAdapter;
import org.seasr.meandre.workbench.client.listeners.SaveFlowListener;
import org.seasr.meandre.workbench.client.listeners.WorkspaceActionListener;
import org.seasr.meandre.workbench.client.widgets.Component.ComponentPort;
import org.seasr.meandre.workbench.client.widgets.Component.PortType;

import pl.balon.gwt.diagrams.client.connection.AbstractConnection;
import pl.balon.gwt.diagrams.client.connection.ConnectionActionListener;
import pl.balon.gwt.diagrams.client.connection.RectilinearTwoEndedConnection;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Margins;
import com.gwtext.client.data.Record;
import com.gwtext.client.dd.DragData;
import com.gwtext.client.dd.DragSource;
import com.gwtext.client.dd.DropTarget;
import com.gwtext.client.dd.DropTargetConfig;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.MessageBox.PromptCallback;
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
public class WorkspaceTab extends Panel {
    private final static String COMP_TOP_KEY = "wb_top_pix_pos";
    private final static String COMP_LEFT_KEY = "wb_left_pix_pos";

    private static int TAB_COUNTER = 1;

    private WBFlowDescription _wbFlow;
    private final Map<String, Component> _componentMap;
    private final Map<AbstractConnection, WBConnectorDescription> _connectionMap;
    private final Set<WorkspaceActionListener> _actionListeners = new HashSet<WorkspaceActionListener>();
    private final FlowOutputPanel _outputPanel = new FlowOutputPanel();
    private Component _selectedComponent = null;
    private ComponentPort _selectedPort = null;
    private WorkspacePanel _parent = null;
    private boolean _dirty = false;
    private boolean _isClosing = false;
    private boolean _isRunning = false;
    private int _componentCount = 0;
    private int _connectorCount = 0;

    private final ToolbarButton _btnSave = new ToolbarButton("Save");
    private final ToolbarButton _btnSaveAs = new ToolbarButton("Save As");
    private final ToolbarButton _btnDeleteComponent = new ToolbarButton("Delete");


    public WorkspaceTab(final WBFlowDescription flow) {
        if (_outputPanel.getUrl().length() > 0)
            Log.warn("Output panel url not empty! " + _outputPanel.getUrl());

        _wbFlow = (flow == null) ? createNewFlowDescription() : flow.clone();
        _componentMap = new HashMap<String, Component>();
        _connectionMap = new HashMap<AbstractConnection, WBConnectorDescription>();

        setTitle((flow != null) ? _wbFlow.getName() : getUntitledName());
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

        _btnDeleteComponent.setIconCls("icon-component-delete");
        _btnDeleteComponent.disable();
        _btnDeleteComponent.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                if (_selectedComponent != null)
                    removeComponent(_selectedComponent);
            }
        });

        final ToolbarButton btnRunFlow = new ToolbarButton("Run flow");
        btnRunFlow.setIconCls("icon-flow-run");
        btnRunFlow.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                runFlow();
            }
        });

        final ToolbarButton btnStopFlow = new ToolbarButton("Stop flow");
        btnStopFlow.setIconCls("icon-flow-stop");
        btnStopFlow.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onFlowStop(_wbFlow);
                }
        });

        toolbar.addButton(_btnSave);
        toolbar.addButton(_btnSaveAs);
        toolbar.addSeparator();
        toolbar.addButton(_btnDeleteComponent);
        toolbar.addFill();
        toolbar.addButton(btnRunFlow);
        toolbar.addButton(btnStopFlow);

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

                xy[0] -= pos[0];
                xy[1] -= pos[1];

                if (data instanceof GridDragData) {
                    GridDragData gridDragData = (GridDragData) data;
                    Record[] records = gridDragData.getSelections();

                    for (Record record : records) {
                        WBExecutableComponentDescription compDesc =
                            (WBExecutableComponentDescription) record.getAsObject("wbComponent");

                        final Component component = new Component(createComponentInstance(compDesc));
                        component.doOnRender(new Function() {
                            public void execute() {
                                component.select();
                            }
                        });

                        addComponent(component, xy, true);
                    }

                    doLayout();
                }

                return true;
            }
        };
    }

    private void loadFlow(final WBFlowDescription flow) {
        for (WBExecutableComponentInstanceDescription compInstance : flow.getExecutableComponentInstances()) {
            WBExecutableComponentDescription compDesc = compInstance.getExecutableComponentDescription();
            if (compDesc == null)
                Log.error("Could not find the component: " + compInstance.getExecutableComponent());

            Component component = new Component(compInstance);
            addComponent(component, false);
        }

        for (WBConnectorDescription connector : flow.getConnectorDescriptions())
            createConnection(connector);

        // Perform lazy add of connections since they don't get rendered otherwise
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                for (AbstractConnection connection : _connectionMap.keySet())
                    connection.addTo(WorkspaceTab.this);
            }
        });
    }

    private void saveFlow(boolean saveAs, final AsyncCallback<WBFlowDescription> callback) {
        if (_wbFlow.getName().length() == 0 || saveAs) {
            SaveFlowDialog saveDialog = new SaveFlowDialog(new SaveFlowListener() {
                public void onSave(String name, String description, String rights, String baseURI, String tags) {
                    _wbFlow.setName(name);
                    _wbFlow.setDescription(description);
                    _wbFlow.setRights(rights);
                    _wbFlow.setDesiredBaseURI(baseURI);
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
                saveDialog.setFormValues(null, _wbFlow.getDescription(), _wbFlow.getRights(),
                        _wbFlow.getDesiredBaseURI(), _wbFlow.getTags().toString());

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

            if (_wbFlow.getDesiredBaseURI() == null) {
                message = "You <u>must</u> save your flow before you can execute it. Would you like to do that now?";
                iconCls = MessageBox.INFO;
                buttons = MessageBox.YESNO;
                callback = new PromptCallback() {
                    public void execute(String btnID, String text) {
                        if (btnID.equalsIgnoreCase("yes")) {
                            saveFlow(false, new AsyncCallback<WBFlowDescription>() {
                                public void onSuccess(WBFlowDescription result) {
                                    doRunFlow();
                                }

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
                    public void execute(String btnID, String text) {
                        if (btnID.equalsIgnoreCase("yes")) {
                            saveFlow(false, new AsyncCallback<WBFlowDescription>() {
                                public void onSuccess(WBFlowDescription result) {
                                    doRunFlow();
                                }

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

    private WBFlowDescription createNewFlowDescription() {
        WBFlowDescription flowDesc = new WBFlowDescription();
        flowDesc.setFlowURI(WBFlowDescription.BASE_URL + TAB_COUNTER + "/");

        return flowDesc;
    }

    @Override
    protected void beforeDestroy() {
        Log.debug("Destroying tab '" + getTitle() + "'");
        _actionListeners.clear();

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

    public boolean isDirty() {
        return _dirty;
    }

    public void setDirty() {
        if (isDirty()) return;

        _dirty = true;

        setTitle("*" + getTitle());

        for (WorkspaceActionListener listener : _actionListeners)
            listener.onFlowModified();
    }

    public void clearDirty() {
        if (!isDirty()) return;

        _dirty = false;

        setTitle(getTitle().substring(1));
    }

    public boolean isClosing() {
        return _isClosing;
    }

    public boolean close() {
        Log.debug("close called");
        if (_isClosing || !isDirty()) {
            if (!_isClosing) {
                _isClosing = true;
                _parent.removeTab(this);
            }

            // unselect any selected components
            // (causes the details panel to clear if any components were previously selected)
            if (_selectedComponent != null)
                _selectedComponent.unselect();

            return true;
        }

        final MessageBox.PromptCallback callback = new MessageBox.PromptCallback() {
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
        WBPropertiesDescription compProps = compInstance.getProperties();
        if (compProps.getKeys().contains(COMP_LEFT_KEY) && compProps.getKeys().contains(COMP_TOP_KEY)) {
            int[] xy = new int[] {
                    Integer.parseInt(compProps.getValue(COMP_LEFT_KEY)),
                    Integer.parseInt(compProps.getValue(COMP_TOP_KEY))
            };

            addComponent(component, xy, setDirty);
        }
        else
            addComponent(component, getRandomCompPosition(), setDirty);
    }

    public void addComponent(final Component component, int[] posXY, boolean setDirty) {
        final int x = posXY[0];
        final int y = posXY[1];

        _componentMap.put(component.getInstanceDescription().getExecutableComponentInstance(), component);

        component.addListener(new ComponentActionListenerAdapter() {
            private int[] _compPosition = null;

            @Override
            public void onClicked(Component component, EventObject e) {
                if (component.isSelected()) {
                    if (e.isCtrlKey())
                        component.unselect();
                }
                else
                    component.select();

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentClicked(component);
            }

            @Override
            public void onSelected(Component component) {
                component.getEl().scrollIntoView(WorkspaceTab.this.getBody().getDOM(), true);

                clearSelection();
                _selectedComponent = component;
                _btnDeleteComponent.enable();

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentSelected(component);
            }

            @Override
            public void onUnselected(Component component) {
                _selectedComponent = null;
                _btnDeleteComponent.disable();

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentUnselected(component);
            }

            @Override
            public void onDragging(Component component) {
                if (_compPosition == null)
                    _compPosition = getComponentRelativeLocation(component);

                component.updateConnections();

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentDragging(component);
            }

            @Override
            public void onDragged(Component component) {
                int xy[] = getComponentRelativeLocation(component);

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onComponentDragged(component);

                if (_compPosition != null && (_compPosition[0] != xy[0] || _compPosition[1] != xy[1])) {
                    WBExecutableComponentInstanceDescription compInstance = component.getInstanceDescription();
                    compInstance.getProperties().add(COMP_LEFT_KEY, Integer.toString(xy[0]));
                    compInstance.getProperties().add(COMP_TOP_KEY, Integer.toString(xy[1]));

                    setDirty();
                }

                _compPosition = null;
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

                    _selectedPort.disconnect();
                    port.disconnect();

                    final ComponentPort srcPort =
                        _selectedPort.getPortType() == PortType.OUTPUT ? _selectedPort : port;
                    final ComponentPort dstPort =
                        port.getPortType() == PortType.INPUT ? port : _selectedPort;

                    WBConnectorDescription connectorDesc = new WBConnectorDescription();
                    String resURI = _wbFlow.getFlowURI();
                    if (!resURI.endsWith("/")) resURI += "/";
                    resURI += "connector/" + _connectorCount++;
                    connectorDesc.setConnector(resURI);
                    connectorDesc.setSourceInstance(srcPort.getComponent().getInstanceDescription().getExecutableComponentInstance());
                    connectorDesc.setSourceInstanceDataPort(srcPort.getDataPortDescription().getResourceURI());
                    connectorDesc.setTargetInstance(dstPort.getComponent().getInstanceDescription().getExecutableComponentInstance());
                    connectorDesc.setTargetInstanceDataPort(dstPort.getDataPortDescription().getResourceURI());

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

        WBExecutableComponentInstanceDescription compInstance = component.getInstanceDescription();
        compInstance.getProperties().add(COMP_LEFT_KEY, Integer.toString(x));
        compInstance.getProperties().add(COMP_TOP_KEY, Integer.toString(y));

        _wbFlow.addExecutableComponentInstance(compInstance);

        if (setDirty)
            setDirty();
    }

    protected int[] getComponentRelativeLocation(Component component) {
        int[] absPos = component.getPosition();
        Margins margins = component.getEl().getMargins();
        int left = getRelativeLeft(absPos[0] - margins.getLeft());
        int top = getRelativeTop(absPos[1] - margins.getTop());

        return new int[] { left, top };
    }

    private int getRelativeLeft(int absoluteLeft) {
        return absoluteLeft - getBody().getLeft();
    }

    private int getRelativeTop(int absoluteTop) {
        return absoluteTop - getBody().getTop();
    }

    private int[] getRandomCompPosition() {
        // TODO change this
        return new int[] { 100, 100 };
    }

    private WBExecutableComponentInstanceDescription createComponentInstance(WBExecutableComponentDescription compDesc) {
        String resourceURI = compDesc.getResourceURI();

        WBExecutableComponentInstanceDescription instance = new WBExecutableComponentInstanceDescription();
        instance.setExecutableComponentDescription(compDesc);
        instance.setExecutableComponent(resourceURI);
        String resURI = _wbFlow.getFlowURI();
        if (!resURI.endsWith("/")) resURI += "/";
        resURI += "instance/" + compDesc.getName().toLowerCase().replaceAll(" |\t", "-") + "/" + _componentCount++;
        instance.setExecutableComponentInstance(resURI);
        instance.setDescription(compDesc.getDescription());
        instance.setName(compDesc.getName());

        WBPropertiesDescriptionDefinition propsDef = compDesc.getProperties();
        WBPropertiesDescription props = new WBPropertiesDescription();
        for (Entry<String, String> entry : propsDef.getValueMap().entrySet())
            props.add(entry.getKey(), entry.getValue());

        instance.setProperties(props);

        return instance;
    }

    private AbstractConnection createConnection(final ComponentPort srcPort, final ComponentPort dstPort) {
        AbstractConnection connection =
            new RectilinearTwoEndedConnection(srcPort.getConnector(), dstPort.getConnector());

        connection.addListener(new ConnectionActionListener() {
            public void connectionRemoved(AbstractConnection connection) {

                WBConnectorDescription connectorDesc = _connectionMap.get(connection);
                boolean success = _wbFlow.getConnectorDescriptions().remove(connectorDesc);
                if (!success)
                    Log.error("The connector was not found in the flow description!");

                Object result = _connectionMap.remove(connection);
                // for DEBUG purposes
                if (result == null)
                    Log.error("Could not find the specified connection in the connection map. Probably a bug!");

                for (WorkspaceActionListener listener : _actionListeners)
                    listener.onConnectionRemoved(srcPort, dstPort);

                setDirty();
            }
        });

        return connection;
    }

    private AbstractConnection createConnection(WBConnectorDescription connector) {
        String srcCompInstanceURI = connector.getSourceInstance();
        String dstCompInstanceURI = connector.getTargetInstance();

        Component srcComponent = _componentMap.get(srcCompInstanceURI);
        Component dstComponent = _componentMap.get(dstCompInstanceURI);

        if (srcComponent == null || dstComponent == null)
            Log.error("Could not retrieve the source and/or target components for connector: " +
                    connector.getConnector() + ". Probably a bug!");

        String srcDataPort = connector.getSourceInstanceDataPort();
        String dstDataPort = connector.getTargetInstanceDataPort();

        ComponentPort srcPort = srcComponent.getOutputPort(srcDataPort);
        ComponentPort dstPort = dstComponent.getInputPort(dstDataPort);

        if (srcPort == null || dstPort == null)
            Log.error("Could not retrive the source and/or target ports for the connector: " +
                    connector.getConnector() + ". Probably a bug!");

        AbstractConnection connection = createConnection(srcPort, dstPort);
        _connectionMap.put(connection, connector);

        return connection;
    }

    public void removeComponent(final Component component) {
        if (component == _selectedComponent)
            _selectedComponent.unselect();

        component.disconnect();
        component.fadeOut(new Function() {
            public void execute() {
                remove(component);
            }
        });

        if (_selectedPort != null && _selectedPort.getComponent() == component)
            _selectedPort = null;

        String instanceURI = component.getInstanceDescription().getExecutableComponentInstance();
        Object result = _componentMap.remove(instanceURI);
        // for DEBUG purposes
        if (result == null)
            Log.error("Could not find " + instanceURI + " in the component map. Probably a bug!");

        boolean success = _wbFlow.removeExecutableComponentInstance(component.getInstanceDescription());
        if (!success)
            Log.error("Could not find the component " + component.getName() + " in the flow description");

        for (WorkspaceActionListener listener : _actionListeners)
            listener.onComponentRemoved(component);

        setDirty();
    }

    public Component getSelectedComponent() {
        return _selectedComponent;
    }

    public void clearSelection() {
        if (_selectedComponent != null)
            _selectedComponent.unselect();
    }

    public void refresh() {
       for (Component component : _componentMap.values())
           component.updateConnections();
    }

    private static int _webUICounter = 0;
    public void openWebUI(WBWebUIInfo webUIInfo) {
//        WebUI webUI = new WebUI(_wbFlow.getName(), webUIInfo);
//        webUI.show();
        String windowName = _wbFlow.getName().toLowerCase().replaceAll(" |\t", "_") + _webUICounter++;
        Window.open(webUIInfo.getWebUIUrl(), windowName,
                "resizable=yes,scrollbars=yes,status=yes,location=yes,chrome=yes,width=800,height=600,centerscreen=yes");
    }

    public void setFlowDescription(WBFlowDescription uploadedFlow) {
        _wbFlow = uploadedFlow;
    }
}
