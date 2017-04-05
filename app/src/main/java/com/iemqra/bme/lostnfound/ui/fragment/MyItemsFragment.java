package com.iemqra.bme.lostnfound.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.api.SQLiteApi;
import com.iemqra.bme.lostnfound.event.BaseEvent;
import com.iemqra.bme.lostnfound.event.ItemsEvent;
import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.model.APIHelper.FilterData;
import com.iemqra.bme.lostnfound.model.Item;
import com.iemqra.bme.lostnfound.ui.activity.MainActivity;
import com.iemqra.bme.lostnfound.ui.adapter.ItemsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MyItemsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = com.iemqra.bme.lostnfound.ui.activity.MainActivity.class.getSimpleName();

    @BindView(R.id.fragment_items_empty)
    TextView tvEmpty;
    @BindView(R.id.fragment_items_list_view)
    RecyclerView recyclerViewItems;
    @BindView(R.id.fragment_items_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private SQLiteApi localDbApi;
    private SQLiteHandler db;
    private ItemsAdapter itemsAdapter;
    private List<Item> itemsList;
    private boolean loading = true;
    private int loadingQuantity = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_items, container, false);
        ButterKnife.bind(this, view);

        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewItems.setLayoutManager(llm);

        itemsList = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(getContext(), itemsList);
        recyclerViewItems.setAdapter(itemsAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        localDbApi = new SQLiteApi(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        recyclerViewItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisibleItems = llm.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            getItemsByUser(totalItemCount, loadingQuantity, DataManager.INSTANCE.getUser().get("uid"));
                        }
                    }
                }
            }
        });

        getItemsByUser(0, loadingQuantity, DataManager.INSTANCE.getUser().get("uid"));

        return view;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        if (((MainActivity) getActivity()).checkInternetConnection()) {
            getItemsByUser(0, loadingQuantity, DataManager.INSTANCE.getUser().get("uid"));
        } else {
            getItemsByUserFromSQLite(DataManager.INSTANCE.getUser().get("uid"));
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void getItemsByUserFromSQLite(final String user_uid) {
        List<Item> itemsFromLocalDb = localDbApi.getItemsByUserFromSQLite(user_uid);
        if (itemsFromLocalDb != null && itemsFromLocalDb.size() > 0) {
            recyclerViewItems.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);
            itemsList.clear();
            itemsList.addAll(itemsFromLocalDb);
            itemsAdapter.notifyDataSetChanged();
        } else {
            recyclerViewItems.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void getItemsByUser(int startIndex, int queryAmount, final String user_uid) {
        ApiClient.getItems(new FilterData(startIndex, queryAmount, user_uid));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final ItemsEvent event) {
        if (event.getThrowable() != null) {
            getItemsByUserFromSQLite(DataManager.INSTANCE.getUser().get("uid"));
            event.getThrowable().printStackTrace();
            Toast.makeText(getApplicationContext(), "No internet connection, data loaded from local database!", Toast.LENGTH_SHORT).show();
        } else if (event.getMessage().equals("myItems") || event.getMessage().equals("myItemsMore")) {
            if (event.getMessage().equals("myItems")) {
                itemsList.clear();
            } else {
                loading = true;
            }
            if (event.getItems() != null) {
                List<Item> items = event.getItems();
                itemsList.addAll(items);
                itemsAdapter.notifyDataSetChanged();

                if (itemsList.isEmpty()) {
                    recyclerViewItems.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    recyclerViewItems.setVisibility(View.VISIBLE);
                    tvEmpty.setVisibility(View.GONE);
                }

                for (Item item : items) {
                    db.addItem(item.getPlaceUid(), item.getImageId().toString(), item.getUserUid(), item.getTitle(), item.getDescription(), item.getCreatedAt());
                }
            } else {
                Toast.makeText(getApplicationContext(), event.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final BaseEvent event) {
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace();
        } else if (event.getMessage().equals("removePlace")) {
            getItemsByUser(0, loadingQuantity, DataManager.INSTANCE.getUser().get("uid"));
        }
    }
}