package com.example.phattn.mapdirection.util

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.phattn.mapdirection.R

/**
 * The class provides a better and less tightly coupled way
 * to add an {@link OnClickListener} to a {@link RecyclerView}
 */
class ItemClickSupport private constructor(recyclerView: RecyclerView){

    private val mRecyclerView = recyclerView
    private var mOnItemClickListener : OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null

    private val mOnClickListener = View.OnClickListener { view ->
        if (mOnItemClickListener != null) {
            val holder = mRecyclerView.getChildViewHolder(view)
            mOnItemClickListener?.onItemClicked(mRecyclerView, holder.adapterPosition, view)
        }
    }

    private val mOnLongClickListener = object : View.OnLongClickListener {
        override fun onLongClick(view: View): Boolean {
            if (mOnItemLongClickListener != null) {
                val holder = mRecyclerView.getChildViewHolder(view)
                return mOnItemLongClickListener!!.onItemLongClicked(mRecyclerView,
                        holder.adapterPosition, view)
            }
            return false
        }
    }

    private val mAttachListener = object : RecyclerView.OnChildAttachStateChangeListener {
        override fun onChildViewAttachedToWindow(view: View) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener)
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener)
            }
        }

        override fun onChildViewDetachedFromWindow(view: View) {
            view.setOnClickListener(null)
            view.setOnLongClickListener(null)
        }
    }

    init {
        mRecyclerView.setTag(R.id.item_click_support, this)
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener)
    }

    companion object {
        fun addTo(recyclerView: RecyclerView) : ItemClickSupport {
            var clickSupport = recyclerView.getTag(R.id.item_click_support) as ItemClickSupport?
            if (clickSupport == null) clickSupport = ItemClickSupport(recyclerView)
            return clickSupport
        }

        fun removeFrom(recyclerView: RecyclerView) : ItemClickSupport? {
            val clickSupport = recyclerView.getTag(R.id.item_click_support) as ItemClickSupport?
            clickSupport?.detach(recyclerView)
            return clickSupport
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) : ItemClickSupport {
        mOnItemClickListener = listener
        return this
    }

    fun setOnLongItemClickListener(listener: OnItemLongClickListener) : ItemClickSupport {
        mOnItemLongClickListener = listener
        return this
    }

    private fun detach(recyclerView: RecyclerView) {
        mRecyclerView.removeOnChildAttachStateChangeListener(mAttachListener)
        recyclerView.setTag(R.id.item_click_support, null)
    }

    interface OnItemClickListener {
        fun onItemClicked(recyclerView: RecyclerView, position: Int, view: View)
    }

    interface OnItemLongClickListener {
        fun onItemLongClicked(recyclerView: RecyclerView, position: Int, view: View) : Boolean
    }
}