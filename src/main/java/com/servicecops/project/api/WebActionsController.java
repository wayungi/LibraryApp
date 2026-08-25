package com.servicecops.project.api;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.services.WebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/")
@RequiredArgsConstructor
@Slf4j
public class WebActionsController {

    public final WebActionsService webActionsService;

    @Value("${app.version}")
    private String appVersion;

    /**
     * Check if the server is on. It is the only get request we have.
     * @return OperationReturnObject
     */
    @GetMapping
    public OperationReturnObject pingMe(){
        OperationReturnObject returnObject = new OperationReturnObject();
        returnObject.setCodeAndMessageAndReturnObject(0, "pong", appVersion);
        return returnObject;
    }

    /**
     * @implNote  For every request, it must define a SERVICE and an ACTION plus the rest of the data as per action implementation.
     * Without the first two, it will fail from this controller.
     * All requests, authenticated and unauthenticated reach here.
     * Checking for authentication happens on the service or request level.
     * @see com.servicecops.project.services.base.BaseWebActionsService
     *
     * @param requestBody Request Object.
     * @return OperationReturnObject
     */
    @PostMapping
    public OperationReturnObject processServiceRequest(@RequestBody String requestBody){
        log.info("Request:{}", requestBody);
        // grab the service name from the request body
        try {
            // let's avoid parsing twice, and parse from here once and for all.
            JSONObject jsonObject = JSON.parseObject(requestBody);
            if (!jsonObject.containsKey("SERVICE")){
                throw new IllegalStateException("SERVICE UNDEFINED");
            } else if (!jsonObject.containsKey("ACTION")) {
                throw new IllegalStateException("ACTION UNDEFINED");
            }else {
                String service = jsonObject.getString("SERVICE").trim();
                String action = jsonObject.getString("ACTION").trim();
                return webActionsService.processAction(service, action, jsonObject);
            }
        } catch (Exception e){
            OperationReturnObject responseWithError = new OperationReturnObject();
            responseWithError.setReturnCodeAndReturnMessage(500, e.getMessage());
            return responseWithError;
        }
    }

}
