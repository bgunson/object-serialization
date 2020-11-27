package server;



import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.lang.reflect.*;
import java.util.ArrayList;

import java.util.Scanner;


public class ObjectCreator {

    private final String[] objects = {"ObjectA", "ObjectB", "ObjectC", "ObjectD", "ObjectE"};
    private final String[] objInfo = {
        " - A simple object with primitive instance variables",
        " - An object containing references to other objects (circular reference)",
        " - An object containing an array of primitives",
        " - An object that contains an array of object references",
        " - An object that uses a Java Collection class to refer to other objects"
    };

    public Object createObject() throws Exception {

        System.out.println("Choose an object you wish to create...");
        for (int i = 0; i < objects.length; i++)
            System.out.println("[" + (i + 1) + "] " + objects[i] + objInfo[i]);

        Scanner keyboard = new Scanner(System.in);
        System.out.println("\nEnter you selection: ");
        String input = keyboard.nextLine();
        int selection = Integer.parseInt(input);

        String objName = objects[selection - 1];

        System.out.println("Creating a new " + objName + "...\n");


        Class classDef = Class.forName("objects." + objName);
        ArrayList<Object> object_list = new ArrayList<Object>();
        Object object = createObjectHelper(classDef, object_list);
        return object;

    }

    private Object createObjectHelper(Class c, ArrayList object_list) throws Exception {

        Object obj = c.newInstance();
        object_list.add(obj);
        createFields(obj, object_list);
        return obj;
    }

    private void createObjectField(Object obj, Field field, ArrayList object_list) throws Exception {

        field.setAccessible(true);

        if (objectListContainsType(object_list, field.getType())) {
            // Encountered a field whose class has been created before
            System.out.println("Would you like to...");
            System.out.println("[1] Add another " + obj.getClass().getSimpleName() + " to the path of references, or");
            System.out.println("[2] Create a reference to the initial object (circular reference)");
            Scanner keyboard = new Scanner(System.in);
            int input = keyboard.nextInt();
            if (input == 1) {
                // Add another object to the path
                field.set(obj, createObjectHelper(field.getType(), object_list));
            } else if (input == 2) {
                // Circle back
                field.set(obj, object_list.get(0));
            }
        } else {
            System.out.println("Encountered a field whose class is new to us");

        }


    }


    private void createFields(Object obj, ArrayList object_list) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        System.out.println(obj.getClass().getSimpleName() + " has " + fields.length + " field(s), please enter the desired values when instructed");
        for (Field field : fields) {
            field.setAccessible(true);
            // Handle required field types
            // Primitive
            if (field.getType().isPrimitive()) {
                System.out.println("Enter value for: ");
                System.out.println(field.getType() + " " + field.getName() + " = ");
                field.set(obj, getPrimitive(field.getType()));
            }
            // Array
            else if (field.getType().isArray()) {
                createArrayField(obj, field, object_list);
            } else {
                createObjectField(obj, field, object_list);

            }
        }
    }


    private Object getPrimitive(Class c) throws Exception {

        Scanner keyboard = new Scanner(System.in);
        String input = keyboard.nextLine();

        // Try parsing user input to the fields type
        if (c.equals(int.class)) {
            return Integer.parseInt(input);

        } else if (c.equals(boolean.class)) {
            return Boolean.parseBoolean(input);

        } else if (c.equals(short.class)) {
            return Short.parseShort(input);

        } else if (c.equals(long.class)) {
            return Long.parseLong(input);

        } else if (c.equals(float.class)) {
            return Float.parseFloat(input);

        } else if (c.equals(double.class)) {
            return Double.parseDouble(input);
        } else if (c.equals(byte.class)) {
            return Byte.parseByte(input);
        }
        return null;
    }

    private void createArrayField(Object obj, Field field, ArrayList object_list) throws Exception {
        field.setAccessible(true);
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Encountered an array field of type " + field.getType().getComponentType() + ", enter the desired length");
        System.out.println("length = ");
        int length = keyboard.nextInt();

        System.out.println("When instructed, enter the values for the array entries\n");

        Class arrayType = field.getType().getComponentType();

        Object fieldArr = Array.newInstance(arrayType, length);

        for (int i = 0; i < length; i++) {
            System.out.println("[" + i +"] (" + arrayType.getSimpleName() + ") = ");
            if (!arrayType.isPrimitive()) {
                // Array is not primitive
                System.out.println("Input [y] for a reference to another " + arrayType.getSimpleName() + " , or...");
                System.out.println("Input [n] if you do not want to create a new " + arrayType.getSimpleName());
                String input = keyboard.next();
                if (input.equals("y")) {
                    // Create new reference
                    Array.set(fieldArr, i, createObjectHelper(arrayType, object_list));
                } else if (input.equals("n")) {
                    // Create null reference
                    Array.set(fieldArr, i, null);
                }

            } else {    // Array is primitive
                Array.set(fieldArr, i, getPrimitive(arrayType));
            }
        }
        field.set(obj, fieldArr);
    }

    private boolean objectListContainsType(ArrayList list, Class c) {
        for (Object obj: list) {
            if (obj.getClass().equals(c))
                return true;
        }
        return false;
    }

}
