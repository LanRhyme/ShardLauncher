package com.lanrhyme.shardlauncher.ui.developeroptions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lanrhyme.shardlauncher.common.SidebarPosition
import com.lanrhyme.shardlauncher.ui.components.CombinedCard
import com.lanrhyme.shardlauncher.ui.components.CustomButton
import com.lanrhyme.shardlauncher.ui.components.CustomCard
import com.lanrhyme.shardlauncher.ui.components.CustomDialog
import com.lanrhyme.shardlauncher.ui.components.CustomTextField
import com.lanrhyme.shardlauncher.ui.components.ScalingActionButton
import com.lanrhyme.shardlauncher.ui.components.SegmentedNavigationBar
import com.lanrhyme.shardlauncher.ui.components.SimpleListLayout
import com.lanrhyme.shardlauncher.ui.components.SliderLayout
import com.lanrhyme.shardlauncher.ui.components.StyledFilterChip
import com.lanrhyme.shardlauncher.ui.components.SwitchLayout
import com.lanrhyme.shardlauncher.ui.components.TitleAndSummary
import dev.chrisbanes.haze.HazeState

@Composable
fun ComponentDemoScreen(
    isCardBlurEnabled: Boolean,
    hazeState: HazeState
) {
    var textState by remember { mutableStateOf("Hello") }
    var switchState by remember { mutableStateOf(false) }
    var sliderState by remember { mutableStateOf(0.5f) }
    var selectedListPage by remember { mutableStateOf(SidebarPosition.Left) }
    var selectedSegment by remember { mutableStateOf("tab1") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TitleAndSummary(title = "Component Demo", summary = "A showcase of all components")
        }

        item {
            ScalingActionButton(onClick = { }, text = "Scaling Action Button", icon = Icons.Default.Favorite)
        }

        item {
            CustomCard { 
                Text("This is a CustomCard", modifier = Modifier.padding(16.dp))
            }
        }

        item {
            CustomButton(onClick = { }) {
                Text("Custom Button")
            }
        }

        item {
            var showDialog by remember { mutableStateOf(false) }
            CustomButton(onClick = { showDialog = true }) {
                Text("Show CustomDialog")
            }
            if (showDialog) {
                CustomDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Dialog Title") },
                    text = { Text("This is the dialog content.") },
                    confirmButton = {
                        CustomButton(onClick = { showDialog = false }) {
                            Text("Confirm")
                        }
                    }
                )
            }
        }

        item {
            CustomTextField(value = textState, onValueChange = { textState = it }, label = "Custom Text Field")
        }
        
        item {
            SwitchLayout(
                checked = switchState, 
                onCheckedChange = { switchState = !switchState }, 
                title = "Switch Layout",
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        
        item {
            SliderLayout(
                value = sliderState, 
                onValueChange = { sliderState = it }, 
                title = "Slider Layout", 
                isGlowEffectEnabled = true,
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }
        
        item {
            SimpleListLayout(
                title = "Simple List Layout",
                items = SidebarPosition.entries,
                selectedItem = selectedListPage,
                onValueChange = { selectedListPage = it },
                getItemText = { pos -> pos.name  },
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            )
        }

        item {
            SegmentedNavigationBar(
                title = "Segmented Nav",
                selectedPage = selectedSegment,
                onPageSelected = { selectedSegment = it },
                pages = listOf("tab1", "tab2", "tab3"),
                getTitle = { title -> title }
            )
        }

        item {
            var chipSelected by remember { mutableStateOf(false) }
            StyledFilterChip(selected = chipSelected, onClick = { chipSelected = !chipSelected}, label = { Text("Styled Filter Chip") })
        }

        item {
            CombinedCard(
                title = "Combined Card", 
                summary = "With some content",
                isCardBlurEnabled = isCardBlurEnabled,
                hazeState = hazeState
            ) {
                Text("This is the content of the combined card", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
