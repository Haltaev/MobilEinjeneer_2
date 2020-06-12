package ru.mobilengineer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_warehouse.view.*
import ru.mobilengineer.OnNameEngineerItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.data.api.model.response.AnotherEngineersResponse

class TransferToEngineerAdapter (
    var items: ArrayList<AnotherEngineersResponse>,
    val context: Context,
    val callback: OnNameEngineerItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.apply {
            warehouse_title.text = items[position].name
            warehouse_title.setOnClickListener {
                callback.onNameEngineerItemClicked(items[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.item_warehouse,
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