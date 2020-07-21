import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        List<Test> tests = new ArrayList<Test>();
        
        log("Creando objetos Test");
        String[] nombres = {"Diego", "Roberto", "Ricardo"};
        for (int i = 0; i < nombres.length; i++) {
            tests.add(new Test(i, nombres[i]));
            System.out.println("  [+] ".concat(tests.get(i).getId().toString()).concat(":").concat(tests.get(i).getNombre()));
        }

        log("Insertando objetos en la DB");
        for (Test test : tests) {
            services.Consultas.guardar(test);
            System.out.println("  [+] ".concat(test.getId().toString()).concat(":").concat(test.getNombre()));
        }

        log("Actualizando el nombre del Id=0");
        for (Test test : tests) {
            if (test.getId() == 0) {
                String nuevoNombre = "Tito";
                System.out.println("  [~] ".concat(test.getId().toString()).concat(":").concat(test.getNombre().concat(" => ").concat(test.getId().toString().concat(":").concat(nuevoNombre))));
                test.setNombre(nuevoNombre);
                services.Consultas.modificar(test);
                break;
            }
        }
        log("Eliminando el Test cuyo Id=1");
        for (Test test : tests) {
            if (test.getId() == 1) {
                System.out.println("  [-] ".concat(test.getId().toString()).concat(":").concat(test.getNombre()));
                services.Consultas.eliminar(test);
                break;
            }
        }

        log("Recuperando el Test cuyo Id=2 de la DB");
        for (Test test : tests) {
            if (test.getId() == 2) {
                Test testRecuperado = (Test) services.Consultas.obtenerPorId(Test.class, test);
                System.out.println("  [+] ".concat(testRecuperado.getId().toString()).concat(":").concat(testRecuperado.getNombre()));
                break;
            }
        }

        log("Fin de la ejecucion");
    }

    private static void log(String msg) {
        System.out.println("[INFO] ".concat(msg));
    }
}