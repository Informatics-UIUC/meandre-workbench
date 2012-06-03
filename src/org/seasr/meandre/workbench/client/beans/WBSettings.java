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
    		"  { 'tag': '#default',   'color': '#CCCCCC' }" +
            ", { 'tag': '#analytics', 'color': '#859D38' }" +
            ", { 'tag': '#transform', 'color': '#386CB0' }" +
            ", { 'tag': '#input',     'color': '#994801' }" +
            ", { 'tag': '#vis',       'color': '#8B91BC' }" +
            ", { 'tag': '#control',   'color': '#E6AB02' }" +
            ", { 'tag': '#output',    'color': '#666666' }" +
    		" ] }";

    private final Set<SettingsListener> _actionListeners = new HashSet<SettingsListener>();
    private Map<String, ComponentColor> _compCatColors;

    private WBSettings() { }

    public static WBSettings fromJSON(String json) {
        WBSettings settings = new WBSettings();

        JSONObject joSettings = JSONParser.parseStrict(json).isObject();
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
