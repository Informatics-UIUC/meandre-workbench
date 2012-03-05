package org.seasr.meandre.workbench.client.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.seasr.meandre.workbench.client.listeners.SettingsListener;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;


public class WBSettings {

    public static final String DEFAULT_SETTINGS = "{ 'compCatColors': [" +
    		"  { 'tag': '#default',   'color': '#BEBADA' }" +
            ", { 'tag': '#analytics', 'color': '#8DD3C7' }" +
            ", { 'tag': '#transform', 'color': '#FFFFB3' }" +
            ", { 'tag': '#input',     'color': '#FB8072' }" +
            ", { 'tag': '#vis',       'color': '#80B1D3' }" +
            ", { 'tag': '#control',   'color': '#FDB462' }" +
            ", { 'tag': '#output',    'color': '#B3DE69' }" +
    		" ] }";

    private final Set<SettingsListener> _actionListeners = new HashSet<SettingsListener>();
    private Map<String, ComponentColor> _compCatColors;

    private WBSettings() { }

    public static WBSettings fromJSON(String json) {
        WBSettings settings = new WBSettings();

        JSONObject joSettings = JSONParser.parse(json).isObject();
        JSONArray jaCompCatColors = joSettings.get("compCatColors").isArray();
        settings._compCatColors = new HashMap<String, ComponentColor>();
        for (int i = 0, iMax = jaCompCatColors.size(); i < iMax; i++) {
            JSONObject joCatColor = jaCompCatColors.get(i).isObject();
            ComponentColor compColor = new ComponentColor(joCatColor.get("color").isString().stringValue());
            settings._compCatColors.put(joCatColor.get("tag").isString().stringValue(), compColor);
        }

        return settings;
    }

    public Map<String, ComponentColor> getComponentCategoryColors() {
        return _compCatColors;
    }

    public void setComponentCategoryColors(Map<String, ComponentColor> compCatColors) {
        _compCatColors = compCatColors;

        for (SettingsListener listener : _actionListeners)
            listener.onComponentCategoryColorsChanged(_compCatColors);
    }

    public String toJSON() {
        JSONArray jaCatColors = new JSONArray();
        int i = 0;
        for (Map.Entry<String, ComponentColor> entry : _compCatColors.entrySet()) {
            JSONObject joCatColor = new JSONObject();
            joCatColor.put("tag", new JSONString(entry.getKey()));
            joCatColor.put("color", new JSONString(entry.getValue().getMainColor()));

            jaCatColors.set(i++, joCatColor);
        }

        JSONObject joSettings = new JSONObject();
        joSettings.put("compCatColors", jaCatColors);

        return joSettings.toString();
    }

    public void addListener(SettingsListener listener) {
        _actionListeners.add(listener);
    }

    public void removeListener(SettingsListener listener) {
        _actionListeners.remove(listener);
    }
}
