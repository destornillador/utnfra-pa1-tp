package services;

import annotations.Tabla;
import annotations.Id;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import annotations.Columna;
import utils.UBean;
import utils.UConexion;

public class Consultas {

    // Esta variable se utiliza para detectar el nombre de la columna que lleva el
    // annotation Id
    static final String COLUMN_ID_KEY = "COLUMN_ID";

    static String configFile = System.getProperty("user.dir").concat("/framework.properties");
    static Connection dbConnection = (Connection) UConexion.Initialize(configFile);

    public Consultas() {
    }

    public static void guardar(Object o) {
        String table;
        Map<String, String> objMap; // [columnName: value]

        // Obtener tabla, nombre de la columna y el valor
        table = o.getClass().getAnnotation(Tabla.class).nombre();
        objMap = getObjAttributes(o);

        // FIXME: Solo se llama a este metodo porque elimina COLUMN_ID_KEY del map
        // Recuperar la columna utilizada como Id
        String columnId = getColumnIdName(objMap);

        // Generar query dinamicamente
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ".concat(table).concat(" (").concat(generateColumnFormat(objMap)).concat(") VALUES (")
                .concat(generateParameterFormat(objMap.size())).concat(")"));

        // Preparar Statement
        PreparedStatement ps = null;
        try {
            ps = dbConnection.prepareStatement(query.toString());

            int i = 1;
            for (Map.Entry<String, String> e : objMap.entrySet()) {
                ps.setObject(i, e.getValue());
                i++;
            }

            // Ejecutar query
            ps.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void modificar(Object o) {
        // Modificar todas las columnas excepto Id
        String table;
        Map<String, String> objMap = new HashMap<String, String>(); // [columnName: value]

        // Obtener tabla, nombre de la columna y el valor
        table = o.getClass().getAnnotation(Tabla.class).nombre();
        objMap = getObjAttributes(o);

        // Recuperar la columna utilizada como Id y eliminarla
        String columnId = getColumnIdName(objMap);

        // Generar query dinamicamente
        StringBuilder query = new StringBuilder();
        query.append("UPDATE ".concat(table).concat(" SET ").concat(generateColumnValueFormat(objMap, columnId))
                .concat(" WHERE ".concat(columnId).concat(" = ?")));

        // Preparar Statement
        PreparedStatement ps = null;

        try {
            ps = dbConnection.prepareStatement(query.toString());

            int i = 1;
            String columnIdValue = null;
            for (Map.Entry<String, String> e : objMap.entrySet()) {
                // Setear el valor de columnId al final
                if (e.getKey() == columnId) {
                    columnIdValue = e.getValue();
                    continue;
                }
                ps.setObject(i, e.getValue());
                i++;
            }

            // Actualizar el ultimo parametro que corresponde al Id
            ps.setObject(objMap.size(), columnIdValue);

            // Ejecutar query
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void eliminar(Object o) {
        // Modificar todas las columnas excepto Id
        String table;
        Map<String, String> objMap; // [columnName: value]

        // Obtener tabla, nombre de la columna y el valor
        table = o.getClass().getAnnotation(Tabla.class).nombre();
        objMap = getObjAttributes(o);

        // Recuperar la columna utilizada como Id
        String columnId = getColumnIdName(objMap);

        // Generar query dinamicamente
        StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ".concat(table).concat(" WHERE ".concat(columnId).concat(" = ?")));

        // Preparar Statement
        PreparedStatement ps = null;

        try {
            ps = dbConnection.prepareStatement(query.toString());

            for (Map.Entry<String, String> e : objMap.entrySet()) {
                if (e.getKey() != columnId) {
                    continue;
                }
                ps.setObject(1, e.getValue());
                break;
            }

            // Ejecutar query
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object obtenerPorId(Class<?> c, Object o) {
        // Modificar todas las columnas excepto Id
        String table;
        Map<String, String> objMap; // [columnName: value]

        // Obtener tabla, nombre de la columna y el valor
        table = o.getClass().getAnnotation(Tabla.class).nombre();
        objMap = getObjAttributes(o);

        // Recuperar la columna utilizada como Id
        String columnId = getColumnIdName(objMap);

        // Generar query dinamicamente
        StringBuilder query = new StringBuilder();
        query.append("SELECT ".concat(generateColumnFormat(objMap))
                .concat(" FROM ".concat(table).concat(" WHERE ".concat(columnId).concat(" = ?"))));

        // Preparar Statement
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object objReturn = null;

        try {
            ps = dbConnection.prepareStatement(query.toString());

            // Obtener el valor correspondiente al Id
            for (Map.Entry<String, String> e : objMap.entrySet()) {
                if (e.getKey() != columnId) {
                    continue;
                }
                ps.setObject(1, e.getValue());
                break;
            }

            // Ejecutar query
            rs = ps.executeQuery();

            // Guardar los datos del Objecto para ser utilizado mas adelante
            // 1. Instanciar un objeto vacio
            // Obtener el nombre de los parametros para llamar al setter
            try {
                objReturn = c.newInstance();
                rs.next();
                java.sql.ResultSetMetaData rsmd = rs.getMetaData();
                for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
                    UBean.ejecutarSet(objReturn, rsmd.getColumnName(i), rs.getObject(i));
                }

            } catch (InstantiationException | IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return objReturn;
    }

    /**
     * Obtener el nombre de la columna que tiene la annotation Id
     * @param map Contiene el atributo/valor de un Objeto
     * @return Nombre de la columna utilizada como Id
     */
    private static String getColumnIdName(Map<String, String> map) {
        String columnId = map.get(COLUMN_ID_KEY);
        map.remove(COLUMN_ID_KEY);
        return columnId;
    }

    /**
     * Genera un Map<Atributo,Valor> a partir de los atributos del Object
     * @param obj Object que se le debe extraer sus atributos
     * @return  Map conteniendo <ColumnName,Value>
     */
    private static Map<String, String> getObjAttributes(Object obj) {
        Map<String, String> objMap = new LinkedHashMap(); // [columnName: value]

        // Obtener todos los atributos y chequear su metadata
        ArrayList<Field> atts = UBean.obtenerAtributos(obj);

        for (Field att : atts) {
            Columna column = att.getAnnotation(Columna.class);

            if (column == null) {
                continue;
            }

            // Discriminar la columna que tiene el annotation Id
            Id id = att.getAnnotation(Id.class);
            if (id != null) {
                objMap.put(COLUMN_ID_KEY, column.nombre());
            }

            objMap.put(column.nombre(), UBean.ejecutarGet(obj, att.getName()).toString());
        }

        return objMap;
    }

    /**
     * Genera un String con el nombre de la columna y su valor. Ej: ColumnNameA=ValueA,ColumnNameB
     * @param map Contiene el atributo/valor de un Objeto
     * @param id Nombre de la columna utilizada como Id
     * @return String con nombre de la columna y su valor separado por ","
     */
    private static String generateColumnValueFormat(Map<String, String> map, String id) {
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> e : map.entrySet()) {
            // Ignore if column name is Id
            if (e.getKey().equals(id)) {
                continue;
            }
            query.append(e.getKey()).append("=?,");
        }

        // Probably this could be improved
        query.replace(query.length() - 1, query.length(), "");

        return query.toString();
    }

    /***
     * Crea un String separado por "," a partir de todas las key de la variable map
     * @param map Contiene el atributo/valor de un Objeto
     * @return String con todas los nombres de las columnas, separadas por ","
     */
    private static String generateColumnFormat(Map<String, String> map) {
        StringBuilder query = new StringBuilder();

        for (Map.Entry<String, String> e : map.entrySet()) {
            query.append(e.getKey()).append(",");
        }

        // Probably this could be improved
        query.replace(query.length() - 1, query.length(), "");

        return query.toString();
    }

    /***
     * Crea un String concatenando tantos "?," como lo indique count
     * @param count Cantidad de "?," a generar 
     * @return String con "?," concatenado
     */
    private static String generateParameterFormat(int count) {
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < count; i++) {
            // Add ? into values
            query.append("?");

            // Last iteration
            if (i != count - 1) {
                query.append(",");
                continue;
            }
        }
        return query.toString();
    }
}