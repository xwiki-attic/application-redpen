package org.xwiki.contrib.redpen.internal;

import org.xwiki.contrib.redpen.ProofReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;


import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.parser.DocumentParser;
import cc.redpen.validator.ValidationError;
import cc.redpen.formatter.XMLFormatter;

/**
 * Created by DeSheng on 13/6/2017.
 */
@Component
@Singleton

public class ProofReadingMain implements ProofReader
{
    public String renderValidation(String input) throws RedPenException{
        String inputFormat = "plain";
        String inputFileName = "./src/main/java/com/xwiki/internal/sampledoc/sampledoc-en.txt";
        stringInit(input, inputFileName);
        File configFile = configGenerate();

        List<Document> documents = getDocuments(inputFormat, inputFileName, configFile);
        List<ValidationError> validate = validateDocuments(documents);

        XMLFormatter format = new XMLFormatter();
        String res = "";
        for(Document d : documents) {
            for (ValidationError v : validate) {
                res += format.formatError(d,v) + "\n";
            }
        }
        return res;

    }


    private void stringInit(String input, String inputFileName){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("inputFileName"));
            writer.write(input);
            writer.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    private File configGenerate(){
        File result = new File("./src/main/java/com/xwiki/internal/conf/redpen-conf-en.xml");
        if (result.exists()){
            System.out.println("Config file exists.");
            return result;
        }
        else {

            System.out.println("file does not exist");
            return null;
        }
    }

    private List<ValidationError> validateDocuments (List<Document> document) throws RedPenException{
        File configFile = configGenerate();
        List<ValidationError> res = new ArrayList();
        for ( Document d : document){
            RedPen r = new RedPen(configFile);
            List<ValidationError> tmp = r.validate(d);
            res.addAll(tmp);
        }
        return res;
    }

    private List<Document> getDocuments(String inputFormat, String inputFileName, File configFile) throws RedPenException{
        RedPen r = new RedPen(configFile);
        DocumentParser parser = DocumentParser.of(inputFormat);
        System.out.println("Document parsed");
        return r.parse(parser, extractInputFiles(inputFileName));

    }

    private File[] extractInputFiles(String inputFileName) {
        File[] fileReturn = new File[1];
        fileReturn[0] = new File(inputFileName);
        return fileReturn;
    }
}
