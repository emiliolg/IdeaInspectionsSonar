

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

import org.hamcrest.core.Is;

import org.junit.Test;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.utils.ValidationMessages;

import static org.hamcrest.number.OrderingComparisons.greaterThan;

import static org.junit.Assert.assertThat;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SunConventionsProfileTest
{
    //~ Methods ........................................................................................................

    @Test
    public void shouldCreateProfile()
    {
        ProfileDefinition  definition = new SunConventionsProfile(new XMLProfileParser(newRuleFinder()));
        ValidationMessages validation = ValidationMessages.create();
        RulesProfile       sunProfile = definition.createProfile(validation);
        assertThat(sunProfile.getActiveRulesByRepository(IdeaConstants.REPOSITORY_KEY).size(),
                   greaterThan(1));
        assertThat(validation.hasErrors(), Is.is(false));
    }

    private RuleFinder newRuleFinder()
    {
        RuleFinder ruleFinder = mock(RuleFinder.class);
        when(ruleFinder.findByKey(anyString(), anyString())).thenAnswer(new Answer<Rule>() {
                public Rule answer(InvocationOnMock iom)
                    throws Throwable
                {
                    return Rule.create((String) iom.getArguments()[0],
                                       (String) iom.getArguments()[1],
                                       (String) iom.getArguments()[1]);
                }
            });
        return ruleFinder;
    }
}
