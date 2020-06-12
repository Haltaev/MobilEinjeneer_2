package ru.mobilengineer.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_product.view.*
import ru.mobilengineer.OnHomeItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.data.api.model.SerialNumber
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.data.api.model.response.MyStockResponse
import ru.mobilengineer.ui.activity.ScannerActivity


class MyStockAdapter(
    var items: ArrayList<MyStockResponse>,
    val context: Context,
    val callback: OnHomeItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var positionPickedItem: Int = -1
    var isAnyItemPicked: Boolean = false
    var countPickedItems: Int = 0

    private var adapter: SerialNumberAdapter? = null


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val warehouse = items[position]
        val itemOfMyStock = IncommingSkuFromAnyResponse()

        holder.itemView.apply {
            itemOfMyStock.apply {
                warehouseId = warehouse.warehouseId
                targetWarehouseId = warehouse.targetWarehouseId
                movingId = warehouse.movingId
                quantity = warehouse.quantity
                skuId = warehouse.skuId
                serialNumber = warehouse.serialNumber
                received = warehouse.received
                name = warehouse.name
                vendorCode = warehouse.vendorCode
                isPicked= warehouse.isPicked
                isEnabled= warehouse.isEnabled
                serialNumbers = warehouse.serialNumbers
            }
            if (position == 0) {
                top_line.visibility = View.GONE
            }

            name.text = warehouse.name
            art.text = "Арт: ${warehouse.vendorCode}"
            count.text = "${warehouse.quantity} шт"

            if (warehouse.movingId == null) {
                is_in_stock.text =
                    IN_STOCK
                is_in_stock.setTextColor(ContextCompat.getColor(context, R.color.colorMain))
            } else {
                is_in_stock.text =
                    TRANSFER
                is_in_stock.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorButtonNegative
                    )
                )
            }


            if (warehouse.isEnabled) {
                if (callback.isConsiderSerialNumber() && !warehouse.serialNumber.isNullOrEmpty()) {
                    buttons_group.visibility = View.GONE
                    warehouse.serialNumber?.let {
                        warehouse.serialNumbers = arrayListOf(SerialNumber(it))
                    }
                    context?.let { ctx ->
                        adapter = SerialNumberAdapter(
                            warehouse.serialNumbers,
                            itemOfMyStock,
                            ctx
                        )
                        serial_number_recycler_view.layoutManager = LinearLayoutManager(ctx)
                        serial_number_recycler_view.adapter = adapter
                    }
                }
                else {
                    if (warehouse.movingId == null) {
                        buttons_group.visibility = View.VISIBLE
                        button_not_transfer.visibility = View.GONE
                    } else {
                        buttons_group.visibility = View.GONE
                        button_not_transfer.visibility = View.VISIBLE
                    }
                }
            } else {
                if (callback.isConsiderSerialNumber()) serial_number_recycler_view.adapter = null
                buttons_group.visibility = View.GONE
                button_not_transfer.visibility = View.GONE
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

            button_not_transfer.setOnClickListener {
                callback.onWarehouseItemClicked(itemOfMyStock, position)
            }

            button_return_to_warehouse.setOnClickListener {
                ScannerActivity.open(
                    context,
                    key = RETURN_TO_WAREHOUSE,
                    items = Gson().toJson(arrayListOf(items[position]))
                )
            }

            button_install_in_device.setOnClickListener {
                ScannerActivity.open(
                    context,
                    key = INSTALL_IN_DEVICE,
                    items = Gson().toJson(arrayListOf(items[position]))
                )
            }

            button_transfer_to_engineer.setOnClickListener {
                ScannerActivity.open(
                    context,
                    key = TRANSFER_TO_ENGINEER,
                    items = Gson().toJson(arrayListOf(items[position]))
                )
                Log.e("arrayItems", Gson().toJson(arrayListOf(items[position])))
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
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var view = view
    }

    companion object {
        const val IN_STOCK = "В наличии"
        const val TRANSFER = "Передача"

        const val RETURN_TO_WAREHOUSE = "RETURN_TO_WAREHOUSE"
        const val INSTALL_IN_DEVICE = "INSTALL_IN_DEVICE"
        const val TRANSFER_TO_ENGINEER = "TRANSFER_TO_ENGINEER"
    }

}
