package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class BasicExecutor implements Executor {
    protected Object inputData;
    protected Object outputData;
    protected Status status;
    protected List<Producer> producers;
    protected List<Consumer> consumers;
    protected Logger logger;

    public void run(){
        for (Consumer consumer: consumers){
            consumer.loadDataFrom(this);
            consumer.run();
        }
    }

    protected void getInputData(){
        for (Producer p: producers)
            if (inputData == null){
                if(p.status() == Status.OK)
                    inputData = p.get();
                else if (logger != null)
                    logger.log("warning in BasicExecutor.get: one of producers is not OK");
            } else break;
        if (inputData == null) {
            status = Status.EXECUTOR_ERROR;
            if(logger != null)
                logger.log("error in BasicExecutor.get: all producers failed to provide valid inputData");
        }
    }

    public Object get(){
        if(outputData != null) return outputData;
        if(inputData == null) getInputData();
        outputData = inputData;
        return outputData;
    }

    public Status status(){
        return this.status;
    }

    public void addConsumer(Consumer consumer) {
        if (consumers.contains(consumer)) return;
        if (consumer != null) consumers.add(consumer);
        else if (logger != null) logger.log("Warning in BasicExecutor: " +
                "tried to add consumer that is null");
    }

    public void addConsumers(List<Consumer> consumers){
        for (Consumer consumer: consumers)
            addConsumer(consumer);
    }

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
        inputData = producer.get();
    }

    protected BasicExecutor(){
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
        status = Status.OK;
    }

    public BasicExecutor(String configFilename, Logger logger){
        this();
        this.logger = logger;
    }
    public BasicExecutor(Logger logger, String configFilename){
        this(configFilename, logger);
    }
}
