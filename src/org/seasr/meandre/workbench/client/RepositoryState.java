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

import java.util.HashSet;
import java.util.Set;

import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBFlowDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBLocation;
import org.seasr.meandre.workbench.client.callbacks.WBCallback;
import org.seasr.meandre.workbench.client.rpc.IRepository;
import org.seasr.meandre.workbench.client.rpc.IRepositoryAsync;

import com.allen_sauer.gwt.log.client.Log;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.ObjectFieldDef;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.MessageBox;

/**
 * A singleton class that maintains the state of the repository.
 * It stores the list of components, list of flows, and list of locations with their specific metadata.
 *
 * @author Boris Capitanu
 *
 */
public class RepositoryState {

    // reference to the RPC interface providing repository access on Meandre server
    public static final IRepositoryAsync Repository = IRepository.Util.getInstance();

    private static RepositoryState _instance = null;

    // State stores
    private final Store _componentsStore = new GroupingStore();
    private final Store _componentsStoreFull = new GroupingStore();
    private final Store _flowsStore = new GroupingStore();
    private final Store _flowsStoreFull = new GroupingStore();
    private Store _locationsStore;

    private ArrayReader _compsReader;
    private ArrayReader _flowsReader;
    private ArrayReader _locationsReader;

    /**
     * Provides access to the singleton instance of RepositoryState
     *
     * @return The singleton RepositoryState instance
     */
    public static RepositoryState getInstance() {
        if (_instance == null)
            _instance = new RepositoryState();

        return _instance;
    }

    /**
     * Private constructor - initializes the state stores
     */
    private RepositoryState() {
        initComponentsStore();
        initFlowsStore();
        initLocationsStore();
    }

    /**
     * Initializes the Components store - creates the field definitions for the metadata
     * associated with components that the workbench might need to access later.
     */
    private void initComponentsStore() {
        RecordDef recordDef = new RecordDef(new FieldDef[] {
                new ObjectFieldDef("wbComponent"),      // the WBExecutableComponentDescription object for the component
                new StringFieldDef("componentURI"),
                new StringFieldDef("name"),
                new StringFieldDef("description"),
                new StringFieldDef("rights"),
                new StringFieldDef("creator"),
                new DateFieldDef("creationDate"),
                new StringFieldDef("runnable"),
                new StringFieldDef("firingPolicy"),
                new StringFieldDef("format"),
                new ObjectFieldDef("context"),
                new StringFieldDef("location"),
                new ObjectFieldDef("inputs"),
                new ObjectFieldDef("outputs"),
                new ObjectFieldDef("properties"),
                new ObjectFieldDef("tags")
            });

        _compsReader = new ArrayReader(recordDef);
        _compsReader.setId(1);  // use "componentURI" as the ID

        _componentsStore.setReader(_compsReader);
        _componentsStore.setSortInfo(new SortState("name", SortDir.ASC));

        _componentsStoreFull.setReader(_compsReader);
        _componentsStoreFull.setSortInfo(new SortState("name", SortDir.ASC));
    }

    /**
     * Initializes the Flows store - creates the field definitions for the metadata
     * associated with flows that the workbench might need to access later.
     */
    private void initFlowsStore() {
        RecordDef recordDef = new RecordDef(new FieldDef[] {
                new ObjectFieldDef("wbFlow"),       // the WBFlowDescription object for the flow
                new StringFieldDef("flowURI"),
                new StringFieldDef("name"),
                new StringFieldDef("description"),
                new StringFieldDef("rights"),
                new StringFieldDef("creator"),
                new DateFieldDef("creationDate"),
                new ObjectFieldDef("compInstances"),
                new ObjectFieldDef("connectorDescriptions"),
                new ObjectFieldDef("tags")
            });

        _flowsReader = new ArrayReader(recordDef);
        _flowsReader.setId(1);  // use "flowURI" as the ID

        _flowsStore.setReader(_flowsReader);
        _flowsStore.setSortInfo(new SortState("name", SortDir.ASC));

        _flowsStoreFull.setReader(_flowsReader);
        _flowsStoreFull.setSortInfo(new SortState("name", SortDir.ASC));
    }

    /**
     * Initializes the Locations store - creates the field definitions for the metadata
     * associated with locations that the workbench might need to access later.
     */
    private void initLocationsStore() {
        RecordDef recordDef = new RecordDef(new FieldDef[] {
                new ObjectFieldDef("wbLocation"),       // the WBLocation object for the location
                new StringFieldDef("description"),
                new StringFieldDef("url")
        });

        _locationsReader = new ArrayReader(recordDef);
        _locationsReader.setId(2);  // use "url" as the ID

        _locationsStore = new Store(_locationsReader);
        _locationsStore.setSortInfo(new SortState("description", SortDir.ASC));
    }

    /**
     * Retrieves the components store
     *
     * @return The components store
     */
    public Store getComponentsStore() {
        return _componentsStore;
    }

    /**
     * Retrieves all components in the store
     *
     * @return The set of all components
     */
    public Set<WBExecutableComponentDescription> getComponents() {
        Set<WBExecutableComponentDescription> components =
            new HashSet<WBExecutableComponentDescription>(_componentsStoreFull.getCount());

        for (Record record : _componentsStoreFull.getRecords())
            components.add(getComponent(record));

        return components;
    }

    /**
     * Retrieves a component by its URI
     *
     * @param compURI The URI of the component sought
     * @return The component sought, or null if none found
     */
    public WBExecutableComponentDescription getComponent(String compURI) {
        WBExecutableComponentDescription component = getComponent(_componentsStoreFull.getById(compURI));
        if (component == null)
            Log.warn("The attempt to retrieve component " + compURI + " failed");
        return component;
    }

    /**
     * Retrieves a component from its store record
     *
     * @param record The record associated with the component
     * @return The component, or null if record was null
     */
    private WBExecutableComponentDescription getComponent(Record record) {
        return (record != null) ? (WBExecutableComponentDescription) record.getAsObject("wbComponent") : null;
    }

    public boolean removeComponent(WBExecutableComponentDescription component) {
    	boolean success = false;
    	Record toRemove = null;

    	for (Record record : _componentsStore.getRecords()) {
    		if (component.getResourceURI().equals(getComponent(record).getResourceURI())) {
    			toRemove = record;
    			break;
    		}
    	}

    	if (toRemove != null) {
    		_componentsStore.remove(toRemove);
    		success = true;
    		toRemove = null;
    	}

    	for (Record record : _componentsStoreFull.getRecords()) {
    		if (component.getResourceURI().equals(getComponent(record).getResourceURI())) {
    			toRemove = record;
    			break;
    		}
    	}

    	if (toRemove != null) {
    		_componentsStoreFull.remove(toRemove);
    		success = true;
    	}

    	return success;
    }

    /**
     * Retrieves the flows store
     *
     * @return The flows store
     */
    public Store getFlowsStore() {
        return _flowsStore;
    }

    /**
     * Retrieves all flows in the store
     *
     * @return The set of all flows
     */
    public Set<WBFlowDescription> getFlows() {
        Set<WBFlowDescription> flows =
            new HashSet<WBFlowDescription>(_flowsStoreFull.getCount());

        for (Record record : _flowsStoreFull.getRecords())
            flows.add(getFlow(record));

        return flows;
    }

    /**
     * Retrieves a flow by its URI
     *
     * @param flowURI The URI of the flow sought
     * @return The flow sought, or null if none found
     */
    public WBFlowDescription getFlow(String flowURI) {
        return getFlow(_flowsStoreFull.getById(flowURI));
    }

    /**
     * Retrieves a flow from its store record
     *
     * @param record The record associated with the flow
     * @return The flow, or null if record was null
     */
    private WBFlowDescription getFlow(Record record) {
        return (record != null) ? (WBFlowDescription) record.getAsObject("wbFlow") : null;
    }

    public boolean removeFlow(WBFlowDescription flow) {
    	boolean success = false;
    	Record toRemove = null;

    	for (Record record : _flowsStore.getRecords()) {
    		if (flow.getFlowURI().equals(getFlow(record).getFlowURI())) {
    			toRemove = record;
    			break;
    		}
    	}

    	if (toRemove != null) {
    		_flowsStore.remove(toRemove);
    		success = true;
    		toRemove = null;
    	}

    	for (Record record : _flowsStoreFull.getRecords()) {
    		if (flow.getFlowURI().equals(getFlow(record).getFlowURI())) {
    			toRemove = record;
    			break;
    		}
    	}

    	if (toRemove != null) {
    		_flowsStoreFull.remove(toRemove);
    		success = true;
    	}

    	return success;
    }

    /**
     * Adds a flow to the store
     *
     * @param flow The flow to be added
     */
    public void addFlow(WBFlowDescription flow) {
        // check if the flow already exists
        Record storedFlow = _flowsStore.getById(flow.getFlowURI());
        Record storedFlowFull = _flowsStoreFull.getById(flow.getFlowURI());

        if (storedFlowFull != null) {
            // ...and if it does, remove it so it can be replaced
            Log.warn("Flow " + flow.getFlowURI() + " already exists in store - replacing...");
            _flowsStore.remove(storedFlow);
            _flowsStoreFull.remove(storedFlowFull);
        }
        else
            Log.info("Adding flow " + flow.getFlowURI());

        // add the flow to the store
        Record newFlow = _flowsReader.getRecordDef().createRecord(flow.getFlowURI(), new Object[] {
            flow,
            flow.getFlowURI(),
            flow.getName(),
            flow.getDescription(),
            flow.getRights(),
            flow.getCreator(),
            flow.getCreationDate(),
            flow.getExecutableComponentInstances(),
            flow.getConnectorDescriptions(),
            flow.getTags()
        });

        Record newFlowFull = _flowsReader.getRecordDef().createRecord(flow.getFlowURI(), new Object[] {
            flow,
            flow.getFlowURI(),
            flow.getName(),
            flow.getDescription(),
            flow.getRights(),
            flow.getCreator(),
            flow.getCreationDate(),
            flow.getExecutableComponentInstances(),
            flow.getConnectorDescriptions(),
            flow.getTags()
        });

        _flowsStore.addSorted(newFlow);
        _flowsStoreFull.addSorted(newFlowFull);
    }

    /**
     * Retrieves the locations store
     *
     * @return The locations store
     */
    public Store getLocationsStore() {
        return _locationsStore;
    }

    /**
     * Retrieves all locations in the store
     *
     * @return The set of all locations
     */
    public Set<WBLocation> getLocations() {
        Set<WBLocation> locations = new HashSet<WBLocation>(_locationsStore.getCount());
        for (Record record : _locationsStore.getRecords())
            locations.add(getLocation(record));

        return locations;
    }

    /**
     * Retrieves a location by its url
     *
     * @param url The url for the location sought
     * @return The location sought
     */
    public WBLocation getLocation(String url) {
        return getLocation(_locationsStore.getById(url));
    }

    /**
     * Retrieves a location from its store record
     *
     * @param record The record associated with the location
     * @return The location, or null if the record was null
     */
    private WBLocation getLocation(Record record) {
        return (record != null) ? (WBLocation) record.getAsObject("wbLocation") : null;
    }

    /**
     * Adds a location to the store
     *
     * @param location The location to be added
     */
    public void addLocation(WBLocation location) {
        // check whether the location already exists
        Record storeLocation = _locationsStore.getById(location.getLocation());
        if (storeLocation == null) {
            Log.info("Adding location " + location.getLocation());

            _locationsStore.addSorted(_locationsReader.getRecordDef().createRecord(location.getLocation(),
                    new Object[] {
                        location,
                        location.getDescription(),
                        location.getLocation()
                    }
            ));
        } else
            Log.warn("Location already found! store: " + getLocation(storeLocation).getLocation() +
                    " param: " + location.getLocation());
    }

    /**
     * Performs a refresh of the repository
     * This effectively loads all the components, flows, and locations from the server into the stores anew.
     *
     * @param cmd The command to be executed afterwards, or null if none
     */
    public void refresh(final ICommand<?> cmd) {
        Repository.clearCache(new WBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Log.debug("[1/4] Cache cleared");

                Repository.retrieveComponentDescriptors(new WBCallback<Set<WBExecutableComponentDescription>>() {
                    @Override
                    public void onSuccess(Set<WBExecutableComponentDescription> components) {
                        Log.debug("[2/4] Component descriptors retrieved");

                        loadComponentsStore(components);

                        Repository.retrieveFlowDescriptors(new WBCallback<Set<WBFlowDescription>>() {
                            @Override
                            public void onSuccess(Set<WBFlowDescription> flows) {
                                Log.debug("[3/4] Flow descriptors retrieved");

                                loadFlowsStore(flows);

                                Repository.retrieveLocations(new WBCallback<Set<WBLocation>>() {
                                    @Override
                                    public void onSuccess(Set<WBLocation> locations) {
                                        Log.debug("[4/4] Locations retrieved");

                                        loadLocationsStore(locations);

                                        if (cmd != null)
                                            cmd.execute(null);
                                    };
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    /**
     * Populates the components store
     *
     * @param components The set of components to load into the store
     */
    private void loadComponentsStore(Set<WBExecutableComponentDescription> components) {
        if (components.contains(null)) {
            Log.warn("'null' component retrieved - this could be an indication of a corrupt component!");
            showRepositoryCorruptWarning("component");
            components.remove(null);
        }

        Object[][] data = new Object[components.size()][];
        int i = 0;

        for (WBExecutableComponentDescription comp : components) {
            data[i++] = new Object[] {
                    comp,
                    comp.getResourceURI(),
                    comp.getName(),
                    comp.getDescription(),
                    comp.getRights(),
                    comp.getCreator(),
                    comp.getCreationDate(),
                    comp.getRunnable(),
                    comp.getFiringPolicy(),
                    comp.getFormat(),
                    comp.getContext(),
                    comp.getLocation(),
                    comp.getInputs(),
                    comp.getOutputs(),
                    comp.getProperties(),
                    comp.getTags()
            };
        }

        MemoryProxy memoryProxy = new MemoryProxy(data);
        _componentsStore.setDataProxy(memoryProxy);
        _componentsStore.load();

        _componentsStoreFull.setDataProxy(memoryProxy);
        _componentsStoreFull.load();
    }

    /**
     * Populates the flows store
     *
     * @param flows The set of flows to load into the store
     */
    private void loadFlowsStore(Set<WBFlowDescription> flows) {
        if (flows.contains(null)) {
            Log.warn("'null' flow retrieved - this could be an indication of a corrupt flow!");
            showRepositoryCorruptWarning("flow");
            flows.remove(null);
        }

        Object[][] data = new Object[flows.size()][];
        int i = 0;

        for (WBFlowDescription flow : flows) {
            if (flow == null) {
                continue;
            }

            data[i++] = new Object[] {
                    flow,
                    flow.getFlowURI(),
                    flow.getName(),
                    flow.getDescription(),
                    flow.getRights(),
                    flow.getCreator(),
                    flow.getCreationDate(),
                    flow.getExecutableComponentInstances(),
                    flow.getConnectorDescriptions(),
                    flow.getTags()
            };
        }

        MemoryProxy memoryProxy = new MemoryProxy(data);
        _flowsStore.setDataProxy(memoryProxy);
        _flowsStore.load();

        _flowsStoreFull.setDataProxy(memoryProxy);
        _flowsStoreFull.load();
    }

    /**
     * Populates the locations store
     *
     * @param locations The set of locations to load into the store
     */
    private void loadLocationsStore(Set<WBLocation> locations) {
        if (locations.contains(null)) {
            Log.warn("'null' location retrieved - this could be an indication of a corrupt repository!");
            showRepositoryCorruptWarning("location");
            locations.remove(null);
        }

        Object[][] data = new Object[locations.size()][];
        int i = 0;

        for (WBLocation location : locations) {
            data[i++] = new Object[] {
                    location,
                    location.getDescription(),
                    location.getLocation()
            };
        }

        _locationsStore.setDataProxy(new MemoryProxy(data));
        _locationsStore.load();
    }

    /**
     * Shows a warning message to the user indicating that there is a good chance
     * that the repository has become corrupted.
     */
    private void showRepositoryCorruptWarning(String target) {
        Application.showMessage("Repository Problem", "A possible corrupted " + target + " was detected. Some " + target + "s will not be available.",
                MessageBox.WARNING, MessageBox.OK);
    }
}
