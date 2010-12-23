

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

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.plugins.ideainspections.rules.IdeaRepository;

import java.util.Arrays;
import java.util.List;

@Properties(
            {
                @Property(
                          key = IdeaConstants.IDEA_HOME_KEY,
                          name = "Idea Installation Path",
                          description =
                          "Path (absolute or relative) where Intellij is installed." +
                          "The plugin will search under the 'lib' subdirectory for the jars needed to run the inspections.",
                          global = true
                         ),
                @Property(
                          key = IdeaConstants.IDEA_PROJECT_KEY,
                          name = "Idea project File",
                          description = "The (relative to basedir) path to the Idea Project file (\".ipr\")",
                          project = true,
                          global = false
                         ),
                @Property(
                          key = IdeaConstants.JDK_NAME_KEY,
                          defaultValue = IdeaConstants.JDK_NAME_DEFAULT,
                          name = "JDK Name",
                          description = "JDK Name as specified in \".ipr\" file",
                          project = true,
                          global = true
                         ),
                @Property(
                          key = IdeaConstants.JDK_HOME_KEY,
                          defaultValue = IdeaConstants.JDK_HOME_DEFAULT,
                          name = "JDK Home",
                          description = "Path to JDK specified in \".ipr\" file",
                          project = true,
                          global = true
                         ),
                @Property(
                          key = IdeaConstants.MEMORY_MAX_KEY,
                          defaultValue = IdeaConstants.MEMORY_MAX_DEFAULT,
                          name = "Max Memory",
                          description = "Maximum memory to pass to JVM of Intellij processes",
                          project = true,
                          global = true
                         ),
                @Property(
                          key = IdeaConstants.MEMORY_PERM_SIZE_KEY,
                          defaultValue = IdeaConstants.MEMORY_PERM_SIZE_DEFAULT,
                          name = "Perm Size",
                          description = "Maximum Permanent Generation Memory Size to pass to JVM of Intellij processes",
                          project = true,
                          global = true
                         )
            }
           )
public class IdeaPlugin
    implements Plugin
{
    //~ Methods ........................................................................................................

    public String getKey()
    {
        return IdeaConstants.REPOSITORY_KEY;
    }

    public String getName()
    {
        return IdeaConstants.PLUGIN_NAME;
    }

    public String getDescription()
    {
        return "This plugin allows you to run Intellij Idea inspection engine inside Sonar";
    }

    public List getExtensions()
    {
        return Arrays.asList(IdeaSensor.class,
                             IdeaConfiguration.class,
                             IdeaExecutor.class,
                             IdeaAuditListener.class,
                             IdeaProfileExporter.class,
                             IdeaImporter.class,
                             IdeaRepository.class,
                             SonarWayProfile.class,
                             SunConventionsProfile.class,
                             SonarWayWithFindbugsProfile.class);
    }
}
