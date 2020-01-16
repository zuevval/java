package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.Consumer;
import ru.spbstu.pipeline.InitializableConsumer;
import ru.spbstu.pipeline.Producer;
import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.logging.Logger;
import ru.spbstu.pipeline.parsing.WorkerParser;

import java.util.*;

public abstract class AbstractConsumer
        implements Consumer, InitializableConsumer {
    protected Status status = Status.OK;
    protected Map<Producer, Producer.DataAccessor> producers = new HashMap<>();
    protected Logger logger;
    protected WorkerParser parser;
    protected static Set<String> supportedInputTypes = TypeCaster.getSupportedTypes();
    protected final static long waitPeriodMillis = 200;
    protected volatile int readyProducersCounter = 0; // counts producers that are over and provided data

    @Override
    public void addProducer(Producer producer){
        if(producers.containsKey(producer)) return;
        if (producer == null){
            if (logger != null) logger.log("Warning in Consumer.addProducer: " +
                    "tried to add producer that is null");
            return;
        }
        Set<String> types = producer.outputDataTypes();
        Producer.DataAccessor da = null;
        for (String type:supportedInputTypes)
            if (types.contains(type))
                da = producer.getAccessor(type);
        if(da == null) {
            if(logger != null)
                logger.log("Error in Consumer.addProducer: couldn't match supported data types");
            status = Status.ERROR;
            return;
        }
        producers.put(producer, da);
    }

    @Override
    public void addProducers(List<Producer> producers){
        for (Producer producer: producers)
            addProducer(producer);
    }

    @Override
    public long loadDataFrom(Producer producer){
        Producer.DataAccessor da = producers.get(producer);
        if(da == null){
            if(logger != null)
                logger.log("Error in Consumer.loadDataFrom(producer):" +
                        " unknown producer "+  "of type "+ producer.getClass().getCanonicalName() +
                        ", must run addProducer(producer)");
            status = Status.ERROR;
            return 0L;
        }
        readyProducersCounter++;
        return da.size();
    }
}