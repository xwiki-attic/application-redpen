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


import org.slf4j.Logger;
import org.xwiki.contrib.redpen.ProofReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;


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
public class ProofReaderComponent implements ProofReader
{

    @Inject
    private Logger logger;
    private String inputFormat = "plain";
    private String inputFileName = "./application-redpen-api/src/main/resources/sampledoc-en.txt";
    /**
     *
     * @param input input string from wiki documents or XObjects
     * @return results of text validation in an XML formatted string
     */
    public String renderValidation(String input)
    {
        stringInit(input, inputFileName);
        File configFile = configGenerate();
        String res;
        try {
            List<Document> documents = getDocuments(inputFormat, inputFileName, configFile);
            List<ValidationError> validate = validateDocuments(documents);

            XMLFormatter format = new XMLFormatter();
            res = "";
            for (Document d : documents) {
                for (ValidationError v : validate) {
                    res += format.formatError(d, v) + "\n";
                }
            }
            return res;
        } catch (RedPenException r) {
            res = r.getMessage();
        }
        return res;
    }

    /**
     *
     * @param input takes input text from renderValidation method
     * @param inputFileName takes in the relative directory of the text file used to store input text
     */
    private void stringInit(String input, String inputFileName)
    {
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(inputFileName));
            writer.write(input);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return configuration settings as a File object
     */
    private File configGenerate()
    {
        File result = new File("./application-redpen-api/src/main/resources/redpen-conf-en.xml");
        if (result.exists()) {
            return result;
        } else {
            this.logger.error("File not found");
            return null;
        }
    }

    /**
     *
     * @param document takes in RedPen Documents as input
     * @return a list of errors in the input text, in json format
     * @throws RedPenException if redpen object cannot be instantiated
     */
    private List<ValidationError> validateDocuments(List<Document> document) throws RedPenException
    {
        File configFile = configGenerate();
        List<ValidationError> res = new ArrayList<>();
        for (Document d : document) {
            RedPen r = new RedPen(configFile);
            List<ValidationError> tmp = r.validate(d);
            res.addAll(tmp);
        }
        return res;
    }

    /**
     *
     * @param inputFormat takes in inputFormat as defined in renderValidation method
     * @param inputFileName
     * @param configFile takes in configuration settings
     * @return
     * @throws RedPenException if redpen object cannot be instantiated
     */
    private List<Document> getDocuments(String inputFormat, String inputFileName, File configFile)
            throws RedPenException
    {
        RedPen r = new RedPen(configFile);
        DocumentParser parser = DocumentParser.of(inputFormat);
        return r.parse(parser, extractInputFiles(inputFileName));

    }
    /**
     *
     * @param inputFileName
     * @return input file as an array of Files as RedPen instance can only parse File array objects
     */
    private File[] extractInputFiles(String inputFileName)
    {
        File[] fileReturn = new File[1];
        fileReturn[0] = new File(inputFileName);
        return fileReturn;
    }
}
