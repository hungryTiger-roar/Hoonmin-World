package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.background
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.MapsInitializer
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Density

@Composable
fun StoreSelectScreen(
    onBack: () -> Unit,
    onSelectStore: (Int) -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val stores = remember {
        listOf(
            StoreLocation(1, "메인게이트 매장", "010-8824-1592", LatLng(36.110266, 128.418414), 0xFF3B82F6.toInt()),
            StoreLocation(2, "중앙광장 매장", "010-3299-9322", LatLng(36.104853, 128.410783), 0xFF10B981.toInt()),
            StoreLocation(3, "퍼레이드로 매장", "010-2298-1121", LatLng(36.105043, 128.422588), 0xFFEC4899.toInt())
        )
    }
    val storeMarkerStates = remember(stores) {
        stores.associate { it.id to MarkerState(position = it.latLng) }
    }
    var storeMarkerIcons by remember { mutableStateOf<Map<Int, BitmapDescriptor>>(emptyMap()) }
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(stores[0].latLng, 14f)
    }
    var currentLatLng by remember { mutableStateOf<LatLng?>(null) }
    val currentMarkerState = remember { MarkerState(position = LatLng(0.0, 0.0)) }
    var mapLoaded by remember { mutableStateOf(false) }
    var selectedStore by remember { mutableStateOf<StoreLocation?>(null) }
    val background = Brush.verticalGradient(
        colors = listOf(Color(0xFFF5F1FF), Color(0xFFFDFBFF))
    )
    val headerColor = Color(0xFF2A2430)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "수령 장소 선택",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = headerColor,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.size(40.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0xFFE7D6FF),
                shape = CircleShape,
                modifier = Modifier.size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFFB259FF),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("상품을 받을 매장을 선택해주세요", fontWeight = FontWeight.Bold, color = headerColor)
            Spacer(modifier = Modifier.height(6.dp))
            Text("선택한 매장에서 10분 이내에 상품을 준비해드려요", color = Color(0xFF7A7282), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFEAF3FF), Color(0xFFF8F1FF))
                    )
                )
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapLoaded = { mapLoaded = true }
            ) {
                if (currentLatLng != null) {
                    Circle(
                        center = currentMarkerState.position,
                        radius = 18.0,
                        strokeColor = Color(0xFF1E88E5),
                        fillColor = Color(0xFF1E88E5)
                    )
                }
                stores.forEach { store ->
                    val state = storeMarkerStates[store.id] ?: MarkerState(position = store.latLng)
                    Marker(
                        state = state,
                        title = store.name,
                        icon = storeMarkerIcons[store.id]
                            ?: BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        anchor = Offset(0.5f, 1f),
                        onClick = {
                            selectedStore = store
                            true
                        }
                    )
                }
            }
        }
    }

    selectedStore?.let { store ->
        AlertDialog(
            onDismissRequest = { },
            title = { Text(store.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("매장을 선택했어요. 아래에서 바로 이동하거나 연락할 수 있어요.")
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF3F4F6))
                                .clickable { openGoogleMaps(context, store) },
                            headlineContent = { Text("길찾기") }
                        )
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF3F4F6))
                                .clickable { openDialer(context, store.phone) },
                            headlineContent = { Text("전화하기") }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { selectedStore = null },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("취소", fontSize = 12.sp)
                            }
                            Button(
                                onClick = {
                                    onSelectStore(store.id)
                                    selectedStore = null
                                },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2DB400),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("주문", fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = { },
            dismissButton = { }
        )
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (fineGranted || coarseGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    currentLatLng = latLng
                    cameraPositionState.position =
                        com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(latLng, 15f)
                }
            }
        }
    }

    androidx.compose.runtime.LaunchedEffect(currentLatLng) {
        currentLatLng?.let { currentMarkerState.position = it }
    }

    androidx.compose.runtime.LaunchedEffect(mapLoaded) {
        if (!mapLoaded) return@LaunchedEffect
        MapsInitializer.initialize(context, MapsInitializer.Renderer.LATEST, null)
        storeMarkerIcons = stores.associate { store ->
            store.id to createStoreLabelIcon(context, store.name, store.markerColor)
        }
    }
}

private data class StoreLocation(
    val id: Int,
    val name: String,
    val phone: String,
    val latLng: LatLng,
    val markerColor: Int
)

private fun createStoreLabelIcon(
    context: android.content.Context,
    label: String,
    color: Int
): BitmapDescriptor {
    val density = context.resources.displayMetrics.density
    val textSize = 12f * density
    val paddingH = 10f * density
    val paddingV = 6f * density
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = android.graphics.Color.WHITE
        this.textSize = textSize
        this.typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    val textWidth = paint.measureText(label)
    val textHeight = textSize
    val width = (textWidth + paddingH * 2).toInt()
    val height = (textHeight + paddingV * 2).toInt()

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
    canvas.drawRoundRect(rect, 10f * density, 10f * density, bgPaint)
    val textY = paddingV + textHeight - (paint.descent() / 2)
    canvas.drawText(label, paddingH, textY, paint)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

private fun openGoogleMaps(context: android.content.Context, store: StoreLocation) {
    val uri = Uri.parse(
        "google.navigation:q=${store.latLng.latitude},${store.latLng.longitude}&mode=w"
    )
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val web = Uri.parse(
            "https://www.google.com/maps/dir/?api=1&destination=${store.latLng.latitude},${store.latLng.longitude}&travelmode=walking"
        )
        context.startActivity(Intent(Intent.ACTION_VIEW, web))
    }
}

private fun openDialer(context: android.content.Context, phone: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phone")
    }
    context.startActivity(intent)
}
