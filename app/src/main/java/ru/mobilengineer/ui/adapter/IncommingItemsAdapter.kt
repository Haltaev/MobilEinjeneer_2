package ru.mobilengineer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product.view.*
import ru.mobilengineer.R
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse


class IncommingItemsAdapter(
    var items: ArrayList<IncommingSkuFromAnyResponse>,
    val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val warehouse = items[position]

        holder.itemView.apply {

            name.text = warehouse.name
            art.text = "Арт: ${warehouse.vendorCode}"
            count.text = "${warehouse.quantity} шт"


            buttons_group.visibility = View.GONE
            button_not_transfer.visibility = View.GONE
            is_in_stock.visibility =View.GONE
            check.visibility =View.GONE

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_product,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view = view
    }

}
