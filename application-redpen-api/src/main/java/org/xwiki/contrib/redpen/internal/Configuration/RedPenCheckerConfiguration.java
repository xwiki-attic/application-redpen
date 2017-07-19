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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.redpen.CheckerConfiguration;
import org.xwiki.contrib.redpen.DictionaryHandler;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

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

    private static final String INVALID_EXPRESSION = "InvalidExpression";

    private static final String SUGGEST_EXPRESSION = "SuggestExpression";


    @Inject
    private Logger logger;

    @Inject
    @Named("CheckerConfigSource")
    private ConfigurationSource checkerConfigSource;

    @Inject
    private ConfigurationSource propertyConfigSource;

    @Inject
    private QueryManager queryManager;

    @Inject
    @Named("RedpenDictionary")
    private DictionaryHandler dictionaryHandler;

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
        return ((Integer) this.checkerConfigSource.getProperty(CHECK_START) == 1);

    }

    /**
     * @return severity level of validators as Map<String,SeverityLevel>
     */
    public Map<String, SeverityLevel> getSeverityLevels() {
        Map<String, SeverityLevel> res = new HashMap<>();
        Properties value = this.propertyConfigSource.getProperty("redpen.severityLevels", Properties.class);
        Enumeration e = value.propertyNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement().toString();
            String val = value.getProperty(key).toUpperCase();
            SeverityLevel level = SeverityLevel.valueOf(val);
            res.put(key, level);

        }
        return res;
    }

    private List<DocumentReference> getListFromConfigSource(DocumentReference source, String key)
    {
        List<DocumentReference> res = new ArrayList<>();
        WikiReference wiki = source.getWikiReference();
        List<Object> list = this.checkerConfigSource.getProperty(key);
        for (Object o : list) {
            String spacename = (String) o;
            SpaceReference space = new SpaceReference(wiki.getName(), spacename);
            try {
                res.addAll(queryNestedDocs(space));
            } catch (QueryException q) {
                this.logger.error(q.getMessage());
            }
        }

        return res;
    }
    /**
     * @return ValidatorConfiguration object using key and its corresponding property
     */
    private List<ValidatorConfiguration> validationBuilder(String key)
    {
        List<ValidatorConfiguration> res = new ArrayList<>();
        if (!(key.equals(CHECK_START) || key.equals(CHECK_EXCEPTION) || key.equals(CHECK_INCLUSION))) {
            if (key.equals(INVALID_EXPRESSION) || key.equals(SUGGEST_EXPRESSION)) {
                res.add(expressionHandler(key));
            } else {
                String[] keys = key.split("\\.");
                Object prop = this.checkerConfigSource.getProperty(key);
                int len = keys.length;
                switch (len) {
                    case 1:
                        if ((Integer) prop == 1) {
                            res.add(new ValidatorConfiguration(keys[0]));
                        }
                        break;
                    case 2:
                        res.add(new ValidatorConfiguration(keys[0]).addProperty(keys[1], prop));
                        break;
                    default:
                        break;
                }
            }
            return res;
        } else {
            return null;
        }
    }

    private ValidatorConfiguration expressionHandler(String key) {
        switch (key) {
            case INVALID_EXPRESSION:
                String list = dictionaryHandler.getInvalidWords();
                return new ValidatorConfiguration(key).addProperty("list", list);
            case SUGGEST_EXPRESSION:
                Map<String, String> synonyms = dictionaryHandler.getSuggestedExpressions();
                return new ValidatorConfiguration(key).addProperty("map", synonymsHandler(synonyms));
            default:
                return null;
        }
    }


    private String synonymsHandler(Map<String, String> synonyms) {
        //Formatted to a string format that is the only one to be accepted by redpen currently.
        //Placed the handler here as HashMap in Dictionary Handler should be a better data structure to receive
        //expression-suggestion pair
        String synonymDefString = synonyms.toString();
        String res = synonymDefString.replace(",", "},{").replace('=', ',');
        return res;
    }
    /**
     * @return List of keys from configuration document
     */
    private List<String> getConfigFields()
    {
        return this.checkerConfigSource.getKeys();
    }

    private List<DocumentReference> queryNestedDocs(SpaceReference space) throws QueryException
    {
        List<DocumentReference> docList = new ArrayList<>();
        String wikiname = space.getWikiReference().getName();
        String spacename = space.getName();
        String nestedRef = spacename + "With\\.Dot.%";
        String nestedRef2 = nestedRef.replaceAll("([%_!])", "!$1").concat(".%");
        String queryString = "where (doc.space like :space1 or doc.space like :space2) and doc.hidden = '0'";
        Query query = this.queryManager.createQuery(queryString, Query.XWQL).bindValue("space1", spacename)
                .bindValue("space2", nestedRef2);
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
