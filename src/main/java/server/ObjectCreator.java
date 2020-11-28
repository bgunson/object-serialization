package server;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Scanner;


public class ObjectCreator {

    private boolean isCollection;
    private final String[] objects = {"ObjectA", "ObjectB", "ObjectC", "ObjectD", "ObjectE"};
    private final String[] objInfo = {
        " - A simple object with primitive instance variables",
        " - An object containing references to other objects (circular reference)",
        " - An object containing an array of primitives",
        " - An object that contains an array of object references",
        " - An object that uses a Java Collection class to refer to other objects"
    };

    /**
     * Gives the user option to create one of five objects, gets users choice
     * @return the name of the object the user chose
     */
    private String promptUser() {
        System.out.println("Choose an object you wish to create...");
        for (int i = 0; i < objects.length; i++)
            System.out.println("[" + (i + 1) + "] " + objects[i] + objInfo[i]);

        Scanner keyboard = new Scanner(System.in);
        System.out.println("\nEnter you selection: ");
        String input = keyboard.nextLine();
        int selection = Integer.parseInt(input);

        return objects[selection - 1];
    }

    /**
     * Initial method called when a user wishes to create an object. Displays the menu showing object options
     * @return the specific object the user chose to create, with assigned fields
     */
    public Object createObject() throws Exception {

        String objName = promptUser();
        System.out.println("Creating a new " + objName + "...\n");
        if (objName.equals("ObjectE"))
            isCollection = true;

        Class classDef = Class.forName("objects." + objName);
        ArrayList<Object> object_list = new ArrayList<Object>();
        return createObjectHelper(classDef, object_list);

    }

    /**
     * A helper method which is used by createObject and anytime an object has a field that is a reference
     * to another object.
     * @param c the class definition for the object to be created
     * @param object_list an array list tracking created objects (mostly used for circular refs)
     * @return the object created after fields have been assigned
     */
    private Object createObjectHelper(Class c, ArrayList object_list) throws Exception {

        Object obj = c.newInstance();
        object_list.add(obj);
        createFields(obj, object_list);
        return obj;
    }

    /**
     * This method when called for a specific object's field will set that field based on user input
     * @param obj the source object that the field belongs to
     * @param field the specific field that is being set
     * @param object_list a list tracking already created objects
     */
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
                field.set(obj, object_list.get(0)); // Circle back
            }
        } else {
            // Encountered some object that is new to us
            field.set(obj, createObjectHelper(field.getType(), object_list));
        }


    }

    /**
     * This method will iterate through a given object's declared fields and set them accordingly
     * @param obj the source object the user chose which is having its declared fields set
     * @param object_list the object tracking list
     */
    private void createFields(Object obj, ArrayList object_list) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        System.out.println("Enter field(s) for " + obj.getClass().getSimpleName());
        for (Field field : fields) {
            field.setAccessible(true);
            // Handle required field types
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
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
    }

    /**
     * When a primitive typed field is encountered this method parses System input to the desired type
     * @param c the desired primitive class
     * @return the parsed object from System.in
     */
    private Object getPrimitive(Class c) {
        Scanner keyboard = new Scanner(System.in);
        String input = keyboard.nextLine();
        // Try parsing user input to the fields type
        if (c.equals(int.class)) { return Integer.parseInt(input); }
        else if (c.equals(boolean.class)) { return Boolean.parseBoolean(input); }
        else if (c.equals(short.class)) { return Short.parseShort(input); }
        else if (c.equals(long.class)) { return Long.parseLong(input); }
        else if (c.equals(float.class)) { return Float.parseFloat(input); }
        else if (c.equals(double.class)) { return Double.parseDouble(input); }
        else if (c.equals(byte.class)) { return Byte.parseByte(input); }
        return null;
    }

    /**
     * When an array type field is encountered, this method will ask the user for a length then create an array
     * and ask the user to input the arrays elements one at a time.
     * @param obj the source object who has an array type field
     * @param field the field that is the array
     * @param object_list the object tracking list
     */
    private void createArrayField(Object obj, Field field, ArrayList object_list) throws Exception {
        field.setAccessible(true);
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Encountered an array field of type " + field.getType().getComponentType().getName() + ", enter the desired length");
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
                    if (isCollection) {
                        String objName = promptUser();
                        Class newObjClass = Class.forName("objects." + objName);
                        Array.set(fieldArr, i, createObjectHelper(newObjClass, object_list));
                    } else {
                        Array.set(fieldArr, i, createObjectHelper(arrayType, object_list));
                    }
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

    /**
     * Utility method to check if the object tracking list contains an object of a given type
     * @param list the array list tracking created objects at this point
     * @param c the class definition we are looking for in the list
     * @return true if an object of Class c has been made already, false otherwise
     */
    private boolean objectListContainsType(ArrayList list, Class c) {
        for (Object obj: list) {
            if (obj.getClass().equals(c))
                return true;
        }
        return false;
    }

}
