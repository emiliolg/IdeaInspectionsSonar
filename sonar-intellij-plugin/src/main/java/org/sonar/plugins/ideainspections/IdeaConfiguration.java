

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

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;

import org.sonar.api.BatchExtension;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import static org.sonar.plugins.ideainspections.IdeaConstants.*;

public class IdeaConfiguration
    implements BatchExtension
{
    //~ Instance fields ................................................................................................

    private final Configuration configuration;

    private IdeaProfileExporter exporter;
    private final Project                  project;
    private RulesProfile                   profile;

    //~ Constructors ...................................................................................................

    public IdeaConfiguration(IdeaProfileExporter exporter, RulesProfile profile, Project project)
    {
        this.exporter = exporter;
        this.profile = profile;
        this.project = project;
        configuration = project.getConfiguration();
    }

    //~ Methods ........................................................................................................

    public File getXMLDefinitionFile()
    {
        Writer writer = null;
        File   xmlFile = new File(project.getFileSystem().getSonarWorkingDirectory(), CONFIGURATON_FILE);

        try {
            writer = new OutputStreamWriter(new FileOutputStream(xmlFile, false), CharEncoding.UTF_8);
            exporter.exportProfile(profile, writer);
            writer.flush();
            return xmlFile;
        }
        catch (IOException e) {
            throw new SonarException("Fail to save the Idea Inspections configuration to " + xmlFile.getPath(), e);
        }
        finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public List<File> getSourceFiles()
    {
        return project.getFileSystem().getSourceFiles(Java.INSTANCE);
    }

    public File getReportDirectory()
    {
            return new File(project.getFileSystem().getSonarWorkingDirectory(), RESULT_DIR);
    }

    public Charset getCharset()
    {
        Charset charset = project.getFileSystem().getSourceCharset();

        if (charset == null) {
            charset = Charset.forName(System.getProperty("file.encoding", CharEncoding.UTF_8));
        }

        return charset;
    }

    public String getJdkName()
    {
        return configuration.getString(IdeaConstants.JDK_NAME_KEY, IdeaConstants.JDK_NAME_DEFAULT);
    }

    public String getJdkHome()
    {
        String result = configuration.getString(IdeaConstants.JDK_HOME_KEY, IdeaConstants.JDK_HOME_DEFAULT);
        return result.isEmpty() ? System.getProperty(IdeaConstants.JAVA_HOME) : result;
    }

    public String getJavaHome()
    {
        final String result = configuration.getString(IdeaConstants.JAVA_HOME, "");
        return result.isEmpty() ? System.getProperty(IdeaConstants.JAVA_HOME) : result;
    }

    public String getMaxMemory()
    {
        return configuration.getString(MEMORY_MAX_KEY, MEMORY_MAX_DEFAULT);
    }

    public String getPermSize()
    {
        return configuration.getString(MEMORY_PERM_SIZE_KEY, MEMORY_PERM_SIZE_DEFAULT);
    }

    public String getInspectionsLibDir()
    {
        final String ideaHome = configuration.getString(IdeaConstants.IDEA_HOME_KEY, "");

        if (ideaHome.isEmpty()) {
            throw new SonarException("You must configure the Idea Installation Path (under System/Settings/IdeaInspections)");
        }

        return new File(ideaHome, "lib").getPath();
    }

    public File getIdeaProject()
    {
        String projectFile = configuration.getString(IdeaConstants.IDEA_PROJECT_KEY, "");
        if (projectFile.isEmpty()) {
            projectFile = project.getArtifactId() + ".ipr";
        }
        return new File(project.getFileSystem().getBasedir(), projectFile);
    }

}
