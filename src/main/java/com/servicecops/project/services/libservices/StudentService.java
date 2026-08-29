package com.servicecops.project.services.libservices;
import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.authorities.Role;
import com.servicecops.project.models.database.SystemRoleModel;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.entities.Student;
import com.servicecops.project.repositories.StudentRepo;
import com.servicecops.project.repositories.SystemRoleRepository;
import com.servicecops.project.utils.OperationReturnObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class StudentService extends CrudService<Student> {

    private final StudentRepo studentRepo;
    private final PasswordEncoder passwordEncoder;
    private final SystemRoleRepository systemRoleRepository;

    protected StudentService(StudentRepo studentRepo,
                             PasswordEncoder passwordEncoder,
                             SystemRoleRepository systemRoleRepository) {
        super(Student.class);
        this.studentRepo = studentRepo;
        this.passwordEncoder = passwordEncoder;
        this.systemRoleRepository = systemRoleRepository;


    }

    // saving or updating student. for updates to occur, you must pass admission number inside the body.
    public OperationReturnObject upsert(JSONObject request) {

        requires("body", request);

        List<String> requiredFields = List.of(
                "name",
                "dateOfBirth",
                "gender"
        );

        JSONObject studentJSON = request.getJSONObject("body");
        requires(requiredFields, studentJSON);
        // Find student using email.
        String admissionNumber  = studentJSON.getString("admissionNumber");
       if (admissionNumber != null && !admissionNumber.isEmpty()) {
           Optional<Student> existingStudent =
                   studentRepo.findByAdmissionNumber(admissionNumber);

           if (existingStudent.isPresent()) {

               Student student = existingStudent.get();

               student.setName(
                       studentJSON.getString("name")
               );

               student.setGender(
                       studentJSON.getString("gender")
               );

               student.setPhoneNumber(
                       studentJSON.getString("phoneNumber")
               );
               student.setDateOfBirth(
                       studentJSON.getObject(
                               "dateOfBirth",
                               java.time.LocalDate.class
                       )
               );

               student.setCountry(
                       studentJSON.getString(
                               "country"
                       )
               );
               student.setNationality(
                       studentJSON.getString("nationality")
               );
               student.setEmail(
                       studentJSON.getString("email")
               );

               Student updatedStudent =
                       studentRepo.save(student);

               operationReturnObject.setReturnObject(updatedStudent);
               operationReturnObject.setReturnCode(0);
               operationReturnObject.setReturnMessage(
                       "success"
               );

               return operationReturnObject;
           }


       }

        Student student = studentJSON.toJavaObject(Student.class);
        // save student first before creating user of type student.
        Student savedStudent = studentRepo.save(student);

        // Create system user
        SystemUserModel userModel = new SystemUserModel();
        userModel.setUsername(savedStudent.getAdmissionNumber());
        userModel.setPassword(
                passwordEncoder.encode(savedStudent.getAdmissionNumber())
        );
        userModel.setIsActive(true);
        userModel.setLastLoggedInAt(new Date());
        SystemRoleModel studentRole =
                systemRoleRepository
                        .findFirstByRoleCode(Role.MEMBER.name())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student role was not found"
                                )
                        );
        userModel.setRole(studentRole);
        savedStudent.setUser(userModel);
        operationReturnObject.setReturnObject(
                studentRepo.save(savedStudent)
        );
        operationReturnObject.setReturnMessage("success");
        return operationReturnObject;
    }

    public OperationReturnObject findByAdmissionNumber(JSONObject request) {
        requires("admissionNumber", request);
        String admissionNumber = request.getString("admissionNumber").trim();
        operationReturnObject.setReturnObject(studentRepo.findByAdmissionNumber(admissionNumber));
        operationReturnObject.setReturnCode(0);
        operationReturnObject.setReturnMessage("success");
        return operationReturnObject;
    }
    @Override
    public OperationReturnObject switchActions(
            String action,
            JSONObject request
    ) {
        return switch (action) {
            case "upsert" -> upsert(request);
            case "findByAdmissionNumber" -> findByAdmissionNumber(request);
            default ->
                    super.switchActions(action, request);
        };
    }

}
