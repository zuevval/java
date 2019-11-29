package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.parsing.WorkerParser;

public class ConfiguredReader extends DummyReader {
    protected WorkerParser parser;

    @Override
    public Object get(){
        String inputFilename = parser.inputFilename();
        if(inputFilename == null){
            if(logger != null) logger.log("Error in ConfiguredReader.get(): input filename not set");
        }
        return readBytes(inputFilename);
    }

    protected byte[] readBytes(String filename){
        //TODO read bytes from file
        byte [] res = {0, 1, 2};
        return res;
    }

    public ConfiguredReader(){super();}

    public ConfiguredReader(Logger logger){super(logger);}

    public ConfiguredReader(String configFilename){
        super();
        parser = new WorkerParser(configFilename, null);
    }

    public ConfiguredReader(String configFilename, Logger logger){
        super();
        parser = new WorkerParser(configFilename, null);
    }

    public ConfiguredReader(Logger logger, String configFilename){
        this(configFilename, logger);
    }
}
