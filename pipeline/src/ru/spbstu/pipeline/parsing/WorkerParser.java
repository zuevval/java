package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.logging.Logger;

public class WorkerParser extends Parser {
    private enum grammar{
        INPUT_FILENAME("inputFilename");

        private final String title;
        public String toString(){
            return title;
        }
        grammar(String title) { this.title = title; }
    }

    public String inputFilename(){
        return properties.get(grammar.INPUT_FILENAME.toString());
    }

    public WorkerParser(String configFilename, Logger logger) {
        super(configFilename, logger);
    }
}
