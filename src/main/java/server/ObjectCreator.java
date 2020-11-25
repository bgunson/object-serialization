package server;

import java.lang.reflect.*;
import java.util.Scanner;


public class ObjectCreator {

    private final String[] objects = {"ObjectA", "ObjectB", "ObjectC", "ObjectD", "ObjectE"};
    private final String[] objInfo = {
        " - A simple object with primitive instance variables",
        " - An object containing references to other objects",
        " - An object containing an array of primitives",
        " - An object that contains an array of object references",
        " - An object that uses a Java Collection class to refer to other objects"
    };

    public Object createObject() {

        System.out.println("Choose an object you wish to create...");
        for (int i = 0; i < objects.length; i++)
            System.out.println("[" + (i+1) + "] " + objects[i].getClass().getSimpleName() + objInfo[i]);

        Scanner keyboard = new Scanner(System.in);
        System.out.println("\nEnter you selection: ");
        String input = keyboard.nextLine();
        int selection = Integer.parseInt(input);

        String objName = objects[selection-1];

        System.out.println("Creating a new " + objName + "...\n");


        try {
            Class classDef = Class.forName("objects." + objName);

            Object obj = classDef.newInstance();

            createFields(obj);

            return obj;

        } catch (ClassNotFoundException e) {
            System.out.println("Unable to find a class with that definition...");
        } catch (InstantiationException ie) {
            System.out.println("Unable to create new instance of selected object...");
        } catch (IllegalAccessException iae) {
            System.out.println("Unable to access desired class...");
        }
        return null;

    }

    private void createFields(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        System.out.println(obj.getClass().getSimpleName() + " has " + fields.length + " field(s), please enter the desired values when instructed");


        for (Field field : fields) {

            if (field.getType().isPrimitive()) {
                createPrimitiveField(obj, field);
            }

            if (field.getType().isArray()) {
                createArrayField(obj, field);
            }


        }



    }

    private void createPrimitiveField(Object obj, Field field) {

        Scanner keyboard = new Scanner(System.in);
        System.out.println("Enter value for: ");
        System.out.println(field.getType() + " " + field.getName() + " = ");
        String input = keyboard.nextLine();

        try {
            // Tyr parsing user input to the fields type
            if (field.getType().equals(int.class)) {
                int arg = Integer.parseInt(input);
                field.set(obj, arg);
            } else if (field.getType().equals(boolean.class)) {
                boolean arg = Boolean.parseBoolean(input);
                field.set(obj, arg);
            } else if (field.getType().equals(short.class)) {
                short arg = Short.parseShort(input);
                field.set(obj, arg);
            } else if (field.getType().equals(long.class)) {
                long arg = Long.parseLong(input);
                field.set(obj, arg);
            } else if (field.getType().equals(float.class)) {
                float arg = Float.parseFloat(input);
                field.set(obj, arg);
            } else if (field.getType().equals(double.class)) {
                double arg = Double.parseDouble(input);
                field.set(obj, arg);
            } else if (field.getType().equals(byte.class)) {
                byte arg = Byte.parseByte(input);
                field.set(obj, arg);
            }
        } catch (IllegalAccessException iae) {
            System.out.println("Could not access tat field...");
        }
    }

    private void createArrayField(Object obj, Field field) {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Encountered an array field of type " + field.getType().getComponentType() + ", enter the desired length");
        System.out.println("length = ");
        int length = keyboard.nextInt();
        System.out.println(length);

        System.out.println("When instructed, enter the values for the array entries\n");

        Class arrayType = field.getType().getComponentType();

        Object fieldArr = Array.newInstance(arrayType, length);


        for (int i = 0; i < length; i++) {
            System.out.println("[" + i +"] (" + arrayType + ") = ");
            int entry = keyboard.nextInt();
            Array.set(fieldArr, i, entry);
        }

        try {
            field.set(obj, fieldArr);
        } catch (IllegalAccessException iae) {
            System.out.println("Could not access the array field...");
        }
    }



}
