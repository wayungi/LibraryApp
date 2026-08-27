package com.servicecops.project.services.libservices;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.models.entities.BaseEntity;
import com.servicecops.project.repositories.GenericRepository;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * provides definition logic for common crud operations.
 * */

public abstract class CrudService<T extends BaseEntity> extends BaseWebActionsService
{
    @Autowired
    private GenericRepository<T> genericRepository;
    private final Class<T> entityClass;

    protected final OperationReturnObject operationReturnObject = new OperationReturnObject();
    private final String JsonBody = "body";

    protected CrudService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // General queries from jpa repo interface.
    public OperationReturnObject save(JSONObject request) {
        requires(JsonBody, request);
        String entityBody = request.getString(JsonBody);
        T entity = JSONObject.parseObject(entityBody, entityClass);
        operationReturnObject.setReturnObject(genericRepository.save(entity));
        return operationReturnObject;
    }


    public OperationReturnObject saveAll(JSONObject request) {
        requires(JsonBody, request);
        String entityBody = request.getString(JsonBody);
        List<T> entities = JSON.parseArray(
                entityBody,
                entityClass
        );
        operationReturnObject.setReturnObject(genericRepository.saveAll(entities));
        return operationReturnObject;
    }

    public OperationReturnObject findById(JSONObject request) {
        requires("id", request);
        String id = request.getString("id");
        Optional<T> record = genericRepository.findById(Long.parseLong(id));
        if (record.isEmpty()) {
            operationReturnObject.setReturnMessage("No record found with id " + id);
            operationReturnObject.setReturnObject(null);
            return operationReturnObject;
        }
        operationReturnObject.setReturnObject(record);
        return operationReturnObject;
    }

    public OperationReturnObject existsById(JSONObject request) {
        requires("id", request);
        String id = request.getString("id");
        operationReturnObject.setReturnObject(genericRepository.existsById(Long.parseLong(id)));
        return operationReturnObject;
    }

    public OperationReturnObject findAll() {
        operationReturnObject.setReturnObject(genericRepository.findAll());
        return operationReturnObject;
    }

    public OperationReturnObject findAllById(JSONObject request) {

        requires("ids", request);
        List<Long> ids = request.getList("ids", Long.class);

        operationReturnObject.setReturnObject(genericRepository.findAllById(ids));
        return operationReturnObject;
    }

    public OperationReturnObject count() {
        operationReturnObject.setReturnObject(genericRepository.count());
        return operationReturnObject;
    }

    public OperationReturnObject deleteById(JSONObject request) {
        requires("id", request);
        String id = request.getString("id");
        genericRepository.deleteById(Long.parseLong(id));

        if (genericRepository.existsById(Long.parseLong(id))) {
            operationReturnObject.setReturnObject(null);
            operationReturnObject.setReturnMessage("Failed to delete record with id " + id);
            operationReturnObject.setReturnCode(0);
            return operationReturnObject;
        }
        operationReturnObject.setReturnObject("deleted successfully");
        return operationReturnObject;
    }

    public OperationReturnObject deleteAllById(JSONObject request) {

        requires("ids", request);
        List<Long> ids = request.getList("ids", Long.class);

        genericRepository.deleteAllById(ids);
        operationReturnObject.setReturnObject("deleted successfully");
        return operationReturnObject;
    }


    public OperationReturnObject updateById(JSONObject request) {

        requires(List.of(JsonBody, "id"), request);
        T entity = request.getObject(JsonBody, entityClass);
        String id = request.getString("id");

        Optional<T> object = genericRepository.findById(Long.parseLong(id));
        if (object.isPresent()){
            operationReturnObject.setReturnObject( genericRepository.save(entity));
            return operationReturnObject;
        }
        operationReturnObject.setReturnObject("No such record");
        return operationReturnObject;
    }

    // utility method for copying jsons.
    protected void copyJSONs(JSONObject from, JSONObject to) {

        from.forEach((key, fromValue) -> {

            if (fromValue == null) {
                return;
            }

            // corresponding to value.
            Object toValue = to.get(key);

            if (fromValue instanceof JSONObject fromObject
                    && toValue instanceof JSONObject toObject) {

                copyJSONs(fromObject, toObject);

            } else if (fromValue instanceof JSONArray fromArray
                    && toValue instanceof JSONArray toArray) {

                for (int i = 0; i < fromArray.size(); i++) {

                    Object fromItem = fromArray.get(i);

                    if (i < toArray.size()) {

                        Object toItem = toArray.get(i);

                        if (fromItem instanceof JSONObject fromObject
                                && toItem instanceof JSONObject toObject) {
                            copyJSONs(fromObject, toObject);

                        } else {
                            toArray.set(i, fromItem);
                        }

                    } else {
                        toArray.add(fromItem);
                    }
                }
            } else {

                to.put(key, fromValue);
            }
        });
    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action) {

            case "save" ->
                    save(request);
            case "saveAll" ->
                    saveAll(request);

            case "findById" ->
                    findById(request);

            case "existsById" ->
                    existsById(request);

            case "findAll" ->
                    findAll();

            case "findAllById" ->
                    findAllById(request);

            case "count" ->
                    count();

            case "deleteById" ->
                    deleteById(request);

            case "deleteAllById" ->
                    deleteAllById(request);
            case "updateById" ->
                    updateById(request);
            default ->{
                operationReturnObject.setReturnObject(null);
                operationReturnObject.setReturnCode(0);
                operationReturnObject.setReturnMessage( "Action " + action + " not known in this context");
                yield operationReturnObject;
            }
        };
    }
}
