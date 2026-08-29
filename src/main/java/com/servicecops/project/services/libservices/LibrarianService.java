package com.servicecops.project.services.libservices;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.authorities.Role;
import com.servicecops.project.models.database.SystemRoleModel;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.entities.Librarian;
import com.servicecops.project.repositories.LibrarianRepo;
import com.servicecops.project.repositories.SystemRoleRepository;
import com.servicecops.project.utils.OperationReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LibrarianService extends CrudService<Librarian>{
    @Autowired
    private LibrarianRepo librarianRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SystemRoleRepository systemRoleRepository;

    protected LibrarianService() {
        super(Librarian.class);
    }

    // saving librarian.
    public OperationReturnObject upsert(JSONObject request) {
        requires("body", request);

        List<String> requiredFields = List.of(
                "name",
                "gender",
                "email"
        );
        JSONObject librarianJSON = request.getJSONObject("body");

        requires(requiredFields, librarianJSON);

        String email = librarianJSON.getString("email");
        // Find librarian using email.
        Optional<Librarian> existingLibrarian =
                librarianRepo.findFirstByEmail(email);

        if (existingLibrarian.isPresent()) {

            Librarian librarian = existingLibrarian.get();

            librarian.setName(
                    librarianJSON.getString("name")
            );

            librarian.setGender(
                    librarianJSON.getString("gender")
            );

            librarian.setPhoneNumber(
                    "phoneNumber"
            );
            librarian.setDateOfBirth(
                    librarianJSON.getObject(
                            "dateOfBirth",
                            java.time.LocalDate.class
                    )
            );

            librarian.setDateJoined(
                    librarianJSON.getObject(
                            "dateJoined",
                            java.time.LocalDate.class
                    )
            );

            Librarian updatedLibrarian =
                    librarianRepo.save(librarian);

            operationReturnObject.setReturnObject(updatedLibrarian);
            operationReturnObject.setReturnCode(0);
            operationReturnObject.setReturnMessage(
                    "success"
            );

            return operationReturnObject;
        }
        Librarian librarian =
                librarianJSON.toJavaObject(Librarian.class);

        /*
         * Get librarian role before saving.
         */
        SystemRoleModel librarianRole =
                systemRoleRepository
                        .findFirstByRoleCode(Role.LIBRARIAN.name())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "LIBRARIAN role was not found"
                                )
                        );

        /*
         * Save librarian first so that employeeNumber
         * is generated.
         */
        Librarian savedLibrarian =
                librarianRepo.save(librarian);

        /*
         * Create system user only for a NEW librarian.
         */
        SystemUserModel userModel =
                new SystemUserModel();

        userModel.setUsername(
                savedLibrarian.getEmployeeNumber()
        );

        userModel.setPassword(
                passwordEncoder.encode(
                        savedLibrarian.getEmployeeNumber()
                )
        );

        userModel.setIsActive(true);
        userModel.setLastLoggedInAt(new Date());
        userModel.setRole(librarianRole);

        savedLibrarian.setUser(userModel);

        Librarian result =
                librarianRepo.save(savedLibrarian);
        operationReturnObject.setReturnObject(result);
        operationReturnObject.setReturnCode(0);
        operationReturnObject.setReturnMessage(
                "Librarian created successfully"
        );

        return operationReturnObject;
    }

    public OperationReturnObject findByEmail(JSONObject request) {
        requires("email", request);
        String email = request.getString("email").trim();
        operationReturnObject.setReturnObject(librarianRepo.findFirstByEmail(email));
        operationReturnObject.setReturnCode(0);
        operationReturnObject.setReturnMessage("success");
        return operationReturnObject;
    }
    public OperationReturnObject findByEmployeeNumber(JSONObject request) {
        requires("employeeNumber", request);
        String employeeNumber = request.getString("employeeNumber").trim();
        operationReturnObject.setReturnObject(librarianRepo.findByEmployeeNumber(employeeNumber));
        operationReturnObject.setReturnCode(0);
        operationReturnObject.setReturnMessage("success");
        return operationReturnObject;
    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){
            case "upsert" -> upsert(request);
            case "findByEmail" -> findByEmail(request);
            case "findByEmployeeNumber" -> findByEmployeeNumber(request);
            default ->   super.switchActions(action, request);
        };
    }
}
