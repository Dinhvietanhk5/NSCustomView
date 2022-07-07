package com.newsoft.nscustomview.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newsoft.nscustomview.R
import com.newsoft.nscustomview.model.UserSuitableModel
import com.newsoft.nscustomview.recyclerview.BaseAdapter
import kotlinx.android.synthetic.main.item_text.view.*


class Adapter : BaseAdapter<UserSuitableModel.Item?, Adapter.AdapterHolder>() {


    fun setItems(items: List<UserSuitableModel.Item?>?, index: Int) {
        super.setItemsBase(items as java.util.ArrayList<UserSuitableModel.Item?>?, index)
    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): AdapterHolder {
        return AdapterHolder(setView(R.layout.item_text))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindView(
        holder: AdapterHolder?,
        item: UserSuitableModel.Item?,
        position: Int,
        size: Int
    ) {
        item?.let {
            holder!!.tv.text = it.name
        }
    }

    class AdapterHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val tv = mView.tv
    }


}