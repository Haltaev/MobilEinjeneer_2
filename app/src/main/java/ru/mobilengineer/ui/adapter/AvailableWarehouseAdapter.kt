package ru.mobilengineer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_available_warehouse.view.*
import ru.mobilengineer.OnAvailableWarehouseClickListener
import ru.mobilengineer.OnHomeItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.data.api.model.SerialNumber
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.ui.activity.ScannerActivity


class AvailableWarehouseAdapter(
    var items: ArrayList<IncommingSkuFromAnyResponse>,
    val context: Context,
    val callback: OnAvailableWarehouseClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var positionPickedItem: Int = -1
    var isAnyItemPicked: Boolean = false
    var countPickedItems: Int = 0
    private var adapter: SerialNumberAdapter? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val warehouse = items[position]

        holder.itemView.apply {

            if (position == 0) {
                top_line.visibility = View.GONE
            }

            name.text = warehouse.name
            art.text = "Арт: ${warehouse.vendorCode}"
            count.text = "${warehouse.quantity} шт"
            is_in_stock.visibility = View.GONE

            if (warehouse.isEnabled) {
                if (callback.isConsiderSerialNumber() && !warehouse.serialNumber.isNullOrEmpty()) {
                    buttons_group.visibility = View.GONE
                    warehouse.serialNumber?.let {
                        warehouse.serialNumbers = arrayListOf(SerialNumber(it))
                    }
                    context?.let { ctx ->
                        adapter = SerialNumberAdapter(
                            warehouse.serialNumbers,
                            warehouse,
                            ctx
                        )
                        serial_number_recycler_view.layoutManager = LinearLayoutManager(ctx)
                        serial_number_recycler_view.adapter = adapter
                    }
                } else {
                    buttons_group.visibility = View.VISIBLE
                }
            } else {
                if (callback.isConsiderSerialNumber()) serial_number_recycler_view.adapter = null
                buttons_group.visibility = View.GONE
            }



            if (isAnyItemPicked) {
                check.isActivated = warehouse.isPicked
                check.visibility = View.VISIBLE
                product_root.setOnClickListener {
                    warehouse.isPicked = !warehouse.isPicked
                    if (warehouse.isPicked) {
                        callback.onHomeItemClicked(warehouse.quantity, position, true)
                        countPickedItems++
                    }
                    else {
                        callback.onHomeItemClicked(warehouse.quantity, position, false)
                        countPickedItems--
                    }
                    if (countPickedItems == 0) {
                        isAnyItemPicked = false
                        callback.onHomeItemLongClicked(isAnyItemPicked, position)
                        notifyDataSetChanged()
                    }
                    notifyItemChanged(position, warehouse)
                }
                product_root.setOnLongClickListener {
                    false
                }
            } else {
                check.visibility = View.GONE
                product_root.setOnClickListener {
                    if (position != positionPickedItem)
                        if (positionPickedItem != -1) {
                            items[positionPickedItem].isEnabled = false
                            notifyItemChanged(positionPickedItem, warehouse)
//                                positionPickedItem = position
                        }
                    warehouse.isEnabled = !warehouse.isEnabled
                    notifyItemChanged(position, warehouse)
                    positionPickedItem = position

                }
                product_root.setOnLongClickListener {
                    isAnyItemPicked = true
                    warehouse.isPicked = !warehouse.isPicked
                    callback.onHomeItemLongClicked(isAnyItemPicked, position)
                    if (positionPickedItem != -1) {
                        items[positionPickedItem].isEnabled = false
                    }
                    callback.onHomeItemClicked(warehouse.quantity, position, true)
                    countPickedItems++
                    notifyDataSetChanged()
                    true
                }

            }

            button_refuse.setOnClickListener {
                callback.onItemRefuseClicked(warehouse, position)
            }

            button_receive.setOnClickListener {
                callback.onItemReceiveClicked(warehouse, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_available_warehouse,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
//        return 20
    }

    companion object {
        const val IN_STOCK = "В наличии"
        const val TRANSFER = "Передача"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view = view
    }

}
