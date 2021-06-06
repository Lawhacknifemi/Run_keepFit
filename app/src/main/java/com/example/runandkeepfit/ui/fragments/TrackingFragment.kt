package com.example.runandkeepfit.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runandkeepfit.R
import com.example.runandkeepfit.databinding.FragmentTrackingBinding
import com.example.runandkeepfit.db.Run
import com.example.runandkeepfit.others.Constants.ACTION_PAUSE_SERVICE
import com.example.runandkeepfit.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runandkeepfit.others.Constants.ACTION_STOP_SERVICE
import com.example.runandkeepfit.others.Constants.CANCEL_TRACKING_DIALOG_TAG
import com.example.runandkeepfit.others.Constants.MAP_ZOOM
import com.example.runandkeepfit.others.Constants.POLY_LINE_COLOR
import com.example.runandkeepfit.others.Constants.POLY_LINE_WIDTH
import com.example.runandkeepfit.others.TrackingUtility
import com.example.runandkeepfit.services.TrackingService
import com.example.runandkeepfit.services.polyLine
import com.example.runandkeepfit.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment :Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

    private var isTracking = false

    private var pathPoint = mutableListOf<polyLine>()

    private var map :GoogleMap? = null

    private var currenTimeInMilli = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 80f


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return FragmentTrackingBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentTrackingBinding.bind(view)
        binding.mapView.onCreate(savedInstanceState)


        if (savedInstanceState != null){
            val cancelTrackingDialog = parentFragmentManager.
            findFragmentByTag(CANCEL_TRACKING_DIALOG_TAG)
            as CancelDialogFragment

            cancelTrackingDialog?.setYesListner {
                stopRun()
            }
        }

        binding.run {
            btnToggleRun.setOnClickListener {

                toggleRun()
            }

            mapView.getMapAsync {
                map = it
                addAllPolyLines()
            }
            btnFinishRun.setOnClickListener{
                zoomToSeeWholeTrack()
                endRunAndSaveToDb()
            }
        }
        subScribeToLifeCycleObserver()



    }

    private fun subScribeToLifeCycleObserver(){
        TrackingService.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingService.pathsPoints.observe(viewLifecycleOwner, Observer {
            pathPoint = it
            addLatestPolyLine()
            moveCameraToUser()
        })

        TrackingService.timeRunInMilliS.observe(viewLifecycleOwner, Observer {
            currenTimeInMilli = it
            val formattedTime = TrackingUtility.
            getFormattedStopWatchTime(currenTimeInMilli,false)
            view!!.findViewById<TextView>(R.id.tvTimer).text = formattedTime
        })
    }

    private fun toggleRun(){
        if(isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currenTimeInMilli > 0L){
            this.menu?.getItem(0)?.isVisible = true

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking -> {showCancelTrackingDialog()}
        }
        return super.onOptionsItemSelected(item)

    }

    private fun showCancelTrackingDialog(){
        CancelDialogFragment().apply {
            setYesListner {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }
    private fun stopRun(){
        view?.findViewById<TextView>(R.id.tvTimer)?.text = "00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun moveCameraToUser(){
        if(pathPoint.isNotEmpty() && pathPoint.last().isNotEmpty()){
            map?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            pathPoint.last().last(),
                            MAP_ZOOM
                    )
            )
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if (!isTracking && currenTimeInMilli> 0L) {
            view!!.findViewById<Button>(R.id.btnToggleRun).text = "Start"
            view!!.findViewById<Button>(R.id.btnFinishRun).visibility = View.VISIBLE
        }else if(isTracking){
            view!!.findViewById<Button>(R.id.btnToggleRun).text = "Stop"
            menu?.getItem(0)?.isVisible = true
            view!!.findViewById<Button>(R.id.btnFinishRun).visibility = View.GONE
        }
    }

    private fun zoomToSeeWholeTrack(){
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoint){
            for (pos in polyline){
                bounds.include(pos)
            }
        }
        map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                        bounds.build(),
                        view!!.findViewById<MapView>(R.id.mapView).width ,
                        view!!.findViewById<MapView>(R.id.mapView).height,
                        (view!!.findViewById<MapView>(R.id.mapView).height * 0.05f).toInt()



                )
        )
    }

    private fun endRunAndSaveToDb(){
        map?.snapshot {bmp->
            var distanceInMeters = 0
            for(polyline in pathPoint){
                distanceInMeters += TrackingUtility.calculatePolyLineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters/1000f) /(currenTimeInMilli/1000f/60/60) * 10)/10f
            val dateTImeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters/1000f) * weight).toInt()
            val run = Run(bmp,dateTImeStamp,avgSpeed,distanceInMeters ,currenTimeInMilli,caloriesBurned)

            viewModel.insertRun(run)
            Snackbar.make(requireActivity().findViewById(R.id.rootView),"Run Saved Successfully"
                    ,Snackbar.LENGTH_LONG).show()
            stopRun()
        }
    }
    private fun addAllPolyLines(){
        for(polyline in pathPoint){
            val polyLineOption = PolylineOptions()
                    .color(POLY_LINE_COLOR)
                    .width(POLY_LINE_WIDTH)
                    .addAll(polyline)
            map?.addPolyline(polyLineOption)
        }
    }

    private fun addLatestPolyLine(){
        if (pathPoint.isNotEmpty() && pathPoint.last().size > 1){
            val preLastLang = pathPoint.last()[pathPoint.last().size - 2]
            val lastLatLang = pathPoint.last().last()

            val polyLineOption = PolylineOptions()
                    .color (POLY_LINE_COLOR)
                    .width (POLY_LINE_WIDTH)
                    .add(preLastLang)
                    .add(lastLatLang)

            map?.addPolyline(polyLineOption)

        }
    }

    private fun sendCommandToService(action: String) =
            Intent(requireContext(),TrackingService::class.java).also {

                it.action = action
                requireContext().startService(it)
    }

    override fun onStart() {
        super.onStart()
        view?.findViewById<MapView>(R.id.mapView)?.onStart()
    }

    override fun onPause() {
        super.onPause()
        view?.findViewById<MapView>(R.id.mapView)?.onPause()
    }

    override fun onResume() {
        super.onResume()
        view?.findViewById<MapView>(R.id.mapView)?.onResume()
    }

    override fun onStop() {
        super.onStop()
        view?.findViewById<MapView>(R.id.mapView)?.onStop()

    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        view?.findViewById<MapView>(R.id.mapView)?.onDestroy()
    }
}