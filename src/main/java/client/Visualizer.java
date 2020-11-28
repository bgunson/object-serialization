package client;

import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *  Visulizer class is a modified version of my Inspector from CPSC501 A2 - Fall 2020
 *  Bennett Gunson - 30041789
 *
 *  The inspection of superclasses , interfaces, constructors, and methods have been disabled to
 *  make the output more readable.
 */

public class Visualizer {

    private Map object_tracking_map = new IdentityHashMap();

    /**
     * This method starts the visualization of an object
     * @param obj the object which is being visualized
     */
    public void visualize(Object obj) throws Exception {
        Class c = obj.getClass();
        inspectClass(c, obj, 0);
    }

    /**
     * Utility function to calculate an indent for object display
     * @param depth the current depth at whcih an object is being inspected
     * @return the String equating to the amount of whitespace for the indent
     */
    private String getIndent(int depth) {
        String tab = "";
        for (int i = 0; i < depth; i++) {
            tab += "\t";
        }
        return tab;
    }

    /**
     * This method inspects a class and an object of it instance and reports its superclass, interfaces, constructors,
     * methods and fields to System.out
     * @param c the class being inspected
     * @param obj the object of c's definition
     * @param depth the current depth compared to the original object
     */
    private void inspectClass(Class c, Object obj, int depth) throws Exception {

        object_tracking_map.put(obj, object_tracking_map.size());

        String indent = getIndent(depth);
        System.out.println(indent + "OBJECT: " + obj);
        System.out.println(indent + "CLASS");
        System.out.println(indent + "Class: " + c.getName());


        /*// Superclass
        Class superC = c.getSuperclass();
        if (superC == null)
            System.out.println(indent + "SuperClass -> NONE");
        else {
            System.out.println(indent + "SUPERCLASS -> Recursively Inspect");
            // inspect superC
            System.out.println(indent + "SuperClass: " + superC.getName());
            inspectClass(superC, obj, true, depth + 8);
        }*/

        // Interface
        /*Class[] interfacesArr = c.getInterfaces();

        System.out.println(indent + "INTERFACES ( " + c.getName() + " )");

        if (interfacesArr.length == 0)
            System.out.println(indent + "Interfaces -> NONE");
        else {
            System.out.println(indent + "Interfaces -> Recursively Inspect");
            for (Class interfaces : interfacesArr)
                inspectClass(interfaces, obj, true, depth + 8);
        }*/

        // Constructor
        /*Constructor[] constructorArr = c.getDeclaredConstructors();
        System.out.println(indent + "CONSTRUCTORS ( " + c.getName() + " )");

        if (constructorArr.length == 0)
            System.out.println(indent + "Constructors -> NONE");
        else {
            System.out.println(indent + "Constructors ->");
            for (Constructor constructor : constructorArr) {
                inspectConstructor(constructor, depth + 1);
            }
        }*/

        // Methods
        /*Method[] cMethods = c.getDeclaredMethods();
        System.out.println(indent + "METHODS ( " + c.getName() + " )");

        if (cMethods.length == 0)
            System.out.println(indent + "Methods -> NONE");
        else {
            System.out.println(indent + "Methods ->");
            for (Method method : cMethods)
                inspectMethod(method, depth + 1);
        }*/

        // Fields
        Field[] fields = c.getDeclaredFields();
        System.out.println(indent + "FIELDS ( " + c.getName() + " )");

        if (fields.length == 0)
            System.out.println(indent + "Fields -> NONE");
        else {
            // for each field inspect...
            for (Field field : fields)
                inspectField(field, obj,depth + 1);
        }
    }

    /**
     * Utility method to individually inspect a class's contructor
     * @param c the constructor being inspected
     * @param depth the current working depth
     */
    public void inspectConstructor(Constructor c, int depth) {

        String indent = getIndent(depth);
        String indent2 = indent + " ";

        System.out.println(indent + "CONSTRUCTOR");
        System.out.println(indent2 + "Name: " + c.getName());

        Class[] exceptions = c.getExceptionTypes();
        listExceptions(exceptions, depth + 1);

        Class[] parameters = c.getParameterTypes();
        listParameterTypes(parameters, depth + 1);

        System.out.println(indent2 + "Modifiers: " + Modifier.toString(c.getModifiers()));

    }

    /**
     * Utility method to inspect a given method individually
     * @param m the method being inspected
     * @param depth the current working depth
     */
    public void inspectMethod(Method m, int depth) {

        String indent = getIndent(depth);
        String indent2 = indent + " ";

        System.out.println(indent + "METHOD");
        System.out.println(indent2 + "Name: " + m.getName());

        Class[] exceptions = m.getExceptionTypes();
        listExceptions(exceptions, depth + 1);

        Class[] parameters = m.getParameterTypes();
        listParameterTypes(parameters, depth + 1);

        System.out.println(indent2 + "Return type: " + m.getReturnType());
        System.out.println(indent2 + "Modifiers: " + Modifier.toString(m.getModifiers()));
    }

    /**
     * Utility method to individually inspect a field
     * @param f the field being inspected
     * @param obj the object who the field belongs
     * @param depth the current working depth
     */
    private void inspectField(Field f, Object obj, int depth) throws Exception {
        String indent = getIndent(depth);
        String indent2 = indent + " ";

        if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers())) {
            System.out.println(indent + "FIELD");
            System.out.println(indent2 + "Name: " + f.getName());
            System.out.println(indent2 + "Type: " + f.getType());
            System.out.println(indent2 + "Modifiers: " + Modifier.toString(f.getModifiers()));

            f.setAccessible(true);
            Object value = f.get(obj);

            if (value.getClass().isArray())
                inspectArray(value, depth + 1);
            else {

                if (!f.getType().isPrimitive()) {
                    System.out.println(indent2 + "Value (ref): " + value);
                    if (!object_tracking_map.containsKey(value)) {
                        System.out.println(indent2 + "  -> Recursively Inspect");
                        inspectClass(value.getClass(), value, depth + 4);
                    } else {
                        System.out.println(indent2 + "  -> Already Inspected");
                    }
                } else
                    System.out.println(indent2 + "Value: " + value);
            }
        }
    }

    /**
     * Utility method to inspect an array object when one is encountered
     * @param obj array object being inspected
     * @param depth the current working depth
     */
    private void inspectArray(Object obj, int depth) throws Exception {

        String indent = getIndent(depth);

        int objLength = Array.getLength(obj);

        System.out.println(indent + "Component Type: " + obj.getClass().getComponentType());
        System.out.println(indent + "Length: " + objLength);

        if (objLength == 0)
            System.out.println(indent + "Entries -> NONE");
        else{
            System.out.println(indent + "Entries ->");
            for (int i = 0; i < objLength; i++) {
                Object entry = Array.get(obj, i);
                if (!obj.getClass().getComponentType().isPrimitive()) {
                    if (entry == null)
                        System.out.println(indent + " Value (ref): null");
                    else {
                        System.out.println(indent + " Value (ref): " + entry);
                        if (!object_tracking_map.containsKey(entry)) {
                            System.out.println(indent + "  -> Recursively Inspect");
                            inspectClass(entry.getClass(), entry, depth + 4);
                        }
                    }
                } else {    // Its a primitive entry
                    System.out.println(indent + " Value: " + entry);
                }
            }
        }
    }

    /**
     * Utility method to list exceptions to System.out
     * @param exceptions the list of exceptions being reported
     * @param depth the current working depth
     */
    private void listExceptions(Class[] exceptions, int depth) {

        String indent = getIndent(depth);

        if (exceptions.length == 0)
            System.out.println(indent + "Exceptions -> NONE");
        else {
            System.out.println(indent + "Exceptions ->");
            for (Class e : exceptions)
                System.out.println(indent + " " + e.getName());
        }
    }

    /**
     * Utility method to list parameters to System.out
     * @param parameters the list of parameters being reported
     * @param depth the current working depth
     */
    private void listParameterTypes(Class[] parameters, int depth) {

        String indent = getIndent(depth);

        if (parameters.length == 0)
            System.out.println(indent + "Parameter types -> NONE");
        else {
            System.out.println(indent + "Parameter types: ");
            for (Class pars : parameters)
                System.out.println(indent + " " + pars.getName());
        }
    }

}