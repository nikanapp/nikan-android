/**
 * 
 */
package com.bloomlife.android.data;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2014-6-29  下午4:22:23
 */
public interface DataLoader {

	boolean needLoad();
	
	void loadData(Object... args);
	
	void onSaveStateData();
}
