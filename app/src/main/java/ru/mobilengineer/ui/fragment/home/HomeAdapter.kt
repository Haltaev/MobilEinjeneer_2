package ru.mobilengineer.ui.fragment.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product.view.*
import ru.mobilengineer.OnHomeItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.data.api.model.Product


class HomeAdapter(
    var items: ArrayList<Product>,
    val context: Context,
    val callback: OnHomeItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var positionPickedItem: Int? = null
    var isAnyItemPicked: Boolean = false
    var countPickedItems: Int = 0
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val product = items[position]

        holder.itemView.apply {

            name.text = product.title
            art.text = "Арт: ${product.articul}"
            count.text = "${product.count} шт"

            if (product.inStock) {
                is_in_stock.text = IN_STOCK
                is_in_stock.setTextColor(ContextCompat.getColor(context, R.color.colorMain))
            } else {
                is_in_stock.text = TRANSFER
                is_in_stock.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorButtonNegative
                    )
                )
            }
            if (isAnyItemPicked) {
                check.isActivated = product.isPicked
                check.visibility = View.VISIBLE
                product_root.setOnClickListener {
                    product.isPicked = !product.isPicked
                    if (product.isPicked) callback.onHomeItemClicked(++countPickedItems)
                    else callback.onHomeItemClicked(--countPickedItems)
                    if (countPickedItems == 0) {
                        isAnyItemPicked = false
                        callback.onHomeItemLongClicked(isAnyItemPicked)
                        notifyDataSetChanged()
                    }
                    notifyItemChanged(position, product)
                }

                product_root.setOnLongClickListener {
                    true
                }
            } else {
                check.visibility = View.GONE
                product_root.setOnClickListener {
                    if (position != positionPickedItem)
                        if (positionPickedItem != null) {
                            items[positionPickedItem!!].isEnabled = false
                            notifyItemChanged(positionPickedItem!!, product)
                            positionPickedItem = position
                        }
                    product.isEnabled = !product.isEnabled
                    notifyItemChanged(position, product)
                    positionPickedItem = position
                }

                product_root.setOnLongClickListener {
                    isAnyItemPicked = true
                    product.isPicked = !product.isPicked
                    callback.onHomeItemLongClicked(isAnyItemPicked)
                    if (positionPickedItem != null)
                        items[positionPickedItem!!].isEnabled = false
                    callback.onHomeItemClicked(++countPickedItems)
                    notifyDataSetChanged()
                    true
                }
            }

            if (product.isEnabled) {
                if (product.inStock) {
                    positive_buttons_group.visibility = View.VISIBLE
                    button_not_transfer.visibility = View.GONE
                } else {
                    positive_buttons_group.visibility = View.GONE
                    button_not_transfer.visibility = View.VISIBLE
                }
            } else {
                positive_buttons_group.visibility = View.GONE
                button_not_transfer.visibility = View.GONE
            }
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
//        return 20
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view = view
    }

    companion object {
        const val IN_STOCK = "В наличии"
        const val TRANSFER = "Передача"
    }

}
