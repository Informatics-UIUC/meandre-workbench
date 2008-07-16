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
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentInstanceDescription;
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

/**
 * @author Boris Capitanu
 *
 */
public class RepositoryState {

    public static final IRepositoryAsync Repository = IRepository.Util.getInstance();
    private static RepositoryState _instance = null;

    private final Store _componentsStore = new GroupingStore();
    private final Store _flowsStore = new GroupingStore();
    private Store _locationsStore;

    private ArrayReader _compsReader;
    private ArrayReader _flowsReader;
    private ArrayReader _locationsReader;

    public static RepositoryState getInstance() {
        if (_instance == null)
            _instance = new RepositoryState();

        return _instance;
    }

    private RepositoryState() {
        initComponentsStore();
        initFlowsStore();
        initLocationsStore();
    }

    private void initComponentsStore() {
        RecordDef recordDef = new RecordDef(new FieldDef[] {
                new ObjectFieldDef("wbComponent"),
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
        _compsReader.setId(1);

        _componentsStore.setReader(_compsReader);
        _componentsStore.setSortInfo(new SortState("name", SortDir.ASC));
    }

    private void initFlowsStore() {
        RecordDef recordDef = new RecordDef(new FieldDef[] {
                new ObjectFieldDef("wbFlow"),
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
        _flowsReader.setId(1);

        _flowsStore.setReader(_flowsReader);
        _flowsStore.setSortInfo(new SortState("name", SortDir.ASC));
    }

    private void initLocationsStore() {
        RecordDef recordDef = new RecordDef(new FieldDef[] {
                new ObjectFieldDef("wbLocation"),
                new StringFieldDef("description"),
                new StringFieldDef("url")
        });

        _locationsReader = new ArrayReader(recordDef);
        _locationsReader.setId(2);

        _locationsStore = new Store(_locationsReader);
        _locationsStore.setSortInfo(new SortState("description", SortDir.ASC));
    }

    public Store getComponentsStore() {
        return _componentsStore;
    }

    public Set<WBExecutableComponentDescription> getComponents() {
        Set<WBExecutableComponentDescription> components =
            new HashSet<WBExecutableComponentDescription>(_componentsStore.getCount());

        for (Record record : _componentsStore.getRecords())
            components.add(getComponent(record));

        return components;
    }

    public WBExecutableComponentDescription getComponent(String compURI) {
        return getComponent(_componentsStore.getById(compURI));
    }

    private WBExecutableComponentDescription getComponent(Record record) {
        return (record != null) ? (WBExecutableComponentDescription) record.getAsObject("wbComponent") : null;
    }

    public Store getFlowsStore() {
        return _flowsStore;
    }

    public Set<WBFlowDescription> getFlows() {
        Set<WBFlowDescription> flows =
            new HashSet<WBFlowDescription>(_flowsStore.getCount());

        for (Record record : _flowsStore.getRecords())
            flows.add(getFlow(record));

        return flows;
    }

    public WBFlowDescription getFlow(String flowURI) {
        return getFlow(_flowsStore.getById(flowURI));
    }

    private WBFlowDescription getFlow(Record record) {
        return (record != null) ? (WBFlowDescription) record.getAsObject("wbFlow") : null;
    }

    public void addFlow(WBFlowDescription flow) {
        Record storedFlow = _flowsStore.getById(flow.getFlowURI());

        if (storedFlow != null) {
            _flowsStore.remove(storedFlow);
            _flowsStore.commitChanges();
        }
        else
            Log.info("Adding flow " + flow.getFlowURI());

        for (WBExecutableComponentInstanceDescription instance : flow.getExecutableComponentInstances())
            instance.setExecutableComponentDescription(getComponent(instance.getExecutableComponent()));

        _flowsStore.addSorted(_flowsReader.getRecordDef().createRecord(flow.getFlowURI(), new Object[] {
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
        }));
    }

    public Store getLocationsStore() {
        return _locationsStore;
    }

    public Set<WBLocation> getLocations() {
        Set<WBLocation> locations = new HashSet<WBLocation>(_locationsStore.getCount());
        for (Record record : _locationsStore.getRecords())
            locations.add(getLocation(record));

        return locations;
    }

    public WBLocation getLocation(String url) {
        return getLocation(_locationsStore.getById(url));
    }

    private WBLocation getLocation(Record record) {
        return (record != null) ? (WBLocation) record.getAsObject("wbLocation") : null;
    }

    public void addLocation(WBLocation location) {
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

    public void refresh(final ICommand<?> cmd) {
        Repository.clearCache(new WBCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Repository.retrieveComponentDescriptors(new WBCallback<Set<WBExecutableComponentDescription>>() {
                    @Override
                    public void onSuccess(Set<WBExecutableComponentDescription> components) {

                        loadComponentsStore(components);

                        Repository.retrieveFlowDescriptors(new WBCallback<Set<WBFlowDescription>>() {
                            @Override
                            public void onSuccess(Set<WBFlowDescription> flows) {

                                loadFlowsStore(flows);

                                Repository.retrieveLocations(new WBCallback<Set<WBLocation>>() {
                                    @Override
                                    public void onSuccess(Set<WBLocation> locations) {

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

    private void loadComponentsStore(Set<WBExecutableComponentDescription> components) {
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

        _componentsStore.setDataProxy(new MemoryProxy(data));
        _componentsStore.load();
    }

    private void loadFlowsStore(Set<WBFlowDescription> flows) {
        Object[][] data = new Object[flows.size()][];
        int i = 0;

        for (WBFlowDescription flow : flows) {
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

            for (WBExecutableComponentInstanceDescription instance : flow.getExecutableComponentInstances())
                instance.setExecutableComponentDescription(getComponent(instance.getExecutableComponent()));
        }

        _flowsStore.setDataProxy(new MemoryProxy(data));
        _flowsStore.load();
    }

    private void loadLocationsStore(Set<WBLocation> locations) {
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
}
