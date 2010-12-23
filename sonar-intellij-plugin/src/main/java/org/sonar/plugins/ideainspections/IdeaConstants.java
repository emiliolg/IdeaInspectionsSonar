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

public final class IdeaConstants
{

  public static final String REPOSITORY_KEY = "ideainspections";
  public static final String REPOSITORY_NAME = "IdeaInspections";
  public static final String PLUGIN_NAME = "Idea Inspections";

    public static final String IDEA_HOME_KEY = "sonar.ideainspections.home";

    public static final String IDEA_PROJECT_KEY = "sonar.ideainspections.project";

    public static final String MEMORY_MAX_KEY = "sonar.ideainspections.memory.max";
    public static final String MEMORY_MAX_DEFAULT = "256m";

    public static final String MEMORY_PERM_SIZE_KEY = "sonar.ideainspections.memory.perm.size";
    public static final String MEMORY_PERM_SIZE_DEFAULT = "128m";


    public static final String JDK_NAME_KEY = "sonar.ideainspections.jdk.name";
    public static final String JDK_NAME_DEFAULT = "1.6";

    public static final String JDK_HOME_KEY = "sonar.ideainspections.jdk.home";
    public static final String JDK_HOME_DEFAULT = "${java.home}";

    public static final String INSPECTION_TOOL_NODE = "inspection_tool";
    public static final String INSPECTION_CLASS_ATTR = "class";
    public static final String INSPECTION_LEVEL_ATTR = "level";
    public static final String INSPECTION_ENABLED = "enabled";
    public static final String INSPECTION_OPTION_NODE = "option";
    public static final String INSPECTION_OPTION_NAME_ATTR = "name";
    public static final String INSPECTION_OPTION_VALUE_ATTR = "value";
    public static final String CONFIGURATON_FILE = "ideainspections.xml";
    public static final String RESULT_DIR = "idea-inspections-result";
    public static final String JAVA_HOME = "java.home";
}
