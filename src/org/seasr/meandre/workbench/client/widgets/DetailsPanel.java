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
import java.util.Map;
import java.util.Set;

import org.seasr.meandre.workbench.client.beans.repository.WBDataPortDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;

import com.gwtext.client.core.Margins;
import com.gwtext.client.core.NameValuePair;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.data.Record;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.PropertyGridPanel;
import com.gwtext.client.widgets.grid.event.EditorGridListenerAdapter;
import com.gwtext.client.widgets.grid.event.PropertyGridPanelListener;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;

/**
 * @author Boris Capitanu
 *
 */
public class DetailsPanel extends Panel {
    private final TabPanel _tabPanelTop = new TabPanel();
    private final TabPanel _tabPanelBottom = new TabPanel();
    private final DocPanel _docPanel = new DocPanel();
    private final PropPanel _propPanel = new PropPanel();

    private WorkspaceTab _focusedTab = null;

    public DetailsPanel() {
        setTitle("Details");
        setCollapsible(true);
        setWidth(300);
        setLayout(new BorderLayout());

        BorderLayoutData southLayoutData = new BorderLayoutData(RegionPosition.SOUTH);
        southLayoutData.setMinSize(100);
        southLayoutData.setMaxSize(600);
        southLayoutData.setMargins(new Margins(0, 0, 0, 0));
        southLayoutData.setSplit(true);

        add(_tabPanelTop, new BorderLayoutData(RegionPosition.CENTER));
        add(_tabPanelBottom, southLayoutData);

        _tabPanelTop.setBorder(false);
        _tabPanelTop.add(_propPanel);

        _tabPanelBottom.setBorder(false);
        _tabPanelBottom.add(_docPanel);
    }

    public DocPanel getDocPanel() {
        return _docPanel;
    }

    public PropPanel getPropPanel() {
        return _propPanel;
    }

    public WorkspaceTab getFocusedTab() {
        return _focusedTab;
    }

    public boolean isShowingComponentInstance() {
        return _focusedTab != null;
    }

    public class DocPanel extends Panel {
        public DocPanel() {
            setHtml("");
            setHeight(300);
            setTitle("Description");
            setAutoScroll(true);
            setIconCls("icon-docpanel");
        }
    }

    public class PropPanel extends PropertyGridPanel {

        private Map<String, String> _instancePropMap;
        private boolean _allowEditing = false;

        public PropPanel() {
            setTitle("Properties");
            setIconCls("icon-propertygrid");
            setClicksToEdit(2);
            setSorted(true);
            getView().setEmptyText("No properties to display");

            addEditorGridListener(new EditorGridListenerAdapter() {
                @Override
                public boolean doBeforeEdit(GridPanel grid, Record record,
                        String field, Object value, int rowIndex, int colIndex) {
                    return _allowEditing;
                }
            });

            addPropertyGridPanelListener(new PropertyGridPanelListener() {
                public void onPropertyChange(PropertyGridPanel source, String recordID, Object value, Object oldValue) {
                    _instancePropMap.put(recordID, (String)value);

                    if (_focusedTab != null)
                        _focusedTab.setDirty();
                }

                public boolean doBeforePropertyChange(PropertyGridPanel source, String recordID, Object value, Object oldValue) {
                    return true;
                }
            });
        }

        private void viewComponent(WBExecutableComponentDescription comp) {
            _allowEditing = false;
            setSource(comp.getProperties().getValueMap());
        }

        private void viewComponent(WBExecutableComponentInstanceDescription comp, Set<String> keySet) {
            viewComponent(comp, keySet, true);
        }

        private void viewComponent(WBExecutableComponentInstanceDescription comp, Set<String> keySet, boolean allowEditing) {
            _allowEditing = allowEditing;

            _instancePropMap = comp.getProperties().getValueMap();
            Map<String, String> defaultValueMap = comp.getExecutableComponentDescription().getProperties().getValueMap();
            Map<String, String> editablePropMap = new HashMap<String, String>(keySet.size());
            for (String key : keySet) {
                String instancePropValue = _instancePropMap.get(key);
                if (instancePropValue == null)
                    instancePropValue = defaultValueMap.get(key);
                editablePropMap.put(key, instancePropValue);
            }

            setSource(editablePropMap);
        }

        public void reset() {
            setSource(new NameValuePair[] {});
        }

    }

    public void view(WorkspaceTab tab, Component component) {
        _focusedTab = tab;

        WBExecutableComponentDescription compDesc = component.getExecutableComponentDescription();
        WBExecutableComponentInstanceDescription compInstanceDesc = component.getInstanceDescription();
        _propPanel.viewComponent(compInstanceDesc, compDesc.getProperties().getKeys());
        _docPanel.setHtml(getDocumentationHtml(compInstanceDesc));
    }

    public void view(WBExecutableComponentDescription comp) {
        _focusedTab = null;

        _propPanel.viewComponent(comp);
        _docPanel.setHtml(getDocumentationHtml(comp));
    }

    public void view(WBFlowDescription flow) {
        _focusedTab = null;

        _propPanel.reset();
        _docPanel.setHtml(getDocumentationHtml(flow));
    }

    private String getDocumentationHtml(WBExecutableComponentInstanceDescription instance) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: arial, tahoma, helvetica, sans-serif; font-size: 12px; margin: 4px;'>");
        sb.append("<b style='font-size: 14px;'>").append(instance.getName()).append("</b><br/>");
        sb.append("<i style='font-size: 11px;'>").append(instance.getExecutableComponentInstance()).append("</i>");
        sb.append("<br/><br/>");
        sb.append("<p>");

        WBExecutableComponentDescription comp = instance.getExecutableComponentDescription();
        String tags = "";
        for (String tag : comp.getTags().getTags())
            tags += ", " + tag;
        tags = "[" + tags.substring(2) + "]";

        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Tags:</b>&nbsp;&nbsp;").append(tags).append("<br/>");
        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Creator:</b>&nbsp;&nbsp;").append(comp.getCreator()).append("<br/>");
        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Date:</b>&nbsp;&nbsp;").append(comp.getCreationDate()).append("<br/>");

        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px;'><u>Description:</u></b><br/>");
        sb.append("<div style='text-align: justify;'>").append(instance.getDescription()).append("</div>");
        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px;'><u>Rights:</u></b><br/>");
        sb.append("<div style='text-align: justify;'>").append(comp.getRights()).append("</div>");
        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px'><u>Inputs:</u></b><br/>");

        if (!comp.getInputs().isEmpty())
            for (WBDataPortDescription inputPort : comp.getInputs()) {
                sb.append("<div style='border: 1px dotted gray; padding: 2px; margin-top: 4px;'>");
                sb.append("<b>").append(inputPort.getName()).append("</b><br/");
                sb.append("<i style='text-align: justify;'>").append(inputPort.getDescription()).append("</i><br/>");
                sb.append("</div>");
            }
        else
            sb.append("None<br/>");

        sb.append("<br/>");
        sb.append("<b style='font-size: 13px'><u>Outputs:</u></b><br/>");

        if (!comp.getOutputs().isEmpty())
            for (WBDataPortDescription outputPort : comp.getOutputs()) {
                sb.append("<div style='border: 1px dotted gray; padding: 2px; margin-top: 4px;'>");
                sb.append("<b>").append(outputPort.getName()).append("</b><br/");
                sb.append("<i style='text-align: justify;'>").append(outputPort.getDescription()).append("</i><br/>");
                sb.append("</div>");
            }
        else
            sb.append("None<br/>");

        sb.append("</p>");
        sb.append("</div>");

        return sb.toString();
    }

    private String getDocumentationHtml(WBExecutableComponentDescription comp) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: arial, tahoma, helvetica, sans-serif; font-size: 12px; margin: 4px;'>");
        sb.append("<b style='font-size: 14px;'>").append(comp.getName()).append("</b><br/>");
        sb.append("<i style='font-size: 11px;'>").append(comp.getResourceURI()).append("</i>");
        sb.append("<br/><br/>");
        sb.append("<p>");

        String tags = "";
        for (String tag : comp.getTags().getTags())
            tags += ", " + tag;
        tags = "[" + tags.substring(2) + "]";

        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Tags:</b>&nbsp;&nbsp;").append(tags).append("<br/>");
        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Creator:</b>&nbsp;&nbsp;").append(comp.getCreator()).append("<br/>");
        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Date:</b>&nbsp;&nbsp;").append(comp.getCreationDate()).append("<br/>");

        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px;'><u>Description:</u></b><br/>");
        sb.append("<div style='text-align: justify;'>").append(comp.getDescription()).append("</div>");
        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px;'><u>Rights:</u></b><br/>");
        sb.append("<div style='text-align: justify;'>").append(comp.getRights()).append("</div>");
        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px'><u>Inputs:</u></b><br/>");

        if (!comp.getInputs().isEmpty())
            for (WBDataPortDescription inputPort : comp.getInputs()) {
                sb.append("<div style='border: 1px dotted gray; padding: 2px; margin-top: 4px;'>");
                sb.append("<b>").append(inputPort.getName()).append("</b><br/");
                sb.append("<i style='text-align: justify;'>").append(inputPort.getDescription()).append("</i><br/>");
                sb.append("</div>");
            }
        else
            sb.append("None<br/>");

        sb.append("<br/>");
        sb.append("<b style='font-size: 13px'><u>Outputs:</u></b><br/>");

        if (!comp.getOutputs().isEmpty())
            for (WBDataPortDescription outputPort : comp.getOutputs()) {
                sb.append("<div style='border: 1px dotted gray; padding: 2px; margin-top: 4px;'>");
                sb.append("<b>").append(outputPort.getName()).append("</b><br/");
                sb.append("<i style='text-align: justify;'>").append(outputPort.getDescription()).append("</i><br/>");
                sb.append("</div>");
            }
        else
            sb.append("None<br/>");

        sb.append("</p>");
        sb.append("</div>");

        return sb.toString();
    }

    private String getDocumentationHtml(WBFlowDescription flow) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='font-family: arial, tahoma, helvetica, sans-serif; font-size: 12px; margin: 4px;'>");
        sb.append("<b style='font-size: 14px;'>").append(flow.getName()).append("</b><br/>");
        sb.append("<i style='font-size: 11px;'>").append(flow.getFlowURI()).append("</i>");
        sb.append("<br/><br/>");
        sb.append("<p>");

        String tags = "";
        for (String tag : flow.getTags().getTags())
            tags += ", " + tag;
        tags = "[" + tags.substring(2) + "]";

        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Tags:</b>&nbsp;&nbsp;").append(tags).append("<br/>");
        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Creator:</b>&nbsp;&nbsp;").append(flow.getCreator()).append("<br/>");
        sb.append("<b style='width: 50px; text-align: right; font-weight: bold;'>Date:</b>&nbsp;&nbsp;").append(flow.getCreationDate()).append("<br/>");

        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px;'><u>Description:</u></b><br/>");
        sb.append("<div style='text-align: justify;'>").append(flow.getDescription()).append("</div>");
        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px;'><u>Rights:</u></b><br/>");
        sb.append("<div style='text-align: justify;'>").append(flow.getRights()).append("</div>");
        sb.append("</p><br/>");
        sb.append("<p>");
        sb.append("<b style='font-size: 13px'><u>Components:</u></b><br/>");
        for (WBExecutableComponentInstanceDescription instance : flow.getExecutableComponentInstances()) {
            sb.append("<div style='border: 1px dotted gray; padding: 2px; margin-top: 4px;'>");
            WBExecutableComponentDescription compDesc = instance.getExecutableComponentDescription();
            String imgName = compDesc != null ? compDesc.getRunnable() : "warning";
            sb.append("<img src='images/" + imgName + ".png'/> ");
            sb.append("<b>").append(instance.getName()).append("</b>");
            if (compDesc == null)
                sb.append(" <i style='color: red;'>(missing)</i>");
            sb.append("<br/>");
            sb.append("<i style='font-size: 11px;'>").append(instance.getExecutableComponentInstance()).append("</i><br/>");
            sb.append("<br/>");
            sb.append("<i>Properties:</i><br/>");
            sb.append("<div style='margin-left: 10px;'>");

            if (!compDesc.getProperties().getKeys().isEmpty())
                for (String key : compDesc.getProperties().getKeys())
                    sb.append("<b>" + key).append(":</b>&nbsp;&nbsp;<font color='blue'>").append(instance.getProperties().getValue(key)).append("</font><br/>");
            else
                sb.append("None<br/>");

            sb.append("</div>");
            sb.append("</div>");
        }

        sb.append("</p>");
        sb.append("</div>");

        return sb.toString();
    }

    public void reset() {
        _focusedTab = null;

        _propPanel.reset();
        _docPanel.setHtml("");
    }

}
