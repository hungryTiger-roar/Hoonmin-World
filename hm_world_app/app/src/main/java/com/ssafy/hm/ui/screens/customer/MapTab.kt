package com.ssafy.hm.ui.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.ssafy.hm.R

@Composable
fun MapTab(paddingValues: PaddingValues) {
    Image(
        painter = painterResource(id = R.drawable.map),
        contentDescription = "map",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    )
}
