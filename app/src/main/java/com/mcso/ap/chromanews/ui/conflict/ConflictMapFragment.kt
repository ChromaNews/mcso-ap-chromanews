package com.mcso.ap.chromanews.ui.conflict

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mcso.ap.chromanews.R
import com.mcso.ap.chromanews.databinding.ConflictDetailsBinding
import com.mcso.ap.chromanews.databinding.FragmentConflictMapBinding
import com.mcso.ap.chromanews.model.MainViewModel
import com.mcso.ap.chromanews.model.conflict.Conflicts
import com.mcso.ap.chromanews.model.sentiment.SentimentColor
import com.mcso.ap.chromanews.ui.SentimentUtil
import kotlin.math.abs

class ConflictMapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private val TAG = "ConflictMapFragment"
    }

    private val acledLink: String = "https://www.acleddata.com"
    private var _binding: FragmentConflictMapBinding? = null
    private lateinit var conflictsMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private lateinit var progressBar: ProgressBar
    private var locationPermissionGranted = false
    private val viewModel: MainViewModel by viewModels()
    private val binding get() = _binding!!
    private val conflictDetailsBinding get() = _conflictDetailsBinding
    private lateinit var _conflictDetailsBinding: ConflictDetailsBinding

    private val sentimentUtil = SentimentUtil()
    private var sentimentColor: SentimentColor = SentimentColor(0,255, 0)

    private lateinit var locationService: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentConflictMapBinding.inflate(inflater, container, false)
        _conflictDetailsBinding = _binding!!.conflictSection
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "loading map fragment")

        checkGooglePlayServices()
        requestPermission()

        val conflictMap = childFragmentManager.findFragmentById(R.id.conflict_map) as SupportMapFragment
        progressBar = binding.loading

        conflictMap.getMapAsync(this)
        geocoder = Geocoder(requireContext())

        viewModel.observeShowProgress().observe(viewLifecycleOwner){
            showProgress ->
            run {
                if (showProgress) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }

        conflictDetailsBinding.closeButton.setOnClickListener {
            conflictDetailsBinding.conflictDetails.visibility = View.GONE
        }
        conflictDetailsBinding.conflictDetails.visibility = View.GONE

        // launch browser ACLED data
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(acledLink))
        binding.acledAttribution.setOnClickListener {
            startActivity(browserIntent)
        }

        locationService = activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!

    }

    override fun onMapReady(googleMap: GoogleMap) {
        conflictsMap = googleMap
        conflictsMap.setPadding(0,0, 0, 200)

        if (locationPermissionGranted){
            val permission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)

            if (permission != PackageManager.PERMISSION_GRANTED){
                if (!locationPermissionGranted){
                    Toast.makeText(requireContext(), "Unable to show location - permission required",
                        Toast.LENGTH_LONG).show()
                    return
                }
            }

            conflictsMap.isMyLocationEnabled = true

            getConflictLocations()
            val locationResult = locationService.lastLocation

            locationResult.addOnSuccessListener { location ->
                run {
                    if (location != null){
                        val latLng = LatLng(location.latitude, location.longitude)
                        val update = CameraUpdateFactory.newLatLngZoom(latLng, 5.0F);
                        conflictsMap.moveCamera(update);
                    }
                }
            }
        }
    }

    private fun getConflictLocations(){
        conflictsMap.uiSettings.isZoomControlsEnabled = true
        conflictsMap.setOnMarkerClickListener {
            showData(it)
        }
        conflictsMap.setOnMapClickListener {

            // hide details layout
            conflictDetailsBinding.conflictDetails.visibility = View.GONE

            val markerLocation = it
            val markerLat = markerLocation.latitude
            val markerLong = markerLocation.longitude
            var addressList = emptyList<Address>()

            try {
                // handling failures... gracefully (!?)
                addressList = geocoder.getFromLocation(markerLat, markerLong, 1)
            } catch (e: Exception){
                Log.e(TAG, "Exception while identifying selected location: ${e.message}")
            }

            if (addressList.isNotEmpty()){
                val countryName = addressList[0].countryName
                Log.d(TAG, "Country Selected: $countryName")
                Toast.makeText(requireContext(), "Fetching conflicts in $countryName", Toast.LENGTH_LONG).show()

                // api call to get conflicts
                viewModel.netConflict(countryName)

                viewModel.observeConflictData().observe(this){ conflictsResponse ->
                    conflictsMap.clear()
                    run {
                        Log.d(TAG,
                            "Total Conflicts = ${conflictsResponse.count}")
                        val conflictsList = conflictsResponse.conflictList
                        val latLngBounds: LatLngBounds.Builder = LatLngBounds.Builder()

                        conflictsList.forEach { conflict ->
                            run {
                                Log.d(TAG,
                                    "Conflict location: ${conflict.location} " +
                                            "(${conflict.latitude}, ${conflict.longitude})")

                                val conflictLatLng = LatLng(conflict.latitude.toDouble(), conflict.longitude.toDouble())
                                val markerOption = MarkerOptions().position(conflictLatLng)
                                    .title(conflict.location)
                                conflictsMap.addMarker(markerOption)

                                latLngBounds.include(conflictLatLng)
                            }
                        }

                        if (conflictsList.size > 0){
                            conflictsMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    latLngBounds.build(), 150
                                )
                            )
                        } else {
                            conflictsMap.clear()
                            Toast.makeText(requireContext(), "Peace!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Try a different location", Toast.LENGTH_SHORT).show()
                Log.e(javaClass.simpleName, "Unable to find address for location $markerLat, $markerLong")
            }

            viewModel.observerConflictSentiment().observe(viewLifecycleOwner){ data ->
                run {
                    Log.d(TAG, "score => ${data.score} | ${data.type}")
                    calculateSentimentColorCode(data.score.toDouble())
                    val score = String.format(
                        "%.3f", data.score.toDouble()
                    ).toDouble()
                    val sentimentScore = "Sentiment Score: $score"
                    conflictDetailsBinding.conflictSentiment.text = sentimentScore
                    conflictDetailsBinding.conflictSentiment.setBackgroundColor(
                        Color.parseColor(
                            sentimentUtil.getSentimentColorInHex(sentimentColor)
                        )
                    )
                }
            }
        }
    }

    private fun calculateSentimentColorCode(sentimentScore: Double){
        when (sentimentScore){
            in 0.01..1.0 -> sentimentColor = sentimentUtil.calculatePositiveColor(sentimentScore)
            in -1.0..-0.01 -> sentimentColor = sentimentUtil.calculateNegativeColor(sentimentScore)
            else -> {
                // do nothing, default is green
            }
        }
        Log.d(TAG, "Conflict Sentiment value = $sentimentScore")
        Log.d(TAG, "mood color: [${sentimentColor.red}, ${sentimentColor.green}, ${sentimentColor.blue}]")
    }

    @SuppressLint("SetTextI18n")
    private fun showData(marker: Marker): Boolean {
        marker.showInfoWindow()

        val conflictInfo : Conflicts? = viewModel.getConflictForLocation(marker.title.toString())


        if (conflictInfo != null){

            if (conflictInfo.notes != ""){
                viewModel.netAnalyzeConflict(conflictInfo.notes)
            }


            conflictDetailsBinding.conflictDetails.visibility = View.VISIBLE
            conflictDetailsBinding.sources.text = conflictInfo.source
            setNotes(conflictInfo.notes)
            setActors(conflictInfo.actor_one, conflictInfo.actor_two)
            conflictDetailsBinding.eventDate.text = conflictInfo.date
        }

        return true
    }

    private fun setNotes(notes: String){
        val bracketIndex = notes.indexOf("[")
        var eventNotes = "-empty-"
        if (bracketIndex > 0){
            eventNotes = notes.substring(0, bracketIndex)
        }

        conflictDetailsBinding.notes.text = eventNotes
    }

    private fun setActors(actor_one: String, actor_two: String){
        var actorsText = "unknown"
        if (actor_one.isNotEmpty()){
            actorsText = actor_one

            if (actor_two.isNotEmpty()){
                actorsText += " vs $actor_two"
            }
        }

        conflictDetailsBinding.actors.text = actorsText
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        if (resultCode != ConnectionResult.SUCCESS) {
            if (resultCode.let { googleApiAvailability.isUserResolvableError(it) }) {
                googleApiAvailability.getErrorDialog(this, resultCode, 257)?.show()
            } else {
                Log.i(javaClass.simpleName,
                    "This device must install Google Play Services.")
                onDestroy()
            }
        }
    }

    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    locationPermissionGranted = true;
                } else -> {
                Toast.makeText(requireContext(),
                    "Unable to show location - permission required",
                    Toast.LENGTH_LONG).show()
            }
            }
        }
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }
}