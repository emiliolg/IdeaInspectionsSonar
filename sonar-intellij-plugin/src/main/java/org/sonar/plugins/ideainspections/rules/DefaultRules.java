

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


package org.sonar.plugins.ideainspections.rules;

import java.io.InputStreamReader;
import java.util.SortedMap;

import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;

import static org.sonar.plugins.ideainspections.rules.IdeaRepository.getResourceAsStream;

public class DefaultRules
{
    //~ Methods ........................................................................................................

    public static SortedMap<String, Rule> get()
    {
        return rules;
    }

    //~ Static fields/initializers .....................................................................................

    private static final SortedMap<String, Rule> rules;

    static {
        RulesImporter importer = new RulesImporter();

        importer.importProfile(new InputStreamReader(getResourceAsStream("default.xml")),
                               ValidationMessages.create());
        rules = importer.getRules();
    }
}
