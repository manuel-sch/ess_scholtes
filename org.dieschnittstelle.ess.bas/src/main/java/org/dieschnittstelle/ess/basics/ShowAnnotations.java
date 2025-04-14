package org.dieschnittstelle.ess.basics;


import org.dieschnittstelle.ess.basics.annotations.AnnotatedStockItemBuilder;
import org.dieschnittstelle.ess.basics.annotations.DisplayAs;
import org.dieschnittstelle.ess.basics.annotations.StockItemProxyImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.dieschnittstelle.ess.basics.reflection.ReflectedStockItemBuilder.getAccessorNameForField;
import static org.dieschnittstelle.ess.utils.Utils.*;

public class ShowAnnotations {

    public static void main(String[] args) {
        // we initialise the collection
        StockItemCollection collection = new StockItemCollection("stockitems_annotations.xml", new AnnotatedStockItemBuilder());
        // we load the contents into the collection
        collection.load();

        for (IStockItem consumable : collection.getStockItems()) {
            showAttributes(((StockItemProxyImpl) consumable).getProxiedObject());
        }

        // we initialise a consumer
        Consumer consumer = new Consumer();
        // ... and let them consume
        consumer.doShopping(collection.getStockItems());
    }

    /*
     * TODO BAS2
     */
    private static void showAttributes(Object instance) {
        show("class is: " + instance.getClass());

        try {
            // TODO BAS2: create a string representation of instance by iterating
            //  over the object's attributes / fields as provided by its class
            //  and reading out the attribute values. The string representation
            //  will then be built from the field names and field values.
            //  Note that only read-access to fields via getters or direct access
            //  is required here.
            StringBuilder instanceAsTextRepresentationSB = new StringBuilder();
            instanceAsTextRepresentationSB.append("{");
            Class<?> clazz = instance.getClass();
            instanceAsTextRepresentationSB.append(clazz.getSimpleName());
            if (clazz.getDeclaredFields().length != 0) {
                instanceAsTextRepresentationSB.append(" ");
                instanceAsTextRepresentationSB.append(generateTextRepresentationOfDeclaredFields(instance, clazz));
            }
            show(instanceAsTextRepresentationSB.toString());

            // TODO BAS3: if the new @DisplayAs annotation is present on a field,
            //  the string representation will not use the field's name, but the name
            //  specified in the the annotation. Regardless of @DisplayAs being present
            //  or not, the field's value will be included in the string representation.

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("showAnnotations(): exception occurred: " + e, e);
        }

    }

    private static String generateTextRepresentationOfDeclaredFields(Object instance, Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        StringBuilder fieldsAsTextRepresentationSB = new StringBuilder();
        List<String> fieldsAsTextRepresentations = new ArrayList<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            Method getter = clazz.getDeclaredMethod(getAccessorNameForField("get", declaredField.getName()));
            Object fieldValue = getter.invoke(instance);
            String label = declaredField.isAnnotationPresent(DisplayAs.class) ? declaredField.getAnnotation(DisplayAs.class).value() : declaredField.getName();
            fieldsAsTextRepresentations.add(label + ":" + fieldValue);
        }
        fieldsAsTextRepresentationSB.append(String.join(", ", fieldsAsTextRepresentations)).append("}");
        return fieldsAsTextRepresentationSB.toString();
    }


}
