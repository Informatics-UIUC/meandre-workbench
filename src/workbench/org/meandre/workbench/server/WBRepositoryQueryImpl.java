package org.meandre.workbench.server;

//==============
// Java Imports
//==============

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;

//===============
// Other Imports
//===============

import com.google.gwt.user.server.rpc.*;
import com.hp.hpl.jena.rdf.model.*;
//import org.meandre.core.repository.*;
import org.meandre.workbench.client.*;
import org.meandre.workbench.client.beans.*;
//import org.meandre.core.util.RepositoryFactory;
//import org.meandre.core.security.User;

/**
 * <p>Title: Workbench Repository Query Implementation</p>
 *
 * <p>Description: This class implements the Workbench Repository Query
 * interface for talking to the Meandre back-end.</p>
 *
 * <p>Copyright: UIUC Copyright (c) 2007</p>
 *
 * <p>Company: Automated Learning Group at NCSA, UIUC</p>
 *
 * @author Duane Searsmith
 * @version 1.0
 */
public class WBRepositoryQueryImpl extends RemoteServiceServlet /*implements
        WBRepositoryQuery*/ {

    //==============
    // Data Members
    //==============

    //================
    // Constructor(s)
    //================

    public WBRepositoryQueryImpl() {

    }

    //=================
    // Private Methods
    //=================

//    synchronized private QueryableRepository getRepository() {
//        QueryableRepository qr = null;
//        try {
//            qr = ((RepositoryFactory) getThreadLocalRequest().
//                                      getSession().getAttribute(
//                    "repository-factory")).getRepository();
//        } catch(CorruptedDescriptionException cde){
//            cde.printStackTrace();
//        }
//        return qr;
//    }
//
//    synchronized private QueryableRepository getWorkRepository() {
//        QueryableRepository qr = null;
//        String sPath = File.separator +
//                       "resources" + File.separator +
//                       "jetty" + File.separator +
//                       "meandre-app" + File.separator +
//                       "components" + File.separator +
//                       "description" + File.separator;
//        try {
//            sPath = new File(".").getCanonicalPath() + sPath;
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//            qr = ((RepositoryFactory) getThreadLocalRequest().
//                                      getSession().getAttribute(
//                    "repository-factory")).getComponentsInDirectory(sPath, ".rdf");
//        return qr;
//    }
//
//    synchronized private RepositoryFactory getWorkRepositoryFact() {
//        RepositoryFactory qr = null;
//            qr = (RepositoryFactory)getThreadLocalRequest().getSession().getAttribute("repository-factory");
//        return qr;
//    }

    //===================================
    // Interface Impl: WBRepositoryQuery
    //===================================

    /**
     * Check to see if there is a user logged into this session.  Return
     * the user name else null.
     *
     * @return String The user name or null.
     */
//    public String getUser(){
//        User user =  (User)getThreadLocalRequest().getSession().getAttribute("user");
//        if (user == null){
//            return null;
//        } else {
//            String ret = user.getNickName();
//            if (ret != null){
//                return ret;
//            }
//            return user.getName();
//        }
//    }

    /**
     * Get the active components in the current user's repository that match
     * the search criteria.
     *
     * @param search String The search string for this query.
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
     * @return Set Returns set of active components matching search query.
     */
//    public Set getActiveComponents(String search) {
//            //acquire Repository object from current session
//            QueryableRepository queryableRep = getWorkRepository();
//            Set ret = new HashSet();
//            Set comps = queryableRep.getAvailableExecutableComponents(search);
//            for (Iterator itty = comps.iterator(); itty.hasNext(); ) {
//                Resource res = (Resource) itty.next();
//                ExecutableComponentDescription ecd = queryableRep.
//                                                     getExecutableComponentDescription(
//                        res);
//                ret.add(MeandreToWBBeanConverter.convertComponent(ecd));
//            }
//
//            comps = queryableRep.getAvailableFlows(search);
//            for (Iterator itty = comps.iterator(); itty.hasNext(); ) {
//                Resource res = (Resource) itty.next();
//                FlowDescription flow = queryableRep.getFlowDescription(res);
//                ret.add(MeandreToWBBeanConverter.convertFlow(flow, queryableRep));
//            }
//            return ret;
//    }

    /**
     * Returns the set of active components in the repository.
     *
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBComponent>
     * @return Set Returns set of active components.
     */
//    public Set getActiveComponents() {
//
//        //acquire Repository object from current session
//        QueryableRepository queryableRep = getRepository();
//        Set ret = new HashSet();
//        Set comps = queryableRep.getAvailableExecutableComponents();
//        for (Iterator itty = comps.iterator(); itty.hasNext(); ) {
//            Resource res = (Resource) itty.next();
//            ExecutableComponentDescription ecd = queryableRep.
//                                                 getExecutableComponentDescription(
//                    res);
//            ret.add(MeandreToWBBeanConverter.convertComponent(ecd));
//        }
//
//        return ret;
//    }

    /**
     *
     * @gwt.typeArgs <org.meandre.workbench.client.beans.WBFlow>
     */
//    public Set getActiveFlows() {
//
//        //acquire Repository object from current session
//        QueryableRepository queryableRep = getWorkRepository();
//        Set ret = new HashSet();
//        Set flows = queryableRep.getAvailableFlows();
//        for (Iterator itty = flows.iterator(); itty.hasNext(); ) {
//            Resource res = (Resource) itty.next();
//            FlowDescription flow = queryableRep.getFlowDescription(res);
//            ret.add(MeandreToWBBeanConverter.convertFlow(flow, queryableRep));
//        }
//        return ret;
//    }

    /**
     * Saves the flow and returns the callback object.
     *
     * @return WBCallbackObject Returns callback object.
     */
//    public WBCallbackObject saveFlow(WBFlow flow) {
//        WBCallbackObject wbc = new WBCallbackObject();
//        //acquire Repository object from current session
//        RepositoryFactory queryableRep = getWorkRepositoryFact();
//
//        //publish flow
//        String sDir = null;
//
//        try {
//            String sPath = File.separator +
//                           "resources" + File.separator +
//                           "jetty" + File.separator +
//                           "meandre-app" + File.separator +
//                           "components" + File.separator +
//                           "description" + File.separator;
//
//            sDir = new File(".").getCanonicalPath() + sPath;
//
//            // Convert WBFlow to FlowDescription
//
//            Model model = ModelFactory.createDefaultModel();
//            Resource resExecutableComponent = null;
//            if (flow.getBaseURL().trim().length() > 0){
//                resExecutableComponent = model.createResource(flow.getBaseURL());
//            } else {
//                resExecutableComponent = model.createResource("file://");
//            }
//            Set instances = new HashSet();
//            for (Iterator itty = flow.getExecutableComponentInstances().
//                                 iterator(); itty.hasNext(); ) {
//                WBComponentInstance ci = (WBComponentInstance) itty.next();
//                model = ModelFactory.createDefaultModel();
//                Resource res1 = model.createResource(ci.
//                        getExecutableComponentInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res2 = model.createResource(ci.getExecutableComponent().getID());
//                PropertiesDescription pd = new PropertiesDescription(new
//                        Hashtable(ci.getProperties().getValuesMap()));
//                ExecutableComponentInstanceDescription ecid = new
//                        ExecutableComponentInstanceDescription(res1,
//                        res2,
//                        ci.getName(),
//                        ci.getDescription(),
//                        pd);
//                instances.add(ecid);
//
//            }
//            Set connections = new HashSet();
//            for (Iterator itty = flow.getConnectorDescriptions().
//                                 iterator(); itty.hasNext(); ) {
//                WBComponentConnection cc = (WBComponentConnection) itty.next();
//                model = ModelFactory.createDefaultModel();
//                Resource res1 = model.createResource(cc.getConnector());
//                model = ModelFactory.createDefaultModel();
//                Resource res2 = model.createResource(cc.getSourceInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res3 = model.createResource(cc.getSourceIntanceDataPort());
//                model = ModelFactory.createDefaultModel();
//                Resource res4 = model.createResource(cc.getTargetInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res5 = model.createResource(cc.getTargetIntanceDataPort());
//                ConnectorDescription cd = new ConnectorDescription(res1,
//                                          res2,
//                                          res3,
//                                          res4,
//                                          res5);
//                connections.add(cd);
//
//            }
//            FlowDescription flowdesc = new FlowDescription(
//                    resExecutableComponent,
//                    flow.getName(),
//                    flow.getDescription(),
//                    flow.getRights(),
//                    flow.getCreator(),
//                    flow.getCreationDate(),
//                    instances,
//                    connections,
//                    new TagsDescription(new HashSet(flow.getTags().getTags())));
//
//
//
//            //write file
//            queryableRep.publishFlowToDirectory(sDir, flowdesc);
//            wbc.setMessage(flowdesc.getFlowComponent().getURI());
//
//            /* This is necessary for the web app. */
//            queryableRep.flush();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            wbc.setMessage(e.getMessage());
//            //log.warning("Could not write the system model!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            wbc.setMessage(e.getMessage());
//            //log.warning("Could not write the system model!");
//        }
//
//        return wbc;
//    }

    /**
     * Saves the flow and returns the callback object.
     *
     * @return WBCallbackObject Returns callback object.
     */
//    public WBCallbackObject publishFlow(WBFlow flow) {
//        WBCallbackObject wbc = new WBCallbackObject();
//
//        //acquire Repository object from current session
//        QueryableRepository queryableRep = getRepository();
//
//        //publish flow
//    //        File savedFile = null;
//    //        String sDir = null;
//    //        String sFileName = null;
//    //
//    //        try {
//    //            String sPath = File.separator +
//    //                           "resources" + File.separator +
//    //                           "jetty" + File.separator +
//    //                           "meandre-app" + File.separator +
//    //                           "components" + File.separator +
//    //                           "description" + File.separator;
//    //
//    //            sDir = new File(".").getCanonicalPath() + sPath;
//    //
//    //            sFileName = "component-flow-" +
//    //                        (new Random().nextInt()) + "_" + flow.getName().replaceAll(" ", "_") + ".rdf";
//    //
//    //            // Write the new file down
//    //            savedFile = new File(sDir + sFileName);
//
//            // Convert WBFlow to FlowDescription
//
//            Model model = ModelFactory.createDefaultModel();
//            Resource resExecutableComponent = model.createResource(flow.getFlowID());
//            Set instances = new HashSet();
//            for (Iterator itty = flow.getExecutableComponentInstances().
//                                 iterator(); itty.hasNext(); ) {
//                WBComponentInstance ci = (WBComponentInstance) itty.next();
//                model = ModelFactory.createDefaultModel();
//                Resource res1 = model.createResource(ci.
//                        getExecutableComponentInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res2 = model.createResource(ci.getExecutableComponent().getID());
//                PropertiesDescription pd = new PropertiesDescription(new
//                        Hashtable(ci.getProperties().getValuesMap()));
//                ExecutableComponentInstanceDescription ecid = new
//                        ExecutableComponentInstanceDescription(res1,
//                        res2,
//                        ci.getName(),
//                        ci.getDescription(),
//                        pd);
//                instances.add(ecid);
//
//            }
//            Set connections = new HashSet();
//            for (Iterator itty = flow.getConnectorDescriptions().
//                                 iterator(); itty.hasNext(); ) {
//                WBComponentConnection cc = (WBComponentConnection) itty.next();
//                model = ModelFactory.createDefaultModel();
//                Resource res1 = model.createResource(cc.getConnector());
//                model = ModelFactory.createDefaultModel();
//                Resource res2 = model.createResource(cc.getSourceInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res3 = model.createResource(cc.getSourceIntanceDataPort());
//                model = ModelFactory.createDefaultModel();
//                Resource res4 = model.createResource(cc.getTargetInstance());
//                model = ModelFactory.createDefaultModel();
//                Resource res5 = model.createResource(cc.getTargetIntanceDataPort());
//                ConnectorDescription cd = new ConnectorDescription(res1,
//                                          res2,
//                                          res3,
//                                          res4,
//                                          res5);
//                connections.add(cd);
//
//            }
//            FlowDescription flowdesc = new FlowDescription(
//                    resExecutableComponent,
//                    flow.getName(),
//                    flow.getDescription(),
//                    flow.getRights(),
//                    flow.getCreator(),
//                    flow.getCreationDate(),
//                    instances,
//                    connections,
//                    new TagsDescription(flow.getTags().getTags()));
//
//            //write file
//    //            FileWriter fw = new FileWriter(savedFile);
//    //            flowdesc.getModel().write(fw);
//    //            fw.close();
//
//        // Check file
//    //        Model model = ModelFactory.createDefaultModel();
//    //        try {
//    //            model.read(new FileInputStream(new File(sDir + sFileName)), null);
//    //        } catch (Exception e) {
//    //            e.printStackTrace();
//    //            new File(sDir + sFileName).delete();
//    //        }
//
//        //add model to repository
//        try {
//            Model modelTmp = ModelFactory.createDefaultModel();
//
//            modelTmp.setNsPrefix("", "http://www.meandre.org/ontology/");
//            modelTmp.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
//            modelTmp.setNsPrefix("rdfs",
//                                 "http://www.w3.org/2000/01/rdf-schema#");
//            modelTmp.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
//
//            FileReader fr = new FileReader(new File(flow.getFlowID()));
//            modelTmp.read(fr, null);
//            fr.close();
//
//            queryableRep.getModel().add(modelTmp);
//            queryableRep.refreshCache();
//
//            wbc.setSuccess(true);
//        } catch (Exception e) {
//            wbc.setMessage(e.getMessage());
//            e.printStackTrace();
//        }
//
//        return wbc;
//    }

}
