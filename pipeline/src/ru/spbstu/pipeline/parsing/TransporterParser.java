package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.logging.Logger;

public class TransporterParser extends Parser {
    private enum grammar{
        EXECUTORS("executors"),
        EXECUTORS_DELIMITER(">"),
        READER("reader"),
        WRITER("writer"),
        WORKER_PARAMS_DELIMITER(";");

        private final String title;
        public String toString(){
            return title;
        }
        grammar(String title) { this.title = title; }
    }

    public WorkerParameters writer(){
        String writerDescription = properties.get(grammar.WRITER.toString());
        if(writerDescription == null){
            logger.log("Error in class TransporterParser: writer not set");
            return null;
        }
        return new WorkerParameters(writerDescription,
                grammar.WORKER_PARAMS_DELIMITER.toString(), logger);
    }

    public WorkerParameters reader(){
        String readerDescription = properties.get(grammar.READER.toString());
        if(readerDescription == null){
            logger.log("Error in class TransporterParser: reader not set");
            return null;
        }
        return new WorkerParameters(readerDescription,
                grammar.WORKER_PARAMS_DELIMITER.toString(), logger);
    }

    /* TODO implement executors()
    public WorkerParameters [] executors(){
        String descriptions = properties.get(grammar.EXECUTORS.toString());
        ...
    }*/

    public TransporterParser(String configFilename, Logger logger){
        super(configFilename, logger);
    }
}
