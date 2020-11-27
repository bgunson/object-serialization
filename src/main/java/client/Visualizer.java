package client;

import server.Serializer;

import java.lang.reflect.*;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 *  Visulizer class is a modified version of my INspector from CPSC501 A2 - Fall 2020
 *  Bennett Gunson - 30041789
 */

public class Visualizer {

    private Map object_tracking_map = new IdentityHashMap();

    public void visualize(Object obj) throws Exception {
        Class c = obj.getClass();
        inspectClass(c, obj, 0);
        /*if (c.isArray())
            inspectArray(obj, 1);
        else
            inspectClass(c, obj,0);*/
    }

    private String getIndent(int depth) {
        String tab = "";
        for (int i = 0; i < depth; i++) {
            tab += " ";
        }
        return tab;
    }

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
        Constructor[] constructorArr = c.getDeclaredConstructors();
        System.out.println(indent + "CONSTRUCTORS ( " + c.getName() + " )");

        if (constructorArr.length == 0)
            System.out.println(indent + "Constructors -> NONE");
        else {
            System.out.println(indent + "Constructors ->");
            for (Constructor constructor : constructorArr) {
                inspectConstructor(constructor, depth + 1);
            }
        }

        // Methods
        Method[] cMethods = c.getDeclaredMethods();
        System.out.println(indent + "METHODS ( " + c.getName() + " )");

        if (cMethods.length == 0)
            System.out.println(indent + "Methods -> NONE");
        else {
            System.out.println(indent + "Methods ->");
            for (Method method : cMethods)
                inspectMethod(method, depth + 1);
        }

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

    private void inspectField(Field f, Object obj, int depth) throws Exception {
        String indent = getIndent(depth);
        String indent2 = indent + " ";

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
            }
            else
                System.out.println(indent2 + "Value: " + value);
        }
    }

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