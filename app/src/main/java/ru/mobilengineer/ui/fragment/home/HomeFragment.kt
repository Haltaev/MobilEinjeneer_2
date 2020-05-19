package ru.mobilengineer.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import ru.mobilengineer.App
import ru.mobilengineer.OnHomeItemClickListener
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.data.api.model.Product
import ru.mobilengineer.ui.activity.MyProfileActivity
import javax.inject.Inject

class HomeFragment : Fragment(), OnHomeItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    @Inject
    lateinit var preferencesManager: PreferencesManager

    private var adapter: HomeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arrayItems = arrayListOf<Product>(
            Product(
                id = "1",
                title = "Картридж Kyocera TK1140 FS-1035/1135 7.2K SuperFine",
                articul = "TK1140",
                count = "3",
                inStock = true
            ),
            Product(
                id = "2",
                title = "Картридж Kyocera TK1130 FS-1030/1130 3K SuperFine",
                articul = "TK1130",
                count = "8",
                inStock = true
            ),
            Product(
                id = "3",
                title = "Картридж Kyocera TK3100 M-3040/3540/FS-2100 12.5K SuperFine",
                articul = "50F5U0E",
                count = "1",
                inStock = false
            ),
            Product(
                id = "4",
                title = "Тонер-картридж Hi-Black (HB-TK-340) черный для Kyocera-Mita FS-2020D (12K",
                articul = "TK1140",
                count = "2",
                inStock = true
            ),
            Product(
                id = "5",
                title = "Картридж Lexmark 605HE черный высокой емкости для MX310/410/510/511/611",
                articul = "60F5H0E",
                count = "1",
                inStock = true
            ),
            Product(
                id = "6",
                title = "Картридж Lexmark 505UE черный ультравысокой емкости для MS510/610",
                articul = "50F5U0E",
                count = "2",
                inStock = true
            ),
            Product(
                id = "7",
                title = "Картридж Lexmark 505HE черный высокой ёмкости для MS310/MS410/MS510/MS610",
                articul = "TK3100",
                count = "1",
                inStock = true
            ),
            Product(
                id = "8",
                title = "Картридж Kyocera TK1140 FS-1035/1135 7.2K SuperFine",
                articul = "TK1140",
                count = "3",
                inStock = true
            ),
            Product(
                id = "9",
                title = "Картридж Kyocera TK1130 FS-1030/1130 3K SuperFine",
                articul = "TK1130",
                count = "8",
                inStock = false
            ),
            Product(
                id = "10",
                title = "Тонер-картридж Hi-Black (HB-TK-340) черный для Kyocera-Mita FS-2020D (12K",
                articul = "TK1140",
                count = "2",
                inStock = true
            )
        )

        context?.let { ctx ->
            adapter = HomeAdapter(arrayItems, ctx, this)
            home_recycler_view.layoutManager = LinearLayoutManager(ctx)
            home_recycler_view.adapter = adapter
        }

        button_cancel_select.setOnClickListener {
            context?.let { ctx ->
                adapter = HomeAdapter(arrayItems, ctx, this)
                home_recycler_view.layoutManager = LinearLayoutManager(ctx)
                home_recycler_view.adapter = adapter
            }
            group_selected.visibility = View.GONE
        }
        profile_button.setOnClickListener {
            MyProfileActivity.open(requireContext())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onHomeItemClicked(count: Int?) {
//        count_selected.text = "$count шт"
        count_selected.text = getString(R.string._count, count.toString())
    }

    override fun onHomeItemLongClicked(isAnyItemPicked: Boolean) {
        if(isAnyItemPicked) group_selected.visibility = View.VISIBLE
        else group_selected.visibility = View.GONE
    }

}