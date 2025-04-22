package com.example.musicplayerapp.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(onItemSelected: (Int) -> Unit = {}, selectedItem: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            elevation = CardDefaults.cardElevation(10.dp),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .align(Alignment.BottomCenter)
        ) {
            NavigationBar(containerColor = Color.Transparent) {
                val items = listOf(
                    Icons.Default.Home to "Home",
                    Icons.Default.Search to "Search",
                    Icons.Default.LibraryMusic to "library",
                    Icons.Default.AccountCircle to "Profile"
                )

                items.forEachIndexed { index, (icon, label) ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                icon,
                                contentDescription = label,
                                tint = if (selectedItem == index) Color(0xFFDF9FFF) else Color.White
                            )
                        },
                        label = {
                            Text(
                                label,
                                color = if (selectedItem == index) Color(0xFFDF9FFF) else Color.White
                            )
                        },
                        selected = selectedItem == index,
                        onClick = {
                            onItemSelected(index)
                        }
                    )
                }
            }
        }
    }
}
