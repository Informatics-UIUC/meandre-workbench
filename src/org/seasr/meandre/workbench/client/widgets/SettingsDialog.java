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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.seasr.meandre.workbench.client.Workbench;
import org.seasr.meandre.workbench.client.beans.ComponentColor;
import org.seasr.meandre.workbench.client.beans.WBSettings;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Cookies;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.TextAlign;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtextux.client.widgets.grid.plugins.ColumnWithCellActionsConfig;
import com.gwtextux.client.widgets.grid.plugins.GridCellAction;
import com.gwtextux.client.widgets.grid.plugins.GridCellActionListener;
import com.gwtextux.client.widgets.grid.plugins.GridCellActionsPlugin;

/**
 * @author Boris Capitanu
 */

public class SettingsDialog extends Window {

    public SettingsDialog(final WBSettings settings) {
        Map<String, ComponentColor> compCatColors = settings.getComponentCategoryColors();
        Object[][] compCatData = new Object[compCatColors.size()][];
        int i = 0;
        for (Map.Entry<String, ComponentColor> entry : compCatColors.entrySet()) {
            Object[] catColor = new Object[3];
            catColor[0] = entry.getKey();
            catColor[1] = entry.getValue().getMainColor();
            catColor[2] = "";
            compCatData[i++] = catColor;
        }

        final RecordDef recordDef = new RecordDef(new FieldDef[] {
                new StringFieldDef("tag"),
                new StringFieldDef("color"),
                new StringFieldDef("action")
             });

        MemoryProxy proxy = new MemoryProxy(compCatData);
        ArrayReader reader = new ArrayReader(recordDef);
        final Store store = new Store(proxy, reader);
        store.load();

        ColumnConfig tagColumn = new ColumnConfig("Tag", "tag", 160, true);
        tagColumn.setEditor(new GridEditor(new TextField()));

        ColumnConfig colorColumn = new ColumnConfig("Main Color", "color", 160, false);
        TextField colorField = new TextField();
        colorField.setRegex("^#[a-fA-F0-9]{6}$");
        colorField.setRegexText("Color must be specified as hex value: #RRGGBB");
        colorColumn.setEditor(new GridEditor(colorField));

        ColumnConfig sampleColumn = new ColumnConfig("Sample", "color", 62, false, new Renderer() {
            public String render(Object value, CellMetadata cellMetadata, Record record, int rowIndex, int colNum, Store store) {
                return "<div style='width: 48; height: 10; background-color: " + value.toString() + "; border: 1px solid black;'/>";
            }
        });

        ColumnWithCellActionsConfig actionsColumn = new ColumnWithCellActionsConfig("Action", "action", 60, false);
        actionsColumn.setCellActions(new GridCellAction[] { new GridCellAction("icon-delete", "Delete", new GridCellActionListener() {
            public boolean execute(GridPanel gridPanel, Record record, String action, Object value, String dataIndex, int rowIndex, int colIndex) {
                if (!record.getAsString("tag").equals("#default")) {
                    gridPanel.getStore().remove(record);
                    gridPanel.getStore().commitChanges();
                    return true;
                } else {
                    MessageBox.alert("Warning", "Cannot remove the default catch-all category!");
                    return false;
                }
            }
        }) });

        BaseColumnConfig[] columns = new BaseColumnConfig[] {
                tagColumn,
                colorColumn,
                sampleColumn,
                actionsColumn
        };

        ColumnModel columnModel = new ColumnModel(columns);

        final EditorGridPanel grid = new EditorGridPanel();
        grid.setStore(store);
        grid.setColumnModel(columnModel);
        grid.setFrame(true);
        grid.setStripeRows(true);
        grid.setClicksToEdit(2);
        grid.setTitle("Component Category Colors");

        Toolbar toolbar = new Toolbar();
        ToolbarButton button = new ToolbarButton("Add Category", new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                Record category = recordDef.createRecord(new Object[] { "New Category", "#B6B7EE", "" });
                grid.stopEditing();
                int pos = store.getRecords().length;
                store.insert(pos, category);
                grid.startEditing(pos, 0);
            }
        });

        toolbar.addButton(button);
        grid.setTopToolbar(toolbar);

        Button btnOK = new Button("OK", new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                Map<String, ComponentColor> compCatColors = new HashMap<String, ComponentColor>();

                for (Record record : store.getRecords()) {
                    String tag = record.getAsString("tag");
                    String color = record.getAsString("color");
                    compCatColors.put(tag, new ComponentColor(color));
                }

                settings.setComponentCategoryColors(compCatColors);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, 10);
                Log.info("Setting expiration date of cookie '" + Workbench.WB_SETTINGS_COOKIE_NAME + "' to " + sdf.format(cal.getTime()));
                Cookies.setCookie(Workbench.WB_SETTINGS_COOKIE_NAME, settings.toJSON(), cal.getTime());
                SettingsDialog.this.close();
            }
        });

        Button btnCancel = new Button("Cancel", new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button, EventObject e) {
                SettingsDialog.this.close();
            }
        });

        setLayout(new FitLayout());
        setModal(true);
        setPlain(true);
        setPaddings(5);
        setWidth(500);
        setHeight(400);
        setMinWidth(300);
        setMinHeight(200);
        setTitle("Settings");
        setIconCls("icon-settings");
        setButtonAlign(Position.CENTER);
        addButton(btnOK);
        addButton(btnCancel);

        GridCellActionsPlugin actionsPlugin = new GridCellActionsPlugin(TextAlign.LEFT, null);
        grid.addPlugin(actionsPlugin);

        add(grid);
    }

}
