package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.parsing.WorkerParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfiguredReader extends DummyReader {
    protected WorkerParser parser;

    @Override
    protected byte[] getBytes(){
        String inputFilename = parser.inputFilename();
        if (inputFilename == null) {
            if (logger != null) logger.log("Error in ConfiguredReader.get(): input filename not set");
        }
        return readBytes(inputFilename);
    }

    private byte[] readBytes(String filename){
        byte[] res = null;
        if(filename == null) {
            if(logger != null) logger.log("error in reader: input filename not set");
            return res;
        }
        try{
            res = Files.readAllBytes(Paths.get(filename));
        } catch(IOException e){
            if(logger != null) logger.log("error in reader: exception while reading input file " + filename);
        }
        return res;
    }

    public ConfiguredReader(String configFilename, Logger logger){
        super();
        this.logger = logger;
        parser = new WorkerParser(configFilename, logger);
    }

    public ConfiguredReader(Logger logger, String configFilename){
        this(configFilename, logger);
    }
}
