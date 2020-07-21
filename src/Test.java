import annotations.Columna;
import annotations.Id;
import annotations.Tabla;

@Tabla(nombre = "Test")
public class Test {
    @Id
    @Columna(nombre = "Id")
    Integer id;
    
    @Columna(nombre = "Nombre")
    String nombre;
    
    public Test() {}

    public Test(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}