package tests;

import objects.*;
import server.Serializer;
import client.Deserializer;
import static org.junit.Assert.*;

public class DeserializerTest {

    private ObjectA testObjectA;
    private ObjectB testObjectB;
    private ObjectC testObjectC;
    private ObjectD testObjectD;

    @org.junit.Before
    public void setUp() throws Exception {
        this.testObjectA = new ObjectA(3, false);
        //this.testObjectB = new ObjectB();
        //this.testObjectC = new ObjectC();
        //this.testObjectD = new ObjectD();
    }

    @org.junit.Test
    public void deserializeObjectA() throws Exception {
        String serialized_object = Serializer.serializeObject(testObjectA);
        Object deserialize_object = Deserializer.deserializeObject(serialized_object);

        assertEquals(testObjectD, deserialize_object);
    }

    @org.junit.Test
    public void deserializeObjectB() {
    }

    @org.junit.Test
    public void deserializeObjectC() {
    }

    @org.junit.Test
    public void deserializeObjectD() {
    }

    @org.junit.Test
    public void deserializeObjectE() {
    }
}