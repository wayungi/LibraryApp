package com.servicecops.project.models.jpahelpers.sortingAndFiltering;

import com.jmsoft.Moonlight.helpers.EntityCommand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
public class NativeQueryHelperService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<HashMap<String, Object>> customFiler(EntityCommand command) {
        String sql = command.getEffectiveHql();

        Query query = entityManager.createNativeQuery(sql);

        HashMap<String, Object> qlMap = command.getQlMap();
        Iterator iterator = qlMap.keySet().iterator();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            query.setParameter(key, qlMap.get(key));
        }

        if (command.getFirstResult() != null) {
            query.setFirstResult(command.getFirstResult());
        }

        if (command.getMaxResults() != null) {
            query.setMaxResults(command.getMaxResults());
        }


        NativeQuery nativeQuery = query.unwrap(NativeQuery.class)
                .setTupleTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        //                .setResultListTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<HashMap<String, Object>> resultList = nativeQuery.getResultList();
        return resultList;
    }
}
