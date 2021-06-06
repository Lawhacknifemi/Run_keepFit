package com.example.runandkeepfit.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.runandkeepfit.R
import com.example.runandkeepfit.adapters.RunAdapter
import com.example.runandkeepfit.others.TrackingUtility
import com.example.runandkeepfit.databinding.FragmentRunBinding
import com.example.runandkeepfit.others.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.runandkeepfit.others.SortType
import com.example.runandkeepfit.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment :Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks{

  private val viewModel : MainViewModel by viewModels()
  private lateinit var runAdapter: RunAdapter



  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return FragmentRunBinding.inflate(inflater, container, false).root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val binding = FragmentRunBinding.bind(view)

      requestPermission()

      binding.fab.setOnClickListener {
      findNavController().navigate(R.id.action_runFragment_to_trackingFragment)

    }

      when(viewModel.sortType){
          SortType.DATE -> binding.spFilter.setSelection(0)
          SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
          SortType.DISTANCE -> binding.spFilter.setSelection(2)
          SortType.AVG_SPEED -> binding.spFilter.setSelection(3)
          SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
      }

      binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
          override fun onNothingSelected(parent: AdapterView<*>?) {

          }

          override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
             when(position){
                 0 -> viewModel.sortRuns(SortType.DATE)
                 1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                 3 -> viewModel.sortRuns(SortType.DISTANCE)
                 4 -> viewModel.sortRuns(SortType.AVG_SPEED)
                 5 -> viewModel.sortRuns(SortType.CALORIES_BURNED)

             }
          }
      }

      viewModel.runs.observe(viewLifecycleOwner, Observer {
          runAdapter.submitList(it)
      })
      setUpRecyclerView()



  }
    private fun setUpRecyclerView() = view!!.findViewById<RecyclerView>(R.id.rvRuns)
            .apply {
        runAdapter = RunAdapter()
                adapter = runAdapter
                layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestPermission(){
        if (TrackingUtility.hasLocationPermission(requireContext())){
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(this,
            "You need Location Permission to use this App",
            REQUEST_CODE_LOCATION_PERMISSION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )

        }else{
            EasyPermissions.requestPermissions(
                    this,
                    "You nedd Location Permission to use this App.",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
        }


    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)


    }
}