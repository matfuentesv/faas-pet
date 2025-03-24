package cl.veterinary.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "PET")
@NoArgsConstructor
@AllArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pet_seq")
    @SequenceGenerator(name = "pet_seq", sequenceName = "PET_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ESPECIE")
    private String especie;

    @Column(name = "RAZA")
    private String raza;

    @Column(name = "SEXO")
    private String sexo;

    @Column(name = "FECHA_NACIMIENTO")
    private String fechaNacimiento;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "PESO")
    private Double peso;

    @Column(name = "ESTERILIZADO")
    private Boolean esterilizado;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "FECHA_REGISTRO")
    private String fechaRegistro;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;



}
