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

public class WBLocation implements IsSerializable {

    /** The location URL */
    private String sLocation;

    /** The location description */
    private String sDescription;

    public WBLocation() {}

    /** Creates a bean and sets the location and description.
     *
     * @param sLocation The location URL
     * @param sDescription The description
     */
    public WBLocation ( String sLocation, String sDescription ) {
        setLocation(sLocation);
        setDescription(sDescription);
    }

    /** Sets the URL location
     *
     * @param sLocation the sLocation to set
     */
    public void setLocation(String sLocation) {
        this.sLocation = sLocation;
    }

    /** Gets the URL location
     *
     * @return the sLocation
     */
    public String getLocation() {
        return sLocation;
    }

    /** Sets the location description
     *
     * @param description the sDescription to set
     */
    public void setDescription(String sDescription) {
        this.sDescription = sDescription;
    }

    /** Gets the location description
     *
     * @return the description
     */
    public String getDescription() {
        return sDescription;
    }

    /**
     * tests if this bean has equivalent values to another one.
     *
     * @param other
     * @return
     */

    public boolean equals(Object otherLocation){
        boolean isEqual = true;
        WBLocation otherBean = null;
        try{
            otherBean = (WBLocation)otherLocation;
        }catch(ClassCastException e){
            return false;
        }

        isEqual = isEqual && (this.getLocation().equals(otherBean.getLocation()));
        isEqual = isEqual && (this.getDescription().equals(otherBean.getDescription()));
        return isEqual;
    }

    /**
     * returns a pretty print version of this location's url and description
     */
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("LocationURL=\'");
        sb.append(getLocation());
        sb.append("\' Description=\'");
        sb.append(getDescription());
        sb.append("\'");
        return sb.toString();

    }

    /** a hashcode derived from the internal url and description. used by hashmaps. */
    public int hashCode(){
        int hc = this.getLocation().hashCode();
        hc += this.getDescription().hashCode();
        return hc;
    }

}
