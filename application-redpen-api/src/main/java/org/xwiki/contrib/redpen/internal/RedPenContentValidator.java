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
 * Created by DeSheng on 13/6/2017.
 */


//import org.apache.commons.io.IOUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.xwiki.contrib.redpen.ContentValidator;

import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.FileEditor;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.validator.ValidationError;
import cc.redpen.formatter.XMLFormatter;

/**
 * This component takes in string of plain text input and performs RedPen validation checks on it.
 * Outputs validation results as XML.
 * @version $Id: $
 * @since 1.0
 */

@Component
@Singleton
@Named("redpen-validator")
public class RedPenContentValidator implements ContentValidator
{

    @Inject
    @Named("RedpenSettingsEditor")
    private FileEditor redpenSettingsEditor;

    @Inject
    private Logger logger;



    /**
     *
     * @param input input string from wiki documents or XObjects
     * @return results of text validation in an XML formatted string
     * @throws RedPenException if redpen object is unsuccessfully instantiated
     */
    public String validate(String input)
    {
        String inputFormat = "plain";
        File configFile = configGenerate();
        this.logger.info("Config File generated");
        String res;
        Document doc;
        try {
            if (input == null) {
                doc = getDocument(inputFormat, " ", configFile);
            } else {
                doc = getDocument(inputFormat, input, configFile);
            }
            List<ValidationError> validate = validateDocuments(doc, configFile);
            this.logger.info("document validated");
            XMLFormatter format = new XMLFormatter();
            res = "";

            for (ValidationError v : validate) {
                res += format.formatError(doc, v) + "\n";
            }
        } catch (RedPenException r) {
            res = r.getMessage();
            this.logger.error(r.getMessage());
        }
        return res;
    }


    /**
     *
     * @return configuration settings as a File object
     */
    private File configGenerate()
    {
        File tmp = new File(
                "C:\\Program Files\\XWiki Enterprise 9.4\\webapps\\xwiki\\WEB-INF\\lib\\redpen-conf-en.xml");
        if (tmp == null) {
            try {
                InputStream in = getClass().getResourceAsStream("/redpen-conf-en.xml");
                File tempFile = File.createTempFile("redpen-conf-en", ".xml");
                tempFile.deleteOnExit();
                try (FileOutputStream out = new FileOutputStream(tempFile)) {
                    IOUtils.copy(in, out);
                }
            } catch (IOException i) {
                this.logger.error(i.getMessage());
            }
        }
        tmp = redpenSettingsEditor.updateFile(tmp);
        return tmp;
    }

    /**
     *
     * @param document takes in RedPen Documents as input
     * @return a list of errors in the input text, in json format
     * @throws RedPenException if redpen object cannot be instantiated
     */
    private List<ValidationError> validateDocuments(Document document, File configFile) throws RedPenException
    {
        RedPen r = new RedPen(configFile);
        List<ValidationError> res = r.validate(document);

        return res;
    }

    /**
     *
     * @param inputFormat takes in inputFormat as defined in renderValidation method
     * @param input
     * @param configFile takes in configuration settings
     * @return
     * @throws RedPenException if redpen object cannot be instantiated
     */
    private Document getDocument(String inputFormat, String input, File configFile)
            throws RedPenException
    {
        RedPen r = new RedPen(configFile);
        DocumentParser parser = DocumentParser.of(inputFormat);
        return r.parse(parser, input);

    }



}
