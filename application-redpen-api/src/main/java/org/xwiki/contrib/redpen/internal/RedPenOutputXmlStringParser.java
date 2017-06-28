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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    @Inject
    private Logger logger;

    private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    /**
     * @param input InputStream in xml format
     * @return Custom formatted message on validation errors
     */
    public String formatString(InputStream input)
    {
        InputStream wrappedInput = wrapStream(input);
        String res;
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(wrappedInput);
            doc.getDocumentElement().normalize();
            res = buildString(doc);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            res = e.getMessage();
        }
        return res;
    }

    private InputStream wrapStream(InputStream in)
    {
        List<InputStream> streams = Arrays.asList(
                new ByteArrayInputStream("<errors>".getBytes(StandardCharsets.UTF_8)),
                in,
                new ByteArrayInputStream("</errors>".getBytes(StandardCharsets.UTF_8)));
        InputStream res = new SequenceInputStream(Collections.enumeration(streams));
        return res;
    }


    private String buildString(Document doc)
    {
        String nextLine = "\n";
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        StringBuilder fullMessage = new StringBuilder("\n\n");
        for (int i = 0; i < nodeList.getLength(); i++)
        {
            StringBuilder errorMessage = new StringBuilder("");
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                NodeList childNodes = currentNode.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Element childNode = (Element) childNodes.item(j);
                    String label = childNode.getTagName();
                    switch (label) {
                        case "message":
                            errorMessage.append("Content Error: ");
                            errorMessage.append(childNode.getTextContent());
                            errorMessage.append(nextLine);
                            break;
                        case "lineNum":
                            errorMessage.append("Location: Line ");
                            errorMessage.append(childNode.getTextContent());
                            errorMessage.append(nextLine);
                            break;
                        case "sentence":
                            errorMessage.append("Sentence: ");
                            errorMessage.append(childNode.getTextContent());
                            errorMessage.append(nextLine);
                            break;
                        default:
                            break;
                    }
                }
                fullMessage.append(errorMessage.toString());
            }
        }

        return fullMessage.toString();
    }
}
