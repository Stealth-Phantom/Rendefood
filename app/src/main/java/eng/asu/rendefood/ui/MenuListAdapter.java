package eng.asu.rendefood.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eng.asu.rendefood.Model.MenuItem;
import eng.asu.rendefood.R;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuViewHolder> {
    private final LayoutInflater mInflater;
    private List<MenuItem> menu; // Cached copy of the menu
    Intent intent;

    MenuListAdapter(Context context) { mInflater = LayoutInflater.from(context); }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.menu_recyclerview_item, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        if (menu != null) { //Set data of current menuItem in the UI
            MenuItem current = menu.get(position);
            holder.itemNameText.setText(current.getItemName());
            holder.itemDescriptionText.setText(current.getDescription());
            holder.itemPriceText.setText(Float.toString(current.getPrice()) + " L.E.");
        } else {
            // Covers the case of data not being ready yet.
            holder.itemNameText.setText("Wrong");
            holder.itemDescriptionText.setText("Who Cares");
            holder.itemPriceText.setText("Free");
        }
    }

    void setMenu(List<MenuItem> menuItems){
        menu = menuItems;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (menu != null)
            return menu.size();
        else return 0;
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {//Should have all fields of the recycler view item (Should be final)
        private final TextView itemNameText;
        private final TextView itemDescriptionText;
        private final TextView itemPriceText;
        private MenuViewHolder(View itemView) { //Link variables to their respective fields
            super(itemView);
            itemNameText = itemView.findViewById(R.id.menuItemName);
            itemDescriptionText = itemView.findViewById(R.id.menuItemDescription);
            itemPriceText = itemView.findViewById(R.id.menuItemPrice);
        }
    }
}
