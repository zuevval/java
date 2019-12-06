package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

public class ConverterParser extends Parser {
    enum grammar{
        START_POSITION("start"),
        STOP_POSITION("stop");

        private final String title;
        public String toString(){
            return title;
        }
        grammar(String title) { this.title = title; }
    }

    private int processNumericField (String key, String value, int defaultResult){
        if (value == null) {
            if(logger != null)
                logger.log("Warning in ConverterParser: " + key + " not set, using default");
            return defaultResult;
        }
        try{
            return Integer.parseInt(value);
        } catch(NumberFormatException ne){
            status = Status.ERROR;
            if(logger != null)
                logger.log("Error in ConverterParser: " + grammar.START_POSITION + "has invalid value");
        }
        return -1;
    }

    public int startPosition(){
        String value = properties.get(grammar.START_POSITION.toString());
        return processNumericField(grammar.START_POSITION.toString(), value, 0);
    }

    public int stopPosition(){
        String value = properties.get(grammar.STOP_POSITION.toString());
        return processNumericField(grammar.STOP_POSITION.toString(), value, Integer.MAX_VALUE);
    }

    public ConverterParser(String configFilename, Logger logger){
        super(configFilename, logger);
    }
}
