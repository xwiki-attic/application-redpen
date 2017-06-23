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
 * Created by DeSheng on 23/6/2017.
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.FileEditor;
import org.xwiki.contrib.redpen.ValidationConfiguration;

/**
 * This component edits the configuration file according to the user's chosen settings.
 * @version $Id: $
 * @since 1.0
 */

@Component
@Singleton
@Named("RedpenSettingsEditor")
public class RedPenSettingsEditor implements FileEditor
{
    @Inject
    private Logger logger;

    @Inject
    @Named("RedpenConfiguration")
    private ValidationConfiguration validationConfiguration;

    /**
     * @param configFile original configuration file obtained from JAR resource
     * @return configuration file updated with user settings
     */
    public File updateFile(File configFile)
    {
        ArrayList<String> userConfig = validationConfiguration.getValidationSettings();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        File res = configFile;
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(res);

            doc.getDocumentElement().normalize();
            String sentLength = userConfig.get(0);
            String paraLength = userConfig.get(1);

            if (sentLength.equals(Integer.toString(-1))) {
                doc = editValidatorValue(doc, "SentenceLength", "max_len", sentLength);
            }

            if (paraLength.equals(Integer.toString(-1))) {
                doc = editValidatorValue(doc, "SectionLength", "max_num", paraLength);
            }

            DOMSource source = new DOMSource(doc);
            FileWriter writer = new FileWriter(res);
            StreamResult result = new StreamResult(writer);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            this.logger.error("Unable to update configuration file" + e.getMessage());
        }
        return res;
    }

    /**
     * @param doc document objected converted from configuration file
     * @param validatorName refers to the particular setting (e.g. sentence length) that user wishes to change
     * @param propertyName refers to the property of the setting (e.g. max character number) that user wishes to have
     * @param attrValue the user's custom value of the property
     * @return document object which is edited appropriately with user settings
     */
    private Document editValidatorValue(Document doc, String validatorName, String propertyName, String attrValue)
    {
        NodeList validator = doc.getElementsByTagName("validator");
        String name = "name";
        Element emp;
        Element prop;
        NodeList property;
        for (int i = 0; i < validator.getLength(); i++) {

            emp = (Element) validator.item(i);

            if (emp.getAttribute(name).equals(validatorName)) {
                property = emp.getElementsByTagName("property");

                for (int j = 0; j < property.getLength(); j++) {
                    prop = (Element) property.item(j);
                    if (prop.getAttribute(name).equals(propertyName)) {
                        prop.setAttribute("value", attrValue);
                    }
                }
                break;
            }
        }
        return doc;
    }
}
