package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.Executor;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class BasicExecutor implements Executor {
    protected Object data;
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

    public Object get(){
        for (Producer p: producers)
            if (data == null) data = p.get(); else break;
        if (data == null) {
            status = Status.EXECUTOR_ERROR;
        }
        return data;
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
        data = producer.get();
    }

    public BasicExecutor(){
        producers = new ArrayList<>();
        consumers = new ArrayList<>();
        status = Status.OK;
    }

    public BasicExecutor(String configFilename){
        this();
    }

    public BasicExecutor(Logger logger){
        this();
        this.logger = logger;
    }

    public BasicExecutor(String configFilename, Logger logger){
        this(logger);
    }
}
