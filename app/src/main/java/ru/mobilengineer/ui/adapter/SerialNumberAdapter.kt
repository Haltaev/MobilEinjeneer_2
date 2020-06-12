package ru.mobilengineer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_serial_number.view.*
import ru.mobilengineer.R
import ru.mobilengineer.data.api.model.SerialNumber
import ru.mobilengineer.data.api.model.response.IncommingSkuFromAnyResponse
import ru.mobilengineer.ui.activity.ScannerActivity

class SerialNumberAdapter(
    var items: ArrayList<SerialNumber>,
    var warehouse: IncommingSkuFromAnyResponse,
    val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var positionPickedItem: Int? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val serialNumber = items[position]

        holder.itemView.apply {

            number.text = serialNumber.title
            count.visibility = View.GONE


            if (serialNumber.isEnabled) {
                buttons_group.visibility = View.VISIBLE
            } else {
                buttons_group.visibility = View.GONE
            }

            serial_number_item.setOnClickListener {
                if (position != positionPickedItem)
                    positionPickedItem?.let {
                        items[it].isEnabled = false
                    }
                serialNumber.isEnabled = !serialNumber.isEnabled
                positionPickedItem = position
                notifyItemChanged(position)
            }



            button_return_to_warehouse.setOnClickListener {
                ScannerActivity.open(
                    context,
                    key = MyStockAdapter.RETURN_TO_WAREHOUSE,
                    items = Gson().toJson(arrayListOf(items[position]))
                )
            }

            button_install_in_device.setOnClickListener {
                ScannerActivity.open(
                    context,
                    key = MyStockAdapter.INSTALL_IN_DEVICE,
                    items = Gson().toJson(arrayListOf(items[position]))
                )
            }

            button_take_away.setOnClickListener {
                ScannerActivity.open(
                    context,
                    key = MyStockAdapter.TRANSFER_TO_ENGINEER,
                    items = Gson().toJson(arrayListOf(items[position]))
                )
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_serial_number,
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