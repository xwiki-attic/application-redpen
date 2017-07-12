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
package org.xwiki.contrib.redpen.internal.Configuration;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.redpen.CheckerConfiguration;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.internal.objects.ListPropertyPersistentList;

import cc.redpen.config.ValidatorConfiguration;

/**
 * Contains methods to alter configuration file for settings like sentence length and paragraph length.
 *
 * @version $Id: $
 * @since 1.0
 */
@Component
@Named("RedpenConfiguration")
@Singleton
public class RedPenCheckerConfiguration implements CheckerConfiguration
{
    private static final String CHECK_START = "checker_start";

    private static final String CHECK_EXCEPTION = "checker_exception";

    private static final String CHECK_INCLUSION = "checker_inclusion";

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    @Inject
    @Named("CheckerConfigSource")
    private ConfigurationSource configSource;

    @Inject
    private QueryManager queryManager;

    /**
     * @param source Document Reference of the page content checker is running in
     * @return List of document references of excluded pages
     */
    public List<DocumentReference> getExceptionList(DocumentReference source)
    {
        return getListFromConfigSource(source, CHECK_EXCEPTION);
    }

    /**
     * @param source Document Reference of the page content checker is running in
     * @return List of document references of included pages
     */
    public List<DocumentReference> getInclusionList(DocumentReference source)
    {
        return getListFromConfigSource(source, CHECK_INCLUSION);
    }

    /**
     * @return validation settings as a List of ValidatorConfiguration objects
     */
    public List<ValidatorConfiguration> getValidationSettings()
    {
        List<ValidatorConfiguration> res = new ArrayList<>();
        List<String> keys = getConfigFields();
        for (String s : keys) {
            if (validationBuilder(s) != null) {
                res.addAll(validationBuilder(s));
            }
        }
        return res;
    }

    /**
     * @return boolean value indicating if Automatic validation is turned on or off
     */
    public boolean willStart()
    {
        return ((Integer) this.configSource.getProperty(CHECK_START) == 1);

    }

    private List<DocumentReference> getListFromConfigSource(DocumentReference source, String key)
    {
        List<DocumentReference> res = new ArrayList<>();
        WikiReference wiki = source.getWikiReference();
        ListPropertyPersistentList list = this.configSource.getProperty(key);
        for (Object o : list) {
            String spacename = (String) o;
            SpaceReference space = new SpaceReference(wiki.getName(), spacename);
            try {
                res.addAll(queryNestedDocs(space));
            } catch (QueryException q) {
                this.logger.error(q.getMessage());
            }
        }
        //this.logger.info(list.toString());

        return res;
    }
    /**
     * @return ValidatorConfiguration object using key and its corresponding property
     */
    private List<ValidatorConfiguration> validationBuilder(String key)
    {
        List<ValidatorConfiguration> res = new ArrayList<>();
        if (!(key.equals(CHECK_START) || key.equals(CHECK_EXCEPTION) || key.equals(CHECK_INCLUSION))) {
            String[] keys = key.split("\\.");
            Object prop = this.configSource.getProperty(key);
            int len = keys.length;
            switch (len) {
                case 1:
                    List<Object> valList = (List) prop;
                    for (Object e : valList) {
                        String valString = (String) e;
                        res.add(new ValidatorConfiguration(valString));
                    }
                    break;
                case 2:
                    res.add(new ValidatorConfiguration(keys[0]).addProperty(keys[1], prop));
                    break;
                default:
                    break;
            }
            return res;
        } else {
            return null;
        }
    }

    /**
     * @return List of keys from configuration document
     */
    private List<String> getConfigFields()
    {
        return this.configSource.getKeys();
    }

    private List<DocumentReference> queryNestedDocs(SpaceReference space) throws QueryException
    {
        List<DocumentReference> docList = new ArrayList<>();
        String wikiname = space.getWikiReference().getName();
        String spacename = space.getName();
        String queryString = "where (doc.space like :space or doc.space like :space.%) and doc.hidden = '0'";
        Query query = this.queryManager.createQuery(queryString, Query.XWQL);
        query.bindValue("space", spacename);
        List<Object> results = query.execute();
        for (Object o : results) {
            String docStr = (String) o;
            //this.logger.info(docStr);
            DocumentReference doc = new DocumentReference(wikiname, spacename, docStr);
            docList.add(doc);
        }
        return docList;
    }
}
