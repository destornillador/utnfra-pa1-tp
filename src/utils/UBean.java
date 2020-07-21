package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class UBean {

    public UBean() {
    }

    public static ArrayList<Field> obtenerAtributos(Object o) {
        ArrayList<Field> atts = new ArrayList<>();

        Field[] declaredFields = o.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            atts.add(field);
        }

        return atts;

    }

    public static void ejecutarSet(Object o, String att, Object valor) {
        try {
            // Obtener el tipo de dato del value recibido
            Method setter = o.getClass().getMethod(
                    "set".concat(String.valueOf(att.charAt(0)).toUpperCase()).concat(att.substring(1)),
                    valor.getClass());

            setter.invoke(o, valor);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Object ejecutarGet(Object o, String att) {
        Object res = new Object();
        try {
            Method getter = o.getClass().getMethod(
                    "get".concat(String.valueOf(att.charAt(0)).toUpperCase()).concat(att.substring(1)));

            res = getter.invoke(o, new Object[0]);

            return res;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return res;
    }
}