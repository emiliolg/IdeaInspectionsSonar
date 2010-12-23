

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

import org.junit.Test;

import static org.hamcrest.number.OrderingComparisons.greaterThan;

import static org.junit.Assert.assertThat;

public class IdeaPluginTest
{
    //~ Methods ........................................................................................................

    @Test
    public void getSonarWayProfileDefinition()
    {
        IdeaPlugin plugin = new IdeaPlugin();
        assertThat(plugin.getExtensions().size(), greaterThan(1));
    }
}
