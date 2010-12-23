

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


package org.sonar.plugins.ideainspections.rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;

import org.codehaus.staxmate.in.SMInputCursor;

import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Iso9126RulesCategories;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.ValidationMessages;

import static org.sonar.plugins.ideainspections.IdeaConstants.REPOSITORY_KEY;

import org.sonar.plugins.ideainspections.IdeaImporter;

import static org.sonar.plugins.ideainspections.rules.IdeaRepository.getResourceAsStream;

/**
* Created by IntelliJ IDEA.
* User: emilio
* Date: Dec 23, 2010
* Time: 3:30:26 PM
* To change this template use File | Settings | File Templates.
*/
class RulesImporter
    extends IdeaImporter
{
    //~ Instance fields ................................................................................................

    private final Properties              names;
    private final SortedMap<String, Rule> rules;

    //~ Constructors ...................................................................................................

    RulesImporter()
    {
        super(null);
        names = loadRuleNames();
        rules = new TreeMap<String, Rule>();
    }

    //~ Methods ........................................................................................................

    public SortedMap<String, Rule> getRules()
    {
        return rules;
    }

    @Override
    protected void importRule(RulesProfile profile, ValidationMessages messages, boolean enabled, String key,
                              RulePriority priority, SMInputCursor options)
        throws XMLStreamException
    {
        if (!rules.containsKey(key)) {
            String name = names.getProperty(key);

            if (name == null) {
                name = key;
            }

            Rule rule =
                Rule.create(REPOSITORY_KEY, key, name).setConfigKey(key).setPriority(priority)
                    .setRulesCategory(Iso9126RulesCategories.RELIABILITY);

            rules.put(key, rule);
        }
    }

    private Properties loadRuleNames()
    {
        Properties result = new Properties();

        try {
            result.load(getResourceAsStream("names.properties"));
        }
        catch (IOException e) {
            throw new SonarException("Fail to load inspections names", e);
        }

        return result;
    }

}
