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

package org.seasr.meandre.workbench.server.rpc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.seasr.meandre.workbench.server.beans.converters.ConnectorDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.DataPortDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.ExecutableComponentDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.ExecutableComponentInstanceDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.FlowDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.IBeanConverter;
import org.seasr.meandre.workbench.server.beans.converters.LocationBeanConverter;
import org.seasr.meandre.workbench.server.beans.converters.PropertiesDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.PropertiesDescriptionDefinitionConverter;
import org.seasr.meandre.workbench.server.beans.converters.TagsDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.WBConnectorDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.WBExecutableComponentInstanceDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.WBFlowDescriptionConverter;
import org.seasr.meandre.workbench.server.beans.converters.WBPropertiesDescriptionConverter;

public class MeandreConverter {

    public static final ConnectorDescriptionConverter
        ConnectorDescriptionConverter = new ConnectorDescriptionConverter();

    public static final WBConnectorDescriptionConverter
        WBConnectorDescriptionConverter = new WBConnectorDescriptionConverter();

    public static final DataPortDescriptionConverter
        DataPortDescriptionConverter = new DataPortDescriptionConverter();

    public static final ExecutableComponentDescriptionConverter
        ExecutableComponentDescriptionConverter = new ExecutableComponentDescriptionConverter();

    public static final ExecutableComponentInstanceDescriptionConverter
        ExecutableComponentInstanceDescriptionConverter = new ExecutableComponentInstanceDescriptionConverter();

    public static final WBExecutableComponentInstanceDescriptionConverter
        WBExecutableComponentInstanceDescriptionConverter = new WBExecutableComponentInstanceDescriptionConverter();

    public static final FlowDescriptionConverter
        FlowDescriptionConverter = new FlowDescriptionConverter();

    public static final WBFlowDescriptionConverter
        WBFlowDescriptionConverter = new WBFlowDescriptionConverter();

    public static final LocationBeanConverter
        LocationBeanConverter = new LocationBeanConverter();

    public static final PropertiesDescriptionConverter
        PropertiesDescriptionConverter = new PropertiesDescriptionConverter();

    public static final WBPropertiesDescriptionConverter
        WBPropertiesDescriptionConverter = new WBPropertiesDescriptionConverter();

    public static final PropertiesDescriptionDefinitionConverter
        PropertiesDescriptionDefinitionConverter = new PropertiesDescriptionDefinitionConverter();

    public static final TagsDescriptionConverter
        TagsDescriptionConverter = new TagsDescriptionConverter();

    public static final IBeanConverter<String, String>
        StringConverter = new IBeanConverter<String, String>() {
            public String convert(String data) {
                return data;
            }
        };


    public static <SRC, TARGET> Set<TARGET> convert(Set<SRC> set, IBeanConverter<SRC, TARGET> converter) {
        Set<TARGET> result = new HashSet<TARGET>(set.size());
        for (SRC element : set)
            result.add(converter.convert(element));

        return result;
    }

    public static <K1, V1, K2, V2> Map<K2, V2> convert(
            Map<K1, V1> map,
            IBeanConverter<K1, K2> converterKeys,
            IBeanConverter<V1, V2> converterValues) {

        Map<K2, V2> mapCopy = new HashMap<K2, V2>(map.size());
        for (K1 key : map.keySet())
            mapCopy.put(converterKeys.convert(key), converterValues.convert(map.get(key)));

        return mapCopy;
    }
}
