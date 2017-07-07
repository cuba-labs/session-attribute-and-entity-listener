package com.company.sample.listener;

import com.haulmont.cuba.core.sys.AppContext;
import com.haulmont.cuba.core.sys.listener.EntityListenerManager;
import com.haulmont.cuba.security.entity.Group;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("sample_AppLifecycle")
public class AppLifecycle implements AppContext.Listener {

    @Inject
    private EntityListenerManager entityListenerManager;

    public AppLifecycle() {
        AppContext.addListener(this);
    }

    @Override
    public void applicationStarted() {
        entityListenerManager.addListener(Group.class, GroupEntityListener.NAME);
    }

    @Override
    public void applicationStopped() {
    }
}
