package org.meandre.workbench.server;

//==============
// Java Imports
//==============
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//===============
// Other Imports
//===============

import org.meandre.workbench.client.beans.*;
import org.meandre.workbench.server.proxy.beans.repository.*;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MeandreToWBBeanConverter {

//==============
// Data Members
//==============

//==============
// Constructors
//==============

    public MeandreToWBBeanConverter() {
    }

//================
// Public Methods
//================

    static public WBFlow convertFlow(FlowDescription
                                     flow, QueryableRepository qr) {
        String _id = flow.getFlowComponent().getURI();
        String sName = flow.getName();
        String sDescription = flow.getDescription();
        String sRights = flow.getRights();
        String sCreator = flow.getCreator();
        Date dateCreation = flow.getCreationDate();
        Set setInstances = new HashSet();
        Set setConns = new HashSet();

        Set insts = flow.getExecutableComponentInstances();
        Set conns = flow.getConnectorDescriptions();

        for (Iterator itty = insts.iterator(); itty.hasNext(); ) {
            ExecutableComponentInstanceDescription ecid = (
                    ExecutableComponentInstanceDescription) itty.next();
            setInstances.add(convertComponentInst(ecid, qr));
        }
        for (Iterator itty = conns.iterator(); itty.hasNext(); ) {
            setConns.add(convertConnection((ConnectorDescription) itty.next()));
        }
        WBTags tagDesc = convertTags(flow.getTags());

        WBFlow f = new WBFlow(_id, sName, sDescription, sRights, sCreator,
                              dateCreation, setInstances, setConns, tagDesc);
   //        try {
   //            f.setBaseURL(new File(".").toURI().toString());
   //            if (f.getBaseURL().endsWith("./")){
   //                f.setBaseURL(f.getBaseURL().substring(0, f.getBaseURL().length()-2));
   //            }
   //        } catch (Exception e){
   //            f.setBaseURL("");
   //        }
        f.setBaseURL("http://mydomain.org/components/");
        return f;
    }

    static public WBComponentInstance convertComponentInst(
            ExecutableComponentInstanceDescription
            ecid, QueryableRepository qr) {

        String _id = ecid.getExecutableComponentInstance().getURI();
        String sName = ecid.getName();
        String sDescription = ecid.getDescription();
        ExecutableComponentDescription ecd = qr.
                                             getExecutableComponentDescription(
                ecid.getExecutableComponent());

        WBProperties pddProperties = convertProperties(ecid.getProperties());
        return new WBComponentInstance(_id, convertComponent(ecd), sName,
                                       sDescription, pddProperties);
    }

    static public WBComponent convertComponent(ExecutableComponentDescription
                                               ecd) {
        String _id = ecd.getExecutableComponent().getURI();
        String sName = ecd.getName();
        String sDescription = ecd.getDescription();
        String sRights = ecd.getRights();
        String sCreator = ecd.getCreator();
        Date dateCreation = ecd.getCreationDate();
        String sRunnable = ecd.getRunnable();
        String sFiringPolicy = ecd.getFiringPolicy();
        String sFormat = ecd.getFormat();
        String sLocation = ecd.getLocation().getURI();
        Set setContext = new HashSet();
        Set setInputs = new HashSet();
        Set setOutputs = new HashSet();

        Set context = ecd.getContext();
        Set inputs = ecd.getInputs();
        Set outputs = ecd.getOutputs();

        for (Iterator itty = context.iterator(); itty.hasNext(); ) {
            setContext.add(((Resource) itty.next()).getURI());
        }

        /* pre-process for tree display. Saves work at the client and allows
                 server to log malformatted components.*/
        boolean bChomped = false;
        String sURI = sLocation.trim();
        for (Iterator itty = setContext.iterator(); itty.hasNext(); ) {
            String sCntx = ((String) itty.next()).trim();
            if (sURI.startsWith(sCntx)) {
                bChomped = true;
                sLocation = sURI.substring(sCntx.length());
            }
        }
        // Check if location does not match any context
        if (!bChomped) {
            System.err.println("Location " + sURI +
                               " does not match any of the executable component contexts");
            sLocation = "UNKNOWN";
        }

        for (Iterator itty = inputs.iterator(); itty.hasNext(); ) {
            setInputs.add(convertDataport((DataPortDescription) itty.next()));
        }
        for (Iterator itty = outputs.iterator(); itty.hasNext(); ) {
            setOutputs.add(convertDataport((DataPortDescription) itty.next()));
        }
        WBPropertiesDefinition pddProperties = convertPropertiesDefn(ecd.
                getProperties());
        WBTags tagDesc = convertTags(ecd.getTags());

        return new WBComponent(_id, sName, sDescription, sRights, sCreator,
                               dateCreation, sRunnable, sFiringPolicy, sFormat,
                               sLocation, setContext, setInputs, setOutputs,
                               pddProperties, tagDesc);
    }

    static public WBDataport convertDataport(DataPortDescription port) {
        String _compID = port.getResource().getURI();
        String sIdentifier = port.getIdentifier();
        String sName = port.getName();
        String sDescription = port.getDescription();
        return new WBDataport(_compID, sIdentifier, sName, sDescription);
    }

    static public WBComponentConnection convertConnection(ConnectorDescription
            conn) {
        String _connID = conn.getConnector().getURI();
        String _src = conn.getSourceInstance().getURI();
        String _srcP = conn.getSourceIntaceDataPort().getURI();
        String _targ = conn.getTargetInstance().getURI();
        String _targP = conn.getTargetIntaceDataPort().getURI();
        return new WBComponentConnection(_connID, _src, _srcP, _targ, _targP);
    }

    static public WBTags convertTags(TagsDescription tags) {
        return new WBTags(tags.getTags());
    }

    static public WBPropertiesDefinition convertPropertiesDefn(
            PropertiesDescriptionDefinition pdd) {
        Map vals = new HashMap();
        Map descs = new HashMap();

        Set keys = pdd.getKeys();
        for (Iterator itty = keys.iterator(); itty.hasNext(); ) {
            String key = (String) itty.next();
            String val = pdd.getValue(key);
            String def = pdd.getDescription(key);
            vals.put(key, val);
            descs.put(key, def);
        }
        return new WBPropertiesDefinition(vals, descs);
    }

    static public WBProperties convertProperties(
            PropertiesDescription pd) {
        Map vals = new HashMap();

        Set keys = pd.getKeys();
        for (Iterator itty = keys.iterator(); itty.hasNext(); ) {
            String key = (String) itty.next();
            String val = pd.getValue(key);
            vals.put(key, val);
        }
        return new WBProperties(vals);
    }

}
