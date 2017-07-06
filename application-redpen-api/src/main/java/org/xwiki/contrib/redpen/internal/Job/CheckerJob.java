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
package org.xwiki.contrib.redpen.internal.Job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.ContentChecker;
import org.xwiki.contrib.redpen.script.CheckerJobScriptService;
import org.xwiki.job.AbstractJob;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;
import org.xwiki.security.authorization.AuthorizationManager;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

/**
 * This is a Job that extracts the XWiki documents from desired spaces.
 *
 * @version $Id: $
 * @since 1.0
 */
@Component
@Named(CheckerJobScriptService.CONTENT_CHECKER)
@Singleton
public class CheckerJob extends AbstractJob<CheckerRequest, CheckerJobStatus>
{
    @Inject
    @Named("redpenchecker")
    private ContentChecker contentChecker;

    @Inject
    private QueryManager queryManager;

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private AuthorizationManager authorization;

    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Override
    public String getType()
    {
        return CheckerJobScriptService.CONTENT_CHECKER;
    }

    @Override
    protected void runInternal() throws Exception
    {
        List<DocumentReference> documentReferences = getDocumentReferences(this.request.getSpaceList());
        this.progressManager.pushLevelProgress(documentReferences.size(), this);

        try {
            for (DocumentReference documentReference : documentReferences) {
                if (this.status.isCanceled()) {
                    break;
                } else {
                    this.progressManager.startStep(this);
                    checkContent(documentReference);
                }
            }
        } finally {
            this.progressManager.popLevelProgress(this);
        }
    }

    private void checkContent(DocumentReference document) throws XWikiException
    {
        XWikiContext xcontext = this.contextProvider.get();
        XWiki xwiki = xcontext.getWiki();
        XWikiDocument xdoc = xwiki.getDocument(document, xcontext);
        String content = xdoc.getContent();
        String result = contentChecker.validate(content);
        xdoc.setContent(content + result);
    }

    private List<DocumentReference> getDocumentReferences(String spaceList) throws QueryException
    {
        List<DocumentReference> documents = new ArrayList<>();
        List<String> spaces = Arrays.asList(spaceList.split(","));
        List<Object> res = queryDoc(spaces);
        for (Object d : res) {
            if (d instanceof DocumentReference) {
                documents.add((DocumentReference) d);
            }
        }
        return documents;
    }

    private List<Object> queryDoc(List<String> spaces) throws QueryException
    {
        List<Object> res2 = new ArrayList<>();
        for (String s : spaces) {
            String queryStr = String.format("where doc.space like %s or doc.space like ", s);
            //query initialisation in such a weird form as % is special character in String.format
            queryStr += "'" + s + ".%'";
            Query query = this.queryManager.createQuery(queryStr, Query.XWQL);
            res2.addAll(query.execute());
        }

        return res2;
    }
}
