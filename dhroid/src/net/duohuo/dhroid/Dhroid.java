package net.duohuo.dhroid;

import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;
import net.duohuo.dhroid.ioc.IocContainer;
/**
 * 完成一些系统的初始化的工作
 * @author Administrator
 *
 */
public class Dhroid {
	public void init(){
		//对话框的配置
		IocContainer.getShare().bind(DialogImpl.class).to(IDialog.class).scope(InstanceScope.SCOPE_PROTOTYPE);
	}
}
