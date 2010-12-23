

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

import java.io.Reader;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.in.SMInputCursor;

import org.sonar.api.profiles.ProfileImporter;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.ValidationMessages;

import static java.lang.Boolean.parseBoolean;

import static org.sonar.plugins.ideainspections.IdeaConstants.INSPECTION_CLASS_ATTR;
import static org.sonar.plugins.ideainspections.IdeaConstants.INSPECTION_ENABLED;
import static org.sonar.plugins.ideainspections.IdeaConstants.INSPECTION_LEVEL_ATTR;
import static org.sonar.plugins.ideainspections.IdeaConstants.INSPECTION_OPTION_NAME_ATTR;
import static org.sonar.plugins.ideainspections.IdeaConstants.INSPECTION_OPTION_NODE;
import static org.sonar.plugins.ideainspections.IdeaConstants.INSPECTION_OPTION_VALUE_ATTR;
import static org.sonar.plugins.ideainspections.IdeaConstants.INSPECTION_TOOL_NODE;
import static org.sonar.plugins.ideainspections.IdeaConstants.PLUGIN_NAME;
import static org.sonar.plugins.ideainspections.IdeaConstants.REPOSITORY_KEY;
import static org.sonar.plugins.ideainspections.IdeaUtils.createStaxParser;
import static org.sonar.plugins.ideainspections.IdeaUtils.fromLevel;

public class IdeaImporter
    extends ProfileImporter
{
    //~ Instance fields ................................................................................................

    private final RuleFinder ruleFinder;

    //~ Constructors ...................................................................................................

    public IdeaImporter(RuleFinder ruleFinder)
    {
        super(REPOSITORY_KEY, PLUGIN_NAME);
        setSupportedLanguages(Java.KEY);
        this.ruleFinder = ruleFinder;
    }

    //~ Methods ........................................................................................................

    @Override
    public RulesProfile importProfile(Reader reader, ValidationMessages messages)
    {
        RulesProfile profile = RulesProfile.create();

        try {
            SMInputCursor cursor = createStaxParser(reader).advance().childElementCursor(INSPECTION_TOOL_NODE);

            while (cursor.getNext() != null) {
                importRule(profile,
                           messages,
                           parseBoolean(cursor.getAttrValue(INSPECTION_ENABLED)),
                           cursor.getAttrValue(INSPECTION_CLASS_ATTR),
                           fromLevel(cursor.getAttrValue(INSPECTION_LEVEL_ATTR)),
                           cursor.childElementCursor(INSPECTION_OPTION_NODE));
            }
        }
        catch (XMLStreamException e) {
            messages.addErrorText("XML is not valid: " + e.getMessage());
        }

        return profile;
    }

    protected void importRule(RulesProfile profile, ValidationMessages messages, boolean enabled, String inspection,
                              RulePriority priority, SMInputCursor options)
        throws XMLStreamException
    {
        if (enabled) {
            Rule rule = ruleFinder.find(RuleQuery.create().withRepositoryKey(REPOSITORY_KEY).withConfigKey(inspection));

            if (rule == null) {
                messages.addWarningText("Intellij rule with config key '" + inspection + "' not found");
            }
            else {
                ActiveRule activeRule = profile.activateRule(rule, null);

                if (priority != null) {
                    activeRule.setPriority(priority);
                }

                processOptions(activeRule, options);
            }
        }
    }

    private void processOptions(ActiveRule activeRule, final SMInputCursor options)
        throws XMLStreamException
    {
        while (options.getNext() != null) {
            activeRule.setParameter(options.getAttrValue(INSPECTION_OPTION_NAME_ATTR),
                                    options.getAttrValue(INSPECTION_OPTION_VALUE_ATTR));
        }
    }
}
