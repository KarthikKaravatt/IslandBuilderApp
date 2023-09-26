package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * This is the main entry point for the app.
 */
class MainActivity : ComponentActivity() {
    private val structureImages = mapOf(
        "Building 1" to R.drawable.ic_building1,
        "Building 2" to R.drawable.ic_building2,
        "Building 3" to R.drawable.ic_building3,
        "Building 4" to R.drawable.ic_building4,
        "Building 5" to R.drawable.ic_building5,
        "Building 6" to R.drawable.ic_building6,
        "Building 7" to R.drawable.ic_building7,
        "Building 8" to R.drawable.ic_building8,
        "Road East" to R.drawable.ic_road_e,
        "Road N" to R.drawable.ic_road_n,
        "Road NE" to R.drawable.ic_road_ne,
        "Road NW" to R.drawable.ic_road_nw,
        "Road S" to R.drawable.ic_road_s,
        "Road SE" to R.drawable.ic_road_se,
        "Road SW" to R.drawable.ic_road_sw,
        "Road W" to R.drawable.ic_road_w,
        "Road NEW" to R.drawable.ic_road_new,
        "Road NSW" to R.drawable.ic_road_nsw,
        "Road NSE" to R.drawable.ic_road_nse,
        "Road NSW" to R.drawable.ic_road_nsw,
        "Tree 1" to R.drawable.ic_tree1,
        "Tree 2" to R.drawable.ic_tree2,
        "Tree 3" to R.drawable.ic_tree3,
        "Tree 4" to R.drawable.ic_tree4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                MainLayout()
            }
        }
    }

    @Composable
    private fun MainLayout() {
        val currentStructureArray: MutableStateFlow<Array<Array<MutableList<MutableState<Int?>>>>> =
            remember{
                MutableStateFlow(
                    Array(MapData.HEIGHT) {
                        Array(MapData.WIDTH) {
                            mutableListOf()
                        }
                    }
                )
            }
        val pressedStructure = remember { mutableStateOf<Int?>(null) }
        Column(modifier = Modifier.fillMaxSize()) {
            GameMap(modifier = Modifier.weight(0.8f), pressedStructure = pressedStructure, currentStructureArray = currentStructureArray)
            ItemSelection(
                modifier = Modifier.weight(0.2f),
                pressedStructure = pressedStructure,
                currentStructureArray = currentStructureArray
            )
        }
    }

    /**
     * The component that displays the game map.
     * Takes the map data and displays it as a grid of images using a LazyRow and LazyColumn.
     * This is equivalent to a RecyclerView in Views .
     */
    @Composable
    private fun GameMap(
        modifier: Modifier = Modifier,
        pressedStructure: MutableState<Int?>,
        currentStructureArray: MutableStateFlow<Array<Array<MutableList<MutableState<Int?>>>>>
    ) {
        val mapData by remember { mutableStateOf(MapData.get()) }
        val currentStructureArrayVal = remember{currentStructureArray.value}
        LazyRow(state = rememberLazyListState(), modifier = modifier.zIndex(0f)) {
            items(MapData.WIDTH) { colIndex ->
                BoxWithConstraints {
                    // The size of each cell is the minimum of the maxWidth and maxHeight divided by the number of cells.
                    // And then divided by a value to make the cells a bit smaller for the structures row
                    val cellSize = min(maxWidth, maxHeight) / (MapData.HEIGHT)
                    LazyColumn(state = rememberLazyListState()) {
                        items(MapData.HEIGHT) { rowIndex ->
                            val mapElement = mapData.value?.get(rowIndex, colIndex)
                            val structure = remember { mutableStateOf(mapElement?.structure) }
                            val currentStructure =
                                rememberSaveable { mutableStateOf(mapElement?.structure?.drawableId) }
                            currentStructureArrayVal[rowIndex][colIndex].add(currentStructure)
                            MapCell(
                                cellSize = cellSize,
                                mapElement = mapElement,
                                structure = structure,
                                currentStructure = currentStructure,
                                pressedStructure = pressedStructure
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MapCell(
        cellSize: Dp,
        mapElement: MapElement?,
        structure: MutableState<Structure?>,
        currentStructure: MutableState<Int?>,
        pressedStructure: MutableState<Int?>
    ) {
        if (mapElement == null) {
            return
        }
        Box(
            modifier = Modifier
                .size(cellSize)
                .clickable {
                    structure.value =
                        pressedStructure.let { it.value?.let { it1 -> Structure(it1, "") } }
                    currentStructure.value = pressedStructure.value
                    pressedStructure.value = null
                },
        ) {
            Image(
                painter = painterResource(id = mapElement.northWest),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(1f),
            )
            Image(
                painter = painterResource(id = mapElement.northEast),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(1f)
            )
            Image(
                painter = painterResource(id = mapElement.southWest),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(1f)
            )
            Image(
                painter = painterResource(id = mapElement.southEast),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(1f)
            )
            if (structure.value != null) {
                currentStructure.value?.let { painterResource(id = it) }
                    ?.let {
                        Image(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(2f)
                        )
                    }
            }
        }
    }

    @Composable
    private fun ItemSelection(
        modifier: Modifier = Modifier,
        pressedStructure: MutableState<Int?>,
        currentStructureArray: MutableStateFlow<Array<Array<MutableList<MutableState<Int?>>>>>
    ) {
        LazyRow(modifier = modifier.padding(2.dp)) {
            item {
                var mapData = MapData.get()
                Button(
                    onClick = {
                        mapData.value?.regenerate()
                        mapData = MapData.get()
                        currentStructureArray.value.forEach { row ->
                            row.forEach { cell ->
                                cell.forEach { structure ->
                                    structure.value = null
                                }
                            }
                        }
                        pressedStructure.value = null
                    },
                    modifier = Modifier.fillMaxSize()
                )
                {
                    Text(text = "Regenerate Map")
                }
            }
            items(structureImages.size) { index ->
                Spacer(modifier = Modifier.padding(15.dp))
                DraggableStructure(
                    img = structureImages.values.elementAt(index),
                    name = structureImages.keys.elementAt(index),
                    pressedStructure = pressedStructure
                )
            }
        }
    }

    @Composable
    private fun DraggableStructure(img: Int, name: String, pressedStructure: MutableState<Int?>) {
//        var offsetX by remember { mutableFloatStateOf(0f) }
//        var offsetY by remember { mutableFloatStateOf(0f) }
//        var startOffsetX by remember { mutableFloatStateOf(0f) }
//        var startOffsetY by remember { mutableFloatStateOf(0f) }
//        var isDragging by remember { mutableStateOf(false) }

        BoxWithConstraints(
            modifier = Modifier
//                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
//                .pointerInput(Unit) {
//                    detectDragGestures(
//                        onDragStart = {
//                            startOffsetX = offsetX
//                            startOffsetY = offsetY
//                            isDragging = true
//                        },
//                        onDragEnd = {
//                            offsetX = startOffsetX
//                            offsetY = startOffsetY
//                            isDragging = false
//                        },
//                        onDrag = { change, dragAmount ->
//                            change.consume()
//                            offsetX += dragAmount.x
//                            offsetY += dragAmount.y
//                        }
//                    )
//                }
        ) {
            val imageSize = min(maxHeight, maxWidth) * 0.75f
            Column {
                Image(
                    painter = painterResource(id = img),
                    contentDescription = null,
                    modifier = Modifier
                        .width(imageSize)
                        .height(imageSize)
                        .fillMaxSize()
                        .clickable {
                            pressedStructure.value = img
                        }
                )
                Text(
                    text = name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black
                )
            }
        }
    }
}

