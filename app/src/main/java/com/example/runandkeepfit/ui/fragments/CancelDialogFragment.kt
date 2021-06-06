package com.example.runandkeepfit.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.runandkeepfit.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelDialogFragment :DialogFragment() {

    private  var yesListner: (() ->Unit)? = null
    fun setYesListner( listner: () -> Unit){
        yesListner = listner
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return  MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle("Cancel Run?")
                .setMessage("Are You sure you want to cancel and delete all run Data")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("Yes"){_,_ ->
                    yesListner?.let {yes ->
                        yes()
                    }
                }
                .setNegativeButton("No"){dialogInterface, _ ->
                    dialogInterface.cancel()
                }
                .create()


    }
}