package ru.spbstu.pipeline.implementation;

import ru.spbstu.pipeline.Status;
import ru.spbstu.pipeline.implementation.BasicExecutor;

import org.junit.Assert;
import org.junit.Test;

public class BasicExecutorTest {
    /*
    @Test
    public void basicNullCheck (){
        BasicExecutor be = new BasicExecutor("", null);
        be.addConsumer(null);
        be.addProducer(null);
        be.run();
        Assert.assertEquals(Status.OK, be.status());
        Object data = be.get();
        Assert.assertNull(data);
        Assert.assertEquals(Status.EXECUTOR_ERROR, be.status());
    }

    @Test
    public void basicPipeline(){
        BasicExecutor be = new BasicExecutor("", null);
        DummyReader dr = new DummyReader();
        be.addProducer(dr);
        Assert.assertNotNull(dr.get());
        Assert.assertArrayEquals((byte[])dr.get(), (byte[])be.get());
        Assert.assertEquals(Status.OK, be.status());
    }*/
}
