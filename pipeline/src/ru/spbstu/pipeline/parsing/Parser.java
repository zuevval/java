package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Parser {  // pure Parser instance is useless: properties are inaccessible
    protected Status status = Status.OK;
    protected MyProperties properties;
    protected Logger logger;

    protected void readConfig (String configFilename) throws IOException {
        InputStream is = new FileInputStream(configFilename);
        properties.load(is);
        is.close();
    }

    public Status getStatus(){return status;}

    protected Parser(String configFilename, Logger logger){
        this.logger = logger;
        properties = new MyProperties();
        try{
            readConfig(configFilename);
            status = Status.OK;
        } catch(IOException e){
            if (logger != null) logger.log("Error in class Parser: cannot read file " + configFilename);
            status = Status.ERROR;
        }
    }

    protected Parser(){};
}
