/**
 * 
 */
package com.bloomlife.android.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.text.format.DateFormat;
import android.text.format.Time;

/**
 * @author <a href="mailto:xiai.fei@gmail.com">xiai_fei</a>  
 *
 * @date 2013-11-20  上午10:42:25
 */
public class Utils {

	public static boolean isCollectionEmpty(Collection<?> collection){
		return collection==null?true:collection.size()==0;
	}
	
	public static boolean isEvenRow(int position){
		int temp = position/2;
		if(temp==0) return true ;
		if(temp==1) return false ;
		return temp%2==0;
	}
	
	
	public static String numberShower(float num){
		if(num<10000) return String.valueOf((int)num);
		if(num<10000*100) {
			if(num>999500){
				return "100万";
			}
			if(num%10000==0){
				return String.valueOf((int)num/10000)+"万";
			}else{
				
				String result = String.format("%.1f", num/10000);
				return result+"万";
			}
		}
		if(num>10000*100){
			int result = (int) (num /10000);
			return String.valueOf(result);
		}
		return String.valueOf(num);
	}

	
	public static boolean isZH(){
		return Locale.getDefault().getLanguage().equals(Locale.CHINESE.getLanguage());
	}

	
	public static boolean isEmptyCollection(Collection<?> collection){
		return collection==null?true : collection.isEmpty();
	}
	
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	
	/**
	 * 生成N位随机码
	 * @return
	 */
	public static String getRandcode(int bit){
		int[] array = {0,1,2,3,4,5,6,7,8,9};
		Random rand = new Random();
		for (int i = 10; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = array[index];
			array[index] = array[i - 1];
			array[i - 1] = tmp;
		}
		String result = "";
		for(int i = 0; i < bit; i++){
			result = result + array[i];
		}
		return result;
	}
}
