/**
 * 
 */
package com.bloomlife.android.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bloomlife.android.bean.ProcessResult;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-22  下午10:13:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME) 
@Documented
public @interface JsonResult {
	Class<? extends ProcessResult> value();
}
