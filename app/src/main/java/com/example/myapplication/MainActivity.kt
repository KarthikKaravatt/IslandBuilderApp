package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

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
    private var pressedStructure by mutableStateOf<Int?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                MainLayout()
            }
        }
    }

    data class DraggedItem(
        var item: MutableState<Int?>,
        var xOffset: MutableState<Float>,
        var yOffset: MutableState<Float>
    )

    @Composable
    private fun MainLayout() {
        val reset = remember { mutableStateOf(false) }
        val draggedItem = remember {
            mutableStateOf(
                DraggedItem(
                    mutableStateOf(null),
                    mutableFloatStateOf(0f),
                    mutableFloatStateOf(0f)
                )
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
            ) {
                GameMap(modifier = Modifier, reset, draggedItem = draggedItem)
            }
            val configuration = resources.configuration
            var weight = 0.1f
            if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
                weight = 0.2f
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight)
            ) {
                ItemSelection(modifier = Modifier, reset, draggedItem = draggedItem)
            }
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
        reset: MutableState<Boolean>,
        draggedItem: MutableState<DraggedItem>
    ) {
        BoxWithConstraints(
            modifier = modifier
        ) {
            val mapData by remember { mutableStateOf(MapData.get()) }
            LazyRow(state = rememberLazyListState(), modifier = modifier.zIndex(0f)) {
                items(MapData.WIDTH) { colIndex ->
                    BoxWithConstraints {
                        // The size of each cell is the minimum of the maxWidth and maxHeight divided by the number of cells.
                        // And then divided by a value to make the cells a bit smaller for the structures row
                        val cellSize = min(maxWidth, maxHeight) / (MapData.HEIGHT)
                        Column(
                            modifier = Modifier.zIndex(0f)
                        ) {
                            repeat(MapData.HEIGHT) { rowIndex ->
                                val mapElement = mapData.value?.get(rowIndex, colIndex)
                                val structure = remember { mutableStateOf(mapElement?.structure) }
                                val currentStructure =
                                    rememberSaveable { mutableStateOf(mapElement?.structure?.drawableId) }
                                val hasStructure = rememberSaveable { mutableStateOf(false) }
                                MapCell(
                                    cellSize = cellSize,
                                    mapElement = mapElement,
                                    structure = structure,
                                    currentStructure = currentStructure,
                                    hasStructure = hasStructure,
                                    reset = reset
                                )
                            }
                        }
                    }
                }
            }
            // Your other UI elements go here

            if (draggedItem.value.item.value != null) {
                val id = draggedItem.value.item.value
                assert(id != null)
                id?.let { painterResource(id = it) }?.let {
                    Image(painter = it,
                        contentDescription = null,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    draggedItem.value.xOffset.value.roundToInt(),
                                    draggedItem.value.yOffset.value.roundToInt()
                                )
                            }
                            .size(50.dp)
                            .zIndex(10f)
                    )
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
        hasStructure: MutableState<Boolean>,
        reset: MutableState<Boolean>
    ) {
        if (mapElement == null) {
            return
        }
        val currentStructureRemember by rememberSaveable { currentStructure }
        Box(
            modifier = Modifier
                .size(cellSize)
                .clickable {
                    if(mapElement.isBuildable){
                        if (reset.value) {
                            reset.value = false
                        }
                        structure.value = pressedStructure?.let { Structure(it, "") }
                        currentStructure.value = pressedStructure
                        hasStructure.value = true
                        pressedStructure = null
                    }
                }
                .zIndex(0f)
        ) {
            Image(
                painter = painterResource(id = mapElement.northWest),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(0f),
            )
            Image(
                painter = painterResource(id = mapElement.northEast),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(0f)
            )
            Image(
                painter = painterResource(id = mapElement.southWest),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(0f)
            )
            Image(
                painter = painterResource(id = mapElement.southEast),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((cellSize.value * 0.51).dp)
                    .zIndex(0f)
            )
            if (!reset.value) {

                if (hasStructure.value) {
                    currentStructureRemember?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .zIndex(0f)
                        )
                    }
                }
            } else {
                hasStructure.value = false
                currentStructure.value = null
                structure.value = null
            }
        }
    }

    @Composable
    private fun ItemSelection(
        modifier: Modifier = Modifier,
        reset: MutableState<Boolean>,
        draggedItem: MutableState<DraggedItem>
    ) {
        val lazyListState: LazyListState = rememberLazyListState()
        LazyRow(
            modifier = modifier
                .padding(2.dp)
                .zIndex(10f),
            state = lazyListState
        ) {
            item {
                val mapData by remember { mutableStateOf(MapData.get()) }
                Button(
                    onClick = {
                        reset.value = true
                        mapData.value?.regenerate()
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
                    draggedItem = draggedItem
                )
            }
        }
    }

    @Composable
    private fun DraggableStructure(img: Int, name: String, draggedItem: MutableState<DraggedItem>) {
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }
        var startOffsetX by remember { mutableFloatStateOf(0f) }
        var startOffsetY by remember { mutableFloatStateOf(0f) }
        var isDragging by remember { mutableStateOf(false) }

        BoxWithConstraints(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            draggedItem.value.item.value = img
                            startOffsetX = offsetX
                            startOffsetY = offsetY
                            draggedItem.value.xOffset.value = offsetX + 500f
                            draggedItem.value.yOffset.value = offsetX + 100f
                            isDragging = true
                        },
                        onDragEnd = {
                            draggedItem.value.item.value = null
                            offsetX = startOffsetX
                            offsetY = startOffsetY
                            draggedItem.value.xOffset.value = offsetX
                            draggedItem.value.yOffset.value = offsetX
                            isDragging = false
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            draggedItem.value.xOffset.value += dragAmount.x
                            draggedItem.value.yOffset.value += dragAmount.y
                        }
                    )
                }
                .zIndex(12f)
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
                            pressedStructure = img
                        }
                        .border(
                            width = 2.dp,
                            color = if (pressedStructure == img) Color.Blue else Color.Transparent
                        )
                        .clip(RoundedCornerShape(4.dp))
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

