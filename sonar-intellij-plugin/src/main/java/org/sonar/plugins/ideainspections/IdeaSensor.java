

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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.Violation;

import static org.sonar.plugins.ideainspections.IdeaConstants.REPOSITORY_KEY;

public class IdeaSensor
    implements Sensor
{
    //~ Instance fields ................................................................................................

    private IdeaExecutor executor;
    private RuleFinder   ruleFinder;

    private RulesProfile profile;

    //~ Constructors ...................................................................................................

    public IdeaSensor(RulesProfile profile, RuleFinder ruleFinder, IdeaExecutor executor)
    {
        this.profile = profile;
        this.ruleFinder = ruleFinder;
        this.executor = executor;
    }

    //~ Methods ........................................................................................................

    public boolean shouldExecuteOnProject(Project project)
    {
        return project.getFileSystem().hasJavaSourceFiles() &&
               (!profile.getActiveRulesByPlugin(REPOSITORY_KEY).isEmpty() || project.getReuseExistingRulesConfig());
    }

    static File theReportDir;

    public void analyse(Project project, SensorContext context)
    {

        final boolean runOnce = executor.isRunOnce();
        File reportDir = runOnce ?theReportDir:null;

        if(reportDir == null) {
            reportDir = executor.execute();
            if(runOnce) {
                theReportDir = reportDir;
            }
        }

        if (reportDir != null) {
            IdeaReportParser parser = new IdeaReportParser(reportDir);
            parser.parseAll();


            final Collection<IdeaReportParser.Violation> violations = parser.getViolations();
            if (!violations.isEmpty()) {
                LOG.info("Found "+ violations.size() + " violations");
            }
            final Set<IdeaReportParser.Violation> reported = new HashSet<IdeaReportParser.Violation>();
            for (IdeaReportParser.Violation v : violations) {

                if(String.valueOf(v.getModule()).equals(project.getName()) && !reported.contains(v)) {
                    reported.add(v);
                    Rule     rule = ruleFinder.findByKey(REPOSITORY_KEY, v.getType());
                    Resource resource = context.getResource(new JavaFile(v.getSonarJavaFileKey()));

                    if (resource != null) {
                        Violation violation =
                            Violation.create(rule, resource).setLineId(v.getLine()).setMessage(v.getDescription());
                        context.saveViolation(violation);
                    }
                }
            }
        } else {
            LOG.info("Skipping result processing");
        }
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    private static Logger LOG = LoggerFactory.getLogger(IdeaSensor.class);

}
