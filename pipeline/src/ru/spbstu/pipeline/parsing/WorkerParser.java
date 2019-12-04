package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.logging.Logger;

public class WorkerParser extends Parser {
    private final String trueString = "true";
    private enum grammar{
        INPUT_FILENAME("inputFilename"),
        OUTPUT_FILENAME("outputFilename"),
        CONSOLE_OUTPUT("consoleOutput");

        private final String title;
        public String toString(){
            return title;
        }
        grammar(String title) { this.title = title; }
    }

    public String inputFilename(){
        return properties.get(grammar.INPUT_FILENAME.toString());
    }
    public String outputFilename(){
        return properties.get(grammar.OUTPUT_FILENAME.toString());
    }
    public boolean consoleOutput(){
        String value = properties.get(grammar.CONSOLE_OUTPUT.toString());
        if (value == null) return false;
        return value.equals(trueString);
    }

    public WorkerParser(String configFilename, Logger logger) {
        super(configFilename, logger);
    }
}
