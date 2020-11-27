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
    public void setUp() {
        this.testObjectA = new ObjectA(420, true);
        this.testObjectB = new ObjectB(new ObjectB(new ObjectB(testObjectB)));
        this.testObjectC = new ObjectC(new int[] {1,234,6,43,57});
        //this.testObjectD = new ObjectD();
    }

    @org.junit.Test
    public void deserializeObjectA() throws Exception {
        System.out.println("==========================================================");
        System.out.println("Testing deserializer for ObjectA...\n");
        String serialized_object = Serializer.serializeObject(testObjectA);
        Object deserialized_object = Deserializer.deserializeObject(serialized_object);

        assertEquals(Serializer.serializeObject(testObjectA), Serializer.serializeObject(deserialized_object));
        System.out.println("==========================================================");
    }

    @org.junit.Test
    public void deserializeObjectB() throws Exception {
        System.out.println("==========================================================");
        System.out.println("Testing deserializer for ObjectB...\n");
        String serialized_object = Serializer.serializeObject(testObjectB);
        Object deserialized_object = Deserializer.deserializeObject(serialized_object);

        assertEquals(Serializer.serializeObject(testObjectB), Serializer.serializeObject(deserialized_object));
        System.out.println("==========================================================");
    }

    @org.junit.Test
    public void deserializeObjectC() throws Exception {
        System.out.println("==========================================================");
        System.out.println("Testing deserializer for ObjectC...\n");

        String serialized_object = Serializer.serializeObject(testObjectC);
        Object deserialized_object = Deserializer.deserializeObject(serialized_object);

        assertEquals(Serializer.serializeObject(testObjectC), Serializer.serializeObject(deserialized_object));
        System.out.println("==========================================================");

    }

    @org.junit.Test
    public void deserializeObjectD() {
    }

    @org.junit.Test
    public void deserializeObjectE() {
    }
}