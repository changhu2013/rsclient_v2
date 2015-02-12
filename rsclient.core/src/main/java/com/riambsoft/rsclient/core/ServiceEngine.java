package com.riambsoft.rsclient.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.riambsoft.rsclient.core.annotation.RSAfterMethod;
import com.riambsoft.rsclient.core.annotation.RSBeforeMethod;
import com.riambsoft.rsclient.core.annotation.RSMethod;
import com.riambsoft.rsclient.core.annotation.RSParam;
import com.riambsoft.rsclient.core.annotation.RSResult;
import com.riambsoft.rsclient.core.exception.ServiceException;

public class ServiceEngine {

	public Variable invoke(Object service, String methodName, VariablePool pool)
			throws ServiceException {
		
		return invokeObjectService(service, methodName, pool);
	}

	private Variable invokeObjectService(Object service, String methodName,
			VariablePool pool) throws ServiceException {

		Method method = getServiceMethod(service.getClass(), methodName);

		if (method != null) {

			// 执行Before方法
			beforeInvokeObjectService(method, service, pool);

			// 执行当前业务方法
			Class<?>[] parameterTypes = method.getParameterTypes();
			List<Parameter> parameters = new ArrayList<Parameter>();
			ParameterCreator creator = new ParameterCreator();

			int idx = 0;
			Annotation[][] ann = method.getParameterAnnotations();
			for (Annotation[] an : ann) {
				for (Annotation a : an) {
					if (a.annotationType().equals(RSParam.class)) {
						RSParam param = (RSParam) a;

						String name = param.value();
						Class<?> clazz = parameterTypes[idx++];

						parameters.add(creator.createObjectParameter(name,
								clazz));
					}
				}
			}

			// 获取参数值
			Object[] args = getMethodArgs(method, pool);

			Object value = null;
			try {
				value = method.invoke(service, args);
			} catch (Exception e) {
				throw new ServiceException(e);
			}

			Variable result = null;
			if (method.isAnnotationPresent(RSResult.class)) {
				RSResult rsResult = ((RSResult) method
						.getAnnotation(RSResult.class));
				String name = rsResult.value();

				result = pool.add(name, value);
			} else {

				result = new Variable("void", Void.class, null);
			}

			// 执行After方法
			afterInvokeObjectService(method, service, pool);

			// 返回当前方法的结果
			return result;
		} else {

			throw new ServiceException("未能在业务服务" + service.getClass()
					+ "中找到业务方法" + methodName);
		}
	}

	/**
	 * 获取参数值
	 * 
	 * @param method
	 * @param pool
	 * @return
	 */
	public Object[] getMethodArgs(Method method, VariablePool pool) {

		Class<?>[] types = method.getParameterTypes();
		Object[] args = new Object[types.length];
		int idx = 0;
		Annotation[][] ann = method.getParameterAnnotations();
		for (Annotation[] an : ann) {
			for (Annotation a : an) {
				if (a.annotationType().equals(RSParam.class)) {
					RSParam p = (RSParam) a;
					args[idx] = pool.getValue(p.value(), types[idx]);
					idx++;
				}
			}
		}
		return args;
	}

	private void beforeInvokeObjectService(Method method, Object service,
			VariablePool pool) throws ServiceException {
		if (method.isAnnotationPresent(RSBeforeMethod.class)) {
			RSBeforeMethod beforeMethods = method
					.getAnnotation(RSBeforeMethod.class);
			String[] methodNames = beforeMethods.value();
			for (String methodName : methodNames) {
				invokeObjectService(service, methodName, pool);
			}
		}
	};

	private void afterInvokeObjectService(Method method, Object service,
			VariablePool pool) throws ServiceException {
		if (method.isAnnotationPresent(RSAfterMethod.class)) {
			RSAfterMethod afterMethods = method
					.getAnnotation(RSAfterMethod.class);
			String[] methodNames = afterMethods.value();
			for (String methodName : methodNames) {
				invokeObjectService(service, methodName, pool);
			}
		}
	};

	private Method getServiceMethod(Class<?> clazz, String methodName) {
		if (Object.class.equals(clazz)) {
			return null;
		}
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)
					&& method.isAnnotationPresent(RSMethod.class)) {
				return method;
			}
		}

		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> inters : interfaces) {
			Method method = getServiceMethod(inters, methodName);
			if (method != null) {
				return method;
			}
		}

		return getServiceMethod(clazz.getSuperclass(), methodName);
	}

}
