package com.servicecops.project.services.libservices;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.entities.Student;
import com.servicecops.project.repositories.StudentRepo;
import com.servicecops.project.utils.OperationReturnObject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;



@Service
public class StudentService extends CrudService<Student> {

    private final StudentRepo studentRepo;
    private final PasswordEncoder passwordEncoder;

    protected StudentService(StudentRepo studentRepo, PasswordEncoder passwordEncoder) {
        super(Student.class);
        this.studentRepo = studentRepo;
        this.passwordEncoder = passwordEncoder;

    }

    // updating student Details using Admission Number.
    public OperationReturnObject updateByAdmissionNumber(JSONObject request) {

        requires(List.of("admissionNumber", "student"), request);

        String admissionNumber = request.getString("admissionNumber");

        Optional<Student> optionalStudent =
                studentRepo.findByAdmissionNumber(admissionNumber);
        if (optionalStudent.isEmpty()) {
            operationReturnObject.setReturnObject(null);
            operationReturnObject.setReturnMessage("No Student with admission Number "+admissionNumber);
            return operationReturnObject;
        }

        Student existingStudent = optionalStudent.get();

        JSONObject incomingStudent = request.getJSONObject("student");

        JSONObject existingStudentJson =
                JSON.parseObject(JSON.toJSONString(existingStudent));

        copyJSONs(incomingStudent, existingStudentJson);

        Student updatedStudent =
                JSON.to( Student.class, existingStudentJson);
        operationReturnObject.setReturnObject(
                studentRepo.save(updatedStudent)
        );
        operationReturnObject.setReturnMessage("success");
        operationReturnObject.setReturnCode(0);
        return operationReturnObject;
    }

    // saving student.
    @Override
    public OperationReturnObject save(JSONObject request) {

        requires("body", request);

        List<String> requiredFields = List.of(
                "name",
                "dateOfBirth",
                "gender",
                "status"
        );

        JSONObject studentJSON = request.getJSONObject("body");
        requires(requiredFields, studentJSON);

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
        userModel.setRole(null);
        savedStudent.setUser(userModel);
        operationReturnObject.setReturnObject(
                studentRepo.save(savedStudent)
        );
        operationReturnObject.setReturnMessage("success");
        return operationReturnObject;
    }


    @Override
    public OperationReturnObject switchActions(
            String action,
            JSONObject request
    ) {

        return switch (action) {
            case "updateByAdmissionNumber" ->
                    updateByAdmissionNumber(request);
            default ->
                    super.switchActions(action, request);
        };
    }

}
