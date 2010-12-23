

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


/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.ideainspections;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.codehaus.staxmate.in.SMInputCursor;

import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Iso9126RulesCategories;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.ValidationMessages;

import static org.sonar.plugins.ideainspections.IdeaConstants.*;

public final class IdeaRepository
    extends RuleRepository
{
    //~ Instance fields ................................................................................................

    // for user extensions
    private ServerFileSystem fileSystem;
    private XMLRuleParser    xmlRuleParser;

    //~ Constructors ...................................................................................................

    public IdeaRepository(ServerFileSystem fileSystem, XMLRuleParser xmlRuleParser)
    {
        super(REPOSITORY_KEY, Java.KEY);
        setName(REPOSITORY_NAME);
        this.fileSystem = fileSystem;
        this.xmlRuleParser = xmlRuleParser;
    }

    //~ Methods ........................................................................................................

    public static void main(String[] args)
        throws IOException
    {
        IdeaRepository repository = new IdeaRepository(null, new XMLRuleParser());
        List<Rule>                    rules = repository.createRules();

        for (Rule rule : rules) {
            System.out.println(rule.getKey() + " = " + rule.getName());
        }
    }

    @Override
    public List<Rule> createRules()
    {
        RulesImporter importer = new RulesImporter();

        importer.importProfile(new InputStreamReader(getResourceAsStream("extra.xml")),
                               ValidationMessages.create());
        importer.loadDescriptions();
        final List<Rule> rules = new ArrayList<Rule>(importer.getRules());
        if (fileSystem != null) {
            for (File userExtensionXml : fileSystem.getExtensions(REPOSITORY_KEY, "xml")) {
                rules.addAll(xmlRuleParser.parse(userExtensionXml));
            }
        }
        return rules;
    }

    private static InputStream getResourceAsStream(String resource)
    {
        return IdeaRepository.class.getResourceAsStream(resource);
    }

    //~ Inner Classes ..................................................................................................

    private static class RulesImporter
        extends IdeaImporter
    {
        private final SortedMap<String,Rule> rules;
        private final Properties names;

        public RulesImporter()
        {
            super(null);
            names = loadRuleNames();
            rules = new TreeMap<String,Rule>();
        }

        public Collection<Rule> getRules()
        {
            return rules.values();
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

                rules.put(key,rule);
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

        public void loadDescriptions()
        {
            for (Rule rule : rules.values()) {
                String description =
                loadDescription(rule);
                if (description != null) {
                    rule.setDescription(description);
                }
            }
        }

        private String loadDescription(Rule rule)
        {
            try {
                final InputStream input = getResourceAsStream("descriptions/" + rule.getKey() + ".html");
                return input == null ? null : IOUtils.toString(input, CharEncoding.UTF_8);
            }
            catch (IOException e) {
                return null;
            }
        }
    }
}
