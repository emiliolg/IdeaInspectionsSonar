

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
import java.io.FileNotFoundException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.sonar.api.BatchExtension;
import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.TimeProfiler;

public class IdeaExecutor
    implements BatchExtension
{
    //~ Instance fields ................................................................................................

    private final File bootJar;

    private final File reportDirectory;
    private final File profile;
    private final File project;

    private final String javaHome;
    private final String jdkHome;

    private final String jdkName;
    private final String libDir;

    private final String memory;
    private final String permSize;

    //~ Constructors ...................................................................................................

    public IdeaExecutor(IdeaConfiguration configuration)
    {
        project = configuration.getIdeaProject();
        profile = configuration.getXMLDefinitionFile();
        reportDirectory = configuration.getReportDirectory();
        jdkName = configuration.getJdkName();
        jdkHome = configuration.getJdkHome();
        javaHome = configuration.getJavaHome();
        memory = configuration.getMaxMemory();
        permSize = configuration.getPermSize();
        libDir = configuration.getInspectionsLibDir();
        bootJar = new File(libDir, "boot.jar");
        validate();
    }

    //~ Methods ........................................................................................................

    /**
     * Execute Idea Inspections and return the generated XML report.
     */
    public File execute()
    {
        TimeProfiler profiler =
            new TimeProfiler().start("Execute Idea Inspections " + IdeaVersion.getVersion());

        CommandLine cmdLine = new CommandLine(javaHome + "/bin/java");

        cmdLine.addArgument("-Xmx" + memory);
        cmdLine.addArgument("-XX:MaxPermSize=" + permSize);
        cmdLine.addArgument("-Xbootclasspath/a:" + bootJar.getPath());

        cmdLine.addArgument("-Djdk." + jdkName + "=" + jdkHome);
        cmdLine.addArgument("-cp");
        cmdLine.addArgument(buildClassPath());
        cmdLine.addArgument(IDEA_MAIN_CLASS_NAME);
        cmdLine.addArgument("inspect");
        cmdLine.addArgument(project.getPath());
        cmdLine.addArgument(profile.getPath());
        cmdLine.addArgument(reportDirectory.getPath());
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(0);

        //ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        //executor.setWatchdog(watchdog);
        try {
            LOG.info("About to execute: \n" + cmdLine.toString());
            //executor.execute(cmdLine);
        }
        catch (Exception e) {
            throw new SonarException("Can not execute Idea Inspections", e);
        }
        finally {
            profiler.stop();
        }
        return reportDirectory;
    }

    private void validate()
    {
        Exception e = null;

        if (!project.exists()) {
            e = new FileNotFoundException(project.getPath());
        }
        else if (!bootJar.exists()) {
            e = new FileNotFoundException(bootJar.getPath());
        }
        else if (!profile.exists()) {
            e = new FileNotFoundException(profile.getPath());
        }

        if (e != null) {
            throw new SonarException(e);
        }
    }

    private String buildClassPath()
    {
        StringBuilder result = new StringBuilder();

        for (String jar : jars) {
            if (result.length() > 0) {
                result.append(File.pathSeparatorChar);
            }

            result.append(new File(libDir, jar).getPath());
        }

        return result.toString();
    }

    //~ Static fields/initializers .....................................................................................

    private static final String[] jars = {
            "bootstrap.jar",
            "util.jar",
            "jdom.jar",
            "log4j.jar",
            "extensions.jar",
            "trove4j.jar",
        };
    private static final String   IDEA_MAIN_CLASS_NAME = "com.intellij.idea.Main";

    private static Logger LOG = LoggerFactory.getLogger(IdeaExecutor.class);

}
