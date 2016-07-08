/**  
 * <p><b>Description:</b>  </p>  
 * <p><b>Title:</b> MResource.java </p>
 * <p><b>Package</b> com.ningfeifei.cameraapplication</p> 
 * @author NingFeifei
 * <p><b>date</b> 2016-7-8 下午3:44:39</p> 
 * @version v1.0.0
 */
package com.ningfeifei.cameraapplication;

import android.content.Context;

/** 
 * <p><b>Description:</b>  </p>
 * <p><b>ClassName:</b> MResource</p> 
 * @author NingFeifei
 * <p><b>date</b> 2016-7-8 下午3:44:39 </p> 
 */

public class MResource {
	/** 
	 * <p><b>Description:</b>根据资源的名字获取其ID值 </p>
	 * <p><b>Title:</b> getIdByName </p>
	 * @param context
	 * @param className
	 * @param name
	 * @return  
	 */
	public static int getIdByName(Context context, String className, String name) {
		String packageName = context.getPackageName();
		Class r = null;
		int id = 0;
		try {
			r = Class.forName(packageName + ".R");

			Class[] classes = r.getClasses();
			Class desireClass = null;

			for (int i = 0; i < classes.length; ++i) {
				if (classes[i].getName().split("\\$")[1].equals(className)) {
					desireClass = classes[i];
					break;
				}
			}

			if (desireClass != null)
				id = desireClass.getField(name).getInt(desireClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return id;
	}

	/**
	 * <p>
	 * <b>Description:</b>当我们的资源Id是一个数组的时候，我们要用下面的方法
	 * </p>
	 * <p>
	 * <b>Title:</b> getIdsByName
	 * </p>
	 * 
	 * @param context
	 * @param className
	 * @param name
	 * @return
	 */
	public static int[] getIdsByName(Context context, String className,
			String name) {
		String packageName = context.getPackageName();
		Class r = null;
		int[] ids = null;
		try {
			r = Class.forName(packageName + ".R");

			Class[] classes = r.getClasses();
			Class desireClass = null;

			for (int i = 0; i < classes.length; ++i) {
				if (classes[i].getName().split("\\$")[1].equals(className)) {
					desireClass = classes[i];
					break;
				}
			}

			if ((desireClass != null)
					&& (desireClass.getField(name).get(desireClass) != null)
					&& (desireClass.getField(name).get(desireClass).getClass()
							.isArray()))
				ids = (int[]) desireClass.getField(name).get(desireClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return ids;
	}
}
