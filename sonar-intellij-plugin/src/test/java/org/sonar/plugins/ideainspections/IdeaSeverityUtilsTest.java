

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

import org.junit.Test;

import org.sonar.api.rules.RulePriority;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import static org.junit.Assert.assertThat;

import static org.sonar.plugins.ideainspections.IdeaUtils.fromLevel;
import static org.sonar.plugins.ideainspections.IdeaUtils.toSeverity;

public class IdeaSeverityUtilsTest
{
    //~ Methods ........................................................................................................

    @Test
    public void testToSeverity()
    {
        assertThat(toSeverity(RulePriority.BLOCKER), is("ERROR"));
        assertThat(toSeverity(RulePriority.CRITICAL), is("ERROR"));
        assertThat(toSeverity(RulePriority.MAJOR), is("WARNING"));
        assertThat(toSeverity(RulePriority.MINOR), is("INFO"));
        assertThat(toSeverity(RulePriority.INFO), is("INFO"));
    }

    @Test
    public void testFromSeverity()
    {
        assertThat(fromLevel("ERROR"), is(RulePriority.BLOCKER));
        assertThat(fromLevel("WARNING"), is(RulePriority.MAJOR));
        assertThat(fromLevel("INFO"), is(RulePriority.INFO));
        assertThat(fromLevel(""), nullValue());
    }
}
