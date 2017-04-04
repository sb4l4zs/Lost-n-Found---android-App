package com.iemqra.bme.lostnfound.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.api.SQLiteApi;
import com.iemqra.bme.lostnfound.event.ItemsEvent;
import com.iemqra.bme.lostnfound.helper.SQLiteHandler;
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
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.fragment_search_list_view)
    RecyclerView recyclerViewItems;
    @BindView(R.id.fragment_search_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fragment_search_empty)
    TextView tvEmpty;
    @BindView(R.id.fragment_search_field)
    EditText searchText;
    @BindView(R.id.fragment_search_spinner)
    Spinner searchSpinner;
    FilterData searchCondition;
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private SQLiteApi localDbApi;
    private SQLiteHandler db;
    private ItemsAdapter itemsAdapter;
    private List<Item> itemsList;
    private boolean loading = true;
    private int loadingQuantity = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        localDbApi = new SQLiteApi(getApplicationContext());
        db = new SQLiteHandler(getActivity().getApplicationContext());

        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewItems.setLayoutManager(llm);

        itemsList = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(getContext(), itemsList);
        recyclerViewItems.setAdapter(itemsAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.search_filter_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
        searchSpinner.setAdapter(spinnerAdapter);

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
                            searchCondition.setStartIndex(totalItemCount);
                            searchItems(searchCondition);
                        }
                    }
                }
            }
        });

        return view;
    }

    @OnClick(R.id.fragment_search_btn_search)
    public void handleOnSearchButtonClick() {
        ((MainActivity) getActivity()).hideSoftKeyboard();
        String searchFor = searchText.getText().toString().trim();

        // TODO, field checks...
        if (!searchFor.isEmpty()) {
            if (((MainActivity) getActivity()).checkInternetConnection()) {
                switch ((int) searchSpinner.getSelectedItemId()) {
                    case 0:                        //title
                        searchCondition = new FilterData(searchFor, "", "", "", 0, loadingQuantity);
                        searchItems(searchCondition);
                        break;
                    case 1:                        //desc
                        searchCondition = new FilterData("", searchFor, "", "", 0, loadingQuantity);
                        searchItems(searchCondition);
                        break;
                    case 2:                        //place
                        searchCondition = new FilterData("", "", searchFor, "", 0, loadingQuantity);
                        searchItems(searchCondition);
                        break;
                    default:
                        break;
                }
            } else {
                searchItemsFromSQLite();
                Toast.makeText(getActivity().getApplicationContext(), "Searching in local database!", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getActivity().getApplicationContext(), "Type something you are looking for!", Toast.LENGTH_SHORT).show();
    }


    private void searchItemsFromSQLite() {
        if (!searchText.getText().toString().isEmpty()) {
            List<Item> itemsFromLocalDb = localDbApi.searchItemsFromSQLite(searchText.getText().toString().trim());
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
        } else {
            recyclerViewItems.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void searchItems(FilterData filterData) {
        ApiClient.getItems(filterData);
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
            searchItemsFromSQLite();
            event.getThrowable().printStackTrace();
            Toast.makeText(getApplicationContext(), "No internet connection, data loaded from local database!", Toast.LENGTH_SHORT).show();
        } else if (event.getMessage().equals("search") || event.getMessage().equals("searchMore")) {
            if (event.getMessage().equals("search")) {
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }
}