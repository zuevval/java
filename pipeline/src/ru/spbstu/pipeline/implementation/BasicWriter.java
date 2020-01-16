package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.*;
import ru.spbstu.pipeline.parsing.WorkerParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class BasicWriter extends AbstractConsumer implements Writer  {
    @Override
    public Status status(){
        return this.status;
    }

    public BasicWriter(String configFilename, Logger logger){
        this.logger = logger;
        supportedInputTypes = new LinkedHashSet<String>(){{
            add(byte[].class.getCanonicalName());
        }};
        parser = new WorkerParser(configFilename, logger);
    }

    public BasicWriter(Logger logger, String configFilename){
        this(configFilename, logger);
    }

    protected void writeData(){
        boolean consoleOutput = parser.consoleOutput();
        String outputFilename = parser.outputFilename();
        if(outputFilename == null){
            if(logger != null)
                logger.log("Error in BasicWriter: output filename not set");
            return;
        }
        try {
            FileOutputStream stream = new FileOutputStream(outputFilename);
            for (Map.Entry<Producer, Producer.DataAccessor> entry : producers.entrySet()) {
                Producer.DataAccessor dataAccessor = entry.getValue();
                if (dataAccessor == null) {
                    status = Status.WRITER_ERROR;
                    if (logger != null)
                        logger.log("Error in BasicWriter.writeData: one of data accessors is null");
                    return;
                }
                synchronized (dataAccessor) {
                    Object inputData = dataAccessor.get();
                    if (inputData.getClass() != byte[].class) {
                        if (logger != null)
                            logger.log("Error in Writer: inputData can't be converted to byte[]");
                        return;
                    }
                    stream.write((byte[]) inputData);
                    if(consoleOutput){
                        System.out.println("bytes:\n" + Arrays.toString((byte[]) inputData));
                        System.out.println("characters:\n" + new String((byte[]) inputData));
                    }
                }
            }
            if(logger != null)
                logger.log("output has been written to " + outputFilename);
        } catch (IOException e){
            if(logger != null)
                logger.log("Warning in BasicWriter: couldn't write output to file, exception occured");
        }
    }

    @Override
    public void run(){
        if(status != Status.OK){
            if(logger != null)
                logger.log("Error in BasicWriter.run(): status is not OK, cancelling run");
            return;
        }
        boolean inputReady = readyProducersCounter == producers.size();
        while(!inputReady) {
            try {
                Thread.sleep(waitPeriodMillis);
                inputReady = readyProducersCounter == producers.size(); // refreshing
            } catch(InterruptedException e){
                status = Status.WRITER_ERROR;
                if(logger != null)
                    logger.log("Error in Writer.run(): exception in Thread.sleep(...)");
            }
        }
        writeData();
    }
}
