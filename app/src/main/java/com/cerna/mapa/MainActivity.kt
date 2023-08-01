package com.cerna.mapa

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createMapFragment()
    }

    //Muestra el mapa en el fragmento que esta dentro del diseño de la actividad.
    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        //La inicializamos con la función getMapAsync(this).
        mapFragment.getMapAsync(this)
        //getMapAsync(this) necesita que nuestra activity implemente la función onMapReady()
    }

    //Se llamará automáticamente cuando el mapa haya cargado y está listo para ser utilizado
    override fun onMapReady(googleMap: GoogleMap) {
        //Para testear
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        //se configura el mapa y se llama a otros métodos para habilitar la ubicación y mostrar un marcador en el mapa.
        //createMarker()
        createPolylines()
        //__________ PARTE II: Ubicacion en tiempo real _____________________
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableMyLocation()
    }

    //Un marker: pequeñas etiquetas para marcar un lugar a través de sus coordenadas.
    private fun createMarker() {
        val lugarFavorito = LatLng(-9.120453, -78.514176)
        map.addMarker(
            MarkerOptions().position(lugarFavorito).title("Universidad Nacional del Santa!")
        )
        //animación para que el mapa haga zoom donde creamos el marker.
        map.animateCamera(
            //18f cantidad de zoom que queremos hacer en dichas coordenadas.
            CameraUpdateFactory.newLatLngZoom(lugarFavorito, 18f),
            4000,
            null
        )
    }

    //_______________________ PARTE II: Ubicacion en tiempo real  ______________
    //verifica si se han otorgado los permisos de ubicación.
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    //comprobar si el permiso ha sido aceptado o no.
    private fun enableMyLocation() {
        //habilita la ubicación actual en el mapa si los permisos están concedidos;
        //compruebe si el mapa ha sido inicializado, si no es así saldrá de la función (gracias a return)
        if (!::map.isInitialized) return
        //map ya ha sido inicializada, es decir que el mapa ya ha cargado, pues comprobaremos los permisos.
        if (isPermissionsGranted()) {
            //activará la ubicación a tiempo real
            map.isMyLocationEnabled = true
        } else {
            //será la encargada de solicitar los permisos.
            requestLocationPermission()
        }
    }

    //variable que sera código de respuesta para saber si al aceptarse permisos ha sido el nuestro.
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private fun requestLocationPermission() {
        //Si entra por el if de significa que ya había rechazado los permisos antes
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            //Si entra al esllse significará que nunca le hemos pedido los permisos y
            // lo haremos a través de la función ActivityCompat.requestPermissions,
            ActivityCompat.requestPermissions(
                this,
                //permiso o los permisos que queremos que acepte
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    //método que nos avise si al pedirle los permisos los ha aceptado.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isPermissionsGranted()) {
            map.isMyLocationEnabled = false
            Toast.makeText(
                this,
                "Para activar la localización ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Boton pulsado", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }

    private fun createPolylines(){
        val polylineOptions = PolylineOptions()
            .add(LatLng(-9.118825987077372,-78.51787776100515))
            .add(LatLng( -9.11709862063067,-78.51601165703241))
            .add(LatLng( -9.123835302500822,-78.50796408364806))
            .add(LatLng( -9.126714187585279,-78.5101800821165))
            .add(LatLng( -9.123950458350109,-78.51653649877443))
            .add(LatLng( -9.118768408330538,-78.51781944525612))
//            .add(LatLng( 40.419173113350965, -3.7048280239105225))
//            .add(LatLng(40.419173113350965,-3.705976009368897))

        polylineOptions.color(R.color.lavender) // Cambia el color de la polilínea
        polylineOptions.width(10f) // Cambia el grosor de la polilínea

        val polyline = map.addPolyline(polylineOptions)
    }

}