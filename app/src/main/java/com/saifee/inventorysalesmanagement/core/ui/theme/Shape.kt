package com.saifee.inventorysalesmanagement.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// StockPilot Enterprise Shape System
val MonolithShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),      // Chips & Tooltips
    medium = RoundedCornerShape(10.dp),    // Form Inputs & Buttons (Spec: 10dp)
    large = RoundedCornerShape(12.dp),     // Cards & Surfaces (Spec: 12dp)
    extraLarge = RoundedCornerShape(16.dp) // Dialogs & Bottom Sheets
)

// StockPilot Pill Shape for Badges and Status Chips
val MonolithPillShape = RoundedCornerShape(100.dp)
val StockPilotInputShape = RoundedCornerShape(10.dp)
val StockPilotButtonShape = RoundedCornerShape(10.dp)
val StockPilotCardShape = RoundedCornerShape(12.dp)
