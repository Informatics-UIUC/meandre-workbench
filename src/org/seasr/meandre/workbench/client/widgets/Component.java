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

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.seasr.meandre.workbench.client.beans.repository.WBDataPortDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
import org.seasr.meandre.workbench.client.listeners.ComponentActionListener;

import pl.balon.gwt.diagrams.client.connection.Connection;
import pl.balon.gwt.diagrams.client.connection.RectilinearTwoEndedConnection;
import pl.balon.gwt.diagrams.client.connection.TwoEndedConnection;
import pl.balon.gwt.diagrams.client.connector.Connector;
import pl.balon.gwt.diagrams.client.connector.Direction;
import pl.balon.gwt.diagrams.client.connector.UIObjectConnector;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.FxConfig;
import com.gwtext.client.dd.DD;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.TextFieldListenerAdapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;
import com.gwtext.client.widgets.menu.event.MenuListenerAdapter;

/**
 * @author Boris Capitanu
 *
 */
public class Component extends VerticalContainerPanel {

    private final WBExecutableComponentInstanceDescription _compInstance;
    private final Set<ComponentActionListener> _actionListeners = new HashSet<ComponentActionListener>();
    private final Map<String, ComponentPort> _inputs;
    private final Map<String, ComponentPort> _outputs;
    private final TextField _tfComponentName;
    private boolean _selected = false;
    private Component.ComponentPort _selectedPort = null;
    private Menu _contextMenu = null;
    private final DDOnTop _dragDrop;
    private String _tfComponentNameValue;
    private boolean _isBeingRenamed = false;
    private boolean _isBeingDragged = false;

    public Component(final WBExecutableComponentInstanceDescription compInstance, final WBExecutableComponentDescription compDesc) {
        _compInstance = compInstance;

        setCls("component");

        VerticalContainerPanel vpInputs = new VerticalContainerPanel();
        vpInputs.setCls("component-vp-ports");

        final Comparator<WBDataPortDescription> comparator = new Comparator<WBDataPortDescription>() {
            public int compare(WBDataPortDescription p1, WBDataPortDescription p2) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        };

        SortedSet<WBDataPortDescription> inputsSorted = new TreeSet<WBDataPortDescription>(comparator);
        Set<WBDataPortDescription> componentInputs = compDesc.getInputs();
        inputsSorted.addAll(componentInputs);

        _inputs = new HashMap<String, ComponentPort>(componentInputs.size());
        for (WBDataPortDescription inputPort : inputsSorted) {
            ComponentPort compInputPort = new ComponentPort(inputPort, PortType.INPUT);
            _inputs.put(inputPort.getIdentifier(), compInputPort);
            vpInputs.add(compInputPort);
        }

        VerticalContainerPanel vpOutputs = new VerticalContainerPanel();
        vpOutputs.setCls("component-vp-ports");

        SortedSet<WBDataPortDescription> outputsSorted = new TreeSet<WBDataPortDescription>(comparator);
        Set<WBDataPortDescription> componentOutputs = compDesc.getOutputs();
        outputsSorted.addAll(componentOutputs);

        _outputs = new HashMap<String, ComponentPort>(componentOutputs.size());
        for (WBDataPortDescription outputPort : outputsSorted) {
            ComponentPort compOutputPort = new ComponentPort(outputPort, PortType.OUTPUT);
            _outputs.put(outputPort.getIdentifier(), compOutputPort);
            vpOutputs.add(compOutputPort);
        }

        ContainerPanel compBox = new ContainerPanel() {
            {
                setCls("component-box");

                ContainerPanel topBand = new ContainerPanel();
                topBand.setCls("component-box-topband");
                add(topBand);

                Image imgIcon = new Image("images/gear.png");
                imgIcon.setStyleName("component-box-icon");
                add(imgIcon);

                if (!compDesc.getProperties().getKeys().isEmpty()) {
                    Image imgProps = new Image("images/component-props.png");
                    imgProps.setStyleName("component-box-prop");
                    add(imgProps);
                }

                doOnRender(new Function() {
                    public void execute() {
                        getEl().addListener("contextmenu", new EventCallback() {
                            public void execute(EventObject e) {
                                if (_contextMenu != null) {
                                    e.stopEvent();
                                    _contextMenu.showAt(e.getXY());
                                }
                            }
                        });
                    }
                });
            }
        };

        HorizontalPanel compPanel = new HorizontalPanel();
        compPanel.setSpacing(1);
        compPanel.setStyleName("component-box-with-io-ports");
        compPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
        compPanel.add(vpInputs);
        compPanel.add(compBox);
        compPanel.add(vpOutputs);

        HorizontalPanel hpLabel = new HorizontalPanel();
        hpLabel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        hpLabel.setWidth("100%");

        _tfComponentName = new TextField();
        _tfComponentName.setCls("gwt-Label");
        _tfComponentName.addClass("component-label");
        _tfComponentName.setValue(compInstance.getName());
        _tfComponentName.setReadOnly(true);
        _tfComponentName.setAllowBlank(false);
        _tfComponentName.setBlankText("Component name cannot be blank");
        //_tfComponentName.setInvalidText("Invalid component name");
        _tfComponentName.setFieldMsgTarget("qtip");
        _tfComponentName.setGrow(true);
//        _tfComponentName.setValidator(new Validator() {
//            public boolean validate(String value) throws ValidationException {
//                Log.info("validating value '" + value + "' length=" + value.length());
//                return value.length() > 0;
//            }
//        });
        _tfComponentName.addKeyPressListener(new EventCallback() {
            public void execute(EventObject e) {
                if (e.getKey() == EventObject.ENTER)
                    _tfComponentName.getEl().blur();
                else
                    if (e.getKey() == EventObject.ESC) {
                        _tfComponentName.setRawValue("");  // invalidate field
                        _tfComponentName.getEl().blur();
                    }
            }
        });
        _tfComponentName.addListener(new TextFieldListenerAdapter() {
            @Override
            public void onChange(Field field, Object newVal, Object oldVal) {
                if (field.isValid()) {
                    _compInstance.setName(_tfComponentName.getText());
                    for (ComponentActionListener listener : _actionListeners)
                        listener.onRenamed(Component.this, (String)oldVal, (String)newVal);
                }
            }

            @Override
            public void onAutoSize(Field field, int width) {
                updateConnections();
            }

            @Override
            public void onBlur(Field field) {
                if (!_isBeingRenamed) return;

                if (!field.isValid())
                    field.setValue(_tfComponentNameValue);

                _tfComponentName.setReadOnly(true);
                _tfComponentName.removeClass("component-label-edit");
                _isBeingRenamed = false;
                _dragDrop.unlock();
            }
        });
        _tfComponentName.doOnRender(new Function() {
            public void execute() {
                _tfComponentName.removeClass("x-form-text");

                _tfComponentName.getEl().addListener("click", new EventCallback() {
                    public void execute(EventObject e) {
                        if (!isSelected() || _isBeingRenamed) return;
                        e.stopPropagation();

                        _dragDrop.lock();
                        _tfComponentNameValue = _tfComponentName.getRawValue();
                        _tfComponentName.addClass("component-label-edit");
                        _tfComponentName.setReadOnly(false);
                        _tfComponentName.focus(true);
                        _isBeingRenamed = true;
                    }
                });
            }
        });

        hpLabel.add(_tfComponentName);

        add(compPanel);
        add(hpLabel);

        doOnRender(new Function() {
            public void execute() {
                getEl().addListener("click", new EventCallback() {
                    public void execute(EventObject e) {
                        raiseClick(e);
                    }
                });
            }
        });

        _dragDrop = new DDOnTop(this);
    }

    @Override
    protected void beforeDestroy() {
        Log.debug("Destroying component '" + getName() + "'");
        _actionListeners.clear();
        _inputs.clear();
        _outputs.clear();

        super.beforeDestroy();
    }

    public WBExecutableComponentInstanceDescription getInstanceDescription() {
        return _compInstance;
    }

    public String getName() {
        return _compInstance.getName();
    }

    public void setName(String name) {
        _compInstance.setName(name);
        _tfComponentName.setValue(name);
    }

    public String getDescription() {
        return _compInstance.getDescription();
    }

    public void setDescription(String description) {
        _compInstance.setDescription(description);
    }

    public Collection<ComponentPort> getInputPorts() {
        return _inputs.values();
    }

    public Collection<ComponentPort> getOutputPorts() {
        return _outputs.values();
    }

    public ComponentPort getInputPort(String portId) {
        return _inputs.get(portId);
    }

    public ComponentPort getOutputPort(String portId) {
        return _outputs.get(portId);
    }

    public void setContextMenu(Menu menu) {
        _contextMenu = menu;
    }

    public void highlight() {
        getEl().frame("C3DAF9", 1, new FxConfig(1));
    }

    public void fadeOut(final Function function) {
        getEl().fadeOut(new FxConfig() {
            {
                setDuration(1);
                setCallback(function);
            }
        });
    }

    public void select() {
        if (isSelected()) return;

        DOM.setIntStyleAttribute(getElement(), "zIndex", 999);
        _tfComponentName.addStyleName("component-label-selected");
        _selected = true;

        for (ComponentActionListener listener : _actionListeners)
            listener.onSelected(this);
    }

    public void unselect() {
        if (!isSelected()) return;

        DOM.setIntStyleAttribute(getElement(), "zIndex", 0);
        _tfComponentName.removeStyleName("component-label-selected");
        _selected = false;

        for (ComponentActionListener listener : _actionListeners)
            listener.onUnselected(this);
    }

    public boolean isSelected() {
        return _selected;
    }

    public void addListener(ComponentActionListener listener) {
        _actionListeners.add(listener);
    }

    public ComponentPort getSelectedPort() {
        return _selectedPort;
    }

    public boolean isConnected() {
        return hasInputsConnected() || hasOutputsConnected();
    }

    public boolean hasInputsConnected() {
        for (ComponentPort port : _inputs.values())
            if (port.isConnected())
                return true;

        return false;
    }

    public boolean hasOutputsConnected() {
        for (ComponentPort port : _outputs.values())
            if (port.isConnected())
                return true;

        return false;
    }

    public void disconnect() {
        disconnectInputs();
        disconnectOutputs();
    }

    public void disconnectInputs() {
        for (ComponentPort port : _inputs.values())
            port.disconnect();
    }

    public void disconnectOutputs() {
        for (ComponentPort port : _outputs.values())
            port.disconnect();
    }

    public void updateConnections() {
        for (ComponentPort port : _inputs.values())
            port.updateConnection();

        for (ComponentPort port : _outputs.values())
            port.updateConnection();
    }

    public static RectilinearTwoEndedConnection getConnection(Connector connector) {
        Collection<Connection> connections = connector.getConnections();
        return connections.isEmpty() ? null : (RectilinearTwoEndedConnection) connections.iterator().next();
    }

    public static UIObjectConnector getLinkedConnector(Connector connector) {
        TwoEndedConnection connection = getConnection(connector);
        if (connection == null) return null;

        return (UIObjectConnector) (connection.getSource() == connector ?
                connection.getTarget() : connection.getSource());
    }

    private void raiseClick(EventObject e) {
        for (ComponentActionListener listener : _actionListeners)
            listener.onClicked(this, e);
    }

    private void portClicked(ComponentPort port) {
        if (_selectedPort != null && _selectedPort != port)
            _selectedPort.unselect();

        if (port.isSelected())
            port.unselect();
        else
            port.select();
    }

    public enum PortType { INPUT, OUTPUT };

    public class ComponentPort extends ContainerPanel {
        private final WBDataPortDescription _portDesc;
        private final PortType _portType;
        private final Connector _connector;
        private final Menu _contextMenu = new Menu();
        private final ToolTip _toolTip;
        private boolean _selected = false;

        public ComponentPort(WBDataPortDescription portDesc, PortType portType) {
            _portDesc = portDesc;
            _portType = portType;

            Direction direction = (portType == PortType.INPUT) ? Direction.LEFT : Direction.RIGHT;
            _connector = UIObjectConnector.wrap(this, new Direction[] { direction });

            final Item btnDisconnect = new Item("Disconnect");
            btnDisconnect.addListener(new BaseItemListenerAdapter() {
                @Override
                public void onClick(BaseItem item, EventObject e) {
                    disconnect();
                }
            });

            _contextMenu.addListener(new MenuListenerAdapter() {
                @Override
                public void doBeforeShow(Menu menu) {
                    if (isConnected())
                        btnDisconnect.enable();
                    else
                        btnDisconnect.disable();
                }
            });

            _contextMenu.addItem(btnDisconnect);

            _toolTip = new ToolTip();
            _toolTip.setHtml("<b>" + portDesc.getName() + "</b><p>" + portDesc.getDescription() + "</p>");
            _toolTip.setShowDelay(0);
            _toolTip.setHideDelay(0);
            _toolTip.applyTo(this);

            setCls("component-port");

            doOnRender(new Function() {
                public void execute() {
                    getEl().addListener("click", new EventCallback() {
                        public void execute(EventObject e) {
                            e.stopPropagation();
                            removeHighlight();
                            portClicked(ComponentPort.this);
                        }
                    });

                    getEl().addListener("mouseover", new EventCallback() {
                        public void execute(EventObject e) {
                            if (_isBeingDragged) return;

                            _dragDrop.lock();
                            highlight();

                            if (isConnected()) {
                                RectilinearTwoEndedConnection connection = getConnection(_connector);
                                connection.addLineStyleName("connection-highlight");
                                ComponentPort linkedPort =
                                    (ComponentPort) getLinkedConnector(_connector).getWrappedElement();
                                linkedPort.highlight();
                                if (e.isShiftKey())
                                    linkedPort.showToolTip();
                            }
                        }
                    });

                    getEl().addListener("mouseout", new EventCallback() {
                        public void execute(EventObject e) {
                            if (_isBeingDragged) return;

                            _dragDrop.unlock();
                            removeHighlight();

                            if (isConnected()) {
                                RectilinearTwoEndedConnection connection = getConnection(_connector);
                                connection.removeLineStyleName("connection-highlight");
                                ComponentPort linkedPort =
                                    (ComponentPort) getLinkedConnector(_connector).getWrappedElement();
                                linkedPort.removeHighlight();
                                linkedPort.hideToolTip();
                            }
                        }
                     });

                    getEl().addListener("contextmenu", new EventCallback() {
                        public void execute(EventObject e) {
                            e.stopEvent();
                            _contextMenu.showAt(e.getXY());
                        }
                    });
                }
            });

        }

        public void highlight() {
            if (!isSelected())
                addClass("component-port-highlight");
        }

        public void removeHighlight() {
            removeClass("component-port-highlight");
        }

        public WBDataPortDescription getDataPortDescription() {
            return _portDesc;
        }

        public PortType getPortType() {
            return _portType;
        }

        public Connector getConnector() {
            return _connector;
        }

        public String getName() {
            return _portDesc.getName();
        }

        public String getDescription() {
            return _portDesc.getDescription();
        }

        public String getIdentifier() {
            return _portDesc.getIdentifier();
        }

        public boolean isSelected() {
            return _selected;
        }

        public void select() {
            if (isSelected()) return;

            addClass("component-port-selected");
            _selected = true;
            _selectedPort = this;

            for (ComponentActionListener listener : _actionListeners)
                listener.onPortSelected(this);
        }

        public void unselect() {
            if (!isSelected()) return;

            removeClass("component-port-selected");
            _selected = false;
            _selectedPort = null;

            for (ComponentActionListener listener : _actionListeners)
                listener.onPortUnselected(this);
        }

        public Component getComponent() {
            return Component.this;
        }

        public void showToolTip() {
            _toolTip.showBy(ComponentPort.this.getElement());
        }

        public void hideToolTip() {
            _toolTip.hide();
        }

        public boolean isConnected() {
            return getConnection(_connector) != null;
        }

        public void disconnect() {
            // TODO embed GWT-Diagrams solution in Workbench and use ComponentPort in place of Connector
            // and provide events for connections and "connectors"...
            // provide a connection.highlight() that highlights the connection and ports

            if (!isConnected()) return;

            ComponentPort otherPort =
                (ComponentPort) getLinkedConnector(_connector).getWrappedElement();
            otherPort.removeHighlight();

            getConnection(_connector).remove();

            for (ComponentActionListener listener : _actionListeners)
                listener.onPortDisconnected(this, otherPort);

            for (ComponentActionListener listener : otherPort.getComponent()._actionListeners)
                listener.onPortDisconnected(otherPort, this);

        }

        public void updateConnection() {
            _connector.update();
        }
    }

    private class DDOnTop extends DD {

        public DDOnTop(Component component) {
            super(component);
        }

        @Override
        public void startDrag(int x, int y) {
            DOM.setIntStyleAttribute(getEl(), "zIndex", 999);
            Ext.get(getEl()).addClass("component-dragging");
            _isBeingDragged = true;
        }

        @Override
        public void endDrag(EventObject e) {
            DOM.setIntStyleAttribute(getEl(), "zIndex", 0);
            Ext.get(getEl()).removeClass("component-dragging");
            _isBeingDragged = false;

            for (ComponentActionListener listener : _actionListeners)
                listener.onDragged(Component.this);
        }

        @Override
        public void onDrag(EventObject e) {
            for (ComponentActionListener listener : _actionListeners)
                listener.onDragging(Component.this);
        }
    }
}
