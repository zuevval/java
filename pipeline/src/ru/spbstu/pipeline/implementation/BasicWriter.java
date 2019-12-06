package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.*;
import ru.spbstu.pipeline.parsing.WorkerParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class BasicWriter implements Writer  {
    protected Object data;
    protected Status status;
    protected List<Producer> producers;
    protected Logger logger;
    protected WorkerParser parser;

    public void addProducer(Producer producer){
        if (producers.contains(producer)) return;
        if (producer != null) producers.add(producer);
        else if (logger != null) logger.log("Warning in BasicExecutor: " +
                "tried to add producer that is null");
    }

    public void addProducers(List<Producer> producers){
        for (Producer producer: producers)
            addProducer(producer);
    }

    public void loadDataFrom(Producer producer){
        if(producer.status() != Status.OK){
            if(logger != null)
                logger.log("Error in BasicWriter.loadDataFrom(producer): producer is not OK");
            status = Status.WRITER_ERROR;
            return;
        }
        data = producer.get();
    }

    public Status status(){
        return this.status;
    }

    public BasicWriter(String configFilename, Logger logger){
        producers = new ArrayList<>();
        status = Status.OK;
        this.logger = logger;
        parser = new WorkerParser(configFilename, logger);
    }

    public BasicWriter(Logger logger, String configFilename){
        this(configFilename, logger);
    }

    protected void writeData(){
        if (data == null) {
            status = Status.WRITER_ERROR;
            if(logger != null)
                logger.log("Error in BasicWriter.writeData: inputData is missing");
            return;
        }
        if(data.getClass() != byte[].class){
            if(logger != null)
                logger.log("Error in Writer: inputData can't be converted to byte[]");
            return;
        }
        boolean consoleOutput = parser.consoleOutput();
        if(consoleOutput){
            System.out.println("bytes:\n" + Arrays.toString((byte[])data));
            System.out.println("characters:\n" + new String((byte[])data));
        }
        String outputFilename = parser.outputFilename();
        if(outputFilename == null){
            if(logger != null)
                logger.log("Warning in BasicWriter: output filename not set");
            return;
        }
        try {
            FileOutputStream stream = new FileOutputStream(outputFilename);
            stream.write((byte[])data);
        } catch (IOException e){
            if(logger != null)
                logger.log("Warning in BasicWriter: couldn't write output to file, exception occured");
        }
    }

    public void run(){
        if(status != Status.OK){
            if(logger != null)
                logger.log("Error in BasicWriter.run(): status is not OK, cancelling run");
            return;
        }
        for (Producer p: producers){
            if (data == null) {
                if(p.status() == Status.OK)
                    data = p.get();
                else if (logger != null)
                    logger.log("warning in BasicWriter.run(): one of producers is not OK");
            } else break;
        }
        writeData();
    }
}
