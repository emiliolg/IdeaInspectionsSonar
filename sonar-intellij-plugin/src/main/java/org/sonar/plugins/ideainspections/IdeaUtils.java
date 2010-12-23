

// Copyright 2008-2009 Emilio Lopez-Gabeiras
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License
//


package org.sonar.plugins.ideainspections;

import java.io.Reader;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMHierarchicCursor;

import org.sonar.api.rules.RulePriority;

import static org.sonar.api.rules.RulePriority.BLOCKER;
import static org.sonar.api.rules.RulePriority.CRITICAL;
import static org.sonar.api.rules.RulePriority.INFO;
import static org.sonar.api.rules.RulePriority.MAJOR;
import static org.sonar.api.rules.RulePriority.MINOR;

public class IdeaUtils
{
    //~ Methods ........................................................................................................

    /**
     * Creates and Initialize a XML stax parser and returns a Cursor to the root element.
     * @param reader
     * @return A Cursor to the root element
     * @throws XMLStreamException
     */
    public static SMHierarchicCursor createStaxParser(Reader reader)
        throws XMLStreamException
    {
        XMLInputFactory xmlFactory = XMLInputFactory2.newInstance();
        xmlFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        xmlFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        xmlFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        return new SMInputFactory(xmlFactory).rootElementCursor(reader);
    }

    public static String toSeverity(RulePriority priority)
    {
        String result = map.get(priority);

        if (result == null) {
            throw new IllegalArgumentException("Priority not supported: " + priority);
        }

        return result;
    }

    public static RulePriority fromLevel(String level)
    {
        return rmap.get(level);
    }

    //~ Static fields/initializers .....................................................................................

    private static final EnumMap<RulePriority, String> map;
    private static final Map<String, RulePriority>     rmap;

    static {
        map = new EnumMap<RulePriority, String>(RulePriority.class);
        map.put(BLOCKER, "ERROR");
        map.put(CRITICAL, "ERROR");
        map.put(MAJOR, "WARNING");
        map.put(MINOR, "INFO");
        map.put(INFO, "INFO");

        rmap = new HashMap<String, RulePriority>();
        rmap.put("ERROR", BLOCKER);
        rmap.put("WARNING", MAJOR);
        rmap.put("INFO", INFO);
    }
}
