package cz.brno.holan.jiri.hunggarkuenfinancials.frontend.listeners;

import android.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import cz.brno.holan.jiri.hunggarkuenfinancials.backend.entities.members.Member;
import cz.brno.holan.jiri.hunggarkuenfinancials.frontend.fragments.MemberDetailDialog;

/**
 * Created by mhatina on 29/09/16.
 */
public class MemberListOnItemClickListener implements AdapterView.OnItemClickListener {

    ListView memberList;
    FragmentManager fragmentManager;

    public MemberListOnItemClickListener(ListView memberList, FragmentManager fragmentManager) {
        this.memberList = memberList;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MemberDetailDialog newFragment = new MemberDetailDialog();
        ListView member_list = memberList;

        if (member_list == null)
            return;

        newFragment.init((Member) member_list.getItemAtPosition(position));
        newFragment.show(fragmentManager, "member_detail");
    }
}
