

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

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.ValidationMessages;
import org.sonar.test.TestUtils;

import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonar.plugins.ideainspections.IdeaConstants.REPOSITORY_KEY;

public class IdeaProfileImporterTest
{
    //~ Instance fields ................................................................................................

    private IdeaImporter importer;

    private ValidationMessages messages;

    //~ Methods ........................................................................................................

    @Before
    public void before()
    {
        messages = ValidationMessages.create();

        importer = new IdeaImporter(newRuleFinder());
    }

    @Test
    public void importSimpleProfile()
    {
        RulesProfile profile = importProfile();

        assertThat(profile.getActiveRules().size(), is(2));
        assertNotNull(profile.getActiveRuleByConfigKey(REPOSITORY_KEY, CYCLOMATIC_COMPLEXITY));
        assertNotNull(profile.getActiveRuleByConfigKey(REPOSITORY_KEY, CLONE_CALLS_SUPER_CLONE));
        assertThat(messages.hasErrors(), is(false));
    }

    @Test
    public void importParameters()
    {
        RulesProfile profile = importProfile();

        ActiveRule javadocCheck = profile.getActiveRuleByConfigKey(REPOSITORY_KEY, CYCLOMATIC_COMPLEXITY);
        assertThat(javadocCheck.getActiveRuleParams().size(), is(1));
        assertThat(javadocCheck.getParameter(COMPLEXITY_LIMIT), is("15"));
    }

    @Test
    public void importPriorities()
    {
        RulesProfile profile = importProfile();

        ActiveRule rule = profile.getActiveRuleByConfigKey(REPOSITORY_KEY, CYCLOMATIC_COMPLEXITY);
        assertThat(rule.getPriority(), is(RulePriority.MAJOR));
    }

    @Test
    public void priorityIsOptional()
    {
        RulesProfile profile = importProfile();

        ActiveRule activeRule =
            profile.getActiveRuleByConfigKey(REPOSITORY_KEY, CLONE_CALLS_SUPER_CLONE);
        assertThat(activeRule.getPriority(), is(RulePriority.MAJOR));
    }

    @Test
    public void testUnvalidXML()
    {
        Reader reader = new StringReader("not xml");
        importer.importProfile(reader, messages);
        assertThat(messages.getErrors().size(), is(1));
    }

    private RulesProfile importProfile()
    {
        Reader reader = new StringReader(TestUtils.getResourceContent(SIMPLE_XML));
        return importer.importProfile(reader, messages);
    }

    /*
    *    The mocked rule finder defines 2 rules :
    *    - CyclomaticComplexity with 1 parameter "m_limit", default priority is MAJOR
    *    - CloneCallsSuperClone without parameters, default priority is MAJOR
    */

    static private RuleFinder newRuleFinder()
    {
        RuleFinder ruleFinder = mock(RuleFinder.class);

        when(ruleFinder.find(any(RuleQuery.class))).thenAnswer(new Answer<Rule>() {
                public Rule answer(InvocationOnMock iom)
                    throws Throwable
                {
                    return ruleFor((RuleQuery) iom.getArguments()[0]);
                }
            });
        return ruleFinder;
    }

    static private Rule ruleFor(RuleQuery query)
    {
        Rule rule = null;

        if (StringUtils.equals(query.getConfigKey(), CYCLOMATIC_COMPLEXITY)) {
            rule =
                Rule.create(query.getRepositoryKey(), CYCLOMATIC_COMPLEXITY, CYCLOMATIC_COMPLEXITY)
                    .setConfigKey(CYCLOMATIC_COMPLEXITY)
                    .setPriority(RulePriority.MAJOR);
            rule.createParameter(COMPLEXITY_LIMIT);
        }
        else if (StringUtils.equals(query.getConfigKey(), CLONE_CALLS_SUPER_CLONE)) {
            rule =
                Rule.create(query.getRepositoryKey(), CLONE_CALLS_SUPER_CLONE, CLONE_CALLS_SUPER_CLONE)
                    .setConfigKey(CLONE_CALLS_SUPER_CLONE)
                    .setPriority(RulePriority.MAJOR);
        }

        return rule;
    }

    //~ Static fields/initializers .....................................................................................

    private static final String COMPLEXITY_LIMIT = "m_limit";

    private static final String CYCLOMATIC_COMPLEXITY = "CyclomaticComplexity";
    private static final String CLONE_CALLS_SUPER_CLONE = "CloneCallsSuperClone";
    private static final String SIMPLE_XML =
        "/org/sonar/plugins/ideainspections/IdeaProfileImporterTest/simple.xml";
}
