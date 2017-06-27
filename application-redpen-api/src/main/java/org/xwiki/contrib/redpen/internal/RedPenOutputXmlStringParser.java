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
 * Created by DeSheng on 27/6/2017.
 */

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.XmlStringParser;

/**
 * This component edits the configuration file according to the user's chosen settings.
 * @version $Id: $
 * @since 1.0
 */

@Component
@Named("RedpenOutputParser")
@Singleton
public class RedPenOutputXmlStringParser implements XmlStringParser
{
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    public String formatString(InputStream input)
    {
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(input);
            doc.getDocumentElement().normalize();
            doc = editDoc(doc);
            return input.toString();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            return e.getMessage();
        }
    }

    private Document editDoc(Document doc) {
        
        return doc;
    }
}
