package com.example.physicshub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Component toggle giữa Light và Dark Mode
 *
 * @param isDarkMode Trạng thái hiện tại (true = Dark, false = Light)
 * @param onToggle Callback khi người dùng toggle theme
 * @param modifier Modifier tùy chỉnh
 */
@Composable
fun ThemeToggle(
    isDarkMode: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onToggle),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeOption(
                icon = {
                    Icon(
                        imageVector = Icons.Default.LightMode,
                        contentDescription = "Light Mode",
                        tint = if (!isDarkMode)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                isSelected = !isDarkMode
            )
            ThemeOption(
                icon = {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = "Dark Mode",
                        tint = if (isDarkMode)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                isSelected = isDarkMode
            )
        }
    }
}

@Composable
private fun ThemeOption(
    icon: @Composable () -> Unit,
    isSelected: Boolean
) {
    Surface(
        modifier = Modifier.padding(2.dp),
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
        }
    }
}