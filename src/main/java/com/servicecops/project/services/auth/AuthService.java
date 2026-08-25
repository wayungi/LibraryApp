package com.servicecops.project.services.auth;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.config.ApplicationConf;
import com.servicecops.project.config.JwtUtility;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService extends BaseWebActionsService {
    private final AuthenticationManager authenticationManager;
    private final ApplicationConf userDetailService;
    private final JwtUtility jwtUtility;

    private OperationReturnObject login(JSONObject request){

        List<String> requiredFields = new ArrayList<>();
        requiredFields.add("username");
        requiredFields.add("password");
        requires(requiredFields, request);

        String username= request.getString("username");
        String password= request.getString("password");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        final SystemUserModel userDetails = userDetailService.loadUserByUsername(username);
        final String token = jwtUtility.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token); // this is the jwt token the user can user from now on.
        response.put("user", userDetails);

        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(0, "Welcome back " + userDetails.getUsername());
        res.setReturnObject(response);

        return res;
    }


    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){
            case "login" -> login(request);
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }
}
