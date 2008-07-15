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

package org.seasr.meandre.workbench.client.beans.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.meandre.core.repository.CorruptedDescriptionException;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WBExecutableComponentDescription implements IsSerializable {

    /** The URI of the resource describing compute components */
    public final static String COMPUTE_COMPONENT = "http://www.meandre.org/ontology/component/type/compute";

    /** The URI of the resource describing compute components */
    public final static String WEBUI_COMPONENT = "http://www.meandre.org/ontology/component/type/webui";


    /** The resource URI of the executable component */
    private String sResURI = null;

    /** The name of the executable component */
    private String sName = null;

    /** The description of the executable component */
    private String sDescription = null;

    /** The rights of the executable component */
    private String sRights = null;

    /** The creator of the executable component */
    private String sCreator = null;

    /** The date of the executable component */
    private Date dateCreation = null;

    /** What kind of runnable component is being described */
    private String sRunnable = null;

    /** What is the firing policy */
    private String sFiringPolicy = null;

    /** What executable format does the runnable component take */
    private String sFormat = null;

    /** The set of context URIs required for execution */
    private Set<String> setContext = null;

    /** The location URI pointing to the executable component implementation */
    private String sLocationURI = null;

    /** The set of input data ports */
    private Set<WBDataPortDescription> setInputs = null;

    /** The hash table for input resource mapping */
    private Map<String, WBDataPortDescription> htInputsMap = null;

    /** The hash table for output resource mapping */
    private Map<String, WBDataPortDescription> htOutputsMap = null;

    /** The set of output data ports */
    private Set<WBDataPortDescription> setOutputs = null;

    /** The property descriptions */
    private WBPropertiesDescriptionDefinition pddProperties = null;

    /** The tag description */
    private WBTagsDescription tagDesc = null;

    /** The component mode */
    private String resMode = null;

    public WBExecutableComponentDescription() {}

    /** Describes an executable component.
     *
     * @param sResURI The resource URI of the wrapped executable component
     * @param sName The name of the component
     * @param sDescription The description of the component
     * @param sRights The rights of the component
     * @param sCreator The creator
     * @param dateCreation The data of creation
     * @param sRunnable What kind of runnable component it is
     * @param sFiringPolicy The firing policy
     * @param sFormat The format for the implementation of the component
     * @param setContext The set of context URIs of the component
     * @param sLocationURI The location URI of the implementation of the component
     * @param setInputs The set of input data ports
     * @param setOutputs The set of output data ports
     * @param pddProperties
     * @param tagDesc
     * @throws CorruptedDescriptionException Either the wrong runnable, format, or firing policy are incorrect
     */
    public WBExecutableComponentDescription (
            String sResURI,
            String sName,
            String sDescription,
            String sRights,
            String sCreator,
            Date dateCreation,
            String sRunnable,
            String sFiringPolicy,
            String sFormat,
            Set<String> setContext,
            String sLocationURI,
            Set<WBDataPortDescription> setInputs,
            Set<WBDataPortDescription> setOutputs,
            WBPropertiesDescriptionDefinition pddProperties,
            WBTagsDescription tagDesc,
            String resMode
        ) {
        // Initialization
        this.sResURI = sResURI;
        this.sName = sName;
        this.sDescription = sDescription;
        this.sRights = sRights;
        this.sCreator = sCreator;
        this.dateCreation = dateCreation;
        this.sRunnable = sRunnable;
        this.sFiringPolicy = sFiringPolicy;
        this.sFormat = sFormat;
        this.setContext = setContext;
        this.sLocationURI = sLocationURI;
        this.setInputs = setInputs;
        this.setOutputs = setOutputs;
        this.pddProperties = pddProperties;
        this.tagDesc  = tagDesc;
        this.resMode = resMode;
        // Update the mappings
        this.htInputsMap = new HashMap<String, WBDataPortDescription>();
        this.htOutputsMap = new HashMap<String, WBDataPortDescription>();
        for ( WBDataPortDescription dpd:setInputs )
            htInputsMap.put(dpd.getResourceURI(), dpd);
        for ( WBDataPortDescription dpd:setOutputs )
            htOutputsMap.put(dpd.getResourceURI(), dpd);
    }

    /** Returns the executable component resource.
     *
     * @return The resource
     */
    public String getResourceURI() {
        return sResURI;
    }

    /** Returns the components name.
     *
     * @return The name
     */
    public String getName() {
        return sName;
    }

    /** Returns the executable component description.
     *
     * @return The description
     */
    public String getDescription () {
        return sDescription;
    }

    /** Returns the rights of the component.
     *
     * @return The rights
     */
    public String getRights () {
        return sRights;
    }

    /** Returns the creator of the component.
     *
     * @return The creator
     */
    public String getCreator () {
        return sCreator;
    }

    /** Returns the creation date of the component.
     *
     * @return The date
     */
    public Date getCreationDate () {
        return dateCreation;
    }

    /** Returns the runnable type.
     *
     * @return The runnable type
     */
    public String getRunnable () {
        return sRunnable;
    }

    /** Returns the firing policy.
     *
     * @return The firing policy
     */
    public String getFiringPolicy () {
        return sFiringPolicy;
    }

    /** Returns the format of the executable component implementations.
     *
     * @return The format of the executable component
     */
    public String getFormat () {
        return sFormat;
    }

    /** The set of contexts associated to the context.
     *
     * @return The context set
     */
    public Set<String> getContext () {
        return setContext;
    }

    /** The location of the executable component.
     *
     * @return The location of the executable component
     */
    public String getLocation () {
        return sLocationURI;
    }

    /** The set of data ports that define the inputs of the executable component.
     *
     * @return The set of data ports
     */
    public Set<WBDataPortDescription> getInputs () {
        return setInputs;
    }

    /** Returns the input data port description linked to the provided resource.
     *
     * @param res The resource
     * @return The data port description
     */
    public WBDataPortDescription getInput ( String res ) {
        return htInputsMap.get(res);
    }


    /** The set of data ports that define the outputs of the executable component.
     *
     * @return The set of data ports
     */
    public Set<WBDataPortDescription> getOutputs () {
        return setOutputs;
    }

    /** Returns the output data port description linked to the provided resource.
     *
     * @param res The resource
     * @return The data port description
     */
    public WBDataPortDescription getOutput ( String res ) {
        return htOutputsMap.get(res);
    }
    /** Returns the property descriptions for the described executable component.
     *
     * @return The property definitions
     */
    public WBPropertiesDescriptionDefinition getProperties () {
        return pddProperties;
    }

    /** Return the tags linked to the executable component.
     *
     * @return The tag set
     */
    public WBTagsDescription getTags () {
        return tagDesc;
    }

    /** Returns the mode of the component (compute or webui component).
     *
     * @return The URI of the resource indicating the component mode
     */
    public String getMode() {
        return resMode;
    }
}
