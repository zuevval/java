package ru.spbstu.pipeline.parsing;

import ru.spbstu.pipeline.Status;
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
            if (logger != null) logger.log("Error in class TransporterParser: writer not set");
            status = Status.ERROR;
            return null;
        }
        return new WorkerParameters(writerDescription,
                grammar.WORKER_PARAMS_DELIMITER.toString(), logger);
    }

    public WorkerParameters reader(){
        String readerDescription = properties.get(grammar.READER.toString());
        if(readerDescription == null){
            if (logger != null) logger.log("Error in class TransporterParser: reader not set");
            status = Status.ERROR;
            return null;
        }
        return new WorkerParameters(readerDescription,
                grammar.WORKER_PARAMS_DELIMITER.toString(), logger);
    }


    public WorkerParameters [] executors(){
        String executorsInfo = properties.get(grammar.EXECUTORS.toString());
        if (executorsInfo == null){
            if (logger != null) logger.log("Warning: parameter 'executors' not set");
            return new WorkerParameters[0];
        }
        String [] descriptions = executorsInfo.split(grammar.EXECUTORS_DELIMITER.toString());
        WorkerParameters [] res = new WorkerParameters[descriptions.length];
        for(int i=0; i<res.length; i++)
            res[i] = new WorkerParameters(descriptions[i],
                    grammar.WORKER_PARAMS_DELIMITER.toString(), logger);
        return res;
    }

    public TransporterParser(String configFilename, Logger logger){
        super(configFilename, logger);
    }
}
