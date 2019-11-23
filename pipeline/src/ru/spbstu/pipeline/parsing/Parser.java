package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.logging.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Parser {
    protected MyProperties properties;
    protected Logger logger;

    protected void readConfig (String configFilename) throws IOException {
        InputStream is = new FileInputStream(configFilename);
        properties.load(is);
        is.close();
    }

    public Parser(String configFilename, Logger logger){
        this.logger = logger;
        properties = new MyProperties();
        try{
            readConfig(configFilename);
        } catch(IOException e){
            logger.log("Error in class Parser: cannot read file " + configFilename);
        }
    }

    protected Parser(){};
}
