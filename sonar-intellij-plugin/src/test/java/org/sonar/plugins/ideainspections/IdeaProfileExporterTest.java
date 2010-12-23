

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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import org.junit.Test;

import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.test.TestUtils;

import org.xml.sax.SAXException;

import static org.sonar.plugins.ideainspections.IdeaConstants.*;

public class IdeaProfileExporterTest
{
    //~ Methods ........................................................................................................

    @Test
    public void empty()
        throws IOException, SAXException
    {
        RulesProfile profile = RulesProfile.create("sonar way", "java");

        assertSimilar(exportProfile(profile), "empty.xml");
    }

    @Test
    public void noIdeaRulesToExport()
        throws IOException, SAXException
    {
        RulesProfile profile = RulesProfile.create("sonar way", "java");

        // this is a PMD rule
        profile.activateRule(Rule.create("pmd", "PmdRule1", "PMD rule one"), null);

        assertSimilar(exportProfile(profile), "empty.xml");
    }

    @Test
    public void simpleIdeaRulesToExport()
        throws IOException, SAXException
    {
        RulesProfile profile = createSimpleProfile();

        assertSimilar(exportProfile(profile), "simpleIdeaRulesToExport.xml");
    }

    public static RulesProfile createSimpleProfile()
    {
        RulesProfile profile = RulesProfile.create("sonar way", "java");
        profile.activateRule(Rule.create("pmd", "PmdRule1", "PMD rule one"), null);
        profile.activateRule(Rule.create(REPOSITORY_KEY, "RedundantCast", "RedundantCast").setConfigKey("RedundantCast"),
                             RulePriority.CRITICAL);
        profile.activateRule(Rule.create(REPOSITORY_KEY,
                                         "BooleanMethodIsAlwaysInverted",
                                         "BooleanMethodIsAlwaysInverted").setConfigKey("BooleanMethodIsAlwaysInverted"),
                             RulePriority.MAJOR);
        profile.activateRule(Rule.create(REPOSITORY_KEY, "BreakStatement", "BreakStatement").setConfigKey("BreakStatement"),
                             RulePriority.MAJOR);
        return profile;
    }

    @Test
    public void addTheIdPropertyWhenManyInstancesWithTheSameConfigKey()
        throws IOException, SAXException
    {
        RulesProfile profile = RulesProfile.create("sonar way", "java");
        Rule         rule1 =
            Rule.create(REPOSITORY_KEY, "RedundantCast", "RedundantCast").setConfigKey("RedundantCast");
        Rule         rule2 =
            Rule.create(REPOSITORY_KEY, "RedundantCast_12345", "RedundantCast").setConfigKey("RedundantCast")
                .setParent(rule1);

        profile.activateRule(rule1, RulePriority.MAJOR);
        profile.activateRule(rule2, RulePriority.CRITICAL);

        assertSimilar(exportProfile(profile), "addTheIdPropertyWhenManyInstancesWithTheSameConfigKey.xml");
    }

    @Test
    public void exportParameters()
        throws IOException, SAXException
    {
        RulesProfile profile = RulesProfile.create("sonar way", "java");
        Rule         rule =
            Rule.create(REPOSITORY_KEY, "ConstantConditions", "ConstantConditions").setConfigKey("ConstantConditions");
        rule.createParameter("SUGGEST_NULLABLE_ANNOTATIONS");
        rule.createParameter("message");  // not set in the profile and no default value => not exported in checkstyle
        rule.createParameter("DONT_REPORT_TRUE_ASSERT_STATEMENTS");

        profile.activateRule(rule, RulePriority.MAJOR).setParameter("DONT_REPORT_TRUE_ASSERT_STATEMENTS", "false")
                // todo remove this
               .setParameter("SUGGEST_NULLABLE_ANNOTATIONS", "false");

        assertSimilar(exportProfile(profile), "exportParameters.xml");
    }

    //    @Test
    //    public void addCustomFilters()
    //        throws IOException, SAXException
    //    {
    //        Configuration conf = new BaseConfiguration();
    //        conf.addProperty(FILTERS_KEY,
    //                         "<module name=\"SuppressionCommentFilter\">" +
    //                         "<property name=\"offCommentFormat\" value=\"BEGIN GENERATED CODE\"/>" +
    //                         "<property name=\"onCommentFormat\" value=\"END GENERATED CODE\"/>" + "</module>" +
    //                         "<module name=\"SuppressWithNearbyCommentFilter\">" +
    //                         "<property name=\"commentFormat\" value=\"CHECKSTYLE IGNORE (\\w+) FOR NEXT (\\d+) LINES\"/>" +
    //                         "<property name=\"checkFormat\" value=\"$1\"/>" +
    //                         "<property name=\"messageFormat\" value=\"$2\"/>" + "</module>");
    //
    //        RulesProfile profile = RulesProfile.create("sonar way", "java");
    //        assertSimilar(exportProfile(profile), "addCustomFilters.xml");
    //    }
    //
    private static StringWriter exportProfile(RulesProfile profile)
    {
        StringWriter writer = new StringWriter();
        new IdeaProfileExporter().exportProfile(profile, writer);
        return writer;
    }

    private void assertSimilar(StringWriter writer, String resource)
        throws IOException, SAXException
    {
        final InputStream stream = getClass().getResourceAsStream(resource);
        String            base = IOUtils.toString(stream);
        String            xml = writer.toString();
        TestUtils.assertSimilarXml(base, xml);
    }
}
