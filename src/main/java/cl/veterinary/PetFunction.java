package cl.veterinary;


import cl.veterinary.model.Pet;
import cl.veterinary.service.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

import java.util.Optional;


public class PetFunction {

    private static final ApplicationContext context =
            new SpringApplicationBuilder(SpringBootAzureApp.class).run();

    private final PetService petService =
            context.getBean(PetService.class);

    @FunctionName("findAllPet")
    public HttpResponseMessage findAllCustomer(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION)
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext executionContext) {

        executionContext.getLogger().info("Procesando solicitud listPet...");

        try {
            var customers = petService.findAll();
            return request.createResponseBuilder(HttpStatus.OK).body(customers).build();
        } catch (Exception e) {
            executionContext.getLogger().severe("Error al obtener pet: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al obtener las mascotas")
                    .build();
        }
    }



    @FunctionName("findPetById")
    public HttpResponseMessage findCustomerById(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "findPetById/{id}") // ID como parte de la ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Buscando mascota por ID: " + id);

        try {
            Long customerId = Long.parseLong(id);
            Optional<Pet> pet = petService.findPetById(customerId);

            if (pet.isPresent()) {
                return request.createResponseBuilder(HttpStatus.OK)
                        .body(pet.get())
                        .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Mascota con ID " + id + " no encontrado.")
                        .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al buscar mascota: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al buscar la moscota.")
                    .build();
        }
    }





    @FunctionName("savePet")
    public HttpResponseMessage savePet(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "savePet")
            HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud savePet...");

        try {

            String requestBody = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();
            Pet pet = mapper.readValue(requestBody, Pet.class);

            Pet saved = petService.savePet(pet);

            return request.createResponseBuilder(HttpStatus.CREATED)
                    .body(saved)
                    .build();

        } catch (Exception e) {
            context.getLogger().severe("Error al guardar mascota: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar mascota")
                    .build();
        }
    }


    @FunctionName("updatePet")
    public HttpResponseMessage updatePet(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.PUT},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "updatePet/{id}") // id por ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud updatePet con ID: " + id);

        try {
            Long petId = Long.parseLong(id);

            // Parsear el JSON recibido
            String requestBody = request.getBody().orElse("");
            ObjectMapper mapper = new ObjectMapper();
            Pet updatedData = mapper.readValue(requestBody, Pet.class);

            // Buscar si el mascota existe
            Optional<Pet> existingOpt = petService.findPetById(petId);
            if (existingOpt.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Mascota con ID " + id + " no encontrado.")
                        .build();
            }

            Pet existing = existingOpt.get();

            // Actualizar campos
            existing.setId(petId);
            existing.setNombre(updatedData.getNombre());
            existing.setEspecie(updatedData.getEspecie());
            existing.setRaza(updatedData.getRaza());
            existing.setSexo(updatedData.getSexo());
            existing.setFechaNacimiento(updatedData.getFechaNacimiento());
            existing.setColor(updatedData.getColor());
            existing.setPeso(updatedData.getPeso());
            existing.setEsterilizado(updatedData.getEsterilizado());
            existing.setEstado(updatedData.getEstado());
            existing.setFechaRegistro(updatedData.getFechaRegistro());
            // Guardar cambios
            Pet updated = petService.updatePet(existing);

            return request.createResponseBuilder(HttpStatus.OK)
                    .body(updated)
                    .build();

        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al actualizar mascota: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar mascota.")
                    .build();
        }
    }

    @FunctionName("deletePet")
    public HttpResponseMessage deleteCustomer(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.FUNCTION,
                    route = "deletePet/{id}") // ID por ruta
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id,
            final ExecutionContext context) {

        context.getLogger().info("Procesando solicitud deletePet con ID: " + id);

        try {
            Long petId = Long.parseLong(id);

            // Buscar si existe
            Optional<Pet> existing = petService.findPetById(petId);
            if (existing.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Mascota con ID " + id + " no encontrado.")
                        .build();
            }

            // Eliminar
            petService.deletePet(petId);

            return request.createResponseBuilder(HttpStatus.OK).build();

        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("ID inválido: debe ser numérico.")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al eliminar mascota: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar mascota.")
                    .build();
        }
    }

}

