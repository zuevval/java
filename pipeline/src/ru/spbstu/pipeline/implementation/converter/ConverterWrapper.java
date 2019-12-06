package ru.spbstu.pipeline.implementation.converter;

import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.implementation.BasicExecutor;
import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.parsing.ConverterParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ConverterWrapper extends BasicExecutor {

    @Override
    public Object get(){
        if(outputData != null)
            return outputData;
        int startPosition = parser.startPosition();
        int stopPosition = parser.stopPosition();
        if(parser.getStatus() != status.OK){
            status = Status.EXECUTOR_ERROR;
            if(logger != null)
                logger.log("error in ConverterWrapper.get: couldn't parse properties file");
            return null;
        }
        getInputData();
        if(inputData == null){
            status = Status.EXECUTOR_ERROR;
            if(logger != null)
                logger.log("error in ConverterWrapper: couldn't get inputData");
            return null;
        }
        if(inputData.getClass() != byte[].class){
            status = Status.EXECUTOR_ERROR;
            if(logger != null)
                logger.log("error in ConverterWrapper: inputData is not of type byte[]");
            return null;
        }
        InputStream is = new ByteArrayInputStream((byte[]) inputData);
        Converter engine = Converter.initFromRaw(is, startPosition, stopPosition, logger);
        if(engine == null){
            status = Status.EXECUTOR_ERROR;
            if(logger != null)
                logger.log("Error in ConverterWrapper: couldn't build Converter");
            return null;
        }
        EncodedText structuredData = engine.getEncoded();
        outputData = ("value:\n" +
                structuredData.value +
                "\ndictionary:\n" +
                structuredData.dictionary.toString()
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")).getBytes();
        return outputData;
    }

    private ConverterParser parser;

    public ConverterWrapper(String configFilename, Logger logger){
        super(configFilename, logger);
        parser = new ConverterParser(configFilename, logger);
    }

    public ConverterWrapper(Logger logger, String configFilename){
        this(configFilename, logger);
    }
}
