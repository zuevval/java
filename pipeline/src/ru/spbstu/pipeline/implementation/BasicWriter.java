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
        if (inputData == null) {
            status = Status.WRITER_ERROR;
            if(logger != null)
                logger.log("Error in BasicWriter.writeData: inputData is missing");
            return;
        }
        if(inputData.getClass() != byte[].class){
            if(logger != null)
                logger.log("Error in Writer: inputData can't be converted to byte[]");
            return;
        }
        boolean consoleOutput = parser.consoleOutput();
        if(consoleOutput){
            System.out.println("bytes:\n" + Arrays.toString((byte[]) inputData));
            System.out.println("characters:\n" + new String((byte[]) inputData));
        }
        String outputFilename = parser.outputFilename();
        if(outputFilename == null){
            if(logger != null)
                logger.log("Warning in BasicWriter: output filename not set");
            return;
        }
        try {
            FileOutputStream stream = new FileOutputStream(outputFilename);
            stream.write((byte[]) inputData);
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
        writeData();
    }
}
