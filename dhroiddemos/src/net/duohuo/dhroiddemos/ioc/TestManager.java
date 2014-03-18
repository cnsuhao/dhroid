package net.duohuo.dhroiddemos.ioc;

import android.util.Log;
import net.duohuo.dhroid.ioc.InjectFields;
import net.duohuo.dhroid.ioc.annotation.Inject;

public class TestManager  implements InjectFields{
	String name;

	@Inject
	public	TestDateHelper helper;
	
	/* (non-Javadoc)
	 * @see net.duohuo.dhroid.ioc.InjectFields#injected()
	 */
	@Override
	public void injected() {
		if(	helper.manager!=null){
			Log.v("DH-INFO", "helper.manager!=null");
		}
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
}
