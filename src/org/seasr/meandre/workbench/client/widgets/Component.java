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

import org.seasr.meandre.workbench.client.Workbench;
import org.seasr.meandre.workbench.client.beans.ComponentColor;
import org.seasr.meandre.workbench.client.beans.repository.WBDataPortDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
import org.seasr.meandre.workbench.client.listeners.ComponentActionListener;
import org.seasr.meandre.workbench.client.listeners.SettingsListener;

import pl.balon.gwt.diagrams.client.connection.Connection;
import pl.balon.gwt.diagrams.client.connection.RectilinearTwoEndedConnection;
import pl.balon.gwt.diagrams.client.connector.Connector;
import pl.balon.gwt.diagrams.client.connector.Direction;
import pl.balon.gwt.diagrams.client.connector.UIObjectConnector;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.gwtext.client.core.EventCallback;
import com.gwtext.client.core.EventObject;
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
import com.gwtextux.client.widgets.image.Image;

/**
 * @author Boris Capitanu
 *
 */
public class Component extends VerticalContainerPanel {

    public static final String DEFAULT_CATEGORY_TAG = "#default";

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
    private String _category = null;
    private final SettingsListener _settingsListener;

    public Component(final WBExecutableComponentInstanceDescription compInstance, final WBExecutableComponentDescription compDesc) {
        _compInstance = compInstance;

        for (String tag : compDesc.getTags().getTags())
            if (tag.startsWith("#")) {
                _category = tag;
                break;
            }

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

        final CompBoxPanel compBox = new CompBoxPanel() {
            final ContainerPanel _topBand;
            final Image _imgIcon;

            {
                Map<String, ComponentColor> componentCategoryColors = Workbench.Settings.getComponentCategoryColors();
                final ComponentColor componentColor = (_category != null && componentCategoryColors.containsKey(_category)) ?
                        componentCategoryColors.get(_category) : componentCategoryColors.get(DEFAULT_CATEGORY_TAG);

                setCls("component-box");

                _topBand = new ContainerPanel();
                _topBand.setCls("component-box-topband");
                _topBand.doOnRender(new Function() {
                    public void execute() {
                        getTopBand().getEl().setStyle("background-color", componentColor.getTopBandColor());
                    }
                });
                add(_topBand);

                _imgIcon = new Image("icon", "images/gear.png");
                _imgIcon.setCls("component-box-icon");
                _imgIcon.doOnRender(new Function() {
                    public void execute() {
                        getImageIcon().getEl().setStyle("border-color", componentColor.getBorderColor());
                    }
                });
                add(_imgIcon);

                if (!compDesc.getProperties().getKeys().isEmpty()) {
                    Image imgProps = new Image("props", "images/component-props.png");
                    imgProps.setCls("component-box-prop");
                    add(imgProps);
                }

                doOnRender(new Function() {
                    public void execute() {
                        getEl().setStyle("background-color", componentColor.getMainColor());
                        getEl().setStyle("border-color", componentColor.getBorderColor());
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

            @Override
            public ContainerPanel getTopBand() {
                return _topBand;
            }

            @Override
            public Image getImageIcon() {
                return _imgIcon;
            }
        };

        _settingsListener = new SettingsListener() {
            public void onComponentCategoryColorsChanged(Map<String, ComponentColor> categoryColors) {
                ComponentColor componentColor = (_category != null && categoryColors.containsKey(_category)) ?
                        categoryColors.get(_category) : categoryColors.get(DEFAULT_CATEGORY_TAG);
                compBox.getEl().setStyle("background-color", componentColor.getMainColor());
                compBox.getTopBand().getEl().setStyle("background-color", componentColor.getTopBandColor());
                compBox.getEl().setStyle("border-color", componentColor.getBorderColor());
                compBox.getImageIcon().getEl().setStyle("border-color", componentColor.getBorderColor());
            }
        };

        Workbench.Settings.addListener(_settingsListener);

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

        Log.debug("Removing settings listener");
        Workbench.Settings.removeListener(_settingsListener);

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

    public void setDrag(boolean isBeingDragged) {
        if (isBeingDragged) {
            DOM.setIntStyleAttribute(getElement(), "zIndex", 999);
            this.addClass("component-dragging");
            _isBeingDragged = true;
        } else {
            DOM.setIntStyleAttribute(getElement(), "zIndex", 0);
            this.removeClass("component-dragging");
            _isBeingDragged = false;
        }
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

    public static Collection<RectilinearTwoEndedConnection> getConnections(Connector connector) {
        Collection<RectilinearTwoEndedConnection> connections = new HashSet<RectilinearTwoEndedConnection>();
        for (Connection connection : connector.getConnections())
            connections.add((RectilinearTwoEndedConnection) connection);

        return connections.isEmpty() ? null : connections;
    }

    public static Collection<UIObjectConnector> getLinkedConnectors(Connector connector) {
        Collection<RectilinearTwoEndedConnection> connections = getConnections(connector);
        if (connections == null) return null;

        Collection<UIObjectConnector> connectors = new HashSet<UIObjectConnector>();
        for (RectilinearTwoEndedConnection connection : connections)
            connectors.add((UIObjectConnector) (connection.getSource() == connector ?
                connection.getTarget() : connection.getSource()));

        return connectors;
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
                                Collection<RectilinearTwoEndedConnection> connections = getConnections(_connector);
                                for (RectilinearTwoEndedConnection connection : connections) {
                                    connection.addLineStyleName("connection-highlight");
                                    for (UIObjectConnector connector : getLinkedConnectors(_connector)) {
                                        ComponentPort linkedPort = (ComponentPort) connector.getWrappedElement();
                                        linkedPort.highlight();
                                        if (e.isShiftKey())
                                            linkedPort.showToolTip();
                                    }
                                }
                            }
                        }
                    });

                    getEl().addListener("mouseout", new EventCallback() {
                        public void execute(EventObject e) {
                            if (_isBeingDragged) return;

                            _dragDrop.unlock();
                            removeHighlight();

                            if (isConnected()) {
                                Collection<RectilinearTwoEndedConnection> connections = getConnections(_connector);
                                for (RectilinearTwoEndedConnection connection : connections) {
                                    connection.removeLineStyleName("connection-highlight");
                                    for (UIObjectConnector connector : getLinkedConnectors(_connector)) {
                                        ComponentPort linkedPort = (ComponentPort) connector.getWrappedElement();
                                        linkedPort.removeHighlight();
                                        linkedPort.hideToolTip();
                                    }
                                }
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
            return getConnections(_connector) != null;
        }

        public void disconnect() {
            // TODO embed GWT-Diagrams solution in Workbench and use ComponentPort in place of Connector
            // and provide events for connections and "connectors"...
            // provide a connection.highlight() that highlights the connection and ports

            if (!isConnected()) return;

            Collection<ComponentPort> otherPorts = new HashSet<ComponentPort>();
            for (UIObjectConnector connector : getLinkedConnectors(_connector)) {
                ComponentPort otherPort = (ComponentPort) connector.getWrappedElement();
                otherPort.removeHighlight();
                otherPorts.add(otherPort);
            }

            for (RectilinearTwoEndedConnection connection : getConnections(_connector))
                connection.remove();

            for (ComponentActionListener listener : _actionListeners)
                for (ComponentPort port : otherPorts)
                    listener.onPortDisconnected(this, port);

            for (ComponentPort port : otherPorts)
                for (ComponentActionListener listener : port.getComponent()._actionListeners)
                    listener.onPortDisconnected(port, this);
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
            for (ComponentActionListener listener : _actionListeners)
                listener.onStartDrag(Component.this, x, y);
        }

        @Override
        public void endDrag(EventObject e) {
            for (ComponentActionListener listener : _actionListeners)
                listener.onDragged(Component.this, e.getXY()[0], e.getXY()[1]);
        }

        @Override
        public void onDrag(EventObject e) {
            for (ComponentActionListener listener : _actionListeners)
                listener.onDragging(Component.this, e.getXY()[0], e.getXY()[1]);
        }
    }
}
