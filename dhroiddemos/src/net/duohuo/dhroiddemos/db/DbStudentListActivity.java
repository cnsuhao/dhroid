package net.duohuo.dhroiddemos.db;

import java.util.List;

import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.adapter.BeanAdapter;
import net.duohuo.dhroid.db.DhDB;
import net.duohuo.dhroid.dialog.DialogCallBack;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.util.ViewUtil;
import net.duohuo.dhroiddemos.R;
import net.duohuo.dhroiddemos.db.bean.Student;
import android.content.Intent;
import android.content.ClipData.Item;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class DbStudentListActivity extends BaseActivity{

	@InjectView(id=R.id.listView,itemClick="toEditStudent",itemLongClick="toDeleteStudent")
	ListView listView;
	@InjectView(id=R.id.search_content)
	EditText contentV;
	@InjectView(id=R.id.to_search,click="onSearch")
	View searchV;
	@InjectView(id=R.id.to_create,click="toCreate")
	View toCreateV;
	@Inject
	DhDB db;
	BeanAdapter<Student> beanAdapter;
	@Inject
	IDialog dialoger;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_list_activity);
		beanAdapter=new BeanAdapter<Student>(this,R.layout.db_item_for_list) {
			@Override
			public void bindView(View itemV, int position, Student jo) {
				ViewUtil.bindView(itemV.findViewById(R.id.name), jo.getName());
				ViewUtil.bindView(itemV.findViewById(R.id.num), "学号:"+jo.getNum());
				ViewUtil.bindView(itemV.findViewById(R.id.sex), jo.getSex(),"sex");
			}
		};
	
	
		listView.setAdapter(beanAdapter);
	}
	public void toCreate(View v) {
		Intent it=new Intent(this,DbStudentSetActivity.class);
		startActivity(it);
	}
	
	public void toEditStudent(AdapterView<?> parent, View view, int position, long id) {
		Student student=beanAdapter.getTItem(position);
		Intent it=new Intent(this,DbStudentSetActivity.class);
		it.putExtra("id", student.getId());
		startActivity(it);
	}
	/**
	 * 
	 * @param v
	 */
	public void onSearch(View v) {
		String key=	"%"+contentV.getText().toString().trim()+"%";
		List<Student> list=db.queryList(Student.class, ":name like ? or :num like ?", key,key);
		beanAdapter.clear();
		beanAdapter.addAll(list);
		beanAdapter.notifyDataSetChanged();
	}
	/**
	 *删除操作 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	public void toDeleteStudent(AdapterView<?> parent, View view, final int position, long id) {
		//这边需要减去headView 的数量headview的数量请勿写固定值防止变化
		dialoger.showDialog(this, "提示", "你确定删除这条信息吗", new DialogCallBack() {
			@Override
			public void onClick(int what) {
				if(what==IDialog.YES){
					Student student=beanAdapter.getTItem(position-listView.getHeaderViewsCount());
					db.delete(student);
					beanAdapter.remove(position-listView.getHeaderViewsCount());
				}else{
					dialoger.showToastShort(DbStudentListActivity.this, "你取消了");
				}
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		List<Student> students=db.queryAll(Student.class);
		beanAdapter.clear();
		beanAdapter.addAll(students);
	}
	
}
