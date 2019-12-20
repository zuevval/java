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
    protected Object inputData;
    protected Status status = Status.OK;
    protected Map<Producer, Producer.DataAccessor> producers = new HashMap<>();
    protected Logger logger;
    protected WorkerParser parser;
    protected static Set<String> supportedInputTypes = TypeCaster.getSupportedTypes();

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
        if(producer.status() != Status.OK){
            if(logger != null)
                logger.log("Error in Consumer.loadDataFrom(producer): producer is not OK");
            status = Status.ERROR;
            return 0L;
        }
        Producer.DataAccessor da = producers.get(producer);
        if(da == null){
            if(logger != null)
                logger.log("Error in Consumer.loadDataFrom(producer):" +
                        " unknown producer, must run addProducer(producer)");
            status = Status.ERROR;
            return 0L;
        }
        inputData = da.get();
        if(inputData == null){
            if(logger != null)
                logger.log("Error in Consumer.loadDataFrom(producer): loaded data is null");
            status = Status.ERROR;
            return 0L;
        }
        return da.size();
    }
}
