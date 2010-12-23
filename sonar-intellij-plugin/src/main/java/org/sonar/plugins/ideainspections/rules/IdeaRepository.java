

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
package org.sonar.plugins.ideainspections.rules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;

import org.sonar.api.platform.ServerFileSystem;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.rules.XMLRuleParser;
import org.sonar.api.utils.ValidationMessages;

import static org.sonar.plugins.ideainspections.IdeaConstants.REPOSITORY_KEY;
import static org.sonar.plugins.ideainspections.IdeaConstants.REPOSITORY_NAME;

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

    public static void loadDescriptions(final Iterable<Rule> rules)
    {
        for (Rule rule : rules) {
            String description = loadDescription(rule);

            if (description != null) {
                rule.setDescription(description);
            }
        }
    }

    @Override
    public List<Rule> createRules()
    {
        final List<Rule> rules = new ArrayList<Rule>();
        rules.addAll(extraRules().values());
        rules.addAll(DefaultRules.get().values());
        loadDescriptions(rules);

        if (fileSystem != null) {
            for (File userExtensionXml : fileSystem.getExtensions(REPOSITORY_KEY, "xml")) {
                rules.addAll(xmlRuleParser.parse(userExtensionXml));
            }
        }

        return rules;
    }

    static InputStream getResourceAsStream(String resource)
    {
        return IdeaRepository.class.getResourceAsStream(resource);
    }

    private static String loadDescription(Rule rule)
    {
        try {
            final InputStream input = getResourceAsStream("descriptions/" + rule.getKey() + ".html");
            return input == null ? null : IOUtils.toString(input, CharEncoding.UTF_8);
        }
        catch (IOException e) {
            return null;
        }
    }

    private SortedMap<String, Rule> extraRules()
    {
        RulesImporter importer = new RulesImporter();
        importer.importProfile(new InputStreamReader(getResourceAsStream("extra.xml")), ValidationMessages.create());
        return importer.getRules();
    }
}
