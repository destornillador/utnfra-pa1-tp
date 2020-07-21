import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        List<Test> tests = new ArrayList();
        
        System.out.println("Crear objetos Test");
        tests.add(new Test(0, "Diego Alberto"));
        tests.add(new Test(1, "Roberto"));
        tests.add(new Test(2, "Ricardo"));

        // Insertar Tests
        System.out.println("Insertando un Tests");
        for (Test test : tests) {
            services.Consultas.guardar(test);
        }

        // Actualizar el nombre del segundo Test
        System.out.println("Actualizando un Test");
        tests.get(1).setNombre("Nombre Modificado");
        services.Consultas.modificar(tests.get(1));

        // Eliminar el primer Test
        System.out.println("Eliminando un Test");
        services.Consultas.eliminar(tests.get(0));

        // Generar un Test a partir de un Id
        System.out.println("Recuperando un Test");
        Test testRecuperado = (Test) services.Consultas.obtenerPorId(Test.class, tests.get(1));
        System.out.println("Id: " + testRecuperado.getId() + " - Nombre: " + testRecuperado.getNombre());

    }
}