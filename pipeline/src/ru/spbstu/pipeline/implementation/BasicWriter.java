package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.logging.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class BasicWriter implements Writer  {
    protected Object data;
    protected Status status;
    protected List<Producer> producers;
    protected Logger logger;

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
        data = producer.get();
    }

    public Status status(){
        return this.status;
    }

    public BasicWriter(){
        this.producers = new ArrayList<>();
        this.status = Status.OK;
    }

    public BasicWriter(String configFilename){
        this();
    }

    public BasicWriter(Logger logger){
        this();
        this.logger = logger;
    }

    public BasicWriter(String configFilename, Logger logger){
        this(logger);
    }

    public BasicWriter(Logger logger, String configFilename){
        this(configFilename, logger);
    }

    public void run(){
        for (Producer p: producers)
            if (data == null) data = p.get(); else break;
        if (data == null) {
            status = Status.WRITER_ERROR;
            return;
        }
        System.out.println(Arrays.toString((byte[])data));
    }
}
