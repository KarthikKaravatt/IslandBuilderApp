package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameMap()
        }
    }
}

@Preview
@Composable
fun GameMap() {
    val mapData = remember { MapData.get() }
    LazyRow {
        items(MapData.WIDTH) { colIndex ->
            BoxWithConstraints {
                val boxSize = min(maxWidth, maxHeight) / (MapData.HEIGHT + 2)
                Column {
                    repeat(MapData.HEIGHT) { rowIndex ->
                        val mapElement = mapData?.get(rowIndex, colIndex)
                        Box(modifier = Modifier.size(boxSize)) {
                            if (mapElement != null) {
                                Image(
                                    painter = painterResource(id =  mapElement.northWest),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .size((boxSize.value * 0.51).dp)
                                )
                                Image(
                                    painter = painterResource(id =  mapElement.northEast),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size((boxSize.value * 0.51).dp)
                                )
                                Image(
                                    painter = painterResource(id =  mapElement.southWest),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .size((boxSize.value * 0.51).dp)
                                )
                                Image(
                                    painter = painterResource(id =  mapElement.southEast),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size((boxSize.value * 0.51).dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}


