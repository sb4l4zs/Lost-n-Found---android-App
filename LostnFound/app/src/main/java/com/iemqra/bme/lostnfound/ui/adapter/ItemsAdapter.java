package com.iemqra.bme.lostnfound.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iemqra.bme.lostnfound.R;
import com.iemqra.bme.lostnfound.api.ApiClient;
import com.iemqra.bme.lostnfound.helper.ItemHandler;
import com.iemqra.bme.lostnfound.manager.DataManager;
import com.iemqra.bme.lostnfound.model.APIHelper.RemoveItemData;
import com.iemqra.bme.lostnfound.model.Item;
import com.iemqra.bme.lostnfound.ui.activity.DetailedActivity;
import com.iemqra.bme.lostnfound.ui.activity.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemsList;

    public ItemsAdapter(Context context, List<Item> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_item, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = itemsList.get(position);
        if (!item.getImagePath().isEmpty()) {
            Picasso.with(context).load(ApiClient.API_URL + item.getImagePath().substring(1)).into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.no_picture);
        }
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("item", new ItemHandler(item));
                Intent myIntent = new Intent(context, DetailedActivity.class);
                myIntent.putExtras(bundle);
                context.startActivity(myIntent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteItem(item.getId());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    private void deleteItem(final int itemId) {
        new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (((MainActivity) context).checkInternetConnection()) {
                            ApiClient.removeItem(new RemoveItemData(DataManager.INSTANCE.getUser().get("uid"), itemId));
                        } else {
                            DataManager.INSTANCE.getItemsToDelete().add(new RemoveItemData(DataManager.INSTANCE.getUser().get("uid"), itemId));
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_image)
        ImageView ivImage;
        @BindView(R.id.item_title)
        TextView tvTitle;
        @BindView(R.id.item_description)
        TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
