/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.redpen.internal;

import org.junit.Before;
import org.junit.Rule;
import org.xwiki.configuration.internal.MemoryConfigurationSource;
import org.xwiki.contrib.redpen.internal.Configuration.RedPenCheckerConfiguration;
import org.xwiki.contrib.redpen.internal.Configuration.RedPenConfigClassDocumentConfigurationSource;
import org.xwiki.test.annotation.BeforeComponent;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentManagerRule;

@ComponentList({
        RedPenContentChecker.class,
        RedPenOutputHandler.class,
        RedPenCheckerConfiguration.class,
        RedPenConfigClassDocumentConfigurationSource.class
})
public class RedPenContentCheckerTest
{
    @Rule
    public final MockitoComponentManagerRule componentManager = new MockitoComponentManagerRule();

    private RedPenCheckerConfiguration config;

    private MemoryConfigurationSource source;

    @BeforeComponent
    public void registerComponents() throws Exception
    {
        // Register some in-memory Configuration Source for the test
        this.source = this.componentManager.registerMemoryConfigurationSource();
    }

    @Before
    public void setUp() throws Exception
    {
        initialiseConfig(1);
        this.config = this.componentManager.getInstance(RedPenCheckerConfiguration.class);
    }



    private void initialiseConfig(int start)
    {
        source.setProperty("checker_start", 1);
    }


}
