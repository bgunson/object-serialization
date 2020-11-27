package tests;

import objects.*;
import org.junit.Ignore;
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
        this.testObjectD = new ObjectD(new ObjectA[] {new ObjectA(734676, true), null, new ObjectA(-4324, false), null});
    }

    @org.junit.Test
    public void deserializeObjectA() throws Exception {
        System.out.println("==========================================================");
        System.out.println("Testing deserializer for ObjectA...\n");
        System.out.println("EXPECTED");
        String serialized_object = Serializer.serializeObject(testObjectA);
        Object deserialized_object = Deserializer.deserializeObject(serialized_object);

        String expected = serialized_object;
        System.out.println("ACTUAL");
        String actual = Serializer.serializeObject(deserialized_object);

        assertEquals(expected, actual);
        System.out.println("==========================================================");
    }

    @org.junit.Test
    public void deserializeObjectB() throws Exception {
        System.out.println("==========================================================");
        System.out.println("Testing deserializer for ObjectA...\n");
        System.out.println("EXPECTED");
        String serialized_object = Serializer.serializeObject(testObjectB);
        Object deserialized_object = Deserializer.deserializeObject(serialized_object);

        String expected = serialized_object;
        System.out.println("ACTUAL");
        String actual = Serializer.serializeObject(deserialized_object);

        assertEquals(expected, actual);
        System.out.println("==========================================================");
    }

    @org.junit.Test
    public void deserializeObjectC() throws Exception {
        System.out.println("==========================================================");
        System.out.println("Testing deserializer for ObjectA...\n");
        System.out.println("EXPECTED");
        String serialized_object = Serializer.serializeObject(testObjectC);
        Object deserialized_object = Deserializer.deserializeObject(serialized_object);

        String expected = serialized_object;
        System.out.println("ACTUAL");
        String actual = Serializer.serializeObject(deserialized_object);

        assertEquals(expected, actual);
        System.out.println("==========================================================");

    }

    @org.junit.Test
    public void deserializeObjectD() throws Exception {
        System.out.println("==========================================================");
        System.out.println("Testing deserializer for ObjectA...\n");
        System.out.println("EXPECTED");
        String serialized_object = Serializer.serializeObject(testObjectD);
        Object deserialized_object = Deserializer.deserializeObject(serialized_object);

        String expected = serialized_object;
        System.out.println("ACTUAL");
        String actual = Serializer.serializeObject(deserialized_object);

        assertEquals(expected, actual);
        System.out.println("==========================================================");
    }

    @Ignore
    @org.junit.Test
    public void deserializeObjectE() {
    }
}