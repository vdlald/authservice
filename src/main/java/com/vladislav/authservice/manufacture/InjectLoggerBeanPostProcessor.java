package com.vladislav.authservice.manufacture;

import com.vladislav.authservice.manufacture.annotations.InjectLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
@SuppressWarnings("unused")
public class InjectLoggerBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> beanClass = bean.getClass();
        for (Field field : beanClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectLogger.class)) {
                final String className = beanClass.getName();
                final String loggerName = field.getAnnotation(InjectLogger.class).name();
                if (field.getType().equals(Logger.class)) {
                    final Logger logger = LoggerFactory.getLogger(loggerName.equals("") ? className : loggerName);
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, bean, logger);
                } else {
                    throw new BeanCreationException("@InjectLogger only support SL4J Logger, in " + className);
                }
            }
        }
        return bean;
    }
}
