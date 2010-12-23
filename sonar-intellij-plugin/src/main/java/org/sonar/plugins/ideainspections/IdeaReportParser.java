

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


package org.sonar.plugins.ideainspections;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.codehaus.staxmate.in.SMInputCursor;

import org.sonar.api.utils.SonarException;
import org.sonar.api.utils.XmlParserException;

import static org.sonar.plugins.ideainspections.IdeaUtils.createStaxParser;

class IdeaReportParser
{
    //~ Instance fields ................................................................................................

    private final File            reportDirectory;
    private final List<Violation> violations;

    //~ Constructors ...................................................................................................

    public IdeaReportParser(File reportDir)
    {
        if (!reportDir.exists() || !reportDir.isDirectory()) {
            throw new SonarException("The directory for Idea Inspections reports '" + reportDir.getAbsolutePath() +
                                     "'does not exists.");
        }

        reportDirectory = reportDir;
        violations = new ArrayList<Violation>();
    }

    //~ Methods ........................................................................................................

    public Collection<Violation> getViolations()
    {
        return violations;
    }

    public void parseAll()
    {
        for (File file : reportDirectory.listFiles()) {
            String name = file.getName();

            if (name.endsWith(XML_EXT) && !name.startsWith(".")) {
                try {
                    parseFile(file, name.substring(0, name.length() - XML_EXT.length()));
                }
                catch (Exception e) {
                    throw new XmlParserException("Unable to parse the Idea XML Report '" + file.getAbsolutePath() + "'",
                                                 e);
                }
            }
        }
    }

    void parseFile(File file, String rule)
        throws FileNotFoundException, XMLStreamException
    {
        SMInputCursor cursor = createStaxParser(new FileReader(file)).advance().childElementCursor("problem");

        while (cursor.getNext() != null) {
            Violation violation = new Violation(rule);
            violation.build(cursor.childElementCursor());
            violations.add(violation);
        }
    }

    //~ Static fields/initializers .....................................................................................

    private static final String XML_EXT = ".xml";

    //~ Inner Classes ..................................................................................................

    public static class Violation
    {
        protected File source;
        private int    line;
        private String description;
        private String module;
        private String pkg;

        private final String type;

        public Violation(String type)
        {
            this.type = type;
        }

        public String getType()
        {
            return type;
        }

        public String getDescription()
        {
            return description;
        }

        public String getSourcePath()
        {
            return source.getPath();
        }

        public String getSonarJavaFileKey()
        {
            StringBuilder result = new StringBuilder();

            if (pkg != null && !pkg.isEmpty()) {
                result.append(pkg);
                result.append(".");
            }

            String fileName = source.getName();
            int    idx = fileName.lastIndexOf('.');
            result.append(idx == -1 ? fileName : fileName.substring(0, idx));
            return result.toString();
        }

        public int getLine()
        {
            return line;
        }

        @SuppressWarnings("UnusedDeclaration")
        public String getPackage()
        {
            return pkg;
        }

        @SuppressWarnings("UnusedDeclaration")
        public String getModule()
        {
            return module;
        }

        private void build(SMInputCursor c)
            throws XMLStreamException
        {
            while (c.getNext() != null) {
                String name = c.getLocalName();

                if ("file".equals(name)) {
                    String s = c.getElemStringValue();

                    if (s.startsWith(FILE_PREFIX)) {
                        s = s.substring(FILE_PREFIX.length());
                    }

                    source = new File(s);
                }
                else if ("line".equals(name)) {
                    line = c.getElemIntValue();
                }
                else if ("module".equals(name)) {
                    module = c.getElemStringValue();
                }
                else if ("package".equals(name)) {
                    final String s = c.getElemStringValue();
                    pkg = "<default>".equals(s) ? "" : s;
                }
                else if ("description".equals(name)) {
                    description = c.getElemStringValue();
                }
            }
        }

        private static final String FILE_PREFIX = "file://$PROJECT_DIR$/";
    }
}
