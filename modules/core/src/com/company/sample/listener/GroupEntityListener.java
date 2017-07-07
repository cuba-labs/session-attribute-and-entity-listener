package com.company.sample.listener;

import com.haulmont.chile.core.datatypes.impl.IntegerDatatype;
import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.core.app.UniqueNumbersAPI;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.SessionAttribute;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component("sample_GroupEntityListener")
public class GroupEntityListener implements BeforeInsertEntityListener<Group> {

    public static final String NAME = "sample_GroupEntityListener";

    @Inject
    private Metadata metadata;
    @Inject
    private UniqueNumbersAPI uniqueNumbers;
    @Inject
    private Persistence persistence;

    @Override
    public void onBeforeInsert(Group entity, EntityManager entityManager) {
        // In case of https://youtrack.cuba-platform.com/issue/PL-9350
        // we need to check if session attribute has been added
        TypedQuery<SessionAttribute> query = persistence.getEntityManager().createQuery(
                "select e from sec$SessionAttribute e where e.group.id = ?1 and e.name = ?2", SessionAttribute.class);
        query.setParameter(1, entity.getId());
        query.setParameter(2, "someNumber");

        List<SessionAttribute> sessionAttributes = query.getResultList();

        if (CollectionUtils.isEmpty(sessionAttributes)) {
            SessionAttribute sessionAttribute = metadata.create(SessionAttribute.class);
            sessionAttribute.setName("someNumber");
            sessionAttribute.setStringValue(Long.toString(uniqueNumbers.getNextNumber(GroupEntityListener.NAME)));
            sessionAttribute.setDatatype(IntegerDatatype.NAME);
            sessionAttribute.setGroup(entity);

            entityManager.persist(sessionAttribute);
        }
    }
}