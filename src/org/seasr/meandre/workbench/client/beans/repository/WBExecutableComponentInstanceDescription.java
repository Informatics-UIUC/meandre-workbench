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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Boris Capitanu
 *
 */
public class WBExecutableComponentInstanceDescription implements IsSerializable, Cloneable {


    /** The resource for the executable component */
    private String sResExecutableComponentInstaceURI = null;

    /** The module pointing to the resource */
    private String sResComponentURI = null;

    /** The name of the executable component */
    private String sName = null;

    /** The description of the executable component */
    private String sDescription = null;

    /** The instance properties */
    private WBPropertiesDescription pdProperties = null;

    // This will not be serialized by GWT
    private transient WBExecutableComponentDescription _compDesc = null;

    /** Create an empty executable component instance description instance
    *
    *
    */
   public WBExecutableComponentInstanceDescription () {
       this(
               null,    // sResExecutableComponentInstanceURI
               null,    // sResComponentURI
               "",      // sName
               "",      // sDescription
               null     // pdProperties
               );
   }

   /** Create a executable component instance description instance
    *
    * @param resExecutableComponentInstance The resource identifying this instance
    * @param resComponent The component this instance belongs to
    * @param sName The name of the flow
    * @param sDescription The description of the flow
    * @param description The instance properties
    */
   public WBExecutableComponentInstanceDescription (
               String sResExecutableComponentInstanceURI,
               String sResComponentURI,
               String sName,
               String sDescription,
               WBPropertiesDescription pdProperties
           ) {
       this.sResExecutableComponentInstaceURI = sResExecutableComponentInstanceURI;
       this.sResComponentURI = sResComponentURI;
       this.sName = sName;
       this.sDescription = sDescription;
       this.pdProperties = pdProperties;
   }


    /** Sets the instance resource.
     *
     * @param res The instance resources
     */
    public void setExecutableComponentInstance ( String sResURI ) {
        sResExecutableComponentInstaceURI = sResURI;
    }

    /** Returns the instance resource.
     *
     * @return The instance resources
     */
    public String getExecutableComponentInstance() {
        return sResExecutableComponentInstaceURI;
    }

    /** Set the executable component resource.
     *
     * @param res The resource
     */
    public void setExecutableComponent ( String sResURI ) {
        sResComponentURI = sResURI;
    }

    /** Returns the executable component resource.
     *
     * @return The resource
     */
    public String getExecutableComponent() {
        return sResComponentURI;
    }

    public void setExecutableComponentDescription(WBExecutableComponentDescription compDesc) {
        _compDesc = compDesc;
    }

    public WBExecutableComponentDescription getExecutableComponentDescription() {
        return _compDesc;
    }

    /** Sets the components name.
     *
     * @param sName The name
     */
    public void setName ( String sName ) {
        this.sName=sName;
    }

    /** Returns the components name.
     *
     * @return The name
     */
    public String getName() {
        return sName;
    }

    /** Sets the executable component description.
     *
     * @param sDesc The description
     */
    public void setDescription ( String sDesc ) {
        this.sDescription=sDesc;
    }

    /** Returns the executable component description.
     *
     * @return The description
     */
    public String getDescription () {
        return sDescription;
    }

    /** Sets the properties for the instance.
     *
     * @param props The property description
     */
    public void setProperties ( WBPropertiesDescription props ) {
        pdProperties=props;
    }

    /** Return the properties for the instance.
     *
     * @return The property description
     */
    public WBPropertiesDescription getProperties () {
        return pdProperties;
    }

    public WBExecutableComponentInstanceDescription clone() {
        WBExecutableComponentInstanceDescription instanceClone = new WBExecutableComponentInstanceDescription(
                this.sResExecutableComponentInstaceURI,
                this.sResComponentURI,
                this.sName,
                this.sDescription,
                (WBPropertiesDescription) this.pdProperties.clone()
                );
        instanceClone.setExecutableComponentDescription(this.getExecutableComponentDescription());

        return instanceClone;
    }
}
