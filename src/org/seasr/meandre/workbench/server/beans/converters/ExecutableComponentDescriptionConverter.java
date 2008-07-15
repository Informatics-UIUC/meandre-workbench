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

package org.seasr.meandre.workbench.server.beans.converters;

import java.util.HashSet;
import java.util.Set;

import org.meandre.core.repository.ExecutableComponentDescription;
import org.seasr.meandre.workbench.client.beans.repository.WBExecutableComponentDescription;
import org.seasr.meandre.workbench.server.rpc.MeandreConverter;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Boris Capitanu
 *
 */
public class ExecutableComponentDescriptionConverter implements
        IBeanConverter<ExecutableComponentDescription, WBExecutableComponentDescription> {

    public WBExecutableComponentDescription convert(ExecutableComponentDescription compDesc) {
        return
            new WBExecutableComponentDescription(
                compDesc.getExecutableComponent().getURI(),
                compDesc.getName(),
                compDesc.getDescription(),
                compDesc.getRights(),
                compDesc.getCreator(),
                compDesc.getCreationDate(),
                compDesc.getRunnable(),
                compDesc.getFiringPolicy(),
                compDesc.getFormat(),
                convertContexts(compDesc.getContext()),
                compDesc.getLocation().getURI(),
                MeandreConverter.convert(compDesc.getInputs(), MeandreConverter.DataPortDescriptionConverter),
                MeandreConverter.convert(compDesc.getOutputs(), MeandreConverter.DataPortDescriptionConverter),
                MeandreConverter.PropertiesDescriptionDefinitionConverter.convert(compDesc.getProperties()),
                MeandreConverter.TagsDescriptionConverter.convert(compDesc.getTags()),
                compDesc.getMode().getURI());
    }

    private static Set<String> convertContexts(Set<RDFNode> contexts) {
        Set<String> contextsCopy = new HashSet<String>(contexts.size());
        for (RDFNode node : contexts) {
            if (node.isResource())
                contextsCopy.add(((Resource)node).getURI());
            else
                contextsCopy.add("Literal");
        }

        return contextsCopy;
    }
}
