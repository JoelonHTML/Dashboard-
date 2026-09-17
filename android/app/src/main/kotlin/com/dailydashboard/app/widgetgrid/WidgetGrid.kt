package com.dailydashboard.app.widgetgrid

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.dailydashboard.core.datastore.model.WidgetConfig
import com.dailydashboard.core.datastore.model.WidgetSize
import com.dailydashboard.core.designsystem.theme.DashboardTheme
import com.dailydashboard.core.designsystem.theme.DashboardTokens

private fun WidgetSize.columnSpan(gridColumns: Int): Int = when (this) {
    WidgetSize.SMALL -> 1
    WidgetSize.WIDE -> minOf(2, gridColumns)
    WidgetSize.LARGE -> gridColumns
}

/**
 * Grid met een instelbaar aantal kolommen (zie instellingen). In bewerkmodus kun je een
 * kaart lang indrukken en verslepen (spring-achtige animatie via animateFloatAsState),
 * van formaat wisselen, of verwijderen.
 */
@Composable
fun WidgetGrid(
    widgets: List<WidgetConfig>,
    isEditMode: Boolean,
    onReorder: (List<WidgetConfig>) -> Unit,
    onRemove: (WidgetConfig) -> Unit,
    onCycleSize: (WidgetConfig) -> Unit,
    modifier: Modifier = Modifier,
    gridColumns: Int = 4,
) {
    val itemBounds = remember { mutableStateOf(mapOf<String, Rect>()) }
    var draggedId by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    val currentWidgets by rememberUpdatedState(widgets)

    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumns),
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(DashboardTokens.spacingGrid),
        horizontalArrangement = Arrangement.spacedBy(DashboardTokens.spacingGrid),
        verticalArrangement = Arrangement.spacedBy(DashboardTokens.spacingGrid),
    ) {
        items(widgets, key = { it.id }, span = { GridItemSpan(it.size.columnSpan(gridColumns)) }) { widget ->
            val isDragged = widget.id == draggedId
            val animatedX by animateFloatAsState(if (isDragged) dragOffset.x else 0f, label = "dragX")
            val animatedY by animateFloatAsState(if (isDragged) dragOffset.y else 0f, label = "dragY")
            val animatedScale by animateFloatAsState(if (isDragged) 1.05f else 1f, label = "dragScale")

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coords ->
                        val topLeft = coords.positionInParent()
                        itemBounds.value = itemBounds.value + (
                            widget.id to Rect(topLeft, coords.size.toSize())
                            )
                    }
                    .graphicsLayer {
                        translationX = animatedX
                        translationY = animatedY
                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                    .zIndex(if (isDragged) 1f else 0f)
                    .then(
                        if (isEditMode) {
                            Modifier.pointerInput(widget.id) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        draggedId = widget.id
                                        dragOffset = Offset.Zero
                                    },
                                    onDrag = { change, delta ->
                                        change.consume()
                                        dragOffset += delta
                                        val myBounds = itemBounds.value[widget.id] ?: return@detectDragGesturesAfterLongPress
                                        val draggedCenter = myBounds.center + dragOffset
                                        val targetId = itemBounds.value.entries
                                            .firstOrNull { (id, bounds) -> id != widget.id && bounds.contains(draggedCenter) }
                                            ?.key
                                        if (targetId != null) {
                                            val list = currentWidgets
                                            val fromIndex = list.indexOfFirst { it.id == widget.id }
                                            val toIndex = list.indexOfFirst { it.id == targetId }
                                            if (fromIndex != -1 && toIndex != -1 && fromIndex != toIndex) {
                                                onReorder(list.toMutableList().apply { add(toIndex, removeAt(fromIndex)) })
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        draggedId = null
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        draggedId = null
                                        dragOffset = Offset.Zero
                                    },
                                )
                            }
                        } else {
                            Modifier
                        },
                    ),
            ) {
                WidgetContent(widget = widget, modifier = Modifier.fillMaxWidth())

                if (isEditMode) {
                    EditModeOverlay(
                        onResize = { onCycleSize(widget) },
                        onRemove = { onRemove(widget) },
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }
            }
        }
    }
}

@Composable
private fun EditModeOverlay(
    onResize: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = DashboardTheme.colors
    androidx.compose.foundation.layout.Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(
            onClick = onResize,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.surface.copy(alpha = 0.9f)),
        ) {
            Icon(Icons.Filled.AspectRatio, contentDescription = "Formaat wijzigen", tint = colors.textPrimary)
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.alert.copy(alpha = 0.9f)),
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Widget verwijderen", tint = MaterialTheme.colorScheme.onError)
        }
    }
}

private fun androidx.compose.ui.unit.IntSize.toSize() =
    androidx.compose.ui.geometry.Size(width.toFloat(), height.toFloat())
