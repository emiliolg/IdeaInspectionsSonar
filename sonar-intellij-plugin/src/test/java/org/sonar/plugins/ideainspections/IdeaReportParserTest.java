

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
import java.util.Collection;

import org.junit.Test;

import org.sonar.api.utils.SonarException;
import org.sonar.test.TestUtils;

import javax.xml.stream.XMLStreamException;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

public class IdeaReportParserTest
{
    //~ Methods ........................................................................................................

    @Test(expected = SonarException.class)
    public void createIdeaReportParserWithUnexistedReportFile()
    {
        File xmlReport = new File("doesntExist");
        new IdeaReportParser(xmlReport);
    }

    @Test
    public void testGetViolations()
    {
        IdeaReportParser parser = new IdeaReportParser(TestUtils.getResource(REPORT_DIR));
        parser.parseAll();
        Collection<IdeaReportParser.Violation> violations = parser.getViolations();

        assertThat(violations.size(), is(13));

        IdeaReportParser.Violation violation = violations.iterator().next();
        assertThat(violation.getType(), is("RedundantCast"));
        assertThat(violation.getDescription(), is("Casting <code>t.tree</code> to <code>Node</code> is redundant"));
        assertThat(violation.getLine(), is(467));
        assertThat(violation.getSonarJavaFileKey(), is("elg.conj.C"));
        assertThat(violation.getSourcePath(), is("target/generated-sources/antlr3/elg/conj/C.java"));
        assertThat(violation.getModule(), is("conj"));
    }

    @Test
    public void testGetSingleFile() throws XMLStreamException, FileNotFoundException
    {
        final File dir = TestUtils.getResource(REPORT_DIR);
        IdeaReportParser parser = new IdeaReportParser(dir);
        parser.parseFile(new File(dir, "UnusedDeclaration.xml"), "UnusedDeclaration");

        Collection<IdeaReportParser.Violation> violations = parser.getViolations();

        assertThat(violations.size(), is(2));

        IdeaReportParser.Violation violation = violations.iterator().next();
        assertThat(violation.getType(), is("UnusedDeclaration"));
        assertThat(violation.getDescription(), is("Method is never used."));
        assertThat(violation.getLine(), is(3));
        assertThat(violation.getSonarJavaFileKey(), is("Hello"));
        assertThat(violation.getSourcePath(), is("src/Hello.java"));
        assertThat(violation.getModule(), is("TestModule"));
    }

    private static final String REPORT_DIR = "/org/sonar/plugins/ideainspections/IdeaReportParserTest";
}
