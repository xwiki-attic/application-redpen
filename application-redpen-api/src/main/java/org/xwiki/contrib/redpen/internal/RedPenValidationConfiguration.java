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

/**
 * Created by DeSheng on 22/6/2017.
 */

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
//import org.xwiki.model.EntityType;
import org.xwiki.contrib.redpen.ValidationConfiguration;
import org.xwiki.model.reference.DocumentReference;
//import org.xwiki.model.reference.EntityReference;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.objects.BaseObject;

    /**
     * Takes in configuration for RedPen Document checker.
     * Also includes methods to alter configuration file for settings like sentence
     * length and paragraph length.
     * @version $Id: $
     * @since 1.0
     */

@Component
@Singleton
@Named("RedpenConfiguration")
public class RedPenValidationConfiguration implements ValidationConfiguration
{
    private static final String WIKI_NAME = "xwiki";
    private static final String SPACE_NAME = "Content Checker.Configuration";
    private static final DocumentReference CONFIG_XCLASS_REFERENCE =
            new DocumentReference(WIKI_NAME, SPACE_NAME, "GeneralConfigurationClass");

    private static final DocumentReference CONFIG_DOCUMENT_REFERENCE =
            new DocumentReference(WIKI_NAME, SPACE_NAME, "WebHome");

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    /**
     * @return boolean value determining whether document checker will run
     */
    public boolean willStart()
    {
        try {

            XWikiDocument configurationDoc = getConfigDocument();
            BaseObject configObject = configurationDoc.getXObject(CONFIG_XCLASS_REFERENCE);
            return (configObject.getIntValue("checker_start") == 1) && (configObject != null);


        } catch (XWikiException e) {
            this.logger.error(e.getMessage());
            return false;
        }
        
    }

    /**
     * @param sourceDoc the source document this check runs in
     * @return boolean result determining if source document will be validated
     */
    public boolean willRunInDocument(XWikiDocument sourceDoc) {
        //TODO: In the UI, change "pages" in pretty text of checker_exception to "space" to reflect actual code
        boolean willRun = true;
        try {
            XWikiDocument configDoc = getConfigDocument();
            if (configDoc.isHidden()) {
                //Document checker will not check any hidden pages as it is assumed they contain only code which aren't
                //natural languages to be validated
                willRun = false;
            } else {

                BaseObject configObject = configDoc.getXObject(CONFIG_XCLASS_REFERENCE);
                String exceptionList = configObject.get("checker_exception").toFormString();

                //remove braces surrounding string
                StringBuilder sb = new StringBuilder(exceptionList);
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.lastIndexOf(exceptionList));
                exceptionList = sb.toString();
                //splits the strings
                String[] exceptionArray = exceptionList.split(" ,");
                String spaceName;
                for (String s : exceptionArray) {
                    spaceName = sourceDoc.getDocumentReference().getLastSpaceReference().getName();
                    if (spaceName.equals(s)) {
                        willRun = false;
                        break;
                    }
                }
            }
        } catch (XWikiException e) {
            willRun = false;
            this.logger.error(e.getMessage());
        }
        return willRun;
    }

    /**
     * @return settings as a List of Strings
     */
    public ArrayList<String> getValidationSettings()
    {
        ArrayList<String> res = new ArrayList<>();
        res.add(getSentLength());
        res.add(getParaLength());
        return res;
    }

    /**
     * @return XWikiDocument from Content Checker.Configuration page in the wiki
     * @throws XWikiException
     */
    private XWikiDocument getConfigDocument() throws XWikiException
    {
        XWikiContext context = contextProvider.get();
        XWiki xwiki = context.getWiki();

        return xwiki.getDocument(CONFIG_DOCUMENT_REFERENCE, context);
    }


    /**
     * @return the sentence length parameter input by the user
     */
    private String getSentLength()
    {
        try {
            XWikiDocument document = getConfigDocument();
            BaseObject configObject = document.getXObject(CONFIG_XCLASS_REFERENCE);
            //-1 will be the token value that will tell the ContentValidator to skip editing configuration
            int sentLength = configObject.getIntValue("sent_length", -1);
            return Integer.toString(sentLength);
        } catch (XWikiException e) {
            this.logger.error(e.getMessage());
            return Integer.toString(-1);
        }
    }

    /**
     * @return the paragraph length parameter input by the user
     */
    private String getParaLength()
    {
        try {
            XWikiDocument document = getConfigDocument();
            BaseObject configObject = document.getXObject(CONFIG_XCLASS_REFERENCE);
            //-1 will be the sentinel value that will tell the ContentValidator to skip editing configuration
            int paraLength = configObject.getIntValue("para_length", -1);
            return Integer.toString(paraLength);
        } catch (XWikiException e) {
            this.logger.error(e.getMessage());
            return Integer.toString(-1);
        }
    }

}
