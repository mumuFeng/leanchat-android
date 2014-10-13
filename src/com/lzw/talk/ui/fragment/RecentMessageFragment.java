package com.lzw.talk.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lzw.talk.R;
import com.lzw.talk.adapter.RecentMessageAdapter;
import com.lzw.talk.entity.RecentMsg;
import com.lzw.talk.service.ChatService;
import com.lzw.talk.ui.activity.ChatActivity;
import com.lzw.talk.util.NetAsyncTask;
import com.lzw.talk.util.Utils;

import java.util.List;

/**
 * Created by lzw on 14-9-17.
 */
public class RecentMessageFragment extends BaseFragment implements AdapterView.OnItemClickListener {
  ListView listview;
  RecentMessageAdapter adapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.message_fragment, null);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    initView();
    refresh();
  }

  private void initView() {
    headerLayout.showTitle(R.string.messages);
    listview = (ListView) getView().findViewById(R.id.convList);
    adapter = new RecentMessageAdapter(getActivity());
    listview.setAdapter(adapter);
    listview.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
    // TODO Auto-generated method stub
    RecentMsg recent = (RecentMsg) adapter.getItem(position);
    if (recent.msg.isSingleChat()) {
      ChatActivity.goUserChat(ctx, recent.toUser.getObjectId());
    } else {
      ChatActivity.goGroupChat(ctx, recent.chatGroup.getObjectId());
    }
  }

  private boolean hidden;

  @Override
  public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    this.hidden = hidden;
    if (!hidden) {
      refresh();
    }
  }

  public void refresh() {
    new GetDataTask(ctx, false).execute();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!hidden) {
      refresh();
    }
  }

  class GetDataTask extends NetAsyncTask {
    List<RecentMsg> recentMsgs;

    GetDataTask(Context cxt, boolean openDialog) {
      super(cxt, openDialog);
    }

    @Override
    protected void doInBack() throws Exception {
      recentMsgs = ChatService.getRecentMsgsAndCache();
    }

    @Override
    protected void onPost(Exception e) {
      if (e != null) {
        Utils.toast(ctx, R.string.pleaseCheckNetwork);
      } else {
        adapter.setDatas(recentMsgs);
        adapter.notifyDataSetChanged();
      }
    }
  }
}
