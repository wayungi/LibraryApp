package com.servicecops.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.services.auth.AuthService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebActionsService {

    private final AuthService authService;
    public OperationReturnObject processAction(String service, String action, JSONObject payload) {
        return switch (service) {
            case "Auth" -> authService.process(action, payload);
            default -> {
                OperationReturnObject res = new OperationReturnObject();
                res.setReturnCodeAndReturnMessage(404, "UNKNOWN SERVICE");
                yield res;
            }
        };
    }
}
