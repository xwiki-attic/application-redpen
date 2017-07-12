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
package org.xwiki.contrib.redpen.test.po;

import java.util.Arrays;

import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.test.ui.po.ViewPage;

/**
 * Represents possible actions to be taken in the dictionary.
 *
 * @version $Id: $
 * @since 1.0
 */
public class ContentCheckerDictionaryPage extends ViewPage
{
    /**
     * Main wiki id.
     */
    public static final String MAIN_WIKI = "xwiki";

    /**
     * Document reference to the Dictionary
     */
    public static final ContentCheckerDictionaryPage DEFAULT_DICTIONARY_PAGE = new ContentCheckerDictionaryPage(
            new DocumentReference(MAIN_WIKI, Arrays.asList("Content Checker"), "Dictionary"));

    private EntityReference dictReference;


    /**
     * @param dictReference the reference to the dictionary page of Content Checker
     */
    public ContentCheckerDictionaryPage(EntityReference dictReference)
    {
        this.dictReference = dictReference;
    }

    /**
     * Opens the home page.
     */
    public void gotoPage()
    {
        getUtil().gotoPage(this.dictReference);
    }

    /**
     * @return the String reference to the space the Dictionary page is in
     */
    public String getSpaces()
    {
        return getUtil().serializeReference(
                this.dictReference.extractReference(EntityType.SPACE).removeParent(new WikiReference(MAIN_WIKI)));
    }

    /**
     * @return the name of the page where the dictionary is installed
     */
    public String getPage()
    {
        return this.dictReference.getName();
    }


}