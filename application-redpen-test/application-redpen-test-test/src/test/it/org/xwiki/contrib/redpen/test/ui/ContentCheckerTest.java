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
package org.xwiki.contrib.redpen.test.ui;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.xwiki.administration.test.po.AdministrationSectionPage;
import org.xwiki.contrib.redpen.test.po.ContentCheckerDictionaryPage;
import org.xwiki.test.ui.AbstractTest;
import org.xwiki.test.ui.SuperAdminAuthenticationRule;

/**
 * UI tests for the Content Checker application.
 *
 * @version $Id: $
 * @since 1.0
 */

public class ContentCheckerTest extends AbstractTest
{
    // Login as superadmin to have delete rights.
    @Rule
    public SuperAdminAuthenticationRule authenticationRule = new SuperAdminAuthenticationRule(getUtil());

    @Test
    public void testChecker() throws Exception
    {
        String dictionaryTestPage = "Test.DictionaryEntry";

        getUtil().rest().deletePage(getTestClassName(), dictionaryTestPage);

        AdministrationSectionPage sectionPage = AdministrationSectionPage.gotoPage("Dictionary");

        ContentCheckerDictionaryPage dictPage = ContentCheckerDictionaryPage.DEFAULT_DICTIONARY_PAGE;
        Assert.assertEquals(dictPage.getSpaces(), sectionPage.getMetaDataValue("space"));
        Assert.assertEquals(dictPage.getPage(), sectionPage.getMetaDataValue("page"));



    }
}
