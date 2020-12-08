package com.example.foodbuddy

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MyDialog : Dialog {

    constructor(context: Context) : this(context, 0)
    constructor(context: Context, themeResId: Int) : super(
            context,
            R.style.MyDialog
    ) {
        setContentView(R.layout.dialog)
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        initView()
        initEvent()
    }

    private lateinit var tvUpdate: TextView
    private lateinit var tvDelete: TextView
    var updateListener: (() -> Unit)? = null
    var deleteListener: (() -> Unit)? = null


    private fun initView() {
        tvUpdate = findViewById<View>(R.id.tv_update) as TextView
        tvDelete = findViewById<View>(R.id.tv_delete) as TextView
    }


    private fun initEvent() {
        tvUpdate.setOnClickListener {
            updateListener?.invoke()
            dismiss()
        }

        tvDelete.setOnClickListener {
            val confirmDialog = DeleteDialog(context)
            confirmDialog.deleteListener = {
                deleteListener?.invoke()
                dismiss()
            }
            confirmDialog.show()
            dismiss()
        }
    }
}