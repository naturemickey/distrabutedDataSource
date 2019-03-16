package org.w01f.dds.utils;

import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class GeneralCallPrinterBase<T> implements InvocationHandler, FactoryBean<Object> {
    protected abstract Object getObj();
    private Class<T> inf;

    private Object proxy;

    public GeneralCallPrinterBase(Class<T> inf) {
        this.inf = inf;
        proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{inf}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method);
        return method.invoke(getObj(), args);
    }

    @Override
    public Object getObject() throws Exception {
        return proxy;
    }

    public T getObject2() {
        try {
            return (T) proxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getObjectType() {
        return inf;
    }
}
