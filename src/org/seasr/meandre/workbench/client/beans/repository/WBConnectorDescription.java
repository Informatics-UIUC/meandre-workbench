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
public class WBConnectorDescription implements IsSerializable, Cloneable {

    /** The connector ID  */
    private String sResConnectorURI = null;

    /** The resource ID of the source instance */
    private String sResInstanceSourceURI = null;

    /** The resource ID of the source instance data port */
    private String sResInstanceDataPortSourceURI = null;

    /** The resource ID of the target instance */
    private String sResInstanceTargetURI = null;

    /** The resource ID of the target instance data port */
    private String sResInstanceDataPortTargetURI = null;

    /** Creates an empty connector description.
     *
     */
    public WBConnectorDescription () {
        this(
                null,   // sResConnectorURI
                null,   // sResInstanceSourceURI
                null,   // sResInstanceDataPortSourceURI
                null,   // sResInstanceTargetURI
                null    // sResInstanceDataPortTargetURI
        );
    }

    /** Creates a connector description object with the given information.
     * @param sResConnectorURI The resource describing the connector
     * @param sResInstanceSourceURI The source instance
     * @param sResInstanceDataPortSourceURI The source instance port
     * @param sResInstanceTargetURI The target instance
     * @param sResInstanceDataPortTargetURI The target instance port
     */
    public WBConnectorDescription (
            String sResConnectorURI ,
            String sResInstanceSourceURI,
            String sResInstanceDataPortSourceURI,
            String sResInstanceTargetURI,
            String sResInstanceDataPortTargetURI
        ) {
        this.sResConnectorURI = sResConnectorURI;
        this.sResInstanceSourceURI = sResInstanceSourceURI;
        this.sResInstanceDataPortSourceURI = sResInstanceDataPortSourceURI;
        this.sResInstanceTargetURI = sResInstanceTargetURI;
        this.sResInstanceDataPortTargetURI= sResInstanceDataPortTargetURI;
    }

    /** Sets the resource connector.
     *
     * @param sResURI The source instance
     */
    public void  setConnector ( String sResURI ) {
        sResConnectorURI = sResURI;
    }


    /** Returns the resource connector.
     *
     * @return The source instance
     */
    public String getConnector () {
        return sResConnectorURI;
    }

    /** Sets the source instance.
     *
     * @param sResURI The source instance
     */
    public void setSourceInstance ( String sResURI ) {
        sResInstanceSourceURI = sResURI;
    }

    /** Returns the source instance.
     *
     * @return The source instance
     */
    public String getSourceInstance () {
        return sResInstanceSourceURI;
    }

    /** Sets the source instance port.
     *
     * @param sResURI The source instance port
     */
    public void setSourceInstanceDataPort ( String sResURI ) {
        sResInstanceDataPortSourceURI = sResURI;
    }

    /** Returns the source instance port.
     *
     * @return The source instance port
     */
    public String getSourceInstanceDataPort () {
        return sResInstanceDataPortSourceURI;
    }

    /** Sets the target instance.
     *
     * @param sResURI The target instance
     */
    public void setTargetInstance ( String sResURI ) {
        sResInstanceTargetURI = sResURI;
    }

    /** Returns the target instance.
     *
     * @return The target instance
     */
    public String getTargetInstance () {
        return sResInstanceTargetURI;
    }

    /** Sets the target instance port
     *
     * @param sResURI The target instance port
     */
    public void setTargetInstanceDataPort ( String sResURI ) {
        sResInstanceDataPortTargetURI = sResURI;
    }

    /** Returns the target instance port
     *
     * @return The target instance port
     */
    public String getTargetInstanceDataPort () {
        return sResInstanceDataPortTargetURI;
    }

    /** Check if two connectors are equal.
     *
     * @param o The other connector to check
     */
    public boolean equals ( Object o ) {
        WBConnectorDescription cdOther = (WBConnectorDescription) o;
        boolean bRes = false;

        if ( sResInstanceDataPortSourceURI.equals(cdOther.sResInstanceDataPortSourceURI) &&
             sResInstanceDataPortTargetURI.equals(cdOther.sResInstanceDataPortTargetURI) &&
             sResInstanceSourceURI.equals(cdOther.sResInstanceSourceURI) &&
             sResInstanceTargetURI.equals(cdOther.sResInstanceTargetURI)
           )
            bRes = true;

        return bRes;
    }

    /**
     * Returns a deep copy of this object
     */
    public WBConnectorDescription clone() {
        return
            new WBConnectorDescription(
                    this.sResConnectorURI,
                    this.sResInstanceSourceURI,
                    this.sResInstanceDataPortSourceURI,
                    this.sResInstanceTargetURI,
                    this.sResInstanceDataPortTargetURI
                    );
    }
}
