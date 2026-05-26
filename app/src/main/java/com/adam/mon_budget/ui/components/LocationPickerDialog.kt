package com.adam.mon_budget.ui.components

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adam.mon_budget.ui.components.PrimaryButton
import com.adam.mon_budget.ui.theme.Black
import com.adam.mon_budget.ui.theme.GreenDark
import com.adam.mon_budget.ui.theme.GreenPrimary
import com.adam.mon_budget.ui.theme.InterFontFamily
import com.adam.mon_budget.ui.theme.NunitoFontFamily
import com.adam.mon_budget.ui.theme.White
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.util.Locale

@Composable
fun LocationPickerDialog(
    onDismiss: () -> Unit,
    onLocationSelected: (address: String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var selectedAddress by remember { mutableStateOf<String?>(null) }
    var loadingAddress by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(White)
                .border(2.dp, Black, RoundedCornerShape(24.dp))
        ) {
            /* ── Titre du popup ── */
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Choisir un lieu",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Black
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, null, tint = Color.Gray)
                }
            }

            Text(
                "Appuie sur la carte pour sélectionner un lieu",
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            /* ── Carte OSMDroid ── */
            AndroidView(
                factory = { ctx ->
                    // Config OSMDroid (cache interne, pas besoin de WRITE_EXTERNAL_STORAGE)
                    Configuration.getInstance().apply {
                        load(ctx, ctx.getSharedPreferences("osmdroid", 0))
                        userAgentValue = ctx.packageName
                        osmdroidTileCache = File(ctx.cacheDir, "osmdroid")
                    }

                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(13.0)
                        // Centre par défaut : Paris
                        controller.setCenter(GeoPoint(48.8566, 2.3522))

                        // Overlay pour capter les taps
                        val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                p ?: return false
                                selectedPoint = p
                                // Supprimer l'ancien marqueur
                                overlays.removeAll { it is Marker }
                                // Ajouter le nouveau marqueur
                                val marker = Marker(this@apply).apply {
                                    position = p
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Position sélectionnée"
                                }
                                overlays.add(marker)
                                invalidate()

                                // Reverse geocoding
                                scope.launch {
                                    loadingAddress = true
                                    selectedAddress = reverseGeocode(ctx, p.latitude, p.longitude)
                                    loadingAddress = false
                                }
                                return true
                            }
                            override fun longPressHelper(p: GeoPoint?) = false
                        })
                        overlays.add(0, eventsOverlay)
                        mapView = this
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            /* ── Adresse sélectionnée ── */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GreenPrimary.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (loadingAddress) {
                        CircularProgressIndicator(color = GreenDark, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text("Recherche de l'adresse…", fontFamily = InterFontFamily, fontSize = 13.sp, color = Color.Gray)
                    } else if (selectedAddress != null) {
                        Icon(Icons.Filled.LocationOn, null, tint = GreenDark, modifier = Modifier.size(18.dp))
                        Text(selectedAddress!!, fontFamily = InterFontFamily, fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp, color = Black)
                    } else {
                        Icon(Icons.Filled.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Text("Aucune position sélectionnée", fontFamily = InterFontFamily, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }

            /* ── Bouton Confirmer ── */
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                PrimaryButton(
                    text = "Confirmer ce lieu",
                    onClick = {
                        val addr = selectedAddress ?: return@PrimaryButton
                        onLocationSelected(addr)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedAddress != null && !loadingAddress
                )
            }
        }
    }
}

private suspend fun reverseGeocode(context: Context, lat: Double, lon: Double): String? =
    withContext(Dispatchers.IO) {
        try {
            @Suppress("DEPRECATION")
            val results = Geocoder(context, Locale.getDefault()).getFromLocation(lat, lon, 1)
            if (!results.isNullOrEmpty()) {
                val a = results[0]
                buildString {
                    if (!a.thoroughfare.isNullOrBlank()) append(a.thoroughfare)
                    if (!a.locality.isNullOrBlank()) {
                        if (isNotEmpty()) append(", ")
                        append(a.locality)
                    }
                }.ifBlank { "${String.format("%.4f", lat)}, ${String.format("%.4f", lon)}" }
            } else "${String.format("%.4f", lat)}, ${String.format("%.4f", lon)}"
        } catch (e: Exception) { null }
    }
