package net.antoniy.gidder.ui.adapter;

import java.util.List;

import net.antoniy.gidder.db.entity.Permission;
import android.content.Context;

public class RepositoryPermissionListAdapter extends BasePermissionListAdapter {

	public RepositoryPermissionListAdapter(Context context, List<Permission> items, int resourceIconPull, int resourceIconPushPull) {
		super(context, items, resourceIconPull, resourceIconPushPull);
	}
	
	@Override
	protected String getItemName(int position) {
		return items.get(0).getRepository().getName();
	}

}