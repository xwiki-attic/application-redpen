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

//import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.xwiki.contrib.redpen.CheckerConfiguration;
import org.xwiki.contrib.redpen.ContentChecker;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.OutputHandler;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.config.Configuration;
import cc.redpen.config.ValidatorConfiguration;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.validator.ValidationError;
import cc.redpen.formatter.XMLFormatter;

/**
 * This component takes in string of plain text input and performs RedPen validation checks on it. Outputs validation
 * results as XML.
 *
 * @version $Id: $
 * @since 1.0
 */
@Component
@Named("redpenchecker")
@Singleton
public class RedPenContentChecker implements ContentChecker
{
    @Inject
    @Named("RedpenConfiguration")
    private CheckerConfiguration redpenConfig;

    @Inject
    private Logger logger;

    @Inject
    @Named("RedpenOutputHandler")
    private OutputHandler outputHandler;

    /**
     * @param input input string from wiki documents or XObjects
     * @return results of text validation in an XML formatted string
     */
    public String validate(String input)
    {
        String res;
        try {
            String inputFormat = "plain";
            Configuration configFile = configGenerate(redpenConfig.getValidationSettings());
            Document doc;
            if (input == null) {
                doc = getDocument(inputFormat, " ", configFile);
            } else {
                doc = getDocument(inputFormat, input, configFile);
            }
            List<ValidationError> validate = validateDocuments(doc, configFile);
            this.logger.info("document validated");
            XMLFormatter format = new XMLFormatter();
            res = "";
            StringBuilder str = new StringBuilder(res);
            for (ValidationError v : validate) {
                str.append(format.formatError(doc, v)).append("\n");
            }
            InputStream is = new ByteArrayInputStream(str.toString().getBytes(StandardCharsets.UTF_8));
            res = this.outputHandler.formatString(is);
        } catch (RedPenException r) {
            res = r.getMessage();
            this.logger.error(r.getMessage());
        }
        return res;
    }

    /**
     * @return error from output handler, true if validation results contain language errors
     */
    public boolean containsError()
    {
        return this.outputHandler.containsValidationErrors();
    }

    /**
     * @param validators ArrayList of validators
     * @return configuration settings as a File object
     */
    private Configuration configGenerate(List validators)
    {
        Configuration.ConfigurationBuilder config = new Configuration.ConfigurationBuilder();
        for (Object v : validators) {
            if (v instanceof ValidatorConfiguration) {
                config.addValidatorConfig((ValidatorConfiguration) v);
            }
        }
        Configuration endConfig = config.build();
        //this.logger.info(config.toString());
        return endConfig;
    }

    /**
     * @param document takes in RedPen Documents as input
     * @return a list of errors in the input text, in json format
     * @throws RedPenException if redpen object cannot be instantiated
     */
    private List<ValidationError> validateDocuments(Document document, Configuration configFile) throws RedPenException
    {
        RedPen r = new RedPen(configFile);
        List<ValidationError> res = r.validate(document);

        return res;
    }

    /**
     * @param inputFormat takes in inputFormat as defined in renderValidation method
     * @param input content as String object
     * @param configFile takes in configuration settings
     * @return document object of Redpen's Document model
     * @throws RedPenException if redpen object cannot be instantiated
     */
    private Document getDocument(String inputFormat, String input, Configuration configFile)
        throws RedPenException
    {
        RedPen r = new RedPen(configFile);
        DocumentParser parser = DocumentParser.of(inputFormat);
        return r.parse(parser, input);
    }
}
