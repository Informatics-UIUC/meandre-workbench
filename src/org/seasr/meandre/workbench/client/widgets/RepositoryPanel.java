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
import java.util.HashSet;
import java.util.Set;

import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBLocation;
import org.seasr.meandre.workbench.client.listeners.ComponentsGridActionListener;
import org.seasr.meandre.workbench.client.listeners.FlowsGridActionListener;
import org.seasr.meandre.workbench.client.listeners.LocationsGridActionListener;
import org.seasr.meandre.workbench.client.listeners.RefreshListener;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.Store;
import com.gwtext.client.util.DateUtil;
import com.gwtext.client.util.Format;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.widgets.grid.event.RowSelectionListenerAdapter;
import com.gwtext.client.widgets.layout.AccordionLayout;
import com.gwtextux.client.widgets.grid.plugins.GridSearchPlugin;

/**
 * @author Boris Capitanu
 *
 */
public class RepositoryPanel extends Panel {
    private final Set<RefreshListener> _refreshListeners = new HashSet<RefreshListener>();
    private final ComponentsGrid _componentsPanel = new ComponentsGrid();
    private final FlowsGrid _flowsPanel = new FlowsGrid();
    private final LocationsGrid _locationsPanel = new LocationsGrid();

    public RepositoryPanel() {
        setTitle("Repository");
        setCollapsible(true);
        setWidth(220);
        setLayout(new AccordionLayout(true));

        addTool(new Tool(Tool.REFRESH, new Function() {
            public void execute() {
                for (RefreshListener listener : _refreshListeners)
                    listener.onRefresh();
            }
        }));

        add(_componentsPanel);
        add(_flowsPanel);
        add(_locationsPanel);
    }

    public ComponentsGrid getComponentsPanel() {
        return _componentsPanel;
    }

    public FlowsGrid getFlowsPanel() {
        return _flowsPanel;
    }

    public LocationsGrid getLocationsPanel() {
        return _locationsPanel;
    }

    public void addListener(RefreshListener refreshListener) {
        _refreshListeners.add(refreshListener);
    }

    public void setMask(String message) {
        getEl().mask(message);
    }

    public void clearMask() {
        getEl().unmask();
    }

    public class ComponentsGrid extends GridPanel {
        private final Set<ComponentsGridActionListener> _actionListeners =
            new HashSet<ComponentsGridActionListener>();
        private WBExecutableComponentDescription _selectedComponent = null;
        private final GridSearchPlugin _gridSearch;

        public ComponentsGrid() {
            GroupingView gridView = new GroupingView();
            gridView.setForceFit(false);
            gridView.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ?  \"Items\" : \"Item\"]})");
            gridView.setEmptyText("No components found");

            ColumnConfig ccIcon = new ColumnConfig("Icon", "runnable", 24, false, new Renderer() {
                public String render(Object value,
                        CellMetadata cellMetadata, Record record,
                        int rowIndex, int colNum, Store store) {
                    return Format.format("<img src='images/{0}.png'/>", value.toString());
                }
            });
            ccIcon.setAlign(TextAlign.CENTER);
            ccIcon.setResizable(false);

            ColumnConfig ccName = new ColumnConfig("Name", "name", 160, true);

            ColumnConfig ccDescription = new ColumnConfig("Description", "description", 160, false);
            ccDescription.setHidden(true);

            ColumnConfig ccRights = new ColumnConfig("Rights", "rights", 80, false);
            ccRights.setHidden(true);

            ColumnConfig ccAuthor = new ColumnConfig("Creator", "creator", 80, true);

            ColumnConfig ccCreated = new ColumnConfig("Date", "creationDate", 70, true, new Renderer() {
                public String render(Object value, CellMetadata cellMetadata,
                        Record record, int rowIndex, int colNum, Store store) {
                    Date creationDate = (Date)value;
                    return DateUtil.format(creationDate, "M j, Y");
                }
            });

            ColumnConfig ccRunnable = new ColumnConfig("Type", "runnable", 40, true);
            ccRunnable.setHidden(true);

            ColumnConfig ccFiringPolicy = new ColumnConfig("Firing Policy", "firingPolicy", 40, false);
            ccFiringPolicy.setHidden(true);

            ColumnConfig ccTags = new ColumnConfig("Tags", "tags", 80, false);
            ccTags.setHidden(true);

            ColumnConfig[] columns = new ColumnConfig[] {
                    ccIcon,
                    ccName,
                    ccDescription,
                    ccTags,
                    ccAuthor,
                    ccRights,
                    ccCreated,
                    ccRunnable,
                    ccFiringPolicy
            };

            RowSelectionModel selectionModel = new RowSelectionModel(true);
            selectionModel.addListener(new RowSelectionListenerAdapter() {
                @Override
                public void onRowSelect(RowSelectionModel sm, int rowIndex, Record record) {
                    _selectedComponent = (WBExecutableComponentDescription) record.getAsObject("wbComponent");
                    for (ComponentsGridActionListener listener : _actionListeners)
                        listener.onSelected(_selectedComponent);
                }

                @Override
                public void onRowDeselect(RowSelectionModel sm, int rowIndex, Record record) {
                    _selectedComponent = null;
                    for (ComponentsGridActionListener listener : _actionListeners)
                        listener.onUnselected((WBExecutableComponentDescription) record.getAsObject("wbComponent"));
                }
            });

            final Toolbar topToolbar = new Toolbar();
            topToolbar.addFill();

            _gridSearch = new GridSearchPlugin(GridSearchPlugin.TOP);
            _gridSearch.setMode(GridSearchPlugin.LOCAL);

            setTopToolbar(topToolbar);
            setSelectionModel(selectionModel);
            setColumnModel(new ColumnModel(columns));
            setView(gridView);
            setLoadMask(false);
            setCollapsible(true);
            //setAnimCollapse(false);
            setTitleCollapse(true);
            setBorder(false);
            setStripeRows(true);
            setTitle("Components", "icon-components");
            setEnableDragDrop(true);
            setDdGroup("ddComponents");
            addPlugin(_gridSearch);
        }

        public void clearSearch() {
            _gridSearch.setSearchText("");
        }

        public void setMask(String message) {
            getEl().mask(message);
        }

        public void clearMask() {
            getEl().unmask();
        }

        public void addListener(ComponentsGridActionListener listener) {
            _actionListeners.add(listener);
        }

        public WBExecutableComponentDescription getSelectedComponent() {
            return _selectedComponent;
        }

        public void clearSelection() {
            getSelectionModel().clearSelections();
        }
    }

    public class FlowsGrid extends GridPanel {

        private final Set<FlowsGridActionListener> _actionListeners =
            new HashSet<FlowsGridActionListener>();
        private WBFlowDescription _selectedFlow = null;
        private final GridSearchPlugin _gridSearch;

        public FlowsGrid() {
            GroupingView gridView = new GroupingView();
            gridView.setForceFit(false);
            gridView.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ?  \"Items\" : \"Item\"]})");
            gridView.setEmptyText("No flows found");

            ColumnConfig ccIcon = new ColumnConfig("Icon", "name", 24, false, new Renderer() {
                public String render(Object value,
                        CellMetadata cellMetadata, Record record,
                        int rowIndex, int colNum, Store store) {
                    return "<img src='images/gears.png'/>";
                }
            });
            ccIcon.setAlign(TextAlign.CENTER);
            ccIcon.setResizable(false);

            ColumnConfig ccName = new ColumnConfig("Name", "name", 160, true);

            ColumnConfig ccDescription = new ColumnConfig("Description", "description", 160, false);
            ccDescription.setHidden(true);

            ColumnConfig ccRights = new ColumnConfig("Rights", "rights", 80, false);
            ccRights.setHidden(true);

            ColumnConfig ccAuthor = new ColumnConfig("Creator", "creator", 80, true);

            ColumnConfig ccCreated = new ColumnConfig("Date", "creationDate", 70, true, new Renderer() {
                public String render(Object value, CellMetadata cellMetadata,
                        Record record, int rowIndex, int colNum, Store store) {
                    Date creationDate = (Date)value;
                    return DateUtil.format(creationDate, "M j, Y");
                }
            });

            ColumnConfig ccTags = new ColumnConfig("Tags", "tags", 80, false);
            ccTags.setHidden(true);

            ColumnConfig[] columns = new ColumnConfig[] {
                    ccIcon,
                    ccName,
                    ccDescription,
                    ccTags,
                    ccAuthor,
                    ccRights,
                    ccCreated
            };

            RowSelectionModel selectionModel = new RowSelectionModel(true);
            selectionModel.addListener(new RowSelectionListenerAdapter() {
                public void onRowSelect(RowSelectionModel sm, int rowIndex, Record record) {
                    _selectedFlow = (WBFlowDescription) record.getAsObject("wbFlow");
                    for (FlowsGridActionListener listener : _actionListeners)
                        listener.onSelected(_selectedFlow);
                }

                @Override
                public void onRowDeselect(RowSelectionModel sm, int rowIndex, Record record) {
                    _selectedFlow = null;
                    for (FlowsGridActionListener listener : _actionListeners)
                        listener.onUnselected((WBFlowDescription) record.getAsObject("wbFlow"));
                }
            });

            addGridRowListener(new GridRowListenerAdapter() {
                @Override
                public void onRowDblClick(GridPanel grid, int rowIndex, EventObject e) {
                    Record record = grid.getStore().getAt(rowIndex);
                    WBFlowDescription flow = (WBFlowDescription) record.getAsObject("wbFlow");

                    for (FlowsGridActionListener listener : _actionListeners)
                        listener.onOpen(flow);
                }
            });

            final Toolbar topToolbar = new Toolbar();
            topToolbar.addFill();

            _gridSearch = new GridSearchPlugin(GridSearchPlugin.TOP);
            _gridSearch.setMode(GridSearchPlugin.LOCAL);

            setTopToolbar(topToolbar);
            setSelectionModel(selectionModel);
            setColumnModel(new ColumnModel(columns));
            setView(gridView);
            setLoadMask(false);
            setCollapsible(true);
            //setAnimCollapse(false);
            setTitleCollapse(true);
            setBorder(false);
            setStripeRows(true);
            setTitle("Flows", "icon-flows");
            addPlugin(_gridSearch);
        }

        public void clearSearch() {
            _gridSearch.setSearchText("");
        }

        public void setMask(String message) {
            getEl().mask(message);
        }

        public void clearMask() {
            getEl().unmask();
        }

        public void addListener(FlowsGridActionListener listener) {
            _actionListeners.add(listener);
        }

        public WBFlowDescription getSelectedFlow() {
            return _selectedFlow;
        }

        public void clearSelection() {
            getSelectionModel().clearSelections();
        }
    }

    public class LocationsGrid extends GridPanel {
        private LocationsGridActionListener _actionListener;
        private WBLocation _selectedLocation;

        public LocationsGrid() {
            GridView gridView = new GridView();
            gridView.setForceFit(false);
            gridView.setEmptyText("No locations found");

            ColumnConfig[] columns = new ColumnConfig[] {
                    new ColumnConfig("Description", "description", 100, true),
                    new ColumnConfig("URL", "url", 160, false)
            };

            final ToolbarButton btnAdd = new ToolbarButton();
            btnAdd.setIcon("images/location_add.png");
            btnAdd.setCls("x-btn-icon");
            btnAdd.setTooltip("Add location");
            btnAdd.addListener(new ButtonListenerAdapter() {
                @Override
                public void onClick(Button button, EventObject e) {
                    _actionListener.onAdd();
                }
            });

            final ToolbarButton btnRemove = new ToolbarButton();
            btnRemove.setIcon("images/location_remove.png");
            btnRemove.setCls("x-btn-icon");
            btnRemove.setTooltip("Remove location");
            btnRemove.disable();
            btnRemove.addListener(new ButtonListenerAdapter() {
                @Override
                public void onClick(Button button, EventObject e) {
                    _actionListener.onRemove(_selectedLocation);
                }
            });

            final ToolbarButton btnRegenerate = new ToolbarButton();
            btnRegenerate.setIcon("images/regenerate.png");
            btnRegenerate.setCls("x-btn-icon");
            btnRegenerate.setTooltip("Regenerate repository");
            btnRegenerate.addListener(new ButtonListenerAdapter() {
                @Override
                public void onClick(Button button, EventObject e) {
                    _actionListener.onRegenerate();
                }
            });

            Toolbar topToolbar = new Toolbar();
            topToolbar.addFill();
            topToolbar.addButton(btnAdd);
            topToolbar.addButton(btnRemove);
            topToolbar.addSeparator();
            topToolbar.addButton(btnRegenerate);

            RowSelectionModel selectionModel = new RowSelectionModel(true);
            selectionModel.addListener(new RowSelectionListenerAdapter() {
                @Override
                public void onRowSelect(RowSelectionModel sm, int rowIndex, Record record) {
                    _selectedLocation = (WBLocation) record.getAsObject("wbLocation");
                    btnRemove.enable();
                }

                @Override
                public void onRowDeselect(RowSelectionModel sm, int rowIndex, Record record) {
                    _selectedLocation = null;
                    btnRemove.disable();
                }
            });

            setTopToolbar(topToolbar);
            setColumnModel(new ColumnModel(columns));
            setSelectionModel(selectionModel);
            setView(gridView);
            setLoadMask(false);
            setCollapsible(true);
            //setAnimCollapse(false);
            setTitleCollapse(true);
            setBorder(false);
            setStripeRows(true);
            setTitle("Locations", "icon-locations");
        }

        public void setActionListener(LocationsGridActionListener listener) {
            _actionListener = listener;
        }
    }
}
