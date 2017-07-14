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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.DictionaryHandler;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

/**
 * Provides the two sets of information required to build InvalidExpression and SuggestedExpression validators.
 *
 * @version $Id: $
 * @since 1.0
 */
@Component
@Named("RedpenDictionary")
@Singleton
public class RedPenDictionaryHandler implements DictionaryHandler
{

    @Inject
    private QueryManager queryManager;

    @Inject
    private Logger logger;

    /**
     * @return String of invalid words delimited by comma
     */
    public String getInvalidWords()
    {
        String res = "";
        StringBuilder builder = new StringBuilder(res);
        try {
            List<Object[]> words = getEntries();
            for (Object[] o : words) {
                String type = (String) o[0];
                if (type.toLowerCase().equals("Invalid")) {
                    String entry = (String) o[1];
                    builder.append(entry);
                    builder.append(",");
                }
            }
            res = builder.toString();
        } catch (QueryException e) {
            this.logger.error(e.getMessage());
        }
        return res;
    }

    /**
     * @return map of subpar expressions and their corresponding corrections
     */
    public Map<String, String> getSuggestedExpressions()
    {
        Map<String, String> expressions = new HashMap<>();
        try {
            List<Object[]> words = getEntries();
            for (Object[] o : words) {
                String type = (String) o[0];
                if (type.toLowerCase().equals("Suggestion")) {
                    String entry = (String) o[1];
                    String suggestion = (String) o[2];
                    expressions.put(entry, suggestion);
                }
            }

        } catch (QueryException e) {
            this.logger.error(e.getMessage());
        }
        return expressions;
    }

    private List<Object[]> getEntries() throws QueryException
    {
        //work in progress
        //TODO: formulate proper query
        String queryStr = "select obj.EntryType, obj.entry, obj.suggestion "
                + "from doc.object('Content Checker.DictionaryCode.DictionaryEntryClass') "
                + "as obj where doc.space like 'Content Checker.Entries'";
        Query query = this.queryManager.createQuery(queryStr, Query.XWQL);
        List<Object[]> results = query.execute();
        this.logger.info(results.toString());

        return results;
    }
}
