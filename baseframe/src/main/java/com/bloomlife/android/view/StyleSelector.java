/**
 * 
 */
package com.bloomlife.android.view;

/**
 * 
 * 		提供一个方法，当关联了对应的样式文件之后，提供一个方法，通过参数决定样式
 * 
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-7-6  下午1:14:44
 */
public interface StyleSelector {

	
	/**
	 * @param args  一组样式文件
	 */
	void setResources(Integer... args);
	
	
	/**
	 * 
	 * @param resSeq  对应的样式资源文件的顺序
	 */
	void changeStyle(int resSeq);
}
