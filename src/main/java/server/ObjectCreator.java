package server;

import java.lang.reflect.*;
import java.util.Scanner;

import objects.*;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class ObjectCreator {

    private String[] objects = {"ObjectA", "ObjectB", "ObjectC", "ObjectD", "ObjectE"};
    private String[] objInfo = {
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

            Field[] fields = classDef.getDeclaredFields();
            System.out.println(objName + " has " + fields.length + " fields, please enter the desired values when instructed");

            Object[] args = new Object[fields.length];
            Class[] cArgs = new Class[fields.length];

            for (int i = 0; i < fields.length; i++) {
                Class type = fields[i].getType();
                cArgs[i] = type;
                System.out.println("Enter value for: " + fields[i].getName() + " (" + fields[i].getType() + ")");
                args[i] = keyboard.nextLine();

            }

            // Parse user input and type cast to required types
            for (int i = 0; i < cArgs.length; i++) {
                if (cArgs[i].equals(int.class))
                    args[i] = Integer.parseInt(args[i].toString());
                if (cArgs[i].equals(boolean.class))
                    args[i] = Boolean.parseBoolean(args[i].toString());
                if (cArgs[i].equals(float.class))
                    args[i] = Float.parseFloat(args[i].toString());
            }

            Object obj = classDef.getDeclaredConstructor(cArgs).newInstance(args);
            return obj;

        } catch (ClassNotFoundException e) {
            System.out.println("Unable to find a class with that definition...");
        } catch (InstantiationException ie) {
            System.out.println("Unable to create new instance of selected object...");
        } catch (IllegalAccessException iae) {
            System.out.println("Unable to access desired class...");
        } catch (NoSuchMethodException e) {
            System.out.println("Unable to create an object with those arguments...");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;

    }



}
