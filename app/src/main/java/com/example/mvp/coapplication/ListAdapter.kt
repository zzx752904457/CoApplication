package com.example.mvp.coapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvp.coapplication.bean.Article
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_list.*

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private var list = mutableListOf<Article>()

    override fun getItemCount(): Int {
        return list.size
    }

    fun refreshDataList(dataList: List<Article>) {
        list.clear()
        list.addAll(dataList)
        notifyDataSetChanged()
    }

    fun addDataList(dataList: List<Article>) {
        list.addAll(dataList)
        notifyItemRangeInserted(list.size - dataList.size, dataList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {
        lateinit var article: Article

        fun bindData(article: Article) {
            this.article = article
            tv_title.text = article.title
        }
    }
}