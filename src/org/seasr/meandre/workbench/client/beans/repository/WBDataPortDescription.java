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

public class WBDataPortDescription implements IsSerializable {

    /** The resource ID of the data port */
    private String sResURI = null;

    /** The relative identifier of the port */
    private String sIdentifier = null;

    /** The pretty name of the data port */
    private String sName = null;

    /** The description of the data port */
    private String sDescription = null;

    public WBDataPortDescription() {}

    /** Creates a data port description based on the given information.
     *
     * @param res The resource locator
     * @param sIdent The relative port identifier
     * @param sName The name of the port
     * @param sDesc the description of the port
     * @throws CorruptedDescriptionException The resource and identifier are different
     */
    public WBDataPortDescription(String sResURI, String sIdent, String sName, String sDesc) {
        this.sResURI  = sResURI;
        this.sIdentifier  = sIdent;
        this.sName        = sName;
        this.sDescription = sDesc;
    }

    /** Returns the resource of this data port.
     *
     * @return The resource
     */
    public String getResourceURI () {
        return sResURI;
    }

    /** Returns the identifier of the data port
     *
     * @return The identifier
     */
    public String getIdentifier () {
        return sIdentifier;
    }

    /** Returns the name of the data port
     *
     * @return The name
     */
    public String getName() {
        return sName;
    }

    /** Returns the description of the data port
     *
     * @return The description
     */
    public String getDescription () {
        return sDescription;
    }


}
