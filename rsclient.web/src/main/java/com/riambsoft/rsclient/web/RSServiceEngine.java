package com.riambsoft.rsclient.web;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.riambsoft.rsclient.core.ServiceEngine;
import com.riambsoft.rsclient.core.VariablePool;
import com.riambsoft.rsclient.core.annotation.RSParam;
import com.riambsoft.rsclient.web.annotation.RSParamHttpServletRequest;
import com.riambsoft.rsclient.web.annotation.RSParamHttpServletResponse;
import com.riambsoft.rsclient.web.annotation.RSParamHttpSession;

public class RSServiceEngine extends ServiceEngine {

	/**
	 * 重写父类方法,取Session Request等参数
	 */
	public Object[] getMethodArgs(Method method, VariablePool pool) {

		Class<?>[] types = method.getParameterTypes();
		Object[] args = new Object[types.length];
		int idx = 0;
		Annotation[][] ann = method.getParameterAnnotations();
		for (Annotation[] an : ann) {
			for (Annotation a : an) {
				Class<? extends Annotation> at = a.annotationType();
				if (at.equals(RSParam.class)) {
					RSParam p = (RSParam) a;

					args[idx] = pool.getValue(p.value(), types[idx]);
					idx++;
				} else if (at.equals(RSParamHttpSession.class)) {

					args[idx] = pool.getValue(
							RSVariablePool.PARAMETER_HTTP_SESSION, types[idx]);
					idx++;
				} else if (at.equals(RSParamHttpServletRequest.class)) {

					args[idx] = pool.getValue(
							RSVariablePool.PARAMETER_HTTP_REQUEST, types[idx]);
					idx++;
				} else if (at.equals(RSParamHttpServletResponse.class)) {

					args[idx] = pool.getValue(
							RSVariablePool.PARAMETER_HTTP_RESPONSE, types[idx]);
					idx++;
				}
			}
		}
		return args;
	}

}
