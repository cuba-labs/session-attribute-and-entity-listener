package com.company.sample.security.app;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.security.app.UserManagementServiceBean;
import com.haulmont.cuba.security.entity.Constraint;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.SessionAttribute;

import java.util.List;
import java.util.Set;

public class CustomUserManagementServiceBean extends UserManagementServiceBean {
    @Override
    protected Group cloneGroup(Group group, Group parent, Set<String> groupNames, EntityManager em) {
        Group groupClone = metadata.create(Group.class);

        String newGroupName = generateName(group.getName(), groupNames);
        groupClone.setName(newGroupName);
        groupNames.add(newGroupName);

        groupClone.setParent(parent);

        em.persist(groupClone);
        // fire hierarchy listeners
        em.flush();

        if (group.getConstraints() != null) {
            for (Constraint constraint : group.getConstraints()) {
                Constraint constraintClone = cloneConstraint(constraint, groupClone);
                em.persist(constraintClone);
            }
        }

        if (group.getSessionAttributes() != null) {
            for (SessionAttribute attribute : group.getSessionAttributes()) {
                // Skip attributes with certain name
                if (!attribute.getName().equals("someNumber")) {
                    SessionAttribute attributeClone = cloneSessionAttribute(attribute, groupClone);
                    em.persist(attributeClone);
                }
            }
        }

        TypedQuery<Group> query = em.createQuery("select g from sec$Group g where g.parent.id = :group", Group.class);
        query.setParameter("group", group);

        List<Group> subGroups = query.getResultList();
        if (subGroups != null && subGroups.size() > 0) {
            for (Group subGroup : subGroups) {
                cloneGroup(subGroup, groupClone, groupNames, em);
            }
        }

        return groupClone;
    }
}
