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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import org.xwiki.contrib.redpen.OutputHandler;

/**
 * This component edits the configuration file according to the user's chosen settings.
 *
 * @version $Id: $
 * @since 1.0
 */
@Component
@Named("RedpenOutputHandler")
@Singleton
public class RedPenOutputHandler implements OutputHandler
{
    private static final String[] ERRORS = { "SuccessiveWord", "DoubleNegative" };

    @Inject
    private Logger logger;

    private DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    private ArrayList<Node> errorList = new ArrayList<Node>();

    /**
     * @param input InputStream in xml format
     * @return Custom formatted message on validation errors
     */
    public String formatString(InputStream input)
    {
        String res;
        try {
            res = buildString(docBuilder(input));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            res = e.getMessage();
        }
        return res;
    }

    /**
     * @return true if sorted validators show that there are validation errors
     */
    public boolean containsValidationErrors()
    {
        return (errorList.size() > 0);
    }

    private Document docBuilder(InputStream in) throws SAXException, IOException, ParserConfigurationException
    {
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(wrapStream(in));
        doc.getDocumentElement().normalize();
        return doc;
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
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        this.errorList = errorNodes(nodeList);
        ArrayList<Node> warningList = warningNodes(nodeList);

        return buildStringFromNodes(errorList, true) + buildStringFromNodes(warningList, false);
    }

    private String buildStringFromNodes(ArrayList<Node> nodes, boolean error)
    {
        String nextLine = "\n";
        StringBuilder errorMessage = new StringBuilder("");
        for (Node n : nodes) {

            if (n.getNodeType() == Node.ELEMENT_NODE) {
                NodeList childNodes = n.getChildNodes();
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
            }
        }
        if (error) {
            this.logger.error(errorMessage.toString());
            return "Error:" + nextLine + errorMessage.toString();
        } else {
            this.logger.warn(errorMessage.toString());
            return "Warning:" + nextLine + errorMessage.toString();
        }
    }

    private ArrayList<Node> errorNodes(NodeList nodes)
    {
        this.errorList.clear();
        ArrayList<Node> res2 = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);
            Node validatorNode = currentNode.getFirstChild();
            if (isError(validatorNode.getTextContent().trim())) {
                res2.add(validatorNode);
            }
        }
        return res2;
    }

    private ArrayList<Node> warningNodes(NodeList nodes)
    {
        ArrayList<Node> res3 = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node currentNode = nodes.item(i);
            Node validatorNode = currentNode.getFirstChild();
            if (!isError(validatorNode.getTextContent().trim())) {
                res3.add(validatorNode);
            }
        }
        return res3;
    }

    private boolean isError(String s)
    {
        Boolean error = false;
        List<String> defaultErrors = Arrays.asList(ERRORS);
        for (String str : defaultErrors) {
            if (s.equals(str)) {
                error = true;
                break;
            }
        }
        return error;
    }
}
