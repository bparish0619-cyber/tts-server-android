package com.github.jing332.compose.widgets.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState

@Preview
@Composable
private fun PreviewNestedList() {
    MaterialTheme {
        val list: List<Element> = remember {
            mutableStateListOf<Element>().apply {
                add(GroupElement(true, "group1", children = mutableStateListOf(ItemElement("1233"))))
            }
        }
        NestedLazyColumn(
            list = list,
            onListChange = {},
            headerContent = {
                Text("group")
            },
            itemContent = {
                Text("hello")
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NestedLazyColumn(
    modifier: Modifier = Modifier,
    list: List<Element>, // The list passed in from outside
    onListChange: (List<Element>) -> Unit, // Callback invoked after the list changes
    headerContent: @Composable BoxScope.(GroupElement) -> Unit,
    itemContent: @Composable BoxScope.(ItemElement) -> Unit
) {
    val state = rememberLazyListState()
    // Internally maintained cacheList, used for real-time updates while dragging
    val (cacheList, setCacheList) = remember { mutableStateOf(list) }

    val reorderState = rememberReorderableLazyListState(
        listState = state,
        onMove = { from, to ->
            val fromKey = from.key ?: return@rememberReorderableLazyListState
            val toKey = to.key ?: return@rememberReorderableLazyListState
            // Update cacheList to respond to UI changes in real time
            val newList = updateNestedList(cacheList, fromKey, toKey)
            setCacheList(newList)
        },
        onDragEnd = { _, _ ->
            // After dragging ends, call onListChange to notify the external state
            onListChange(cacheList)
        }
    )

    fun LazyListScope.listContent(elements: List<Element>) {
        elements.forEach { element ->
            when (element) {
                is GroupElement -> {
                    stickyHeader(key = element.key) {
                        ReorderableItem(reorderableState = reorderState, key = element.key) {
                            headerContent(element)
                        }
                    }
                    listContent(element.children)
                }

                is ItemElement -> {
                    item(key = element.key) {
                        ReorderableItem(reorderableState = reorderState, key = element.key) {
                            itemContent(element)
                        }
                    }
                }
            }
        }
    }

    LazyColumn(modifier = modifier.detectReorderAfterLongPress(reorderState), state = state) {
        // Render using cacheList
        listContent(cacheList)
    }
}

// The updateNestedList and findElementAndParent functions remain the same as in the previous version.
fun updateNestedList(list: List<Element>, fromKey: Any, toKey: Any): List<Element> {
    // Use toMutableList() to create a mutable copy, avoiding modification of the original list
    val newList = list.toMutableList()

    // Find the from and to elements and their parents
    val (fromElement, fromParent) = findElementAndParent(newList, fromKey)
    val (toElement, toParent) = findElementAndParent(newList, toKey)

    if (fromElement == null || toElement == null) {
        return newList // If the elements cannot be found, return the original list
    }

    // Remove the element from its original position
    if (fromParent != null) {
        fromParent.children.remove(fromElement)
    } else {
        newList.remove(fromElement)
    }

    // Insert the element at the new position
    if (toParent != null) {
        val toIndex = toParent.children.indexOf(toElement)
        toParent.children.add(toIndex, fromElement)
    } else {
        val toIndex = newList.indexOf(toElement)
        newList.add(toIndex, fromElement)
    }

    return newList
}

// Helper function used to find an element and its parent
fun findElementAndParent(list: List<Element>, key: Any): Pair<Element?, GroupElement?> {
    fun find(elements: List<Element>, parent: GroupElement? = null): Pair<Element?, GroupElement?> {
        for (element in elements) {
            if (element.key == key) {
                return Pair(element, parent)
            }
            if (element is GroupElement) {
                val result = find(element.children, element)
                if (result.first != null) {
                    return result
                }
            }
        }
        return Pair(null, null)
    }
    return find(list)
}
